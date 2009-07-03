/*
 * BaseSplash.java
 *
 * Created on 15 Ноябрь 2006 г., 17:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Kostya
 */
public class BaseSplash extends Canvas {

    /** Creates a new instance of BaseSplash */
    public BaseSplash() {
        setFullScreenMode(true);
        GUIMisc.initialize(this);
    }

    protected void paint(Graphics graphics) {
        graphics.setColor(GUIMisc.getBgColor());
        graphics.fillRect(0, 0, getWidth(), getHeight());
        paintInternal(graphics);
    }

    protected void paintInternal(Graphics g) {
    }
}
