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

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: setup
 * Date: 2009/10/29
 * Time: 13:45:17
 * To change this template use File | Settings | File Templates.
 */
public class XMPPInputStream {

    private InputStream is;

    private KXmlParser reader;
    boolean supportTLS;
    boolean supportBind;
    boolean supportZip;
    boolean supportSession;
    boolean supportPlain;
    boolean supportGoogleToken;
    public static final String GROUP = "group";

    public XMPPInputStream(InputStream is) throws XmlPullParserException {
        this.is = is;
        this.reader = new KXmlParser();
        this.reader.setInput(this.is, null /* auto detect?? "UTF-8"*/);
    }

    public void startStream() throws IOException, XmlPullParserException {
        reader.nextTag();
        reader.require(XmlPullParser.START_TAG, null, XMPP.STREAM_STREAM);
        reader.nextTag();
        reader.require(XmlPullParser.START_TAG, null, XMPP.STREAM_FEATURES);
        while (reader.nextTag() != XmlPullParser.END_TAG) {
            String tagName = reader.getName();
            if (tagName == null) {
                throw new IllegalStateException("read features error");
            } else if (tagName.equals(XMPP.STARTTLS)) {
                this.supportTLS = true;
                reader.skipSubTree();
            } else if (tagName.equals(XMPP.MECHANISMS)) {
                while (reader.nextTag() != XmlPullParser.END_TAG) {
                    tagName = reader.getName();
                    if (tagName.equals(XMPP.MECHANISM)) {
                        String text = reader.nextText();
                        if (text == null) {

                        } else if (text.equals(XMPP.PLAIN)) {
                            supportPlain = true;
                        } else if (text.equals(XMPP.X_GOOGLE_TOKEN)) {
                            supportGoogleToken = true;
                        }

                    }
                }
            } else if (tagName.equals(XMPP.BIND)) {
                this.supportBind = true;
                reader.skipSubTree();
            } else if (tagName.equals(XMPP.COMPRESSION)) {
                this.supportZip = true;
                reader.skipSubTree();
            } else if (tagName.equals(XMPP.SESSION)) {
                this.supportSession = true;
                reader.skipSubTree();
            }

        }
    }

    public void requireSuccess() throws IOException, XmlPullParserException {
        this.requireTree(XMPP.SUCCESS);
    }

    private void requireTree(String tagName) throws IOException, XmlPullParserException {
        reader.nextTag();
        reader.require(XmlPullParser.START_TAG, null, tagName);
        reader.skipSubTree();
    }

    public String readIQ() throws IOException, XmlPullParserException {
        String ret;
        reader.nextTag();
        reader.require(KXmlParser.START_TAG, null, XMPP.IQ);

        ret = reader.getAttributeValue(null, XMPP.ID);
        parseIQ();
        return ret;
    }

    private void parseIQ() throws XmlPullParserException, IOException {
        String tagName;
        String type = reader.getAttributeValue(null, XMPP.TYPE);
        final String from = reader.getAttributeValue(null, XMPP.FROM);
        if (!reader.isEmptyElementTag()) {
            if (type.equals("error")) {
                reader.skipSubTree();
            } else {
                reader.nextTag();
                /* see what is inside iq tag */
                tagName = reader.getName();
                if (tagName == null) {
                    //
                } else if (tagName.equals(XMPP.BIND)) {
                    if (!reader.isEmptyElementTag()) {
                        parseBindResult();
                    }
                    reader.nextTag();
                } else if (tagName.equals(XMPP.QUERY)) {
                    String xmlns = reader.getAttributeValue(null, XMPP.XMLNS);
                    if (xmlns.equals(XMPP.JABBER_IQ_ROSTER)) {
                        while (this.reader.nextTag() == XmlPullParser.START_TAG) {
                            if (this.reader.getName().equals(XMPP.ITEM)) {
                                String jid = reader.getAttributeValue(null, XMPP.JID);
                                String name = reader.getAttributeValue(null, XMPP.NAME);
                                String subscription = reader.getAttributeValue(null, XMPP.SUBSCRIPTION);
                                boolean check = true;
                                while (this.reader.nextTag() == XmlPullParser.START_TAG) {
                                    if (this.reader.getName().equals(GROUP)) {
                                        final String groupName = reader.nextText();
                                        //TODO trigger listener
                                        check = false;
                                    } else {
                                        reader.skipSubTree();
                                    }
                                }
                                //TODO subscription .equals null.
                            } else {
                                reader.skipSubTree();
                            }

                        }
                        //TODO Listener
                    } else if (xmlns.equals("jabber:iq:version")) {
                        while (this.reader.nextTag() == XmlPullParser.START_TAG) {
                            this.reader.skipSubTree();
                        }
                        // TODO trigger SendVersion

                    } else if (xmlns.equals(XMPP.GOOGLE_SHARED_STATUS)) {
                        String status = null;
                        int show = 0;
                        Vector awayList = new Vector();
                        Vector busyList = new Vector();
                        Vector onlineList = new Vector();
                        while (this.reader.nextTag() == XmlPullParser.START_TAG) {
                            if (this.reader.getName().equals(XMPP.STATUS)) {
                                status = reader.nextText();
                            } else if (this.reader.getName().equals(XMPP.SHOW)) {
                                show = statusStringToNumber(reader.nextText());
                            } else if (this.reader.getName().equals(XMPP.STATUS_LIST)) {
                                if (this.reader.getAttributeValue(null, XMPP.SHOW).equals(XMPP.AWAY)) {
                                    while (this.reader.nextTag() == XmlPullParser.START_TAG) {
                                        awayList.addElement(reader.nextText());
                                    }
                                } else if (this.reader.getAttributeValue(null, XMPP.SHOW).equals(XMPP.DND)) {
                                    while (this.reader.nextTag() == XmlPullParser.START_TAG) {
                                        busyList.addElement(reader.nextText());
                                    }
                                } else if (this.reader.getAttributeValue(null, XMPP.SHOW).equals(XMPP.DEFAULT)) {
                                    while (this.reader.nextTag() == XmlPullParser.START_TAG) {
                                        onlineList.addElement(reader.nextText());
                                    }
                                }
                            } else {
                                this.reader.skipSubTree();
                            }
                        }

                        //TODO listener System.out.println(this.reader.getName() + this.reader.getType());
//                        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
//                            XmppListener xl = (XmppListener) e.nextElement();
//                            xl.onSharedStatusEvent(status, show, awayList, busyList, onlineList);
//                        }

                    } else {
                        this.reader.skipSubTree();
                    }
                } else {
                    this.reader.skipSubTree();
                }
            }
        }
        reader.nextTag();   //IQ
    }

