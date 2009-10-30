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

import org.kxml2.io.KXmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: setup
 * Date: 2009/10/29
 * Time: 13:45:44
 * To change this template use File | Settings | File Templates.
 */
public class XMPPOutputStream {
    private String domain;
    private int iq_id = 0;
    private String jid;

    public XMPPOutputStream(OutputStream os, String domain) throws IOException {
        this.os = os;
        this.domain = domain;
        this.writer = new KXmlSerializer();
        this.writer.setOutput(this.os, XMPP.UTF_8);
    }

    private OutputStream os;

    private KXmlSerializer writer;


    public void startStream() throws IOException {
        writer.startDocument(XMPP.UTF_8, null);
        writer.setPrefix(XMPP.STREAM, XMPP.HTTP_ETHERX_JABBER_ORG_STREAMS);
        writer.startTag(XMPP.HTTP_ETHERX_JABBER_ORG_STREAMS, XMPP.STREAM);
        writer.attribute(null, XMPP.XMLNS, XMPP.JABBER_CLIENT);
        writer.attribute(null, XMPP.TO, domain);
        writer.attribute(null, XMPP.VERSION, XMPP.V_10);
        writer.flush();
    }

    public void writeSASL(String mechanism, String token) throws IOException {
        writer.startTag(null, XMPP.AUTH);
        writer.attribute(null, XMPP.XMLNS, XMPP.URN_IETF_PARAMS_XML_NS_XMPP_SASL);
        if (mechanism != null) {
            writer.attribute(null, XMPP.MECHANISM, mechanism);
        }
        if (token != null) {
            writer.text(token);
        }
        writer.endTag(null, XMPP.AUTH);
        writer.flush();
    }

    public int writeBind(String resource) throws IOException {
        //"<iq type='set' id='res_binding'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><resource>" + resource + "</resource></bind></iq>";
        iq_id++;
        writer.startTag(null, XMPP.IQ);
        writer.attribute(null, XMPP.TYPE, XMPP.SET);
        writer.attribute(null, XMPP.ID, getIqId());

        writer.startTag(null, XMPP.BIND);
        writer.attribute(null, XMPP.XMLNS, XMPP.URN_IETF_PARAMS_XML_NS_XMPP_BIND);
        if (resource != null) {
            writer.startTag(null, XMPP.RESOURCE);
            writer.text(resource);
            writer.endTag(null, XMPP.RESOURCE);
        }
        writer.endTag(null, XMPP.BIND);
        writer.endTag(null, XMPP.IQ);
        writer.flush();
        return iq_id;
    }

    private String getIqId() {
        return String.valueOf(iq_id++);
    }

    public int writeVersion() throws IOException {
        // send version
        this.writer.startTag(null, "iq");
        this.writer.attribute(null, "type", "result");
        this.writer.attribute(null, "id", getIqId());
        this.writer.attribute(null, "to", this.jid);   //TODO recive bind message.
        this.writer.startTag(null, "query");
        this.writer.attribute(null, "xmlns", "jabber:iq:version");

        this.writer.startTag(null, XMPP.NAME);
        this.writer.text("m-im");
        this.writer.endTag(null, XMPP.NAME);
        this.writer.startTag(null, XMPP.VERSION);
        writer.text(XMPP.V_10);
        this.writer.endTag(null, XMPP.VERSION);
        this.writer.startTag(null, XMPP.OS);
        this.writer.text("J2ME");
        this.writer.endTag(null, XMPP.OS);

        this.writer.endTag(null, XMPP.QUERY); // query
        this.writer.endTag(null, XMPP.IQ); // iq
        writer.flush();
        return iq_id;
    }

    public synchronized void sendGoogleSettings() throws IOException {
        writer.startTag(null, XMPP.IQ);
        writer.attribute(null, XMPP.TYPE, XMPP.GET);
        writer.attribute(null, XMPP.ID, getIqId());
        writer.startTag(null, XMPP.QUERY);
        writer.attribute(null, XMPP.XMLNS, "google:relay");
        writer.endTag(null, XMPP.QUERY);
        writer.endTag(null, XMPP.IQ);
        writer.flush();

        String shortJid = jid.substring(0, jid.indexOf('/'));
        writer.startTag(null, XMPP.IQ);
        writer.attribute(null, XMPP.TYPE, XMPP.SET);
        writer.attribute(null, XMPP.ID, getIqId());
        writer.attribute(null, XMPP.TO, shortJid);
        writer.attribute(null, XMPP.FROM, jid);
        {
            writer.startTag(null, "usersetting");
            writer.attribute(null, XMPP.XMLNS, "google:setting");
            {
                writer.startTag(null, "autoacceptsuggestions");
                writer.attribute(null, "value", "false");
                writer.endTag(null, "autoacceptsuggestions");
            }
            {
                writer.startTag(null, "mailnotifications");
                writer.attribute(null, "value", "true");
                writer.endTag(null, "mailnotifications");
            }
            writer.endTag(null, "usersetting");
        }
        writer.endTag(null, XMPP.IQ);
        writer.flush();
        {
            //TODO ???
            writer.startTag(null, XMPP.PRESENCE);
            writer.startTag(null, XMPP.SHOW);
            writer.startTag(null, XMPP.STATUS);
            writer.endTag(null, XMPP.STATUS);
            writer.endTag(null, XMPP.SHOW);
            writer.endTag(null, XMPP.PRESENCE);
            writer.flush();
        }
        writer.startTag(null, XMPP.IQ);
        writer.attribute(null, XMPP.TYPE, XMPP.GET);
        writer.attribute(null, XMPP.ID, getIqId());
        writer.attribute(null, XMPP.TO, shortJid);
        {
            writer.startTag(null, XMPP.QUERY);
            writer.attribute(null, XMPP.XMLNS, XMPP.GOOGLE_SHARED_STATUS);
            writer.attribute(null, XMPP.VERSION, "2");

            writer.endTag(null, XMPP.QUERY);
        }
        writer.endTag(null, XMPP.IQ);
        writer.flush();


    }

