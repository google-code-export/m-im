/*
 * Copyright 2004-2006 Swen Kummer, Dustin Hass, Sven Jost, Grzegorz Grasza
 * modified by Yuan-Chu Tai
 * http://jxa.sourceforge.net/
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. Mobber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with mobber; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package net.sourceforge.jxa;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.pki.CertificateException;

import org.rost.mobile.guilib.core.Constants;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.model.Profile;

import com.google.code.mim.Log;

/**
 * J2ME XMPP API Class
 * 
 * @author Swen Kummer, Dustin Hass, Sven Jost, Grzegorz Grasza
 * @version 4.0
 * @since 1.0
 */
public class Jxa extends Thread {

    final static boolean DEBUG = true;
    private final String host,  port,  username,  password,  myjid,  server;
    private final boolean use_ssl;
    private String resource;
    private final int priority;
    private XmlReader reader;
    private XmlWriter writer;
    private InputStream is;
    private OutputStream os;
    private SocketConnection connection;
    private Vector listeners = new Vector();
    private String googleToken = "";

    private boolean connected = false;
    private int retryCount = 0;
    
    public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

    /**
     * If you create this object all variables will be saved and the
     * method {@link #run()} is started to log in on jabber server and
     * listen to parse incomming xml stanzas. Use
     * {@link #addListener(XmppListener xl)} to listen to events of this object.
     *
     * @param host the hostname/ip of the jabber server
     * @param port the port number of the jabber server
     * @param username the username of the jabber account
     * @param password the passwort of the jabber account
     * @param resource a unique identifier of the used resource, for e.g. "mobile"
     * @param priority the priority of the jabber session, defines on which
     * resource the messages arrive
     */
    /*	public Jxa(final String host, final String port, final String username, final String password, final String resource, final int priority) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.resource = resource;
    this.priority = priority;
    this.myjid = username + "@" + host;
    this.early_jabber = true;
    this.server = host;
    this.start();
    }*/
    // jid must in the form "username@host"
    // to login Google Talk, set port to 5223 (NOT 5222 in their offical guide)
    public Jxa(final String jid, final String password, final String resource, final int priority, final String server, final String port, final boolean use_ssl) {
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
    //this.start();
    }

    public void close() {
        close("");
    }

