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

	private Profile profile = null;

    private TextBoxItem name, googlemail;
    private PasswordItem password;
    private CheckBoxItem advancedOptions, security, connectAtStartup, showOfflineContacts;

    private boolean advancedSet = false;
    
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

        advancedOptions = new CheckBoxItem(i18n.getMessage("profile_wizard_advanced_options"));
        advancedOptions.setSelected(false);
        addItem(advancedOptions);
        
        security = new CheckBoxItem(i18n.getMessage("profile_wizard_security"));
        if (AppStore.isS60()) {
        	security.setSelected(true);
        }
        
        connectAtStartup = new CheckBoxItem(i18n.getMessage("profile_wizard_autoconnect"));
        connectAtStartup.setSelected(false);

        showOfflineContacts = new CheckBoxItem(i18n.getMessage("profile_show_offline"));
        showOfflineContacts.setSelected(false);

    }

    public void refreshView() {
        if (advancedOptions.isSelected()) {
        	advancedSet = true;
            addItem(security);
            addItem(connectAtStartup);
            addItem(showOfflineContacts);
        }
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
    	if (advancedOptions.isSelected() && !advancedSet) {
    		// First time save was pressed with advanced options enabled... refresh the UI
    		refreshView();
    		return false;
    	}
    	// the user really wants to save the profile, so continue here...
    	
    	// If the profile is invalid (incorrect username or missing password), warn them..
    	if (!validates()) {
    		AppStore.errorBuzz();
    		return false;
    	}
    	
    	// All ok, so save it!
        profile.setName(i18n.getMessage("profile_name_google"));
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

    private boolean validates() {
    	boolean validEntries = true;
    	String gmailaddress = (String) googlemail.getValue();
    	// If default value, null/zero string or missing @ sign, that's a problem!
    	if (gmailaddress == null || "@gmail.com".equals(gmailaddress) || gmailaddress.length() == 0 || gmailaddress.indexOf("@") == -1) {
    		validEntries = false;
    	}
    	String pw = (String) password.getValue();
    	// Anything except a blank password should be ok
    	if (pw == null || pw.length() == 0) {
    		validEntries = false;
    	}
    	return validEntries;
    }
    
}
