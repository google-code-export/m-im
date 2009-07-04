/*
 * ReadStanzaListener.java
 *
 * Created on 18 Ноябрь 2006 г., 11:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.controllers;

import org.rost.mobile.mgtalk.utils.XmlNode;

/**
 *
 * @author Kostya
 */
public interface ReadStanzaListener {

    public boolean stanzaReceived(XmlNode stanza);
}
