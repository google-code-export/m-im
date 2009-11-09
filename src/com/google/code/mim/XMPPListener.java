/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.mim;

import java.util.Vector;

/**
 *
 * @author yaochunlin
 */
public interface XMPPListener {

    void onAuthFailed(String message);

    void onBind(String resource);

    void onConnFailed(String msg);

    void onContactEvent(String jid, String name, String group, String subscription);

    void onContactOverEvent();

    void onMessageEvent(String from, String body);

    void onSharedStatusEvent(String status, int show, Vector awayList, Vector busyList, Vector onlineList);

    void onStatusEvent(String jid, String show, String status);

    void onSubscribeEvent(String jid);

    void onUnsubscribeEvent(String jid);

    void onVersion();

}
