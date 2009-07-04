/*
 * BaseMidlet.java
 *
 * Created on 11 Ноябрь 2006 г., 12:20
 */
package org.rost.mobile.guilib.core;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import org.rost.mobile.mgtalk.AppStore;

import com.google.code.mim.Log;

/**
 *
 * @author  Kostya
 * @version
 */
abstract public class BaseMidlet extends MIDlet {

    static BaseMidlet midlet = null;
    boolean started = false;
    protected BaseSplash splash = new BaseSplash();

    public BaseMidlet() {
    	
    }
    
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
        Constants.init();
        if (Constants.LOGGING) {
        	Log.debug("Screen Resolution is " + Constants.screenWidth + "x" + Constants.screenHeight);
    	}
    }

    abstract public void appStarted();

    public static BaseMidlet getMidlet() {
        return midlet;
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    	// Close down anything in the AppStore which needs to be
    	AppStore.close();
    	try {
    		// Sometimes this takes a few hundred millis, just give it half a second to close gracefully
    		// this isn't noticeable by the user, but does help the app close down properly more often
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
