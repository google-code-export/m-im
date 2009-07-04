/*
 * SharedStatus.java
 *
 * Created on 20 Ноябрь 2006 г., 11:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.model;

import java.util.Vector;
import net.sourceforge.jxa.Jxa;
import net.sourceforge.jxa.XmppAdapter;
import org.rost.mobile.mgtalk.AppStore;

/**
 *
 * @author Kostya
 */
public class SharedStatus extends XmppAdapter {

    /** Creates a new instance of SharedStatus */
    protected Vector onlineList = new Vector();
    protected Vector awayList = new Vector();
    protected Vector busyList = new Vector();
    String status = "";
    protected boolean firstRefresh = true;

    public SharedStatus() {
    }

    public void onSharedStatusEvent(String status, int show, Vector awayList, Vector busyList, Vector onlineList) {
        this.status = status;
        if (!firstRefresh) {
            AppStore.getSelectedProfile().setStatus(status);
            AppStore.getSelectedProfile().setStatusID(show);
            AppStore.getSelectedProfile().setStatusActive(true);
            
        }else{
            refreshPresenceStatus();
        }
        firstRefresh = false;
        this.awayList = awayList;
        this.busyList = busyList;
        this.onlineList = onlineList;
    }

    public String statusListToString(Vector v) {
        String s = "";
        for (int i = 0; i < v.size(); i++) {
            s += "<status>" + v.elementAt(i) + "</status>";
        }
        return s;
    }

    public void sendStatusStanza() {
        //StringBuffer sb = new StringBuffer();
        if (AppStore.getSelectedProfile().isStatusActive()) {
            status = AppStore.getSelectedProfile().getStatus();
        }
        String to = AppStore.getSelectedProfile().getUserName();
        String show = Jxa.statusIDtoString(AppStore.getSelectedProfile().getStatusID());
        AppStore.getJxa().sendShareStatus(to, status, show, onlineList, awayList, busyList);

    }

    public boolean isFirstRefresh() {
        return firstRefresh;
    }

    public Vector getOnlineList() {
        return onlineList;
    }

    public Vector getAwayList() {
        return awayList;
    }

    public Vector getBusyList() {
        return busyList;
    }

    public void addToOnlineList(String status) {
        if (!onlineList.contains(status)) {
            onlineList.insertElementAt(status, 0);
        }
    }

    public void addToBusyList(String status) {
        if (!busyList.contains(status)) {
            busyList.insertElementAt(status, 0);
        }
    }

    public void addToAwayList(String status) {
        if (!awayList.contains(status)) {
            awayList.insertElementAt(status, 0);
        }
    }

    public void refreshPresenceStatus() {
        final Profile selectedProfile = AppStore.getSelectedProfile();
        if (selectedProfile.isGoogle()) {
            if (!AppStore.getSharedStatus().isFirstRefresh()) {
                final SharedStatus sharedStatus = AppStore.getSharedStatus();
                sharedStatus.sendStatusStanza();
            }
        } else {
            AppStore.getJxa().setStatus(Jxa.statusIDtoString(selectedProfile.getStatusID()), selectedProfile.isStatusActive() ? selectedProfile.getStatus() : null, 5);
        }
    }
}
