/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.jxa;

import java.util.Vector;

/**
 *
 * @author yaochunlin
 */
public class XmppAdapter implements XmppListener {

    public void onConnFailed(String msg) {
    }

    public void onAuth(String resource) {
    }

    public void onAuthFailed(String message) {
    }

    public void onMessageEvent(String from, String body) {
    }

    public void onContactEvent(String jid, String name, String group, String subscription) {
    }

    public void onContactOverEvent() {
    }

    public void onStatusEvent(String jid, String show, String status) {
    }

    public void onSubscribeEvent(String jid) {
    }

    public void onUnsubscribeEvent(String jid) {
    }

    public void onSharedStatusEvent(String status, int show, Vector awayList, Vector busyList, Vector onlineList) {
    }
}
