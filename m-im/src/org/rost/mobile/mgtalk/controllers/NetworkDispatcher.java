/*
 * NetworkDispatcher.java
 *
 * Created on 16 Ноябрь 2006 г., 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.controllers;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SocketConnection;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.model.Profile;
import org.rost.mobile.mgtalk.model.SharedStatus;
import org.rost.mobile.mgtalk.utils.Base64;
import org.rost.mobile.mgtalk.utils.MD5;
import org.rost.mobile.mgtalk.utils.NetworkTools;
import org.rost.mobile.mgtalk.utils.XmlNode;

/**
 *
 * @author Kostya
 */
public class NetworkDispatcher extends Thread implements ReadStanzaListener {

    static long PING_INTERVAL = 60000L;
    static long PRESENCE_INTERVAL = 8 * 60000L;
    int sessionInc = 0;
    boolean pingReceived = true;

    class PingTask extends TimerTask {

        public void run() {
            try {
                System.out.println("PingTask");
                if (connectionEstablished) {
                    if (!pingReceived) {
                        softlyCloseConnection();
                    } else {
                        System.out.println("Pinging");
                        AppStore.getInfoTicker().setMessage("Pinging");
                        pingReceived = false;
                        sendMessage("<iq id=\"p" + (++sessionInc) + "\"/>");
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            pingTimer.schedule(new PingTask(), PING_INTERVAL);
        }
    }

    class PresenceTask extends TimerTask {

        public void run() {
            try {

                System.out.println("PresenceTask");
                if (connectionEstablished) {
                    System.out.println("Sending");
                    AppStore.getInfoTicker().setMessage("Presence");
//TODO XXX
                //refreshPresenceStatus();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
    static int CHECK_STREAM_INTERVAL = 1000;
    /** Creates a new instance of NetworkDispatcher */
    SocketConnection connection = null;
    XmlNode xmlReader = null;
    boolean connectionEstablished = false;
    boolean forceTerminate = false;
    boolean forceCreateConnection = false;
    boolean connectionManuallyClosed = false;
    boolean terminatedNotified = false;
    InputStreamReader reader = null;
    OutputStreamWriter writer = null;
    String user = "";
    String domain = "";
    TokensStore tokensStore = new TokensStore();
    protected SharedStatus sharedStatus = null;
    ConnectionTerminatedListener terminatedListener = null;
    protected Profile profile;
    protected Vector stanzaListeners = new Vector();

    public void addStanzaListener(ReadStanzaListener listener) {
        stanzaListeners.addElement(listener);
    }

    public void clearStanzaListeners() {
        stanzaListeners.removeAllElements();
    }
    protected Vector messagesToSend = new Vector();

    public void sendMessage(String message) {
        messagesToSend.addElement(message);
    }

    public void processSendingMessages() {
        try {
            if (!connectionEstablished) {
                return;
            }
            for (int i = 0; i < messagesToSend.size(); i++) {
                String message = (String) messagesToSend.elementAt(i);
                message = NetworkTools.replace(message, "${fullJID}", profile.getFullJID());
                System.out.println("Sending message = " + message);
//                AppStore.getInfoTicker().setMessage(profile.getFullJID(), true);
                if (sendStanza(message)) {
                    messagesToSend.removeElementAt(i);
                    i--;
                } else {
                    runtimeConnectionError();
                    return;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            AppStore.getInfoTicker().setMessage(t.getMessage(), true);
        }
    }

    public void checkNewStanzas() {
        try {
            if (connectionEstablished) {
                while (reader.ready()) {
                    XmlNode x = readStanza();
                    if (!x.getName().equals("")) {
                        for (int i = 0; i < stanzaListeners.size(); i++) {
                            ReadStanzaListener listener =
                                    (ReadStanzaListener) stanzaListeners.elementAt(i);
                            if (listener.stanzaReceived(x)) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void setConnectionTerminatedListener(ConnectionTerminatedListener terminatedListener) {
        this.terminatedListener = terminatedListener;
    }
    Timer pingTimer = new Timer();
    Timer presenceTimer = new Timer();

    public NetworkDispatcher() {
        addStanzaListener(this);
        pingTimer.schedule(new PingTask(), PING_INTERVAL);
        presenceTimer.schedule(new PresenceTask(), PRESENCE_INTERVAL, PRESENCE_INTERVAL);
    }

    void runtimeConnectionError() {
        AppStore.getInfoTicker().setMessage("Disconnected", true);
        softlyCloseConnection();
    }

    void softlyCloseConnection() {
        forceCreateConnection = false;
        if (!terminatedNotified) {
            terminatedNotified = true;
            if (terminatedListener != null) {
                terminatedListener.connectionTerminated();
            }
        }
        //SocketStream specific code

        closeAllStreams();

    }

    public void closeConnection() {
        connectionManuallyClosed = true;
        softlyCloseConnection();

    //Notify termination

    }

    void closeAllStreams() {
        connectionEstablished = false;
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        connection = null;
        reader = null;
        writer = null;
    }

    public void forceStartConnection() {
        closeConnection();
        forceCreateConnection = true;
        connectionManuallyClosed = false;
    }

    String readLine(DataInputStream dis) {
        String s = "";
        byte ch = 0;
        try {
            while ((ch = dis.readByte()) != -1) {
                if (ch == '\n') {
                    return s;
                }
                s += (char) ch;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return s;
    }

    String generateTokenViaGoogle() {
        String first = "Email=" + profile.getUserName() + "&Passwd=" + profile.getPassword() + "&PersistentCookie=false&source=mgtalk";
        try {
            HttpsConnection c = (HttpsConnection) Connector.open("https://www.google.com:443/accounts/ClientAuth?" + first);
            //LOG: Connection go GOOGLE
            DataInputStream dis = c.openDataInputStream();
            String str = readLine(dis);
            String SID = "";
            String LSID = "";
            if (str.startsWith("SID=") && !connectionManuallyClosed) {
                SID = str.substring(4, str.length());
                str = readLine(dis);
                LSID = str.substring(5, str.length());
                first = "SID=" + SID + "&LSID=" + LSID + "&service=mail&Session=true";
                dis.close();
                c.close();
                c = (HttpsConnection) Connector.open("https://www.google.com:443/accounts/IssueAuthToken?" + first);
                //LOG Next connection
                dis = c.openDataInputStream();
                str = readLine(dis);
                String token = MD5.toBase64(new String("\0" + profile.getUserName() + "\0" + str).getBytes());
                dis.close();
                c.close();
                return token;
            } else {
                throw new Exception("Invalid response");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return "";

    }
    static String MY_SERVER = "http://temp.27-i.net/servlet/GenerateToken?";
//    static String MY_SERVER = "http://localhost:8084/MGTalkToken/GenerateToken?";

    String generateTokenViaMyServer() {
        String first = "email=" + profile.getUserName() + "&pass=" + profile.getPassword();
        try {
            HttpConnection c = (HttpConnection) Connector.open(MY_SERVER + first);
            //LOG Coonectiong to help server
            DataInputStream dis = c.openDataInputStream();
            String str = readLine(dis);
            if (!str.equals("") && !connectionManuallyClosed) {
                dis.close();
                c.close();
                return str;
            } else {
                throw new Exception("Invalid response");
            }
        } catch (Throwable ex) {
            //ex.printStackTrace();
            System.out.println("generateTokenViaMyServer socket error");
        }
        return "";
    }

    boolean sendStanza(String mess) {
        if (!connectionEstablished) {
            return false;
        }
        try {
            if (writer != null) {
                writer.write(mess);
                writer.flush();
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public XmlNode readOneStanza() {
        XmlNode x = new XmlNode();
        do {
            x = new XmlNode();
            try {
                x.init("", reader);
            } catch (Throwable e) {
                e.printStackTrace();
                return x;
            }
        } while (x.getName().equals("") && !connectionManuallyClosed);
        return x;
    }

    public XmlNode readStanza() {
        XmlNode x = new XmlNode();
        x = new XmlNode();
        try {
            x.init("", reader);
        } catch (Throwable e) {
            e.printStackTrace();
            runtimeConnectionError();
            return null;
        }
        return x;
    }
    static String RESOURCE = "Mobile";
    static long WAIT_RECONNECT = 3;

    public void reCreateConnection() {
        sharedStatus = new SharedStatus();
        terminatedNotified = false;
        pingReceived = true;
        //SocketStream specific code
        AppStore.getInfoTicker().setMessage("Connecting...", true);
        profile.setFullJID("");
        int tryNum = 0;
        int i = 0;
        if ((i = profile.getUserName().indexOf("@")) != -1) {
            domain = profile.getUserName().substring(i + 1);
            user = profile.getUserName().substring(0, i);
        } else {
            user = profile.getUserName();
            domain = "gmail.com";
        }
        while (!connectionEstablished && !connectionManuallyClosed) {
            if (tryNum > 0) {
                AppStore.getInfoTicker().setMessage("Sleeping " + (tryNum * WAIT_RECONNECT) + " seconds");
                try {
                    Thread.sleep(1000L * (tryNum * WAIT_RECONNECT));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
            tryNum++;
            System.out.println("Connecting with user = " + user + ", domain = " + domain);
            String googleToken = "";
            if (profile.isGoogle()) {
                googleToken = tokensStore.getTokenForUser(profile.getUserName());
                if (googleToken.equals("")) {
                    System.out.println("Token not found - generating");
                    tokensStore.clearTokensForUser(profile.getUserName());
                    googleToken = generateTokenViaGoogle();
                    if (!googleToken.equals("")) {
                        System.out.println("Storing token for user");
                        tokensStore.addTokenForUser(profile.getUserName(), googleToken);
                    }
                } else {
                    System.out.println("Trying stored token");
                }
                if (googleToken.equals("")) {
                    //LOG Token not generated
                    AppStore.getInfoTicker().setMessage("Token generation error");
                    continue;//Next try
                }

            }
            //Next step: initiate stream
            String connString = (profile.isSSL() ? "ssl" : "socket") + "://" + profile.getHost() + ":" + profile.getPort();
            try {
                connection = (SocketConnection) Connector.open(connString);
//                connection.setSocketOption(SocketConnection.LINGER, 0);
                connection.setSocketOption(SocketConnection.KEEPALIVE, 1);

                reader = new InputStreamReader(connection.openInputStream(), "UTF-8");
                writer = new OutputStreamWriter(connection.openOutputStream(), "UTF-8");
            } catch (Throwable t) {
                t.printStackTrace();
                //LOG: Connection not opened
                AppStore.getInfoTicker().setMessage("Cannot open stream connection");
                continue;
            }
            //Opening first stream
            connectionEstablished = true;

            if (!sendStanza("<?xml version=\"1.0\"?><stream:stream to=\"" + domain + "\" xmlns=\"jabber:client\" " +
                    "xmlns:stream=\"http://etherx.jabber.org/streams\" version=\"1.0\">")) {
                //LOG:
                AppStore.getInfoTicker().setMessage("Error opening connection");
                closeAllStreams();
                continue;
            }
            XmlNode response = readOneStanza();
            if (response.getName().equals("stream:error")) {
                //LOG:
                AppStore.getInfoTicker().setMessage("Cannot open first stream");
                closeAllStreams();
                continue;
            }
            //LOG: Authentificating

            boolean authResult = false;
            /*
            if (response.child("mechanisms").hasValueOfChild("DIGEST-MD5")) {
            authResult = processMD5Authentification();
            }
            if (response.child("stream:features").child("mechanisms").hasValueOfChild("X-GOOGLE-TOKEN") && !authResult) {
            authResult = processGOOGLEAuthentification(googleToken);
            if (!authResult) {
            System.out.println("Invalid token - recreating");
            tokensStore.clearTokensForUser(profile.getUserName());
            }
            }
             * */
            if (response.child("stream:features").child("mechanisms").hasValueOfChild("PLAIN") && !authResult) {
                authResult = processPlainAuthentification();
            }
            if (!authResult) {
                //LOG:Auth error
                AppStore.getInfoTicker().setMessage("Invalid username or password");
                closeAllStreams();
                continue;
            } else {
                System.out.println();
                System.out.println("OKOKOKOK!!!");
            }
            if (!sendStanza("<?xml version=\"1.0\"?><stream:stream xmlns:stream=\"http://etherx.jabber.org/streams\" " +
                    "xmlns=\"jabber:client\" to=\"" + domain + "\" version=\"1.0\">")) {
                //LOG:Conn error
                AppStore.getInfoTicker().setMessage("Error opening connection");
                closeAllStreams();
                continue;
            }
            response = readOneStanza();
            if (response.getValue().equals("stream:error")) {
                //LOG:Second stream
                AppStore.getInfoTicker().setMessage("Cannot open second stream");
                closeAllStreams();
                continue;
            }
            if (!sendStanza("<iq type=\"set\" id=\"bind\">" +
                    "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
                    "<resource>" + RESOURCE + "</resource></bind></iq>")) {
                //LOG:Conn error
                AppStore.getInfoTicker().setMessage("Error opening connection");
                closeAllStreams();
                continue;
            }
            response = readOneStanza();
            if (response.getAttr("type").equals("error")) {
                //LOG:Resource
                AppStore.getInfoTicker().setMessage("Cannot bind resource");
                closeAllStreams();
                continue;
            }
            System.out.println("Response = " + response + ", jid = " + response.child("bind").childValue("jid"));
            profile.setFullJID(response.child("bind").childValue("jid"));

            if (!sendStanza("<iq to=\"" + domain + "\" type=\"set\" id=\"sess_1\">" +
                    "<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/></iq>")) {
                //LOG:Session
                AppStore.getInfoTicker().setMessage("Error opening connection");
                closeAllStreams();
                continue;
            }
            response = readOneStanza();
            if (response.getAttr("type").equals("error")) {
                //LOG:Resource
                AppStore.getInfoTicker().setMessage("Cannot open session");
                closeAllStreams();
                continue;
            }
            System.out.println("Response = " + response);
            //Successfull connection. Start working
            //Setup GOOGLE settings
            if (profile.isGoogle()) {
                if (!sendStanza("<iq type=\"get\" id=\"6\"><query xmlns=\"google:relay\"/></iq>")) {
                    //LOG:Session
                    AppStore.getInfoTicker().setMessage("Error opening connection");
                    closeAllStreams();
                    continue;
                }

                response = readOneStanza();
                if (response.getAttr("type").equals("error")) {
                    //LOG:Google rejects us
                    AppStore.getInfoTicker().setMessage("Cannot enable GOOGLE features");
                    closeAllStreams();
                    continue;
                }

                if (!sendStanza("<iq type=\"set\" to=\"" + profile.getUserName() + "\" id=\"15\">" +
                        "<usersetting xmlns=\"google:setting\"><autoacceptrequests value=\"false\"/>" +
                        "<mailnotifications value=\"true\"/></usersetting></iq>")) {
                    //LOG:Conn error
                    AppStore.getInfoTicker().setMessage("Error opening connection");
                    closeAllStreams();
                    continue;
                }

                response = readOneStanza();
                if (response.getAttr("type").equals("error")) {
                    //LOG:Google rejects us
                    AppStore.getInfoTicker().setMessage("Cannot enable GOOGLE features");
                    closeAllStreams();
                    continue;
                }

                if (!sendStanza("<presence><show></show><status></status></presence>" +
                        "<iq type=\"get\" id=\"23\">" +
                        "<query xmlns=\"google:mail:notify\" q=\"(!label:^s) (!label:^k) ((label:^u) (label:^i) (!label:^vm))\"/></iq>" +
                        "<iq type=\"get\" to=\"" + profile.getUserName() + "\" id=\"21\">" +
                        "<query xmlns=\"google:shared-status\"/></iq>")) {
                    //LOG:Conn error
                    AppStore.getInfoTicker().setMessage("Error opening connection");
                    closeAllStreams();
                    continue;
                }
            } else {
                //Non GOOGLE Settings
                if (!sendStanza("<presence><show>" + NetworkTools.getStatusShortNameByNumber(profile.getStatusID()) +
                        "</show><status>" + profile.getStatus() + "</status></presence>")) {
                    //LOG:Conn error
                    AppStore.getInfoTicker().setMessage("Error opening connection");
                    closeAllStreams();
                    continue;
                }
            }
            //For all servers: get roster info
            if (!sendStanza("<iq type=\"get\" id=\"roster\">" +
                    "<query xmlns=\"jabber:iq:roster\"/></iq>")) {
                //LOG:Conn error
                AppStore.getInfoTicker().setMessage("Error opening connection");
                closeAllStreams();
                continue;
            }
        //TODO XXX
        //refreshPresenceStatus();

        }
        AppStore.getInfoTicker().cancelTicker();
    }

    synchronized public void forceTerminateConnection() {
        forceTerminate = true;
        closeConnection();
    }

    public void run() {
        while (!forceTerminate) {
            if (profile != null) {//Thread autocreated after programs starts
                if (!connectionEstablished) {
                    if (forceCreateConnection || (profile.isAutoReconnect() && !connectionManuallyClosed)) {
                        forceCreateConnection = false;
                        reCreateConnection();
                    }
                } else {
                    //Process read and write stanzas
//		    System.out.println("We are ready to work");
                    checkNewStanzas();
                    processSendingMessages();
                }
            }
            try {
                Thread.sleep(CHECK_STREAM_INTERVAL);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                forceTerminate = true;
            }
        }
        pingTimer.cancel();
        presenceTimer.cancel();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public static String statusIDtoString(int statusID) {
        switch (statusID) {
            case 1:
                return "away";
            case 2:
                return "xa";
            case 3:
                return "dnd";
        }
        return "";
    }

    String generateAuthResponse(String user, String pass, String realm, String digest_uri, String nonce, String cnonce) {
        String val1 = user + ":" + realm + ":" + pass;
        byte bb[] = new byte[17];
        bb = NetworkTools.md5It(val1);
        int sl = new String(":" + nonce + ":" + cnonce).length();
        byte cc[] = new String(":" + nonce + ":" + cnonce).getBytes();
        byte bc[] = new byte[99];
        for (int i = 0; i < 16; i++) {
            bc[i] = bb[i];
        }
        for (int i = 16; i < sl + 16; i++) {
            bc[i] = cc[i - 16];
        }
        String val2 = new String(MD5.toHex(NetworkTools.md5It(bc, sl + 16)));
        String val3 = "AUTHENTICATE:" + digest_uri;
        val3 = MD5.toHex(NetworkTools.md5It(val3));
        String val4 = val2 + ":" + nonce + ":00000001:" + cnonce + ":auth:" + val3;
        val4 = MD5.toHex(NetworkTools.md5It(val4));
        String enc = "charset=utf-8,username=\"" + user + "\",realm=\"" + realm + "\"," +
                "nonce=\"" + nonce + "\",cnonce=\"" + cnonce + "\"," +
                "nc=00000001,qop=auth,digest-uri=\"" + digest_uri + "\"," +
                "response=" + val4;
        String resp = MD5.toBase64(enc.getBytes());
        return resp;
    }

    private boolean processMD5Authentification() {
        if (!sendStanza(("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"DIGEST-MD5\"/>"))) {
            return false;
        }
        XmlNode response = readOneStanza();
        if (response.getName().equals("failure")) {
            return false;
        }
        String dec = new String(Base64.decode(response.getValue().getBytes()));
        int ind = dec.indexOf("nonce=\"") + 7;
        String nonce = dec.substring(ind, dec.indexOf("\"", ind + 1));
        String cnonce = "00deadbeef00";
        if (!sendStanza("<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
                generateAuthResponse(user, profile.getPassword(), domain, "xmpp/" + domain, nonce, cnonce) + "</response>")) {
            return false;
        }
        response = readOneStanza();
        if (response.getName().equals("failure")) {
            return false;
        }
        if (!sendStanza("<response xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>")) {
            return false;
        }
        response = readOneStanza();
        if (response.getName().equals("failure")) {
            return false;
        }
        return true;
    }

    private boolean processGOOGLEAuthentification(String token) {
        if (!sendStanza("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"X-GOOGLE-TOKEN\">" + token + "</auth>")) {
            return false;
        }
        XmlNode response = readOneStanza();
        if (response.getName().equals("failure")) {
            return false;
        }
        return true;
    }

    private boolean processPlainAuthentification() {
        /*String resp = "\0" + user + "\0" + profile.getPassword();
        if (!sendStanza("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">" +
        MD5.toBase64(resp.getBytes()) + "</auth>")) {
        return false;
        }
         * */
        String xml = "<iq xmlns=\"jabber:client\" id=\"5\" type=\"set\"><query xmlns=\"jabber:iq:auth\"><username>" + user + "</username><password>" + profile.getPassword() + "</password><resource>Home</resource></query></iq>";

        if (!sendStanza(xml)) {
            return false;
        }
        XmlNode response = readOneStanza();
        if (response.getName().equals("failure")) {
            return false;
        }
        return true;
    }

    public SharedStatus getSharedStatus() {
        return sharedStatus;
    }

    public boolean stanzaReceived(XmlNode stanza) {
        if (stanza.getName().equals("iq")) {
            if (stanza.getAttr("type").equals("error")) {
                String id = stanza.getAttr("id");
                int session = Integer.parseInt(id.substring(1));
                if (session == sessionInc) {
                    pingReceived = true;
                }
                return true;
            }
            if (stanza.getName().equals("iq") && stanza.child("query").getAttr("xmlns").equals("jabber:iq:version")) {
                sendMessage("<iq type=\"error\" to=\"" + stanza.getAttr("from") + "\"><query xmlns=\"jabber:iq:version\"/><error code=\"501\" type=\"cancel\"><feature-not-implemented xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/></error></iq>");
                return true;
            }

        }
        return false;
    }
}
