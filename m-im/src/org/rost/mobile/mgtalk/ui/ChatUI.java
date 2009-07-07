/*
 * ChatUI.java
 *
 * Created on 13 Ноябрь 2006 г., 19:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import javax.microedition.lcdui.Graphics;
import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.guilib.components.TextBoxItem;
import org.rost.mobile.guilib.components.layers.*;
import org.rost.mobile.guilib.core.Constants;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.ItemInterface;
import org.rost.mobile.guilib.core.LayerInterface;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.User;
import org.rost.mobile.mgtalk.model.UserMessageListener;
import org.rost.mobile.mgtalk.model.UserStateListener;

import com.google.code.mim.Log;

/**
 *
 * @author Kostya
 */
public class ChatUI extends LayerInterface implements UserStateListener, UserMessageListener {

    /**
     * Creates a new instance of ChatUI
     */
    TextBoxItem textBox;
    ContactListItem contactInfo;
    UnselectableList history;
    User user = null;

    public ChatUI() {
        textBox = new TextBoxItem(false); // This could be parameterised so that it's a choice whether to show status line or not!
        setLeftCommand(i18n.getMessage("button_send"));
        setRightCommand(i18n.getMessage("button_list"));
        history = new UnselectableList();
        contactInfo = new ContactListItem(AppStore.STATUS_OFFLINE, "", "", false);
    }

    public void paintCustom(Graphics g) {
        contactInfo.paint(g, GUIMisc.getActiveX(), GUIMisc.getActiveY());
        textBox.paintSelected(g, GUIMisc.getActiveX(), GUIMisc.getActiveY() + contactInfo.getHeight());
        history.setYCredentials(GUIMisc.getActiveHeight() - textBox.getHeight() - contactInfo.getHeight(),
                GUIMisc.getActiveY() + textBox.getHeight() + contactInfo.getHeight());
        history.paint(g);
    }

    public boolean rightCommandClick() {
        GUIStore.getManager().push(AppStore.getContactListUI());
        GUIStore.getManager().notifyChanged();
        return true;
    }

    public boolean leftCommandClick() {
        if (textBox.getValue().equals("")) {
            return true;
        }
        /*
        System.out.println("Sending: "+"<message to=\""+
        user.getCurrentSession()+"\" from=\"${fullJID}\" type=\"chat\">"+
        (AppStore.getSelectedProfile().isGoogle()?"<nos:x value=\"disabled\" xmlns:nos=\"google:nosave\"/>":"")+
        "<body>"+NetworkTools.toXML(textBox.getValue().toString())+"</body></message>");
        AppStore.getNetworkDispatcher().sendMessage("<message to=\""+
        user.getCurrentSession()+"\" from=\"${fullJID}\" type=\"chat\">"+
        (AppStore.getSelectedProfile().isGoogle()?"<nos:x value=\"disabled\" xmlns:nos=\"google:nosave\"/>":"")+
        "<body>"+NetworkTools.toXML(textBox.getValue().toString())+"</body></message>");
         */
        AppStore.getJxa().sendMessage(user.getCurrentSession(), textBox.getValue().toString());
        user.addMessageToHistory(textBox.getValue().toString(), user.getCurrentSession(), false);
        textBox.setValue("");
        notifyChanged();
        return true;
    }

    public boolean selectCommandClick() {
        textBox.click();
        return true;
    }

    public boolean processKeyPress(int keyCode) {
    	if (Constants.LOGGING) {
        	Log.debug("Keypress=" + keyCode);
    	}
        if (history.processKeyPress(keyCode)) {
            notifyChanged();
            return true;
        }
        if (keyCode == -3) {
            GUIStore.getManager().push(AppStore.getContactListUI());
            GUIStore.getManager().notifyChanged();
            return true;
        }
        return false;

    }

    public void setUser(User user) {
        if (!user.equals(this.user)) {
            textBox.setValue("");
        }
        this.user = user;
    }

    public void refreshView() {
        user.addUserListener(this);
        user.addMessageListener(this);
        setCaption(i18n.getMessage("title_chatui_chatwith") + " " + user.getUserName());
        //Fill history
        user.setUnreadMessages(0);
        history.clear();
        for (int i = 0; i < user.getHistory().size(); i++) {
            history.pushItemBack((ItemInterface) user.getHistory().elementAt(i));
        }
        contactInfo.setInfo(ContactListUI.statusIDToImage(user.getStatusID()), user.getUserName(), user.getStatus(), true);
    }

    public boolean stateChanged(User user) {
        contactInfo.setInfo(ContactListUI.statusIDToImage(user.getStatusID()), user.getUserName(), user.getStatus(), true);
        notifyChanged();
        return false;
    }

    public void layerRemoved() {
        user.removeUserListener(this);
        user.removeMessageListener(this);
    }

    public boolean isMain() {
        return true;
    }

    public boolean newMessageReceived(User user, StaticRichText item, boolean from) {
        if (from) {
            AppStore.notifyMessage();
        }
        if (Constants.LOGGING) {
        	Log.debug("New message!");
        }
        history.pushItemFront(item);
        history.setCurrentPosition(0);
        notifyChanged();
        return true;
    }
}
