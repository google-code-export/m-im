/*
 * Copyright (c) 2009, Chunlin Yao
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <copyright holder> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <copyright holder> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.code.mim;

import org.xmlpull.v1.XmlPullParserException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SocketConnection;
import org.rost.mobile.mgtalk.model.Profile;

/**
 * Created by IntelliJ IDEA.
 * User: setup
 * Date: 2009/10/29
 * Time: 13:48:02
 * To change this template use File | Settings | File Templates.
 */
public class XMPP extends XmppAdapter{

    private InputStream is = null;
    private java.io.OutputStream os = null;
    private SocketConnection connection = null;
    final static boolean DEBUG = true;
    private String host, port, username, password, myjid, server;
    private boolean use_ssl;
    private String resource;
    private int priority;
    private Vector listeners = new Vector();
    private String googleToken = "";
    private XMPPInputStream reader;
    private XMPPOutputStream writer;
    private boolean connected = false;
    private int retryCount = 0;
    private static final boolean LOGGING = true;
    public static final String IQ = "iq";
    protected static final String TYPE = "type";
    public static final String SET = "set";
    public static final String ID = "id";
    public static final String BIND = "bind";
    public static final String XMLNS = "xmlns";
    public static final String URN_IETF_PARAMS_XML_NS_XMPP_BIND = "urn:ietf:params:xml:ns:xmpp-bind";
    public static final String RESOURCE = "resource";
    public static final String AUTH = "auth";
    public static final String MECHANISM = "mechanism";
    public static final String URN_IETF_PARAMS_XML_NS_XMPP_SASL = "urn:ietf:params:xml:ns:xmpp-sasl";
    public static final String VERSION = "version";
    public static final String V_10 = "1.0";
    public static final String JABBER_CLIENT = "jabber:client";
    public static final String HTTP_ETHERX_JABBER_ORG_STREAMS = "http://etherx.jabber.org/streams";
    public static final String STREAM = "stream";
    public static final String UTF_8 = "UTF-8";
    public static final String TO = "to";
    public static final String X_GOOGLE_TOKEN = "X-GOOGLE-TOKEN";
    public static final String SUCCESS = "success";
    public static final String SESSION = "session";
    public static final String COMPRESSION = "compression";
    public static final String PLAIN = "PLAIN";
    public static final String MECHANISMS = "mechanisms";
    public static final String STARTTLS = "starttls";
    public static final String STREAM_STREAM = "stream:stream";
    public static final String XMLNSSTREAM = "xmlns:stream";
    public static final String STREAM_FEATURES = "stream:features";
    public static final String FROM = "from";
    public static final String QUERY = "query";
    public static final String JABBER_IQ_ROSTER = "jabber:iq:roster";
    public static final String ITEM = "item";
    public static final String JID = "jid";
    protected static final String NAME = "name";
    protected static final String SUBSCRIPTION = "subscription";
    protected static final String OS = "os";
    protected static final String GOOGLE_SHARED_STATUS = "google:shared-status";
    protected static final String STATUS = "status";
    public static final String SHOW = "show";
    protected static final String STATUS_LIST = "status-list";
    public static final String AWAY = "away";
    public static final String DND = "dnd";
    public static final String DEFAULT = "default";
    private boolean boundJID;
    protected static final String GET = "get";
    protected static final String PRESENCE = "presence";
    public static final String URN_IETF_PARAMS_XML_NS_XMPP_SESSION = "urn:ietf:params:xml:ns:xmpp-session";
    public static final String MESSAGE = "message";
    protected static final String CHAT = "chat";
    protected static final String BODY = "body";
    public static final String PING = "ping";
    protected static final String URN_XMPP_PING = "urn:xmpp:ping";
    protected static final String PRIORITY = "priority";
    protected static final String GROUP = "group";
    protected static final String UNSUBSCRIBED = "unsubscribed";
    protected static final String SUBSCRIBE = "subscribe";
    protected static final String UNAVAILABLE = "unavailable";
    protected static final String NA = "na";
    private Profile profile;
    private Thread worker;
    private boolean running = false;

    public XMPP(final Profile profile) {
        this(profile.getFullJID(), profile.getPassword(), profile.getResource(), 10, profile.getHost(), profile.getPort(), profile.isSSL());
        this.profile = profile;
    }

    private XMPP(final String jid, final String password, final String resource, final int priority, final String server, final String port, final boolean use_ssl) {
        int i = jid.indexOf('@');
        this.host = jid.substring(i + 1);
        this.port = port;
        this.username = jid.substring(0, i);
        this.password = password;
        this.resource = resource;
        this.priority = priority;
        this.myjid = jid;
        if (server == null) {
            this.server = host;
        } else {
            this.server = server;
        }
        this.use_ssl = use_ssl;
        this.reader = new XMPPInputStream();
        this.reader.addListener(this);
    }

