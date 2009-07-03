/*
 * SplashScreen.java
 *
 * Created on 15 Ноябрь 2006 г., 17:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk;

import javax.microedition.lcdui.Graphics;
import org.rost.mobile.guilib.core.BaseSplash;
import org.rost.mobile.guilib.core.GUIMisc;

/**
 *
 * @author Kostya
 */
public class SplashScreen extends BaseSplash {

    /** Creates a new instance of SplashScreen */
    public SplashScreen() {
    }

    protected void paintInternal(Graphics g) {
        g.setColor(GUIMisc.getTextColor());
        g.setFont(GUIMisc.getNormalFont());
        g.drawString("Loading...", getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.BASELINE);
    }
}
