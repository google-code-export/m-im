/*
 * UserMessageListener.java
 *
 * Created on November 19, 2006, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.model;

import org.rost.mobile.guilib.components.StaticRichText;

/**
 *
 * @author kostya
 */
public interface UserMessageListener {

    public boolean newMessageReceived(User user, StaticRichText item, boolean from);
}
