/*
 * BaseMidlet.java
 *
 * Created on 11 Ноябрь 2006 г., 12:20
 */
package org.rost.mobile.guilib.core;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author  Kostya
 * @version
 */
abstract public class BaseMidlet extends MIDlet {

    static BaseMidlet midlet = null;
    boolean started = false;
    protected BaseSplash splash = new BaseSplash();

    public void startApp() {
        if (started) {
            return;
        }
        started = true;
        midlet = this;
        GUIStore.setDisplay(Display.getDisplay(this));
        BaseCanvas tc = new BaseCanvas();
        GUIStore.setCanvas(tc);
        GUIStore.getDisplay().setCurrent(splash);
        appStarted();
        GUIStore.getDisplay().setCurrent(tc);
    }

    abstract public void appStarted();

    public static BaseMidlet getMidlet() {
        return midlet;
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public static void closeMIDLet() {
        try {
            getMidlet().destroyApp(true);
            getMidlet().notifyDestroyed();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
