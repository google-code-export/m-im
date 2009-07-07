package org.rost.mobile.mgtalk.ui;

import org.rost.mobile.guilib.components.CheckBoxItem;
import org.rost.mobile.guilib.components.PasswordItem;
import org.rost.mobile.guilib.components.TextBoxItem;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.Profile;

public class NewAccountWizardUI extends SelectableList {

	Profile profile = null;

    TextBoxItem name, googlemail;
    PasswordItem password;
    CheckBoxItem security, connectAtStartup, showOfflineContacts;

    public NewAccountWizardUI() {
		setCaption(i18n.getMessage("title_wizard"));

    	setLeftCommand(i18n.getMessage("save"));
    	setRightCommand(i18n.getMessage("close"));
    
        name = new TextBoxItem(i18n.getMessage("profile_wizard_name"));
        addItem(name);

        googlemail = new TextBoxItem(i18n.getMessage("profile_googlemail"));
        googlemail.setValue("@gmail.com");
        addItem(googlemail);

        password = new PasswordItem(i18n.getMessage("profile_password"));
        addItem(password);

        security = new CheckBoxItem(i18n.getMessage("profile_wizard_security"));
        security.setSelected(true);
        addItem(security);

        connectAtStartup = new CheckBoxItem(i18n.getMessage("profile_wizard_autoconnect"));
        connectAtStartup.setSelected(false);
        addItem(connectAtStartup);

        showOfflineContacts = new CheckBoxItem(i18n.getMessage("profile_show_offline"));
        showOfflineContacts.setSelected(false);
        addItem(showOfflineContacts);
    }

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
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
        profile.setName("Google");
        profile.setUserName((String) googlemail.getValue());
        String displayName = (String) name.getValue();
        if (displayName == null || displayName.length() <= 0) {
        	displayName = (String) googlemail.getValue();
        }
        profile.setDisplayName(displayName);
        profile.setPassword((String) password.getValue());
        profile.setHost("talk.google.com");
        profile.setGoogle(true);
        if (security.isSelected()) {
            profile.setPort("5223");
            profile.setSSL(true);        	
        } else {
        	profile.setPort("5222");
        	profile.setSSL(false);
        }

        if (connectAtStartup.isSelected()) {
            profile.setAutoConnect(true);
        } else {
            profile.setAutoConnect(false);
        }
                
        AppStore.getProfileList().addProfile(profile);
        return rightCommandClick();
    }

}