    public void connect() throws IOException, XmlPullParserException {
        _connect();
        startStream();
        login();
        startStream();
        if (reader.supportBind) {
            bind();
        }
        if (reader.supportSession) {
            int ret = writer.startSession();
        }

        writer.sendPresence(null, null, null, null, this.priority);

        if (this.profile.isGoogle()) {
            writer.sendGoogleSettings();
        }
        writer.getRoster();
        this.running = true;
    }

    public void mainloop() {

        this.worker = new Thread(new Runnable() {

            public void run() {
                try {
                    reader.parse();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (XMPP.this.running) {
                        try {
                            try{
                            XMPP.this.connection.close();
                            }catch(Exception exx){
                                
                            }
                            XMPP.this.connect();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (XmlPullParserException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        this.worker.start();
    }

    private void bind() throws IOException, XmlPullParserException {
        writer.writeBind(this.resource);
        reader.readIQ();
        if (!this.boundJID) {
            throw new RuntimeException("bind error.\n");
        }
    }

    private void _connect() {
        try {
            if (!use_ssl) {
                connection = (SocketConnection) Connector.open("socket://" + this.server + ":" + this.port, Connector.READ_WRITE);
                connection.setSocketOption(SocketConnection.KEEPALIVE, 1);
                is = connection.openInputStream();
                os = connection.openOutputStream();

            } else {
                connection = (SocketConnection) Connector.open("ssl://" + this.server + ":" + this.port, Connector.READ_WRITE);
                connection.setSocketOption(SocketConnection.KEEPALIVE, 1);
                //sc.setSocketOption(SocketConnection.DELAY, 1);
                //sc.setSocketOption(SocketConnection.LINGER, 0);
                is = connection.openInputStream();
                os = connection.openOutputStream();
            }
            this.reader.setInput(is);
            this.writer = new XMPPOutputStream(os, this.host);
            reader.addListener(writer);
            connected = true;
        } catch (final Exception e) {
            if (LOGGING) {
                Log.error("Connection Failed", e);
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    public void startStream() {
        try {
            this.writer.startStream();
            this.reader.startStream();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void login() {
        try {
            if (reader.supportGoogleToken) {
                writer.writeSASL("X-GOOGLE-TOKEN", generateTokenViaGoogle());
                reader.requireSuccess();
            } else {
                writer.writeSASL("PLAIN", generatePlanAuthData());
                reader.requireSuccess();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generatePlanAuthData() {
        byte[] auth_msg = (username + "@" + host + "\0" + username + "\0" + password).getBytes();
        return Base64.encode(auth_msg);
    }

    private String generateTokenViaGoogle() {
        if (this.googleToken.equals("")) {
            String first = "Email=" + myjid + "&Passwd=" + password + "&PersistentCookie=false&source=mgtalk";
            try {
                HttpsConnection c = (HttpsConnection) Connector.open("https://www.google.com:443/accounts/ClientAuth?" + first);
                //LOG: Connection go GOOGLE
                DataInputStream dis = c.openDataInputStream();
                String str = readLine(dis);
                String SID = "";
                String LSID = "";
                if (str.startsWith("SID=")) {
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
                    String token = Base64.encode(new String("\0" + myjid + "\0" + str).getBytes());
                    dis.close();
                    c.close();
                    googleToken = token;
                    return token;

                } else {
                    throw new Exception("Invalid response");
                }

            } catch (Throwable ex) {
                ex.printStackTrace();
            }

        } else {
            return googleToken;
        }

        return "";

    }

    String readLine(
            DataInputStream dis) {
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

    public synchronized void sendShareStatus(String to, String status, String show, Vector onlineList, Vector awayList, Vector busyList) throws IOException {
        writer.sendShareStatus(to, status, show, onlineList, awayList, busyList);
    }

    public synchronized void sendMessage(String to, String msg) throws IOException {
        writer.sendMessage(to, msg);
    }

    public synchronized void setStatus(String show, String status, int priority) throws IOException {
        writer.setStatus(show, status, priority);
    }

    public void addListener(XMPPListener listener) {
        this.reader.addListener(listener);
    }

    public void close() {
        this.running = false;
        if (this.is != null) {
            try {
                this.is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (this.os != null) {
            try {
                this.os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.worker = null;
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onBind(String resource) {
        this.boundJID=true;
    }

    public synchronized void sendPing(int id) throws IOException {
        writer.sendPing(id);
    }

    


}

