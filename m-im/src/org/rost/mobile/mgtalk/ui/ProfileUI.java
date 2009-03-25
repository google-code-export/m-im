/*
 * ProfileUI.java
 *
 * Created on November 14, 2006, 9:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import org.rost.mobile.guilib.components.CheckBoxItem;
import org.rost.mobile.guilib.components.PasswordItem;
import org.rost.mobile.guilib.components.ReadOnlyTextItem;
import org.rost.mobile.guilib.components.TextBoxItem;
import org.rost.mobile.guilib.components.TrackItem;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.RadioGroup;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.Profile;

/**
 *
 * @author kostya
 */
public class ProfileUI extends SelectableList {

    /** Creates a new instance of ProfileUI */
    Profile profile = null;
    TextBoxItem name, userName, host, port, status, historyLength;
    PasswordItem password;
    CheckBoxItem autoConnect, autoReconnect, google, moveChattersTop, showOffline, smiles, SSL, statusActive;
    TrackItem volume;
    CheckBoxItem statusOnline, statusAway, statusNA, statusBusy, sortByName, sortByStatus;
    RadioGroup statusGroup, sortByGroup;

    public ProfileUI() {
        setLeftCommand(i18n.getMessage("save"));
        setRightCommand(i18n.getMessage("close"));

        name = new TextBoxItem(i18n.getMessage("profile_name"));
        addItem(name);

        userName = new TextBoxItem(i18n.getMessage("profile_username"));
        addItem(userName);

        password = new PasswordItem(i18n.getMessage("profile_password"));
        addItem(password);

        host = new TextBoxItem(i18n.getMessage("profile_host"));
        addItem(host);

        port = new TextBoxItem(i18n.getMessage("profile_port"));
        addItem(port);

        SSL = new CheckBoxItem(i18n.getMessage("profile_SSL"));
        addItem(SSL);

        status = new TextBoxItem(i18n.getMessage("profile_status"));
        addItem(status);

        statusActive = new CheckBoxItem(i18n.getMessage("profile_status_active"));
        addItem(statusActive);

        ReadOnlyTextItem statusText = new ReadOnlyTextItem();
        statusText.addText(i18n.getMessage("profile_status_list"));
        addItem(statusText);

        statusOnline = new CheckBoxItem(i18n.getMessage("status_online"), true);
        addItem(statusOnline);

        statusAway = new CheckBoxItem(i18n.getMessage("status_away"), true);
        addItem(statusAway);

        statusNA = new CheckBoxItem(i18n.getMessage("status_na"), true);
        addItem(statusNA);

        statusBusy = new CheckBoxItem(i18n.getMessage("status_busy"), true);
        addItem(statusBusy);

        statusGroup = new RadioGroup();
        statusGroup.addItem(statusOnline);
        statusGroup.addItem(statusAway);
        statusGroup.addItem(statusNA);
        statusGroup.addItem(statusBusy);

        google = new CheckBoxItem(i18n.getMessage("profile_google"));
        addItem(google);

        statusText = new ReadOnlyTextItem();
        statusText.addText(i18n.getMessage("profile_sort_by"));
        addItem(statusText);

        sortByStatus = new CheckBoxItem(i18n.getMessage("profile_sort_by_status"), true);
        addItem(sortByStatus);

        sortByName = new CheckBoxItem(i18n.getMessage("profile_sort_by_name"), true);
        addItem(sortByName);

        sortByGroup = new RadioGroup();
        sortByGroup.addItem(sortByStatus);
        sortByGroup.addItem(sortByName);

        moveChattersTop = new CheckBoxItem(i18n.getMessage("profile_chatters_top"));
        addItem(moveChattersTop);

        showOffline = new CheckBoxItem(i18n.getMessage("profile_show_offline"));
        addItem(showOffline);

        autoConnect = new CheckBoxItem(i18n.getMessage("profile_auto_connect"));
        addItem(autoConnect);

        autoReconnect = new CheckBoxItem(i18n.getMessage("profile_auto_reconnect"));
        addItem(autoReconnect);

        smiles = new CheckBoxItem(i18n.getMessage("profile_smiles"));
        addItem(smiles);

        historyLength = new TextBoxItem(i18n.getMessage("profile_history_length"));
        addItem(historyLength);

        volume = new TrackItem();
        volume.getCaption().addText(i18n.getMessage("profile_volume"));
        addItem(volume);

    }

    void setProfile(Profile p) {
        profile = p;
    }

    public void refreshView() {
        currentSelected = 0;
        startShowing = 0;

        name.setValue(profile.getName());
        userName.setValue(profile.getUserName());
        password.setValue(profile.getPassword());
        host.setValue(profile.getHost());
        port.setValue(profile.getPort());
        SSL.setSelected(profile.isSSL());
        status.setValue(profile.getStatus());
        statusActive.setSelected(profile.isStatusActive());
        statusGroup.setValue(profile.getStatusID());
        google.setSelected(profile.isGoogle());
        sortByGroup.setValue(profile.isSortByName() ? 1 : 0);
        moveChattersTop.setSelected(profile.isMoveChattersTop());
        showOffline.setSelected(profile.isShowOffline());
        autoConnect.setSelected(profile.isAutoConnect());
        autoReconnect.setSelected(profile.isAutoReconnect());
        smiles.setSelected(profile.isSmiles());
        historyLength.setValue("" + profile.getHistoryLength());
        volume.setValues(10, profile.getVolume());
    }

    public boolean isMain() {
        return true;
    }

    public boolean rightCommandClick() {
        GUIStore.getManager().push(AppStore.getProfileListUI());
        GUIStore.getManager().notifyChanged();
        return true;
    }

    public boolean leftCommandClick() {
        if (name.getValue().toString().equals("")) {
            AppStore.getErrorMessage().showErrorMessage(i18n.getMessage("profile_error"));
            return true;
        }
        profile.setName((String) name.getValue());
        profile.setUserName((String) userName.getValue());
        profile.setPassword((String) password.getValue());
        profile.setHost((String) host.getValue());
        profile.setPort(port.getValue().toString());
        profile.setSSL(SSL.isSelected());
        profile.setStatus(status.getValue().toString());
        profile.setStatusActive(statusActive.isSelected());
        profile.setStatusID(statusGroup.getValue());
        profile.setGoogle(google.isSelected());
        profile.setSortByName(sortByGroup.getValue() == 1);
        profile.setMoveChattersTop(moveChattersTop.isSelected());
        profile.setShowOffline(showOffline.isSelected());
        profile.setAutoConnect(autoConnect.isSelected());
        profile.setAutoReconnect(autoReconnect.isSelected());
        profile.setSmiles(smiles.isSelected());
        profile.setHistoryLength(Integer.parseInt(historyLength.getValue().toString()));
        profile.setVolume(Integer.parseInt(volume.getValue().toString()));
        if (profile.getId() == -1) {
            AppStore.getProfileList().addProfile(profile);
        } else {
            AppStore.getProfileList().saveProfile(profile);
        }
        return rightCommandClick();
    }
}
