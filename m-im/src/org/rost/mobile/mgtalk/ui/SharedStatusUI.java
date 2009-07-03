/*
 * SharedStatusUI.java
 *
 * Created on 20 Ноябрь 2006 г., 18:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import java.util.Vector;

import org.rost.mobile.guilib.components.CheckBoxItem;
import org.rost.mobile.guilib.components.ReadOnlyTextItem;
import org.rost.mobile.guilib.components.TextBoxItem;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.ItemActionListener;
import org.rost.mobile.guilib.core.RadioGroup;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;

/**
 *
 * @author Kostya
 */
public class SharedStatusUI extends SelectableList implements ItemActionListener {

    /** Creates a new instance of SharedStatusUI */
    TextBoxItem status;
    RadioGroup radio;

    public SharedStatusUI() {
        status = new TextBoxItem("Set custom status here:");
        setLeftCommand(i18n.getMessage("button_set"));
        setRightCommand(i18n.getMessage("button_close"));
        setCaption(i18n.getMessage("caption_status"));
    }
    String setCustom = i18n.getMessage("set_custom");
    String strDefault = i18n.getMessage("default");
    CheckBoxItem defaultOnline = new CheckBoxItem(strDefault, true);
    CheckBoxItem customOnline = new CheckBoxItem(setCustom, true);
    CheckBoxItem defaultBusy = new CheckBoxItem(strDefault, true);
    CheckBoxItem customBusy = new CheckBoxItem(setCustom, true);
    CheckBoxItem defaultAway = new CheckBoxItem(strDefault, true);
    CheckBoxItem customAway = new CheckBoxItem(setCustom, true);

    void generateRadioFromVector(Vector v, int statusID) {
        for (int i = 0; i < v.size(); i++) {
            CheckBoxItem item = new CheckBoxItem(v.elementAt(i).toString(), true);
            if (AppStore.getSelectedProfile().getStatusID() == statusID &&
                    v.elementAt(i).toString().equals(AppStore.getSelectedProfile().getStatus())) {
                item.setSelected(true);
            }
            radio.addItem(item);
            addItem(item);
        }
    }

    public void refreshView() {
        clear();
        addItem(status);
        radio = new RadioGroup();
        status.setValue("");
        if (AppStore.getSelectedProfile().isStatusActive()) {
            status.setValue(AppStore.getSelectedProfile().getStatus());
        }
        ReadOnlyTextItem online = new ReadOnlyTextItem();
        online.addImage(AppStore.STATUS_ONLINE);
        online.addText("Available");
        addItem(online);

        if (AppStore.getSelectedProfile().isGoogle()) {

            radio.addItem(defaultOnline);
            addItem(defaultOnline);
        }
        radio.addItem(customOnline);
        addItem(customOnline);
        generateRadioFromVector(AppStore.getSharedStatus().getOnlineList(), 0);

        ReadOnlyTextItem busy = new ReadOnlyTextItem();
        busy.addImage(AppStore.STATUS_BUSY);
        busy.addText("Busy");
        addItem(busy);

        if (AppStore.getSelectedProfile().isGoogle()) {

            radio.addItem(defaultBusy);
            addItem(defaultBusy);
        }

        radio.addItem(customBusy);
        addItem(customBusy);
        generateRadioFromVector(AppStore.getSharedStatus().getBusyList(), 3);

        ReadOnlyTextItem away = new ReadOnlyTextItem();
        away.addImage(AppStore.STATUS_AWAY);
        away.addText("Idle");
        addItem(away);

        if (AppStore.getSelectedProfile().isGoogle()) {

            radio.addItem(defaultAway);
            addItem(defaultAway);
        }
        radio.addItem(customAway);
        addItem(customAway);
        generateRadioFromVector(AppStore.getSharedStatus().getAwayList(), 1);

        if (AppStore.getSelectedProfile().isGoogle()) {
            if (AppStore.getSelectedProfile().getStatus().equals("")) {
                switch (AppStore.getSelectedProfile().getStatusID()) {
                    case 0:
                        defaultOnline.setSelected(true);
                        break;
                    case 1:
                        defaultAway.setSelected(true);
                        break;
                    case 3:
                        defaultBusy.setSelected(true);
                        break;
                }
            }
        }

    }

    public boolean isMain() {
        return true;
    }

    void modifyStatus() {
        if (defaultOnline.isSelected()) {
            AppStore.getSelectedProfile().setStatus("");
            AppStore.getSelectedProfile().setStatusID(0);
            return;
        }
        if (defaultBusy.isSelected()) {
            AppStore.getSelectedProfile().setStatus("");
            AppStore.getSelectedProfile().setStatusID(3);
            return;
        }
        if (defaultAway.isSelected()) {
            AppStore.getSelectedProfile().setStatus("");
            AppStore.getSelectedProfile().setStatusID(1);
            return;
        }
        if (customOnline.isSelected()) {
            AppStore.getSelectedProfile().setStatus(status.getValue().toString());
            AppStore.getSelectedProfile().setStatusID(0);
            AppStore.getSharedStatus().addToOnlineList(status.getValue().toString());
            return;
        }
        if (customAway.isSelected()) {
            AppStore.getSelectedProfile().setStatus(status.getValue().toString());
            AppStore.getSelectedProfile().setStatusID(1);
            AppStore.getSharedStatus().addToAwayList(status.getValue().toString());
            return;
        }
        if (customBusy.isSelected()) {
            AppStore.getSelectedProfile().setStatus(status.getValue().toString());
            AppStore.getSelectedProfile().setStatusID(3);
            AppStore.getSharedStatus().addToBusyList(status.getValue().toString());
            return;
        }
        if (radio.getValue() < 2 + AppStore.getSharedStatus().getOnlineList().size()) {
            AppStore.getSelectedProfile().setStatus(AppStore.getSharedStatus().getOnlineList().elementAt(radio.getValue() - 2).toString());
            AppStore.getSelectedProfile().setStatusID(0);
            return;
        }
        if (radio.getValue() < 4 + AppStore.getSharedStatus().getOnlineList().size() +
                AppStore.getSharedStatus().getBusyList().size()) {
            AppStore.getSelectedProfile().setStatus(AppStore.getSharedStatus().getBusyList().elementAt(radio.getValue() - 4 -
                    AppStore.getSharedStatus().getOnlineList().size()).toString());
            AppStore.getSelectedProfile().setStatusID(3);
            return;
        }
        AppStore.getSelectedProfile().setStatus(AppStore.getSharedStatus().getAwayList().elementAt(radio.getValue() - 6 -
                AppStore.getSharedStatus().getOnlineList().size() -
                AppStore.getSharedStatus().getBusyList().size()).toString());
        AppStore.getSelectedProfile().setStatusID(1);
    }

    public boolean leftCommandClick() {
        //Select info from form
        if (AppStore.getSelectedProfile().isGoogle()) {
            modifyStatus();
            AppStore.getSharedStatus().refreshPresenceStatus();
        }
        rightCommandClick();
        return true;
    }


    public boolean rightCommandClick() {
        GUIStore.getManager().push(AppStore.getContactListUI());
        GUIStore.getManager().notifyChanged();
        return true;
    }

    public void actionPerformed() {
    }
}
