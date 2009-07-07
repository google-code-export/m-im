/*
 * AppStore.java
 *
 * Created on November 14, 2006, 5:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;

import net.sourceforge.jxa.Jxa;

import org.rost.mobile.guilib.core.Constants;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.GlobalPrefs;
import org.rost.mobile.mgtalk.model.Profile;
import org.rost.mobile.mgtalk.model.ProfileList;
import org.rost.mobile.mgtalk.model.SharedStatus;
import org.rost.mobile.mgtalk.model.UserList;
import org.rost.mobile.mgtalk.ui.ChatUI;
import org.rost.mobile.mgtalk.ui.ContactListUI;
import org.rost.mobile.mgtalk.ui.ErrorMessage;
import org.rost.mobile.mgtalk.ui.GlobalPrefsUI;
import org.rost.mobile.mgtalk.ui.InfoTicker;
import org.rost.mobile.mgtalk.ui.ProfileListUI;
import org.rost.mobile.mgtalk.ui.ProfileUI;
import org.rost.mobile.mgtalk.ui.SharedStatusUI;

import com.google.code.mim.Log;
import com.google.code.mim.XmppPinger;
import javax.microedition.media.MediaException;

/**
 *
 * @author kostya
 */
public class AppStore {

	protected static GlobalPrefs globalPrefs = null;
	protected static GlobalPrefsUI globalPrefsUI = null;
    protected static ProfileList profileList = null;
    protected static ProfileListUI profileListUI = null;
    protected static ProfileUI profileUI = null;
    protected static ErrorMessage errorMessage = null;
    static Profile selectedProfile = null;
    //protected static NetworkDispatcher networkDispatcher = null;
    protected static Jxa jxa = null;
    static InfoTicker infoTicker = null;
    protected static ContactListUI contactListUI = null;
    static UserList contactList = null;
    protected static ChatUI chatUI = null;
    protected static SharedStatusUI sharedStatusUI = null;
    protected static int incomingMessageColor = 0xff0000;
    protected static int outgoingMessageColor = 0x00ff00;
    public static Image STATUS_ONLINE = null;
    public static Image STATUS_AWAY = null;
    public static Image STATUS_NA = null;
    public static Image STATUS_BUSY = null;
    public static Image STATUS_OFFLINE = null;
    static String IMAGE_PREFIX = "/org/rost/mobile/mgtalk/ui/res/";
    public static SharedStatus sharedStatus = new SharedStatus();

    private static Player PLAYER = null; //Player for playing sound
    
    private static XmppPinger pinger = null; // initialise only when required
    private static Thread pingThread = null; // initialise only when required
    
    public static final boolean S60;
    public static final boolean S40;
    
    public static boolean showWizard = true;
    
    static {
    	// This is about the best detection routine I've found so far..
    	boolean isS60 = false;
    	boolean isS40 = true;
    	try{
    		Class.forName("com.symbian.gcf.NativeInputStream");
    		isS60 = true;
    		isS40 = false;
    	}catch(Exception e){
    	}
    	S60 = isS60;
    	S40 = isS40;
    }
    
    public static synchronized void startPinger() {
    	if (pinger == null) {
    		pinger = new XmppPinger();
    	}
    	if (pingThread == null) {
    		pingThread = new Thread(pinger);
    	}
    	if (!pinger.isRunning()) {
    		pinger.setRunning(true);
    	}
    	if (!pingThread.isAlive()) {
    		pingThread.start();
    	}
    }
    
