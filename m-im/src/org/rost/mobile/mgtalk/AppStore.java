/*
 * AppStore.java
 *
 * Created on November 14, 2006, 5:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk;

import javax.microedition.lcdui.Image;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import net.sourceforge.jxa.Jxa;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.mgtalk.controllers.NetworkDispatcher;
import org.rost.mobile.mgtalk.i18n.i18n;
import org.rost.mobile.mgtalk.model.Profile;
import org.rost.mobile.mgtalk.model.ProfileList;
import org.rost.mobile.mgtalk.model.SharedStatus;
import org.rost.mobile.mgtalk.model.UserList;
import org.rost.mobile.mgtalk.ui.ChatUI;
import org.rost.mobile.mgtalk.ui.ContactListUI;
import org.rost.mobile.mgtalk.ui.ErrorMessage;
import org.rost.mobile.mgtalk.ui.InfoTicker;
import org.rost.mobile.mgtalk.ui.ProfileListUI;
import org.rost.mobile.mgtalk.ui.ProfileUI;
import org.rost.mobile.mgtalk.ui.SharedStatusUI;

/**
 *
 * @author kostya
 */
public class AppStore {

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

    public static SharedStatus getSharedStatus() {
        return sharedStatus;
    }

    /** Creates a new instance of AppStore */
    public static void initApp() {
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
    static Player p = null;//Player for playing sound

    /**
     * This routine plays message
     */
    public static void playMessage() {
        try {
            if (p == null) {
                p = Manager.createPlayer(new Object().getClass().getResourceAsStream(IMAGE_PREFIX + "ring.mid"), "audio/midi");
            }
            if (p.getState() != Player.STARTED) {
                p.realize();
                Control cs[];
                cs = p.getControls();
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
                p.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SharedStatusUI getSharedStatusUI() {
        return sharedStatusUI;
    }

    public static void initJxa() {
        if (jxa != null) {
            jxa.close();
        }
        final Profile profile = getSelectedProfile();
        jxa = new Jxa(profile.getFullJID(), profile.getPassword(), "Mobile", 10, profile.getHost(), profile.getPort(), profile.isSSL());
    }
}
