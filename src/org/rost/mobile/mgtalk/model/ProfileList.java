/*
 * ProfileList.java
 *
 * Created on November 14, 2006, 5:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.model;

import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

/**
 *
 * @author kostya
 */
public class ProfileList {

    static String STORE_NAME = "MGTalk";
    /** Creates a new instance of ProfileList */
    Vector profiles = new Vector();
    protected int autoProfile = -1;

    public ProfileList() {
    }

    public void refreshList() {
        profiles.removeAllElements();
        autoProfile = -1;
        try {
            RecordStore store = RecordStore.openRecordStore(STORE_NAME, true);
            for (RecordEnumeration e = store.enumerateRecords(null, null, false); e.hasNextElement();) {
                int id = e.nextRecordId();
                Profile p = new Profile(false);
                p.setId(id);
                p.fromByteArray(store.getRecord(id));
                profiles.addElement(p);
                if (p.isAutoConnect()) {
                    autoProfile = profiles.size() - 1;
                }
            }
            store.closeRecordStore();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public int getProfilesCount() {
        return profiles.size();
    }

    public Profile getProfileAt(int i) {
        return (Profile) profiles.elementAt(i);
    }

    public String getProfileNameAt(int i) {
        return ((Profile) profiles.elementAt(i)).getName();
    }

    void setAutoConnectProfile() {
        for (int i = 0; i < profiles.size(); i++) {
            Profile profile = (Profile) profiles.elementAt(i);
            if (profile.isAutoConnect()) {
                profile.setAutoConnect(false);
                saveProfile(i, false);
            }
        }
    }

    public void addProfile(Profile p) {
        try {
            if (p.isAutoConnect()) {
                setAutoConnectProfile();
            }
            RecordStore store = RecordStore.openRecordStore(STORE_NAME, true);
            store.addRecord(p.toByteArray(), 0, 512);
            store.closeRecordStore();
            refreshList();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void deleteProfile(int i) {
        try {
            RecordStore store = RecordStore.openRecordStore(STORE_NAME, true);
            store.deleteRecord(((Profile) profiles.elementAt(i)).getId());
            store.closeRecordStore();
            refreshList();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void saveProfile(Profile p) {
        if (profiles.indexOf(p) != -1) {
            saveProfile(profiles.indexOf(p));
        }
    }

    public void saveProfile(int i) {
        saveProfile(i, true);
    }

    public void saveProfile(int i, boolean refresh) {
        try {
            RecordStore store = RecordStore.openRecordStore(STORE_NAME, true);
            Profile p = (Profile) profiles.elementAt(i);
            if (p.isAutoConnect()) {
                setAutoConnectProfile();
                p.setAutoConnect(true);
            }
            store.setRecord(p.getId(), p.toByteArray(), 0, 512);
            store.closeRecordStore();
            if (refresh) {
                refreshList();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public int getAutoProfile() {
        return autoProfile;
    }
}
