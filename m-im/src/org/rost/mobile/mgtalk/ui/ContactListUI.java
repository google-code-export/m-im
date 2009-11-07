/*
 * ContactListUI.java
 *
 * Created on 18 Ноябрь 2006 г., 10:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import java.util.Vector;
import javax.microedition.lcdui.Image;
import com.google.code.mim.XmppAdapter;
import com.google.code.mim.XMPPListener;
import org.rost.mobile.guilib.components.MenuItem;
import org.rost.mobile.guilib.components.layers.Menu;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.BaseMidlet;
import org.rost.mobile.guilib.core.Constants;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.ItemActionListener;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.GlobalPrefs;
import org.rost.mobile.mgtalk.model.Profile;
import org.rost.mobile.mgtalk.model.User;
import org.rost.mobile.mgtalk.model.UserAddedListener;
import org.rost.mobile.mgtalk.model.UserDeletedListener;
import org.rost.mobile.mgtalk.model.UserList;

import com.google.code.mim.Log;
import com.google.code.mim.Utils;

/**
 *
 * @author Kostya
 */
public class ContactListUI extends SelectableList implements UserAddedListener, UserDeletedListener, XMPPListener, ItemActionListener {

    /** Creates a new instance of ContactListUI */
    Menu menu = null;
    
    XMPPListener newMessagesListener = new XmppAdapter() {

        public void onMessageEvent(String from, String body) {
            AppStore.getContactList().processNewMessage(from, body);
        }

    };
    XMPPListener contactListListener = new XmppAdapter() {

        public void onStatusEvent(String jid, String show, String status) {
            AppStore.getContactList().processUserStateChange("", jid, status, Utils.statusStringToNumber(show));
        }

        public void onContactEvent(String jid, String name, String group, String subscription) {
            AppStore.getContactList().processUserStateChange(name, jid, "", -1);
        }

    };

    public Profile profile() {
        return AppStore.getSelectedProfile();
    }

    public void refreshView() {
        //setCaption(AppStore.getSelectedProfile().getUserName());
    	// Display Name : Profile Name makes more sense here...
    	setCaption(AppStore.getSelectedProfile().getDisplayName() + " : " + AppStore.getSelectedProfile().getName());
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
        
        menu = new Menu(0, i18n.getMessage("menu_select"), i18n.getMessage("menu_cancel"));

        MenuItem statusItem = new MenuItem(i18n.getMessage("menu_item_status"));
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

        MenuItem globalPrefsItem = new MenuItem(i18n.getMessage("globalprefs"));
        globalPrefsItem.setItemActionListener(new ItemActionListener() {
        	public void actionPerformed() {
        		GlobalPrefsUI ui = AppStore.getGlobalPrefsUI();
        		ui.setBackToInterface(AppStore.getContactListUI());
        		GUIStore.getManager().push(ui);
        		GUIStore.getManager().notifyChanged();
        	}
        });
        menu.addMenuItem(globalPrefsItem);

        MenuItem muteItem = new MenuItem(i18n.getMessage("menu_item_mute"));
        muteItem.setItemActionListener(new ItemActionListener() {
            public void actionPerformed() {
            	GlobalPrefs gp = AppStore.getGlobalPrefs();
            	boolean soundEnabled = gp.isSoundEnabled();
            	if (soundEnabled) {
            		gp.setSoundEnabled(false);
            	} else {
            		gp.setSoundEnabled(true);
            	}
            }
        });
        menu.addMenuItem(muteItem);        
        
        MenuItem dissItem = new MenuItem(i18n.getMessage("disconnect"));
        dissItem.setItemActionListener(new ItemActionListener() {

            public void actionPerformed() {
                //AppStore.getXMPP().logoff();
                AppStore.getXMPP().close();
                AppStore.setXMPP(null);
                GUIStore.getManager().push(AppStore.getProfileListUI());
            }
        });
        menu.addMenuItem(dissItem);        
        
        MenuItem quitItem = new MenuItem(i18n.getMessage("quit"));
        quitItem.setItemActionListener(new ItemActionListener() {

            public void actionPerformed() {
                AppStore.getXMPP().close();
                AppStore.setXMPP(null);
                BaseMidlet.closeMIDLet();
            }
        });
        menu.addMenuItem(quitItem);        

        setLeftCommand(i18n.getMessage("menu"));
	        
      //minimise if supported (S60) otherwise status S40 etc 
        setRightCommand(AppStore.isS60() ? i18n.getMessage("minimise") : i18n.getMessage("status"));
        
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
        //AppStore.getXMPP().logoff();
        //AppStore.getXMPP().close();
        //AppStore.setXMPP(null);
        //GUIStore.getManager().push(AppStore.getProfileListUI());
        //return true;
    	
    	// Status Change
        //GUIStore.getManager().push(AppStore.getSharedStatusUI());
        //GUIStore.getManager().notifyChanged();
        
        // Minimise or show status UI
    	if (AppStore.isS60()) {
        	GUIStore.minimiseApplication();
    	} else {
            GUIStore.getManager().push(AppStore.getSharedStatusUI());
            GUIStore.getManager().notifyChanged();    		
    	}
        
        return true;
    }

    public void init() {
        AppStore.getXMPP().addListener(contactListListener);
        AppStore.getXMPP().addListener(newMessagesListener);
        AppStore.getXMPP().addListener(AppStore.getSharedStatus());
        AppStore.getContactList().addUserAddedListener(this);
        AppStore.getContactList().addUserDeletedListener(this);
        AppStore.getXMPP().addListener(this);

    }

    public boolean isMain() {
        return true;
    }

    public void userAdded(int userPosition) {
    	if (Constants.LOGGING) {
    		UserList list = AppStore.getContactList();
        	Log.debug("Added user in "+userPosition+", user = "+
        			list.getUserAt(userPosition).getUserName()+" status = "+
        			list.getUserAt(userPosition).getStatus()+", statusID = "+
        			list.getUserAt(userPosition).getStatusID());
    	}
    	
        User user = AppStore.getContactList().getUserAt(userPosition);
        ContactListItem item = new ContactListItem(
                statusIDToImage(user.getStatusID()),
                (user.getUnreadMessages() > 0 ? "[" + user.getUnreadMessages() + "] " : "") + user.getUserName(),
                user.getStatus(), true);
        item.setItemActionListener(this);
        addItem(item, userPosition);
    }

    public void userDeleted(int userPosition) {
    	if (Constants.LOGGING) {
    		Log.debug("Deleted user in " + userPosition);
    	}
        removeItem(userPosition);
    }

    public void connectionTerminated(String msg) {
        if (msg == null){
            msg = i18n.getMessage("disconnected");
        }
        AppStore.getContactList().makeAllUsersOffline();
        AppStore.getInfoTicker().setMessage(msg, true);
    }

    public void actionPerformed() {
        AppStore.getChatUI().setUser(AppStore.getContactList().getUserAt(getSelectedIndex()));
        GUIStore.getManager().push(AppStore.getChatUI());

    }

    public void onConnFailed(String msg) {
    	if (Constants.LOGGING) {
    		Log.info("CLUI.onConnFailed(" + msg + ") called");
    	}
        connectionTerminated(msg);
    }

    public void onBind(String responseJid) {
        profile().setFullJID(responseJid);
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

    public void onVersion() {
    }
    
}
