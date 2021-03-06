package com.google.code.mim;

import java.io.IOException;
import org.rost.mobile.guilib.core.Constants;
import org.rost.mobile.mgtalk.AppStore;

public class XmppPinger implements Runnable {

    //public static final int PING_EVERY_MS = 5 * 60 * 1000; // 5 mins
    //public static final int PING_EVERY_MS = 10 * 1000; // 10 seconds for debugging
    // IF LOGGING (Debug Build), ping server every 30 seconds otherwise every 5 minutes...
    public static final int PING_EVERY_MS = Constants.LOGGING ? (30 * 1000) : (5 * 60 * 1000);
    private int pingCount = 0;
    private boolean running = false;

    public XmppPinger() {
        super();
    }

    public void run() {
        running = true;
        while (running) {
            try {
                AppStore.getXMPP().sendPing(++pingCount);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Thread.sleep(PING_EVERY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return this.running;
    }
}
