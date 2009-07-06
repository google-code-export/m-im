/*
 * User.java
 *
 * Created on 18 Ноябрь 2006 г., 13:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.model;

import java.util.Calendar;
import java.util.Vector;

import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.mgtalk.AppStore;

/**
 *
 * @author Kostya
 */
public class User {

    protected String userName = "";
    protected String JID = "";
    protected String currentSession = "";
    Vector jids = new Vector();
    protected String status = "";
    protected int statusID = -1;
    protected Vector userListeners = new Vector();
    protected Vector messageListeners = new Vector();
    protected Vector history = new Vector();
    protected int unreadMessages = 0;

    public void addUserListener(UserStateListener l) {
        userListeners.insertElementAt(l, 0);
    }

    public void addMessageListener(UserMessageListener l) {
        messageListeners.insertElementAt(l, 0);
    }

    void notifyChanged() {
        try {
            for (int i = 0; i < userListeners.size(); i++) {
                UserStateListener l = (UserStateListener) userListeners.elementAt(i);
                if (l.stateChanged(this)) {
                    return;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void removeUserListener(UserStateListener l) {
        userListeners.removeElement(l);
    }

    public void removeMessageListener(UserMessageListener l) {
        messageListeners.removeElement(l);
    }

    /** Creates a new instance of User */
    public User() {
        refreshStatus();
    }

    public static String getCurrentTime() {
        Calendar cl = Calendar.getInstance();
        int h = 0;
        if (cl.getTimeZone().useDaylightTime()) {
            h = cl.get(Calendar.HOUR_OF_DAY) + 1;
        } else {
            h = cl.get(Calendar.HOUR_OF_DAY);
        }
        String t = h + ":" + (cl.get(Calendar.MINUTE) < 10 ? "0" + cl.get(Calendar.MINUTE) : "" + cl.get(Calendar.MINUTE));
        return t;
    }

    public void addMessageToHistory(String message, String fullJID, boolean from) {
        setCurrentSession(fullJID);
        StaticRichText text = new StaticRichText();
        text.addText(getCurrentTime() + (from ? "<< " : ">> "), true,
                from ? AppStore.getIncomingMessageColor() : AppStore.getOutgoingMessageColor());
        text.addText(message);
        history.insertElementAt(text, 0);
        while (history.size() > AppStore.getSelectedProfile().getHistoryLength()) {
            history.removeElementAt(history.size() - 1);
        }
        try {
            for (int i = 0; i < messageListeners.size(); i++) {
                UserMessageListener l = (UserMessageListener) messageListeners.elementAt(i);
                if (l.newMessageReceived(this, text, from)) {
                    return;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void refreshStatus() {
        //String outp = "";
        int minStatus = 5;
        int ind = -1;
        boolean changed = false;
        for (int i = 0; i < jids.size(); i++) {
            UserJID jid = (UserJID) jids.elementAt(i);
            if (jid.getStatusID() < minStatus) {
                minStatus = jid.getStatusID();
                ind = i;
            }
        }
        if (ind != -1) {
            UserJID jid = (UserJID) jids.elementAt(ind);
            if (statusID != jid.getStatusID()) {
                changed = true;
            }
            if (!status.equals(jid.getStatus())) {
                changed = true;
            }
            statusID = jid.getStatusID();
            status = jid.getStatus();
        } else {
            if (statusID != -1) {
                changed = true;
            }
            if (!status.equals("")) {
                changed = true;
            }
            status = "";
            statusID = -1;
        }
        if (changed) {
            notifyChanged();
        }
    }

    public void makeUserOffline() {
        jids.removeAllElements();
        refreshStatus();
    }

    public void processNewInfo(String userJID, String status, int statusID) {
        for (int i = 0; i < jids.size(); i++) {
            UserJID jid = (UserJID) jids.elementAt(i);
            if (jid.getFullJID().toLowerCase().equals(userJID.toLowerCase())) {
                if (statusID == -1) {
                    jids.removeElementAt(i);
                    refreshStatus();
                    return;
                }
                jid.setStatus(status);
                jid.setStatusID(statusID);
                refreshStatus();
                return;

            }
        }
        if (statusID != -1) {
            jids.addElement(new UserJID(userJID, status, statusID));
            refreshStatus();
        }
    }

    public String getUserName() {
        return userName.equals("") ? JID : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJID() {
        return JID;
    }

    public void setJID(String JID) {
        this.JID = JID;
    }

    public String getCurrentSession() {
        return currentSession.equals("") ? getJID() : currentSession;
    }

    public void setCurrentSession(String currentSession) {
        this.currentSession = currentSession;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusID() {
        return statusID;
    }

    public Vector getHistory() {
        return history;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        if (this.unreadMessages != unreadMessages) {
            this.unreadMessages = unreadMessages;
            notifyChanged();
            return;
        }
        this.unreadMessages = unreadMessages;
    }
}