    private void parseBindResult() throws IOException, XmlPullParserException {
        String rsp_jid;
        reader.nextTag();
        reader.require(XmlPullParser.START_TAG, null, XMPP.JID);
        rsp_jid = reader.nextText();
        int i = rsp_jid.indexOf('/');
        String resource = rsp_jid.substring(i++);
        //TODO triger listener
    }

    public static int statusStringToNumber(String str) {
        if (str.equals(XMPP.AWAY)) {
            return 1;
        }
        if (str.equals(XMPP.NA)) {
            return 2;
        }
        if (str.equals("xa")) {
            return 2;
        }
        if (str.equals("busy")) {
            return 3;
        }
        if (str.equals(XMPP.DND)) {
            return 3;
        }
        return 0;
    }

    public void parse() throws IOException, XmlPullParserException {
        int i=0;
        while (reader.nextTag() == XmlPullParser.START_TAG) {
            i++;
            final String tagName = this.reader.getName();
            if (tagName.equals(XMPP.MESSAGE)) {
                this.parseMessage();
            } else if (tagName.equals(XMPP.PRESENCE)) {
                this.parsePresence();
            } else if (tagName.equals(XMPP.IQ)) {
                this.parseIQ();
            } else {
                this.reader.skipSubTree();
            }
        }
        System.out.println(i);
    }

    private void parsePresence() throws IOException, XmlPullParserException {
                final String from = this.reader.getAttributeValue(null,XMPP.FROM),  type = this.reader.getAttributeValue(null,XMPP.TYPE);
        String status = "", show = "";
        while (this.reader.nextTag() == XmlPullParser.START_TAG) {
            final String tmp = this.reader.getName();
            if (tmp.equals("status")) {
                status = this.reader.nextText();
            } else if (tmp.equals("show")) {
                show = this.reader.nextText();
            } else {
                this.reader.skipSubTree();
            }
        }

        //if ((type != null) && (type.equals("unavailable") || type.equals("unsubscribed") || type.equals("error"))) {
        if (type == null) {
            for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                XmppListener xl = (XmppListener) e.nextElement();
                xl.onStatusEvent(from, show, status);
            }
        } else {
            if (type.equals(XMPP.UNSUBSCRIBED)) {
                for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                    XmppListener xl = (XmppListener) e.nextElement();
                    xl.onUnsubscribeEvent(from);
                }
            } else if (type.equals(XMPP.SUBSCRIBE)) {
                for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                    XmppListener xl = (XmppListener) e.nextElement();
                    xl.onSubscribeEvent(from);
                }
            } else if (type.equals(XMPP.UNAVAILABLE)) {
                //final String jid = (from.indexOf('/') == -1) ? from : from.substring(0, from.indexOf('/'));
                for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                    XmppListener xl = (XmppListener) e.nextElement();
                    //xl.onStatusEvent(jid, show, status);
                    xl.onStatusEvent(from, XMPP.NA, status);
                }
            }
        }
    }
    private Vector listeners = new Vector();

    private void parseMessage() throws IOException, XmlPullParserException {
               final String from = this.reader.getAttributeValue(null,XMPP.FROM);
        //final String type = this.reader.getAttribute("type");
        String body = null, subject = null;
        while (this.reader.nextTag() == XmlPullParser.START_TAG) {
            final String tmp = this.reader.getName();
            if (tmp.equals(XMPP.BODY)) {
                body = this.reader.nextText();
            } else if (tmp.equals("subject")) {
                subject = this.reader.nextText(); // this is checked for, but never used!
            } else {
                this.reader.skipSubTree();
            }
        }
        //TODO  (from, subject, body);
        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
            XmppListener xl = (XmppListener) e.nextElement();
            xl.onMessageEvent((from.indexOf('/') == -1) ? from : from.substring(0, from.indexOf('/')), body);
        }
        System.out.println(from + ":"+body);
    }
}