    public static synchronized void stopPinger() {
    	if (pinger != null) {
    		pinger.setRunning(false);
    	}
    	try {
			pingThread.join();
			pingThread = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /** Creates a new instance of AppStore */
    public static void initApp() {
    	if (Constants.LOGGING) {
    		Log.info("initApp");
    	}
        try {
            STATUS_ONLINE = Image.createImage(IMAGE_PREFIX + "status_avail.png");
            STATUS_AWAY = Image.createImage(IMAGE_PREFIX + "status_idle.png");
            STATUS_NA = Image.createImage(IMAGE_PREFIX + "status_xa.png");
            STATUS_BUSY = Image.createImage(IMAGE_PREFIX + "status_busy.png");
            STATUS_OFFLINE = Image.createImage(IMAGE_PREFIX + "status_offline.png");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        i18n.initLocalizationSupport();
        
        globalPrefs = new GlobalPrefs();
        globalPrefs.load();
        try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        globalPrefsUI = new GlobalPrefsUI();
        
        profileList = new ProfileList();
        profileListUI = new ProfileListUI();
        infoTicker = new InfoTicker();
        GUIStore.getManager().pushFront(infoTicker);

        contactList = new UserList();
        contactListUI = new ContactListUI();
        chatUI = new ChatUI();
        sharedStatusUI = new SharedStatusUI();
    }

    public static ProfileList getProfileList() {
        return profileList;
    }

    public static ProfileListUI getProfileListUI() {
        return profileListUI;
    }

    public static ProfileUI getProfileUI() {
        if (profileUI == null) {
            profileUI = new ProfileUI();
        }
        return profileUI;
    }

    public static ErrorMessage getErrorMessage() {
        if (errorMessage == null) {
            errorMessage = new ErrorMessage();
        }
        return errorMessage;
    }

    public static Profile getSelectedProfile() {
        return selectedProfile;
    }

    public static void setJxa(Jxa jxa) {
        AppStore.jxa = jxa;
    }

    public static void setSelectedProfile(Profile aSelectedProfile) {
        if (!aSelectedProfile.equals(selectedProfile)) {
            getContactList().clear();
            getContactListUI().clear();
        }
        selectedProfile = aSelectedProfile;
    }

    public static Jxa getJxa() {
        return jxa;
    }

    public static InfoTicker getInfoTicker() {
        return infoTicker;
    }

	public static GlobalPrefs getGlobalPrefs() {
		return globalPrefs;
	}

	public static GlobalPrefsUI getGlobalPrefsUI() {
		return globalPrefsUI;
	}

    public static ContactListUI getContactListUI() {
        return contactListUI;
    }

    public static UserList getContactList() {
        return contactList;
    }

    public static ChatUI getChatUI() {
        return chatUI;
    }

    public static int getIncomingMessageColor() {
        return incomingMessageColor;
    }

    public static int getOutgoingMessageColor() {
        return outgoingMessageColor;
    }

    public static void notifyMessage() {
    	if (globalPrefs.isSoundEnabled()) {
    		playMessage();
    	}
    	if (globalPrefs.isVibrate() && getSelectedProfile().isVibrate()) {
    		vibrate();
    	}
    }
    
    /**
     * This routine plays message
     */
    public static void playMessage() {
        try {
            if (PLAYER == null) {
                //p = Manager.createPlayer(new Object().getClass().getResourceAsStream(IMAGE_PREFIX + "ring.mid"), "audio/midi");
            	try {
                    PLAYER = Manager.createPlayer(new Object().getClass().getResourceAsStream(IMAGE_PREFIX + "pidgin-receive.amr"), "audio/amr");
                } catch (MediaException e) {
                    PLAYER = Manager.createPlayer(new Object().getClass().getResourceAsStream(IMAGE_PREFIX + "ring.mid"), "audio/midi");
                }
            }
            if (PLAYER.getState() != Player.STARTED) {
            	PLAYER.realize();
                Control cs[];
                cs = PLAYER.getControls();
                for (int i = 0; i < cs.length; i++) {
                    if (cs[i] instanceof VolumeControl) {
                        try {
                            if (((VolumeControl) cs[i]).getLevel() != getSelectedProfile().getVolume() * 10) {
                                ((VolumeControl) cs[i]).setLevel(getSelectedProfile().getVolume() * 10);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                PLAYER.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void legacyBeep() {
    	AlertType.ERROR.playSound(GUIStore.getDisplay()); 
    }

    public static void close() {
    	// Shutdown the sound player
    	if (PLAYER != null ) {
    		PLAYER.close();
    	}
    }
    
    public static String getJadProperty(String key) {
    	return Starter.getMidlet().getAppProperty(key); 
    }
    
    public static void vibrate() {
    	//GUIStore.getDisplay().vibrate(800);
    	GUIStore.getDisplay().vibrate(AppStore.getSelectedProfile().getVibrateTime() * 100);
    }
    
    public static SharedStatusUI getSharedStatusUI() {
        return sharedStatusUI;
    }

    public static void initJxa() {
        if (jxa != null) {
            jxa.close();
        }
        final Profile profile = getSelectedProfile();
        if (Constants.LOGGING) {
        	Log.info("initialising Jxa...");
        }
        jxa = new Jxa(profile.getFullJID(), profile.getPassword(), profile.getResource(), 10, profile.getHost(), profile.getPort(), profile.isSSL());
    }
    
    public static boolean isS60() {
		return S60;
	}

	public static boolean isS40() {
		return S40;
	}

	public static SharedStatus getSharedStatus() {
        return sharedStatus;
    }

}
