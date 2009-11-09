package org.rost.mobile.mgtalk.ui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;

import org.rost.mobile.guilib.components.CheckBoxItem;
import org.rost.mobile.guilib.components.TextBoxItem;
import org.rost.mobile.guilib.components.TrackItem;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.LayerInterface;
import org.rost.mobile.guilib.core.LayerManager;
import org.rost.mobile.mgtalk.AppStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.GlobalPrefs;
import org.rost.mobile.mgtalk.model.MIMConstants;

public class GlobalPrefsUI extends SelectableList {

	private GlobalPrefs prefs = null;

	private TextBoxItem displayName;
	private CheckBoxItem soundEnabled, vibrate;
	private TrackItem volume, vibrationTime;
	
	private LayerInterface backToInterface;
	
	public GlobalPrefsUI() {
		super();
		prefs = AppStore.getGlobalPrefs();
		
        setLeftCommand(i18n.getMessage("save"));
        setRightCommand(i18n.getMessage("close"));

        displayName = new TextBoxItem(i18n.getMessage("profile_displayname"));
        addItem(displayName);

        soundEnabled = new CheckBoxItem(i18n.getMessage("profile_soundenabled"));
        addItem(soundEnabled);

        volume = new TrackItem();
        volume.getCaption().addText(i18n.getMessage("profile_volume"));
        addItem(volume);

        vibrate = new CheckBoxItem(i18n.getMessage("profile_vibrate"));
        addItem(vibrate);
        
        vibrationTime = new TrackItem();
        vibrationTime.getCaption().addText(i18n.getMessage("profile_vibration_time"));
        vibrationTime.setValues(20, 8); // 20 * 100ms = 2seconds max, 800ms default
        addItem(vibrationTime);
        
        //FIXME - this needs implementing properly!
		Form f = new Form("Language");
		String[] availableLocales = new String[] { "cn", "en" };
	    String[] langlist = { "Chinese", "English" };
		String locale = "en"; // English by default, but this will be changed at Runtime by auto-detection / preferences

        ChoiceGroup langSelector = new ChoiceGroup("Language" + ":", ChoiceGroup.EXCLUSIVE, langlist, null);
        for (int i = 0; i < availableLocales.length; i++) {
			if (locale != null && locale.equals(availableLocales[i])) {
		        langSelector.setSelectedIndex(i, true);
			}
		}
        f.append(langSelector);
//        f.addCommand(backCommand);
//		f.addCommand(languageSaveCommand);
//        f.setItemStateListener(this);
//		f.setCommandListener(this);
//        Display.getDisplay(this).setCurrent(f);

	}
	
	public GlobalPrefs getPrefs() {
		return prefs;
	}

    public void refreshView() {
        setCaption(i18n.getMessage("title_globalprefs"));
        displayName.setValue(prefs.getDisplayName());
        soundEnabled.setSelected(prefs.isSoundEnabled());
        volume.setValues(MIMConstants.MAX_VOLUME, prefs.getVolume());
        vibrate.setSelected(prefs.isVibrate());
        vibrationTime.setValues(MIMConstants.MAX_VIBRATE_TIME, prefs.getVibrateTime());
    }

    public boolean isMain() {
        return true;
    }

    public boolean rightCommandClick() {
    	LayerManager manager = GUIStore.getManager();
    	if (this.backToInterface != null) {
    		manager.push(this.backToInterface);
    	} else {
	        manager.push(AppStore.getProfileListUI());
    	}
		manager.notifyChanged();
        return true;
    }

    public boolean leftCommandClick() {
    	prefs.setDisplayName((String) displayName.getValue());        
    	
    	prefs.setSoundEnabled(soundEnabled.isSelected());
    	prefs.setVolume(Integer.parseInt(volume.getValue().toString()));
    	prefs.setVibrate(vibrate.isSelected());
    	prefs.setVibrateTime(Integer.parseInt(vibrationTime.getValue().toString()));

    	prefs.save();
        return rightCommandClick();
    }

	public LayerInterface getBackToInterface() {
		return backToInterface;
	}

	public void setBackToInterface(LayerInterface backToInterface) {
		this.backToInterface = backToInterface;
	}

}
