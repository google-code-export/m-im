/*
 * GUIStore.java
 *
 * Created on 11 Ноябрь 2006 г., 12:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author Kostya
 */
public class GUIStore {

    /**
     * Creates a new instance of GUIStore
     */
    protected static Display display = null;
    protected static BaseCanvas canvas = null;
    static LayerManager manager = null;

    protected static Displayable previousDisplayable = null;
    
    public static void setDisplay(Display _display) {
        display = _display;
    }

    public static Display getDisplay() {
        return display;
    }

    public GUIStore() {
    }

    public static BaseCanvas getCanvas() {
        return canvas;
    }

    public static void setCanvas(BaseCanvas aCanvas) {
        canvas = aCanvas;
    }

    public static LayerManager getManager() {
        return manager;
    }

    public static void setManager(LayerManager aManager) {
        manager = aManager;
    }
    
    public static void minimiseApplication() {
    	previousDisplayable = display.getCurrent();
    	display.setCurrent(null);
    }
    
    public static void restoreApplication() {
    	display.setCurrent(previousDisplayable);
    	previousDisplayable = null;
    }
    
}
