/*
 * Starter.java
 *
 * Created on November 14, 2006, 4:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk;

import org.rost.mobile.guilib.core.BaseMidlet;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author kostya
 */
public class Starter extends BaseMidlet {

    /** Creates a new instance of Starter */
    public Starter() {
        splash = new SplashScreen();
    }

    public void appStarted() {
        //We are ready to work
        //Creating main forms, Starting profile List
        AppStore.initApp();
        GUIStore.getManager().push(AppStore.getProfileListUI());
        if (AppStore.getProfileList().getAutoProfile() != -1) {
            AppStore.getProfileListUI().setSelectedIndex(AppStore.getProfileList().getAutoProfile());
            AppStore.getProfileListUI().actionPerformed();
        }
        GUIStore.getCanvas().repaint();
    }
}
