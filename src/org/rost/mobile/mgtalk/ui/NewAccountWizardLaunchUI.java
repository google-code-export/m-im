package org.rost.mobile.mgtalk.ui;

import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.Constants;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.Profile;

public class NewAccountWizardLaunchUI extends SelectableList {

	public NewAccountWizardLaunchUI() {
		setCaption(i18n.getMessage("title_welcome_wizard"));
		
        setLeftCommand(i18n.getMessage("choice_yes"));
        setRightCommand(i18n.getMessage("choice_no"));

        StaticRichText text = new StaticRichText(Constants.screenWidth);
        text.addText(i18n.getMessage("wizard_instructions"));
        addItem(text);
	}
	
    public boolean isMain() {
        return true;
    }

    public boolean rightCommandClick() {
    	AppStore.showWizard = false;
        GUIStore.getManager().push(AppStore.getProfileListUI());
        GUIStore.getManager().notifyChanged();
        return true;
    }

    public boolean leftCommandClick() {
        Profile profile = new Profile(true);
        AppStore.getProfileUI().setProfile(profile);
        NewAccountWizardUI ui = new NewAccountWizardUI();
        ui.setProfile(profile);
    	GUIStore.getManager().push(ui);
    	GUIStore.getManager().notifyChanged();
    	return true;
    }
    
}