    public void close(String msg) {
    	if (Constants.LOGGING) {
    		Log.info("Jxa.close(" + msg + ") called");
    	}
        try {
            this.interrupt();
        } catch (Exception e) {
        }
        try {
            this.connected = false;
            this.connection.close();
        } catch (Exception e) {
        }
        try {
            this.writer.close();
        } catch (Exception e) {
        }
        try {
            this.reader.close();
        } catch (Exception e) {
        }

        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
            XmppListener xl = (XmppListener) e.nextElement();
            xl.onConnFailed(msg);
        }
    }

    public void fillShareStatusVector(Vector awayList) throws IOException {
        while (this.reader.next() == XmlReader.START_TAG) {
            awayList.addElement(parseText());
        }
    }

    /**
     * The <code>run</code> method is called when {@link Jxa} object is
     * created. It sets up the reader and writer, calls {@link #login()}
     * methode and listens on the reader to parse incomming xml stanzas.
     */
    public void run() {

    	connected = true;// when called, we should attempt a connection!
    	boolean hasBeenDisconnected = false;
    	boolean loginOK = true;
    	
    	Profile activeProfile = AppStore.getSelectedProfile();
    	
    	while (connected && loginOK) {
    		if (hasBeenDisconnected) {
    			if (!activeProfile.isAutoReconnect()) {
    				break; // break out of this loop if we're not supposed to auto-reconnect!
    			}
				hasBeenDisconnected = false; //reset this
                //reconnect when connection is lost.
                //TODO How many times should we retry.
				
				if (retryCount >= 2) {
					break;
				}
				
                try {
                    connect();
                } catch (Exception e) {
                    hasBeenDisconnected = true;
                    retryCount++;
                	if (Constants.LOGGING) {
                		Log.error("Error connecting", e);
                	}
                    continue;
                }
    		} else {
                //The first connect.
                try {
                    connect();
                } catch (Exception e) {
                    retryCount++;
                	if (Constants.LOGGING) {
                		Log.error("Error connecting", e);
                	}
                    break;
                }
            }
        	
        	try {
        		if (Constants.LOGGING) {
        			Log.debug("logging in..");
        		}
            	loginOK = login();
        	} catch (IOException e) {
        		if (Constants.LOGGING) {
        			Log.error("Login Error", e);
        		}
        	}
        	if (Constants.LOGGING) {
        		Log.debug("login " + (loginOK ? "ok" : "failed!"));
        	}

        	// Not sure if this is the correct logic, but if the login fails, we probably shouldn't continue?!
        	if (!loginOK) {
        		break;
        	}
        	
            try {
        		if (activeProfile.isKeepalive() && activeProfile.isXmppPing()) {
        			AppStore.startPinger();
        		}
                if (Constants.LOGGING) {
                	Log.debug("parsing...");
                }
                this.parse();
                hasBeenDisconnected = true;
                if (Constants.LOGGING) {
                	Log.debug("jxa.run complete..");
                }
                /*
                 * MarkM - If this gets to this stage, it's probably due to a disconnect!!??
                 * What I've found is if you kick/close the connection on the server, it drops out of the parse
                 * method and returns control to this point. If the network itself goes down, something different 
                 * happens, perhaps throwing the Exception caught below?? Not sure who added that German comment?
                 */
            } catch (final Exception e) {
                // hier entsteht der connection failed bug (Network Down)
            	hasBeenDisconnected = true;
            	if (Constants.LOGGING) {
            		Log.error("ConnFailed", e);
            	}
                this.connectionFailed(e.getMessage());
                e.printStackTrace();
            } finally {
            	if (activeProfile.isXmppPing()) {
                    AppStore.stopPinger();
            	}
            }    		
    	}
    }

    private void connect() throws Exception {
        try {
            if (!use_ssl) {
                connection = (SocketConnection) Connector.open("socket://" + this.server + ":" + this.port, Connector.READ_WRITE);
                connection.setSocketOption(SocketConnection.KEEPALIVE, 1);
                is = connection.openInputStream();
                os = connection.openOutputStream();

                this.reader = new XmlReader(is);
                this.writer = new XmlWriter(os);
            } else {
                connection = (SocketConnection) Connector.open("ssl://" + this.server + ":" + this.port, Connector.READ_WRITE);
                connection.setSocketOption(SocketConnection.KEEPALIVE, 1);
                //sc.setSocketOption(SocketConnection.DELAY, 1);
                //sc.setSocketOption(SocketConnection.LINGER, 0);
                is = connection.openInputStream();
                os = connection.openOutputStream();
                this.reader = new XmlReader(is);
                this.writer = new XmlWriter(os);
            }
        	connected = true;
        } catch (final CertificateException ex) {
        	if (Constants.LOGGING) {
        		Log.error("CertificateException", ex);
        	}
            this.connectionFailed(ex.getReason() + ex.getMessage());
            throw ex;
        } catch (final Exception e) {
        	if (Constants.LOGGING) {
        		Log.error("Connection Failed", e);
        	}
            this.connectionFailed(e.getMessage());
            throw e;
        }
    }

    /**
     * Add a {@link XmppListener} to listen for events.
     *
     * @param xl a XmppListener object
     */
    public void addListener(final XmppListener xl) {
        if (!listeners.contains(xl)) {
            listeners.addElement(xl);
        }
    }

    /**
     * Remove a {@link XmppListener} from this class.
     *
     * @param xl a XmppListener object
     */
    public void removeListener(final XmppListener xl) {
        listeners.removeElement(xl);
    }

    /**
     * Opens the connection with a stream-tag, queries authentication type and
     * sends authentication data, which is username, password and resource.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    public synchronized boolean login() throws IOException {
        //if (!use_ssl) {
        /*
        // start stream
        this.writer.startTag("stream:stream");
        this.writer.attribute("to", this.host);
        this.writer.attribute("xmlns", "jabber:client");
        this.writer.attribute("xmlns:stream", "http://etherx.jabber.org/streams");
        this.writer.flush();
        // log in
        this.writer.startTag("iq");
        this.writer.attribute("type", "set");
        this.writer.attribute("id", "auth");
        this.writer.startTag("query");
        this.writer.attribute("xmlns", "jabber:iq:auth");

        this.writer.startTag("username");
        this.writer.text(this.username);
        this.writer.endTag();
        this.writer.startTag("password");
        this.writer.text(this.password);
        this.writer.endTag();
        this.writer.startTag("resource");
        this.writer.text(this.resource);
        this.writer.endTag();

        this.writer.endTag(); // query
        this.writer.endTag(); // iq
        this.writer.flush();
         * */
        //} else {
        String msg = "<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' to='" + this.host + "' version='1.0'>";
        os.write(msg.getBytes());
        os.flush();
        Vector mechanisms = new Vector();
        do {
            reader.next();
            if (reader.getType() == XmlReader.START_TAG &&
                    "mechanisms".equals(reader.getName()) &&
                    "urn:ietf:params:xml:ns:xmpp-sasl".equals(reader.getAttribute("xmlns"))) {
                fillMechanisms(mechanisms);
            }
        } while (!(reader.getType() == XmlReader.END_TAG && reader.getName().equals("stream:features")));

        //java.lang.System.out.println("SASL phase1");
            /*for (Enumeration enu = listeners.elements(); enu.hasMoreElements();) {
        XmppListener xl = (XmppListener) enu.nextElement();
        xl.onDebug("SASL phase 1");
        }*/

        //int ghost = is.available();
        //is.skip(ghost);
        boolean loginSuccess = false;

        if (mechanisms.contains("X-GOOGLE-TOKEN")) {
            msg = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='X-GOOGLE-TOKEN'>";
            msg = msg + generateTokenViaGoogle() + "</auth>";
            os.write(msg.getBytes());
            os.flush();
            reader.next();
            if (reader.getName().equals("success")) {
                loginSuccess = true;
                while (true) {
                    if ((reader.getType() == XmlReader.END_TAG) && reader.getName().equals("success")) {
                        break;
                    }
                    reader.next();
                }
            }
        }
        if (mechanisms.contains("PLAIN") && loginSuccess == false) {
            msg = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>";
            byte[] auth_msg = (username + "@" + host + "\0" + username + "\0" + password).getBytes();
            msg = msg + Base64.encode(auth_msg) + "</auth>";
            os.write(msg.getBytes());
            os.flush();
            reader.next();
            if (reader.getName().equals("success")) {
                loginSuccess = true;
                while (true) {
                    if ((reader.getType() == XmlReader.END_TAG) && reader.getName().equals("success")) {
                        break;
                    }
                    reader.next();
                }
            }
        }

        if (loginSuccess == false) {
            for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                XmppListener xl = (XmppListener) e.nextElement();
                xl.onAuthFailed(reader.getName() + ", failed authentication");
            }
            return false;
        }

        //java.lang.System.out.println("SASL phase2");
            /*for (Enumeration enu = listeners.elements(); enu.hasMoreElements();) {
        XmppListener xl = (XmppListener) enu.nextElement();
        xl.onDebug("SASL phase 2");
        }*/
        msg = "<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' to='" + this.host + "' version='1.0'>";
        os.write(msg.getBytes());
        os.flush();
        reader.next();
        while (true) {
            if ((reader.getType() == XmlReader.END_TAG) && reader.getName().equals("stream:features")) {
                break;
            }
            reader.next();
        }
        //java.lang.System.out.println("SASL done");
            /*for (Enumeration enu = listeners.elements(); enu.hasMoreElements();) {
        XmppListener xl = (XmppListener) enu.nextElement();
        xl.onDebug("SASL done");
        }	*/
        if (resource == null) {
            msg = "<iq type='set' id='res_binding'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/></iq>";
        } else {
            msg = "<iq type='set' id='res_binding'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><resource>" + resource + "</resource></bind></iq>";
        }
        os.write(msg.getBytes());
        os.flush();

        return loginSuccess;
    }

    private void fillMechanisms(Vector mechanisms) {
        try {
            while (this.reader.next() == XmlReader.START_TAG) {
                mechanisms.addElement(parseText());
            }
        } catch (IOException ex) {
            this.connectionFailed(ex.getMessage());
        }
    }

    /**
     * Closes the stream-tag and the {@link XmlWriter}.
     */
    public synchronized void logoff() {
        try {
            this.writer.endTag();
            this.writer.flush();
            this.writer.close();
        } catch (final Exception e) {
            this.connectionFailed();
        }
    }

    /**
     * Sends a message text to a known jid.
     *
     * @param to the JID of the recipient
     * @param msg the message itself
     */
    public synchronized void sendMessage(final String to, final String msg) {
        try {
            this.writer.startTag("message");
            this.writer.attribute("type", "chat");
            this.writer.attribute("to", to);
            this.writer.startTag("body");
            this.writer.text(msg);
            this.writer.endTag();
            this.writer.endTag();
            this.writer.flush();
        } catch (final Exception e) {
            // e.printStackTrace();
            this.connectionFailed(e.getMessage());
        }
    }

    public synchronized void sendPing(final int id) {
        try {
            this.writer.startTag("iq");
            this.writer.attribute("type", "get");
            this.writer.attribute("id", "" + id);
            this.writer.startTag("ping");
            this.writer.attribute("xmlns", "urn:xmpp:ping");
            this.writer.endTag();
            this.writer.endTag();
            this.writer.flush();
            if (Constants.LOGGING) {
            	Log.debug("Sent ping with id " + id);
            }
        } catch (final Exception e) {
            this.connectionFailed(e.getMessage());
        }    	
    }
    
    public synchronized void sendGoogleSettings() {
        try {
            os.write(("<iq type=\"get\" id=\"6\"><query xmlns=\"google:relay\"/></iq>").getBytes());
            os.flush();
            os.write(("<iq type=\"set\" from=\"" + myjid + "/" + resource + "\" to=\"" + myjid + "\" id=\"user-setting\">" +
                    "<usersetting xmlns=\"google:setting\"><autoacceptsuggestions value=\"false\"/>" +
                    "<mailnotifications value=\"true\"/></usersetting></iq>").getBytes());
            os.flush();
            os.write(("<presence><show></show><status></status></presence>" +
                    //"<iq type=\"get\"  from=\""+ myjid + "/" + resource +"\" to=\"" + myjid + "\" id=\"mm-get\">" +
                    //"<query xmlns=\"google:mail:notify\" /></iq>" +
                    "<iq type=\"get\" to=\"" + myjid + "\" id=\"ss-get\">" +
                    "<query xmlns=\"google:shared-status\" version=\"2\" /></iq>").getBytes());
            os.flush();

        } catch (IOException ex) {
            this.connectionFailed(ex.getMessage());
        }
    }

    public synchronized void startSession() {
        try {
            os.write(("<iq to=\"" + host + "\" type=\"set\" id=\"sess_1\">" + "<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/></iq>").getBytes());
            os.flush();
        } catch (IOException ex) {
            this.connectionFailed(ex.getMessage());
        }
    }

    public synchronized void sendShareStatus(String to, String status, String show, Vector onlineList, Vector awayList, Vector busyList) {
        try {
            /*sb.append("<iq type=\"set\" to=\"" + AppStore.getSelectedProfile().getUserName() + "\" id=\"ss-1\">" +
            "<query xmlns=\"google:shared-status\">" +
            "<status>" + NetworkTools.toXML(status) + "</status><show>" +
            NetworkDispatcher.statusIDtoString(AppStore.getSelectedProfile().getStatusID()) +
            "</show>");
            sb.append("<status-list show=\"default\">" + statusListToString(onlineList) + "</status-list>");
            sb.append("<status-list show=\"away\">" + statusListToString(awayList) + "</status-list>");
            sb.append("<status-list show=\"dnd\">" + statusListToString(busyList) + "</status-list>");
            sb.append("</query></iq>");
             * */
            writer.startTag("iq");
            writer.attribute("type", "set");
            writer.attribute("to", to);
            writer.attribute("id", "ss-1");
            {
                writer.startTag("query");
                writer.attribute("xmlns", "google:shared-status");
                {
                    writer.startTag("status");
                    writer.text(status);
                    writer.endTag();
                    writer.startTag("show");
                    writer.text(show);
                    writer.endTag();
                }
                {
                    writer.startTag("status-list");
                    writer.attribute("show", "default");
                    statusListToString(onlineList);
                    writer.endTag();
                }
                {
                    writer.startTag("status-list");
                    writer.attribute("show", "away");
                    statusListToString(awayList);
                    writer.endTag();
                }
                {
                    writer.startTag("status-list");
                    writer.attribute("show", "dnd");
                    statusListToString(busyList);
                    writer.endTag();
                }
                writer.endTag();
            }
            writer.endTag();
        } catch (IOException ex) {
            this.connectionFailed();
        }
    }

    public void statusListToString(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            try {
                writer.startTag("status");
                writer.text((String) v.elementAt(i));
                writer.endTag();
            } catch (IOException ex) {
                this.connectionFailed();
            }
        }
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

    /**
     * Sends a presence stanza to a jid. This method can do various task but
     * it's private, please use setStatus to set your status or explicit
     * subscription methods subscribe, unsubscribe, subscribed and
     * unsubscribed to change subscriptions.
     */
    private synchronized void sendPresence(final String to, final String type, final String show, final String status, final int priority) {
        try {
            this.writer.startTag("presence");
            if (type != null) {
                this.writer.attribute("type", type);
            }
            if (to != null) {
                this.writer.attribute("to", to);
            }
            if (show != null) {
                this.writer.startTag("show");
                this.writer.text(show);
                this.writer.endTag();
            }
            if (status != null) {
                this.writer.startTag("status");
                this.writer.text(status);
                this.writer.endTag();
            }
            if (priority != 0) {
                this.writer.startTag("priority");
                this.writer.text(Integer.toString(priority));
                this.writer.endTag();
            }
            this.writer.endTag(); // presence
            this.writer.flush();
        } catch (final Exception e) {
            // e.printStackTrace();
            this.connectionFailed();
        }
    }

    /**
     * Sets your Jabber Status.
     *
     * @param show is one of the following: <code>null</code>, chat, away,
     *        dnd, xa, invisible
     * @param status an extended text describing the actual status
     * @param priority the priority number (5 should be default)
     */
    public synchronized void setStatus(String show, String status, final int priority) {
        if (show.equals("")) {
            show = null;
        }
        if (status.equals("")) {
            status = null;
        }
        if (show != null && show.equals("invisible")) {
            this.sendPresence(null, "invisible", null, null, priority);
        } else {
            this.sendPresence(null, null, show, status, priority);
        }
    }

    /**
     * Requesting a subscription.
     *
     * @param to the jid you want to subscribe
     */
    public synchronized void subscribe(final String to) {
        this.sendPresence(to, "subscribe", null, null, 0);
    }

    /**
     * Remove a subscription.
     *
     * @param to the jid you want to remove your subscription
     */
    public synchronized void unsubscribe(final String to) {
        this.sendPresence(to, "unsubscribe", null, null, 0);
    }

    /**
     * Approve a subscription request.
     *
     * @param to the jid that sent you a subscription request
     */
    public synchronized void subscribed(final String to) {
        this.sendPresence(to, "subscribed", null, null, 0);
    }

    /**
     * Refuse/Reject a subscription request.
     *
     * @param to the jid that sent you a subscription request
     */
    public synchronized void unsubscribed(final String to) {
        this.sendPresence(to, "unsubscribed", null, null, 0);
    }

    /**
     * Save a contact to roster. This means, a message is send to jabber
     * server (which hosts your roster) to update the roster.
     *
     * @param jid the jid of the contact
     * @param name the nickname of the contact
     * @param group the group of the contact
     * @param subscription the subscription of the contact
     */
    public synchronized void saveContact(final String jid, final String name, final Enumeration group, final String subscription) {
        try {
            this.writer.startTag("iq");
            this.writer.attribute("type", "set");
            this.writer.startTag("query");
            this.writer.attribute("xmlns", "jabber:iq:roster");
            this.writer.startTag("item");
            this.writer.attribute("jid", jid);
            if (name != null) {
                this.writer.attribute("name", name);
            }
            if (subscription != null) {
                this.writer.attribute("subscription", subscription);
            }
            if (group != null) {
                while (group.hasMoreElements()) {
                    this.writer.startTag("group");
                    this.writer.text((String) group.nextElement());
                    this.writer.endTag(); // group
                }
            }
            this.writer.endTag(); // item
            this.writer.endTag(); // query
            this.writer.endTag(); // iq
            this.writer.flush();
        } catch (final Exception e) {
            // e.printStackTrace();
            this.connectionFailed();
        }
    }

    /**
     * Sends a roster query.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    public synchronized void getRoster() throws IOException {
        this.writer.startTag("iq");
        this.writer.attribute("id", "roster");
        this.writer.attribute("type", "get");
        this.writer.startTag("query");
        this.writer.attribute("xmlns", "jabber:iq:roster");
        this.writer.endTag(); // query
        this.writer.endTag(); // iq
        this.writer.flush();
    }

    /**
     * The main parse methode is parsing all types of XML stanzas
     * <code>message</code>, <code>presence</code> and <code>iq</code>.
     * Although ignores any other type of xml.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    private void parse() throws IOException {
        if (DEBUG) {
            //java.lang.System.out.println("*debug* parsing");
        }
        //if (!use_ssl) {
        //    this.reader.next(); // start tag
        //}
        while (this.reader.next() == XmlReader.START_TAG) {
            final String tmp = this.reader.getName();
            if (tmp.equals("message")) {
                this.parseMessage();
            } else if (tmp.equals("presence")) {
                this.parsePresence();
            } else if (tmp.equals("iq")) {
                this.parseIq();
            } else {
                this.parseIgnore();
            }
        }
        //java.lang.System.out.println("leave parse() " + reader.getName());
        this.reader.close();
    }

    /**
     * This method parses all info/query stanzas, including authentication
     * mechanism and roster. It also answers version queries.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    private void parseIq() throws IOException {
        if (DEBUG) {
            //java.lang.System.out.println("*debug* paeseIq");
        }
        String type = this.reader.getAttribute("type");
        final String id = this.reader.getAttribute("id");
        final String from = this.reader.getAttribute("from");
        if (type.equals("error")) {
            while (this.reader.next() == XmlReader.START_TAG) {
                // String name = reader.getName();
                if (this.reader.getName().equals("error")) {
                    final String code = this.reader.getAttribute("code");
                    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                        XmppListener xl = (XmppListener) e.nextElement();
                        xl.onAuthFailed(code + ": " + this.parseText());
                    }
                } else {
                    this.parseText();
                }
            }
        } else if (type.equals("result") && (id != null) && id.equals("res_binding")) {
            // authorized
            String rsp_jid = "";
            while (true) {
                reader.next();
                String tagname = reader.getName();
                if (tagname != null) {
                    if ((reader.getType() == XmlReader.START_TAG) && tagname.equals("jid")) {
                        reader.next();
                        rsp_jid = reader.getText();
                        int i = rsp_jid.indexOf('/');
                        this.resource = rsp_jid.substring(i + 1);
                    //java.lang.System.out.println(this.resource);
                    } else if (tagname.equals("iq")) {
                        break;
                    }
                }
            }
            for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                XmppListener xl = (XmppListener) e.nextElement();
                xl.onAuth(rsp_jid);
            }
            this.sendPresence(null, null, null, null, this.priority);
        } else {
            //java.lang.System.out.println("contacts list");
            while (this.reader.next() == XmlReader.START_TAG) {
                if (this.reader.getName().equals("query")) {
                    if (this.reader.getAttribute("xmlns").equals("jabber:iq:roster")) {
                        while (this.reader.next() == XmlReader.START_TAG) {
                            if (this.reader.getName().equals("item")) {
                                type = this.reader.getAttribute("type");
                                String jid = reader.getAttribute("jid");
                                String name = reader.getAttribute("name");
                                if (Constants.LOGGING) {
                                	Log.info(jid + name);
                                }
                                String subscription = reader.getAttribute("subscription");
                                //newjid = (jid.indexOf('/') == -1) ? jid : jid.substring(0, jid.indexOf('/'));
                                boolean check = true;
                                //yctai
                /*for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                                XmppListener xl = (XmppListener) e.nextElement();
                                xl.onContactRemoveEvent(newjid);
                                }*/
                                while (this.reader.next() == XmlReader.START_TAG) {
                                    if (this.reader.getName().equals("group")) {
                                        final String groupName = this.parseText();
                                        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                                            XmppListener xl = (XmppListener) e.nextElement();
                                            xl.onContactEvent(jid, name, groupName, subscription);
                                        }
                                        check = false;
                                    } else {
                                        this.parseIgnore();
                                    }
                                }
                                //if (check && !subscription.equals("remove"))
                                if (check) {
                                    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                                        XmppListener xl = (XmppListener) e.nextElement();
                                        xl.onContactEvent(jid, name == null ? "" : name, "", subscription);
                                    }
                                }
                            } else {
                                this.parseIgnore();
                            }
                            if (Constants.LOGGING) {
                            	Log.info(this.reader.getName() + reader.getType());
                            }
                        }
                        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                            XmppListener xl = (XmppListener) e.nextElement();
                            xl.onContactOverEvent();
                        }
                    } else if (this.reader.getAttribute("xmlns").equals("jabber:iq:version")) {
                        while (this.reader.next() == XmlReader.START_TAG) {
                            this.parseIgnore();
                        }
                        // reader.next();
                        // send version
                        this.writer.startTag("iq");
                        this.writer.attribute("type", "result");
                        this.writer.attribute("id", id);
                        this.writer.attribute("to", from);
                        this.writer.startTag("query");
                        this.writer.attribute("xmlns", "jabber:iq:version");

                        this.writer.startTag("name");
                        this.writer.text("jxa");
                        this.writer.endTag();
                        this.writer.startTag("version");
                        writer.text("1.0");
                        this.writer.endTag();
                        this.writer.startTag("os");
                        this.writer.text("J2ME");
                        this.writer.endTag();

                        this.writer.endTag(); // query
                        this.writer.endTag(); // iq
                    } else if (this.reader.getAttribute("xmlns").equals("google:shared-status")) {
                        String status = null;
                        int show = 0;
                        Vector awayList = new Vector();
                        Vector busyList = new Vector();
                        Vector onlineList = new Vector();
                        while (this.reader.next() == XmlReader.START_TAG) {
                            if (this.reader.getName().equals("status")) {
                                status = parseText();
                            } else if (this.reader.getName().equals("show")) {
                                show = statusStringToNumber(parseText());
                            } else if (this.reader.getName().equals("status-list")) {
                                if (this.reader.getAttribute("show").equals("away")) {
                                    fillShareStatusVector(awayList);
                                } else if (this.reader.getAttribute("show").equals("dnd")) {
                                    fillShareStatusVector(busyList);
                                } else if (this.reader.getAttribute("show").equals("default")) {
                                    fillShareStatusVector(onlineList);
                                }
                            } else {
                                this.parseIgnore();
                            }
                        }

                        //System.out.println(this.reader.getName() + this.reader.getType());
                        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                            XmppListener xl = (XmppListener) e.nextElement();
                            xl.onSharedStatusEvent(status, show, awayList, busyList, onlineList);
                        }

                    } else {
                        this.parseIgnore();
                    }
                } else {
                    this.parseIgnore();
                }
            }
        }
    }

    public static int statusStringToNumber(String str) {
        if (str.equals("away")) {
            return 1;
        }
        if (str.equals("na")) {
            return 2;
        }
        if (str.equals("xa")) {
            return 2;
        }
        if (str.equals("busy")) {
            return 3;
        }
        if (str.equals("dnd")) {
            return 3;
        }
        return 0;
    }

    /**
     * This method parses all presence stanzas, including subscription requests.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    private void parsePresence() throws IOException {
        final String from = this.reader.getAttribute("from"),  type = this.reader.getAttribute("type");
        String status = "", show = "";
        // int priority=-1;
        while (this.reader.next() == XmlReader.START_TAG) {
            final String tmp = this.reader.getName();
            if (tmp.equals("status")) {
                status = this.parseText();
            } else if (tmp.equals("show")) {
                show = this.parseText();
            // else if(tmp.equals("priority"))
            // priority = Integer.parseInt(parseText());
            } else {
                this.parseIgnore();
            }
        }

        if (Constants.LOGGING) {
        	Log.debug("*debug* from,type,status,show:" + from + "," + type + "," + status + "," + show);
        }

        //if ((type != null) && (type.equals("unavailable") || type.equals("unsubscribed") || type.equals("error"))) {
        if (type == null) {
            for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                XmppListener xl = (XmppListener) e.nextElement();
                xl.onStatusEvent(from, show, status);
            }
        } else {
            if (type.equals("unsubscribed") || type.equals("error")) {
                for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                    XmppListener xl = (XmppListener) e.nextElement();
                    xl.onUnsubscribeEvent(from);
                }
            } else if (type.equals("subscribe")) {
                for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                    XmppListener xl = (XmppListener) e.nextElement();
                    xl.onSubscribeEvent(from);
                }
            } else if (type.equals("unavailable")) {
                //final String jid = (from.indexOf('/') == -1) ? from : from.substring(0, from.indexOf('/'));
                for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                    XmppListener xl = (XmppListener) e.nextElement();
                    //xl.onStatusEvent(jid, show, status);
                    xl.onStatusEvent(from, "na", status);
                }
            }
        }
    }

    /**
     * This method parses all incoming messages.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    private void parseMessage() throws IOException {
        final String from = this.reader.getAttribute("from");
        //final String type = this.reader.getAttribute("type");
        String body = null; //, subject = null;
        while (this.reader.next() == XmlReader.START_TAG) {
            final String tmp = this.reader.getName();
            if (tmp.equals("body")) {
                body = this.parseText();
//            } else if (tmp.equals("subject")) {
//                subject = this.parseText(); // this is checked for, but never used!
            } else {
                this.parseIgnore();
            }
        }
        // (from, subject, body);
        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
            XmppListener xl = (XmppListener) e.nextElement();
            xl.onMessageEvent((from.indexOf('/') == -1) ? from : from.substring(0, from.indexOf('/')), body);
        }
    }

    /**
     * This method parses all text inside of xml start and end tags.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    private String parseText() throws IOException {
        final String endTagName = this.reader.getName();
        final StringBuffer str = new StringBuffer("");
        int t = this.reader.next(); // omit start tag
        while (!endTagName.equals(this.reader.getName())) {
            if (t == XmlReader.TEXT) {
                str.append(this.reader.getText());
            }
            t = this.reader.next();
        }
        return str.toString();
    }

    /**
     * This method doesn't parse tags it only let the reader go through unknown
     * tags.
     *
     * @throws java.io.IOException is thrown if {@link XmlReader} or {@link XmlWriter}
     *	throw an IOException.
     */
    private void parseIgnore() throws IOException {
        int x;
        while ((x = this.reader.next()) != XmlReader.END_TAG) {
            if (x == XmlReader.START_TAG) {
                this.parseIgnore();
            }
        }
    }

    /**
     * This method is used to be called on a parser or a connection error.
     * It tries to close the XML-Reader and XML-Writer one last time.
     *
     */
    private void connectionFailed() {
        connectionFailed("");
    }

    private void connectionFailed(final String msg) {
        close(msg);


    }

    String generateTokenViaGoogle() {
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
};
