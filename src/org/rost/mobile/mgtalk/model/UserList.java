/*
 * UserList.java
 *
 * Created on 18 Ноябрь 2006 г., 14:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.model;

import java.util.Vector;

import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.mgtalk.AppStore;

/**
 *
 * @author Kostya
 */
public class UserList implements UserStateListener, UserMessageListener {

    /** Creates a new instance of UserList */
    UserAddedListener addListener = null;
    UserDeletedListener delListener = null;
    Vector users = new Vector();

    public void addUserAddedListener(UserAddedListener l) {
        addListener = l;
    }

    public void addUserDeletedListener(UserDeletedListener l) {
        delListener = l;
    }

    void notifyUserDeleted(int position) {
        if (delListener != null) {
            delListener.userDeleted(position);
        }
    }

    void notifyUserAdded(int position) {
        if (addListener != null) {
            addListener.userAdded(position);
        }
    }

    public boolean stateChanged(User user) {
        moveUserToRightPlace(user);
        return true;
    }

    public UserList() {
    }

    public int findRightPlaceForUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            User u = (User) users.elementAt(i);
            if (isUser1GreaterUser2(user, u)) {
                return i;
            }
        }
        return users.size();
    }

    synchronized void moveUserToRightPlace(User user) {
        int oldPlace = users.indexOf(user);
        if (oldPlace == -1) {
            return;
        }
        users.removeElementAt(oldPlace);
        notifyUserDeleted(oldPlace);

        int newPos = findRightPlaceForUser(user);
        if (newPos == users.size()) {
            users.addElement(user);
        } else {
            users.insertElementAt(user, newPos);
        }

        if (user.getStatusID() >= 0 || AppStore.getSelectedProfile().isShowOffline() || user.getUnreadMessages() > 0) {
            notifyUserAdded(newPos);
        }
    }

    public void processNewMessage(String fromJID, String message) {
        if (message == null || message.equals("")) {
            return;
        }
        String rightJID = fromJID;
        if (rightJID.indexOf("/") != -1) {
            rightJID = fromJID.substring(0, fromJID.indexOf("/"));
        }
        try {
            for (int i = 0; i < users.size(); i++) {
                User user = (User) users.elementAt(i);
                if (user.getJID().toLowerCase().equals(rightJID.toLowerCase())) {
                    if (message.length() > 70) {
                        int oldIndex = 0;
                        int index = message.indexOf("\n", oldIndex + 60);
                        while(index > 0){
                            String sub = message.substring(oldIndex, index);
                            user.addMessageToHistory(sub, fromJID, true);
                            oldIndex = index;
                            index = message.indexOf("\n", oldIndex + 60);
                        }
                        user.addMessageToHistory(message.substring(oldIndex), fromJID, true);
                    } else {
                        user.addMessageToHistory(message, fromJID, true);
                    }
                    return;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void processUserStateChange(String userName, String JID, String status, int statusID) {
        String rightJID = JID;
        if (rightJID.indexOf("/") != -1) {
            rightJID = JID.substring(0, rightJID.indexOf("/"));
        }
        if (rightJID.toLowerCase().equals(AppStore.getSelectedProfile().getUserName().toLowerCase())) {
            return;
        }
        for (int i = 0; i < users.size(); i++) {
            User user = (User) users.elementAt(i);
            if (user.getJID().toLowerCase().equals(rightJID.toLowerCase())) {
                //Change user
                if (userName.equals("")) {
                    user.processNewInfo(JID, status, statusID);
                } else {
                    user.setUserName(userName);
                    moveUserToRightPlace(user);
                }
                return;
            }
        }
        User user = new User();
        user.setJID(rightJID);
        if (userName.equals("")) {
            user.processNewInfo(JID, status, statusID);
        } else {
            user.setUserName(userName);
        }
        int newPos = findRightPlaceForUser(user);
        if (newPos == users.size()) {
            users.addElement(user);
        } else {
            users.insertElementAt(user, newPos);
        }

        if (user.getStatusID() >= 0 || AppStore.getSelectedProfile().isShowOffline() || user.getUnreadMessages() > 0) {
            notifyUserAdded(newPos);
        }
        user.addUserListener(this);
        user.addMessageListener(this);
    }

    static boolean isString1GreaterString2(String str1, String str2) {
        for (int i = 0; i < Math.min(str1.length(), str2.length()); i++) {
            if (str1.toLowerCase().charAt(i) > str2.toLowerCase().charAt(i)) {
                return true;
            }
            if (str1.toLowerCase().charAt(i) < str2.toLowerCase().charAt(i)) {
                return false;
            }
        }
        return str1.length() > str2.length();
    }

    static boolean isUser1GreaterUser2(User user1, User user2) {
        if (user1.getUnreadMessages() > user2.getUnreadMessages()) {
            return true;
        }
        if (user1.getUnreadMessages() < user2.getUnreadMessages()) {
            return false;
        }
        if (user1.getStatusID() == user2.getStatusID()) {
            return isString1GreaterString2(user2.getUserName(), user1.getUserName());
        }
        if (user1.getStatusID() == -1) {
            return false;
        }
        if (user2.getStatusID() == -1) {
            return true;
        }
        if (!AppStore.getSelectedProfile().isSortByName()) {
            return user1.getStatusID() < user2.getStatusID();
        }
        return isString1GreaterString2(user2.getUserName(), user1.getUserName());
    }

    public User getUserAt(int i) {
        return (User) users.elementAt(i);
    }

    public void makeAllUsersOffline() {
        for (int i = 0; i < users.size(); i++) {
            User user = (User) users.elementAt(i);
            if (user.getStatusID() > -1) {
                user.makeUserOffline();
                i--;
            }
        }
    }

    public void clear() {
        users.removeAllElements();
    }

    public boolean newMessageReceived(User user, StaticRichText item, boolean from) {
        if (from) {
            user.setUnreadMessages(user.getUnreadMessages() + 1);
            AppStore.notifyMessage();
//            AppStore.playMessage();
//            if (AppStore.getSelectedProfile().isVibrate()) {
//            	AppStore.vibrate();
//            }
            AppStore.getInfoTicker().setMessage("New message from " + user.getUserName());
        }
        return true;
    }
}
