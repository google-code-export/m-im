/*
 * ProfileListUI.java
 *
 * Created on 14 Ноябрь 2006 г., 19:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import org.rost.mobile.guilib.components.MenuItem;
import org.rost.mobile.guilib.components.OneLineItem;
import org.rost.mobile.guilib.components.layers.Menu;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.BaseMidlet;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.ItemActionListener;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.Profile;

/**
 *
 * @author Kostya
 */
public class ProfileListUI extends SelectableList implements ItemActionListener {

    /** Creates a new instance of ProfileListUI */
    Menu menu;

    public ProfileListUI() {
        menu = new Menu(0, i18n.getMessage("menu_select"), i18n.getMessage("menu_cancel"));

        MenuItem newItem = new MenuItem(i18n.getMessage("new"));
        newItem.setItemActionListener(new ItemActionListener() {

            public void actionPerformed() {
                //New action
                Profile profile = new Profile();
                AppStore.getProfileUI().setProfile(profile);
                GUIStore.getManager().push(AppStore.getProfileUI());
                GUIStore.getManager().notifyChanged();
            }
        });
        menu.addMenuItem(newItem);

        MenuItem editItem = new MenuItem(i18n.getMessage("edit"));
        editItem.setItemActionListener(new ItemActionListener() {

            public void actionPerformed() {
                //Edit action
                if (AppStore.getProfileList().getProfilesCount() == 0) {
                    return;
                }
                AppStore.getProfileUI().setProfile(
                        AppStore.getProfileList().getProfileAt(getSelectedIndex()));
                GUIStore.getManager().push(AppStore.getProfileUI());
                GUIStore.getManager().notifyChanged();
            }
        });
        menu.addMenuItem(editItem);

        MenuItem delItem = new MenuItem(i18n.getMessage("delete"));
        delItem.setItemActionListener(new ItemActionListener() {

            public void actionPerformed() {
                //Delete action
                if (AppStore.getProfileList().getProfilesCount() == 0) {
                    return;
                }
                AppStore.getProfileList().deleteProfile(getSelectedIndex());
                refreshView();
                notifyChanged();
            }
        });
        menu.addMenuItem(delItem);
        setLeftCommand(i18n.getMessage("menu"));
        setRightCommand(i18n.getMessage("quit"));
    }

    public void refreshView() {
        //RefreshList
        clear();
        setCaption(i18n.getMessage("title_profiles"));
        AppStore.getProfileList().refreshList();
        for (int i = 0; i < AppStore.getProfileList().getProfilesCount(); i++) {
            OneLineItem item = new OneLineItem();
            item.setText(AppStore.getProfileList().getProfileAt(i).getName());
            item.setItemActionListener(this);
            addItem(item);
        }
    }

    public boolean rightCommandClick() {
        if (AppStore.getJxa() != null) {
            AppStore.getJxa().close();
        }
        BaseMidlet.closeMIDLet();
        return true;
    }

    public boolean leftCommandClick() {
        GUIStore.getManager().pushFront(menu);
        GUIStore.getManager().notifyChanged();
        return true;
    }

    public boolean isMain() {
        return true;
    }

    public void actionPerformed() {
        AppStore.setSelectedProfile(AppStore.getProfileList().getProfileAt(getSelectedIndex()));
        AppStore.initJxa();
        AppStore.getContactListUI().init();
        AppStore.getJxa().start();

        GUIStore.getManager().push(AppStore.getContactListUI());

    }
}