    public synchronized int startSession() throws IOException {
        writer.startTag(null, XMPP.IQ);
        writer.attribute(null, XMPP.TO, this.domain);
        writer.attribute(null, XMPP.TYPE, XMPP.SET);
        writer.attribute(null, XMPP.ID, getIqId());
        {
            writer.startTag(null, XMPP.SESSION);
            writer.attribute(null, XMPP.XMLNS, XMPP.URN_IETF_PARAMS_XML_NS_XMPP_SESSION);
            writer.endTag(null, XMPP.SESSION);
        }
        writer.endTag(null, XMPP.IQ);
        writer.flush();
        return iq_id;
    }

    public synchronized void sendShareStatus(String to, String status, String show, Vector onlineList, Vector awayList, Vector busyList) throws IOException {
        writer.startTag(null, XMPP.IQ);
        writer.attribute(null, XMPP.TYPE, XMPP.SET);
        writer.attribute(null, XMPP.TO, to);
        writer.attribute(null, XMPP.ID, getIqId());
        {
            writer.startTag(null, XMPP.QUERY);
            writer.attribute(null, XMPP.XMLNS, XMPP.GOOGLE_SHARED_STATUS);
            {
                writer.startTag(null, XMPP.STATUS);
                writer.text(status);
                writer.endTag(null, XMPP.STATUS);
                writer.startTag(null, XMPP.SHOW);
                writer.text(show);
                writer.endTag(null, XMPP.SHOW);
            }
            {
                writer.startTag(null, XMPP.STATUS_LIST);
                writer.attribute(null, XMPP.SHOW, XMPP.DEFAULT);
                statusListToString(onlineList);
                writer.endTag(null, XMPP.STATUS_LIST);
            }
            {
                writer.startTag(null, XMPP.STATUS_LIST);
                writer.attribute(null, XMPP.SHOW, XMPP.AWAY);
                statusListToString(awayList);
                writer.endTag(null, XMPP.STATUS_LIST);
            }
            {
                writer.startTag(null, XMPP.STATUS_LIST);
                writer.attribute(null, XMPP.SHOW, XMPP.DND);
                statusListToString(busyList);
                writer.endTag(null, XMPP.STATUS_LIST);
            }
            writer.endTag(null, XMPP.QUERY);
        }
        writer.endTag(null, XMPP.IQ);

    }

