/*
 * ContactListUI.java
 *
 * Created on 18 Ноябрь 2006 г., 10:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import net.sourceforge.jxa.Jxa;
import net.sourceforge.jxa.XmppAdapter;
import net.sourceforge.jxa.XmppListener;
import org.rost.mobile.guilib.components.MenuItem;
import org.rost.mobile.guilib.components.layers.Menu;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.BaseMidlet;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.ItemActionListener;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.Profile;
import org.rost.mobile.mgtalk.model.User;
import org.rost.mobile.mgtalk.model.UserAddedListener;
import org.rost.mobile.mgtalk.model.UserDeletedListener;

/**
 *
 * @author Kostya
 */
public class ContactListUI extends SelectableList implements
        UserAddedListener, UserDeletedListener, XmppListener, ItemActionListener {

    /** Creates a new instance of ContactListUI */
    Menu menu = null;
    XmppListener newMessagesListener = new XmppAdapter() {

        public void onMessageEvent(String from, String body) {
            AppStore.getContactList().processNewMessage(from, body);
        }

    };
    XmppListener contactListListener = new XmppAdapter() {

        public void onStatusEvent(String jid, String show, String status) {
            AppStore.getContactList().processUserStateChange("", jid, status, Jxa.statusStringToNumber(show));
        }

        public void onContactEvent(String jid, String name, String group, String subscription) {
            AppStore.getContactList().processUserStateChange(name, jid, "", -1);
        }

    };

    public Profile profile() {
        return AppStore.getSelectedProfile();
    }

    public void refreshView() {
        setCaption(AppStore.getSelectedProfile().getUserName());
    }

    

    public static Image statusIDToImage(int statusID) {
        switch (statusID) {
            case -1:
                return AppStore.STATUS_OFFLINE;
            case 1:
                return AppStore.STATUS_AWAY;
            case 2:
                return AppStore.STATUS_NA;
            case 3:
                return AppStore.STATUS_BUSY;
        }
        return AppStore.STATUS_ONLINE;
    }

    public ContactListUI() {

        setLeftCommand(i18n.getMessage("menu"));
        setRightCommand(i18n.getMessage("disconnect"));
        menu = new Menu(0, i18n.getMessage("menu_select"), i18n.getMessage("menu_cancel"));
        MenuItem quitItem = new MenuItem(i18n.getMessage("quit"));
        quitItem.setItemActionListener(new ItemActionListener() {

            public void actionPerformed() {
                AppStore.getJxa().logoff();
                BaseMidlet.closeMIDLet();
            }
        });
        menu.addMenuItem(quitItem);


        MenuItem statusItem = new MenuItem("Status");
        statusItem.setItemActionListener(new ItemActionListener() {

            public void actionPerformed() {
                /*if(AppStore.getSelectedProfile().isGoogle() &&
                AppStore.getNetworkDispatcher().getSharedStatus().isFirstRefresh()){
                AppStore.getErrorMessage().showErrorMessage("Shared status data has not been retrieved yet");
                return;
                }*/
                GUIStore.getManager().push(AppStore.getSharedStatusUI());
            }
        });
        menu.addMenuItem(statusItem);
    /*
    AppStore.getNetworkDispatcher().addListener(contactListListener);
    AppStore.getNetworkDispatcher().addListener(newMessagesListener);
    AppStore.getContactList().addUserAddedListener(this);
    AppStore.getContactList().addUserDeletedListener(this);
    AppStore.getNetworkDispatcher().addListener(this);
     * */
    }

    public boolean leftCommandClick() {
        GUIStore.getManager().pushFront(menu);
        GUIStore.getManager().notifyChanged();
        return true;

    }

    public boolean rightCommandClick() {
//        clear();
        //AppStore.getJxa().logoff();
        AppStore.getJxa().close();
        AppStore.setJxa(null);
        GUIStore.getManager().push(AppStore.getProfileListUI());
        return true;
    }

    public void init() {
        AppStore.getJxa().addListener(contactListListener);
        AppStore.getJxa().addListener(newMessagesListener);
        AppStore.getJxa().addListener(AppStore.getSharedStatus());
        AppStore.getContactList().addUserAddedListener(this);
        AppStore.getContactList().addUserDeletedListener(this);
        AppStore.getJxa().addListener(this);

    }

    public boolean isMain() {
        return true;
    }

    public void userAdded(int userPosition) {
//        System.out.println("Added user in "+userPosition+", user = "+
//                AppStore.getContactList().getUserAt(userPosition).getUserName()+" status = "+
//                AppStore.getContactList().getUserAt(userPosition).getStatus()+", statusID = "+
//                AppStore.getContactList().getUserAt(userPosition).getStatusID());
        User user = AppStore.getContactList().getUserAt(userPosition);
        ContactListItem item = new ContactListItem(
                statusIDToImage(user.getStatusID()),
                (user.getUnreadMessages() > 0 ? "[" + user.getUnreadMessages() + "] " : "") + user.getUserName(),
                user.getStatus());
        item.setItemActionListener(this);
        addItem(item, userPosition);
    }

    public void userDeleted(int userPosition) {
       // System.out.println("Deleted user in " + userPosition);
        removeItem(userPosition);
    }

    public void connectionTerminated(String msg) {
        if (msg == null){
            msg = "Disconnected";
        }
        AppStore.getContactList().makeAllUsersOffline();
        AppStore.getInfoTicker().setMessage(msg, true);
    }

    public void actionPerformed() {
        AppStore.getChatUI().setUser(AppStore.getContactList().getUserAt(getSelectedIndex()));
        GUIStore.getManager().push(AppStore.getChatUI());

    }

    public void onConnFailed(String msg) {
        connectionTerminated(msg);
    }

    public void onAuth(String responseJid) {
        try {
            profile().setFullJID(responseJid);
            AppStore.getJxa().startSession();
            if(profile().isGoogle()){
                AppStore.getJxa().sendGoogleSettings();
            }else{
                AppStore.getJxa().setStatus(Jxa.statusIDtoString(profile().getStatusID()), profile().getStatus(), 0);
            }
            AppStore.getJxa().getRoster();
        } catch (IOException ex) {
            connectionTerminated(ex.getMessage());
        }
    }

    public void onAuthFailed(String message) {
    }

    public void onMessageEvent(String from, String body) {
    }

    public void onContactEvent(String jid, String name, String group, String subscription) {
    }

    public void onContactOverEvent() {
    }

    public void onStatusEvent(String jid, String show, String status) {
    }

    public void onSubscribeEvent(String jid) {
    }

    public void onUnsubscribeEvent(String jid) {
    }

    public void onSharedStatusEvent(String status, int show, Vector awayList, Vector busyList, Vector onlineList) {
    }
}