    public void statusListToString(Vector v) throws IOException {
        for (int i = 0; i < v.size(); i++) {

            writer.startTag(null, XMPP.STATUS);
            writer.text((String) v.elementAt(i));
            writer.endTag(null, XMPP.STATUS);

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
     * Sends a message text to a known jid.
     *
     * @param to  the JID of the recipient
     * @param msg the message itself
     */
    public synchronized void sendMessage(final String to, final String msg) throws IOException {
        this.writer.startTag(null, XMPP.MESSAGE);
        this.writer.attribute(null, XMPP.TYPE, XMPP.CHAT);
        this.writer.attribute(null, XMPP.TO, to);
        this.writer.startTag(null, XMPP.BODY);
        this.writer.text(msg);
        this.writer.endTag(null, XMPP.BODY);
        this.writer.endTag(null, XMPP.MESSAGE);
        this.writer.flush();
    }

    public synchronized void sendPing(final int id) throws IOException {

        this.writer.startTag(null, XMPP.IQ);
        this.writer.attribute(null, XMPP.TYPE, XMPP.GET);
        this.writer.attribute(null, "id", getIqId());
        this.writer.startTag(null, XMPP.PING);
        this.writer.attribute(null, XMPP.XMLNS, XMPP.URN_XMPP_PING);
        this.writer.endTag(null, XMPP.PING);
        this.writer.endTag(null, XMPP.IQ);
        this.writer.flush();
    }

    /**
     * Sends a presence stanza to a jid. This method can do various task but
     * it's private, please use setStatus to set your status or explicit
     * subscription methods subscribe, unsubscribe, subscribed and
     * unsubscribed to change subscriptions.
     */
    public synchronized void sendPresence(final String to, final String type, final String show, final String status, final int priority) throws IOException {
        this.writer.startTag(null, XMPP.PRESENCE);
        if (type != null) {
            this.writer.attribute(null, XMPP.TYPE, type);
        }
        if (to != null) {
            this.writer.attribute(null, XMPP.TO, to);
        }
        if (show != null) {
            this.writer.startTag(null, XMPP.SHOW);
            this.writer.text(show);
            this.writer.endTag(null, XMPP.SHOW);
        }
        if (status != null) {
            this.writer.startTag(null, XMPP.STATUS);
            this.writer.text(status);
            this.writer.endTag(null, XMPP.STATUS);
        }
        if (priority != 0) {
            this.writer.startTag(null, XMPP.PRIORITY);
            this.writer.text(Integer.toString(priority));
            this.writer.endTag(null, XMPP.PRIORITY);
        }
        this.writer.endTag(null, XMPP.PRESENCE); // presence
        this.writer.flush();

    }

    /**
     * Sets your Jabber Status.
     *
     * @param show     is one of the following: <code>null</code>, chat, away,
     *                 dnd, xa, invisible
     * @param status   an extended text describing the actual status
     * @param priority the priority number (5 should be default)
     */
    public synchronized void setStatus(String show, String status, final int priority) throws IOException {
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
    public synchronized void subscribe(final String to) throws IOException {
        this.sendPresence(to, "subscribe", null, null, 0);
    }

    /**
     * Remove a subscription.
     *
     * @param to the jid you want to remove your subscription
     */
    public synchronized void unsubscribe(final String to) throws IOException {
        this.sendPresence(to, "unsubscribe", null, null, 0);
    }

    /**
     * Approve a subscription request.
     *
     * @param to the jid that sent you a subscription request
     */
    public synchronized void subscribed(final String to) throws IOException {
        this.sendPresence(to, "subscribed", null, null, 0);
    }

    /**
     * Refuse/Reject a subscription request.
     *
     * @param to the jid that sent you a subscription request
     */
    public synchronized void unsubscribed(final String to) throws IOException {
        this.sendPresence(to, "unsubscribed", null, null, 0);
    }

    /**
     * Save a contact to roster. This means, a message is send to jabber
     * server (which hosts your roster) to update the roster.
     *
     * @param jid          the jid of the contact
     * @param name         the nickname of the contact
     * @param group        the group of the contact
     * @param subscription the subscription of the contact
     */
    public synchronized void saveContact(final String jid, final String name, final Enumeration group, final String subscription) throws IOException {

        this.writer.startTag(null, XMPP.IQ);
        this.writer.attribute(null, XMPP.TYPE, XMPP.SET);
        this.writer.startTag(null, XMPP.QUERY);
        this.writer.attribute(null, XMPP.XMLNS, XMPP.JABBER_IQ_ROSTER);
        this.writer.startTag(null, XMPP.ITEM);
        this.writer.attribute(null, XMPP.JID, jid);
        this.writer.attribute(null, XMPP.ID, getIqId());
        if (name != null) {
            this.writer.attribute(null, XMPP.NAME, name);
        }
        if (subscription != null) {
            this.writer.attribute(null, XMPP.SUBSCRIPTION, subscription);
        }
        if (group != null) {
            while (group.hasMoreElements()) {
                this.writer.startTag(null, XMPP.GROUP);
                this.writer.text((String) group.nextElement());
                this.writer.endTag(null, XMPP.GROUP); // group
            }
        }
        this.writer.endTag(null, XMPP.ITEM); // item
        this.writer.endTag(null, XMPP.QUERY); // query
        this.writer.endTag(null, XMPP.IQ); // iq
        this.writer.flush();

    }

    /**
     * Sends a roster query.
     *
     * @throws java.io.IOException is thrown if {@link net.sourceforge.jxa.XmlReader} or {@link net.sourceforge.jxa.XmlWriter}
     *                             throw an IOException.
     */
    public synchronized void getRoster() throws IOException {
        this.writer.startTag(null, XMPP.IQ);
        this.writer.attribute(null, XMPP.ID, getIqId());
        this.writer.attribute(null,XMPP.TYPE, XMPP.GET);
        this.writer.startTag(null,XMPP.QUERY);
        this.writer.attribute(null,XMPP.XMLNS, XMPP.JABBER_IQ_ROSTER);
        this.writer.endTag(null,XMPP.QUERY); // query
        this.writer.endTag(null, XMPP.IQ); // iq
        this.writer.flush();
    }

}
