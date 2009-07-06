/*
 * InfoTicker.java
 *
 * Created on 17 Ноябрь 2006 г., 11:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.LayerInterface;

/**
 *
 * @author Kostya
 */
public class InfoTicker extends LayerInterface {

    /** Creates a new instance of InfoTicker */
    static long SHOW_INTERVAL = 3000L;
    boolean hideTicker = false;
    boolean taskStarted = false;
    Vector messages = new Vector();

    class HideTicker extends TimerTask {

        public void run() {
            hideTicker = true;
            taskStarted = false;
            if (messages.size() > 0) {
                try {
                    String message = (String) messages.elementAt(0);
                    messages.removeElementAt(0);
                    setMessage(message, false);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                Thread.yield();
                notifyChanged();
            }
        }
    }
    StaticRichText info = null;
    int width = GUIMisc.getActiveWidth() * 4 / 5;
    int activeY = GUIMisc.getActiveY() / 2;

    public InfoTicker() {
    }

    synchronized public void setMessage(String message) {
        if (taskStarted) {
            messages.addElement(message);
        } else {
            setMessage(message, false);
        }
    }

    synchronized public void setMessage(String message, boolean continueShow) {
        info = new StaticRichText(width - 4);
        info.addText(message);
        hideTicker = false;
        if (!continueShow) {
            Timer t = new Timer();
            taskStarted = true;
            t.schedule(new HideTicker(), SHOW_INTERVAL);
        }
        notifyChanged();
        Thread.yield();
    }

    public void cancelTicker() {
        hideTicker = true;
        Thread.yield();
        notifyChanged();
    }

    public void paintCustom(Graphics g) {
        if (info == null) {
            return;
        }
        if (hideTicker) {
            return;
        }
        int textWidth = info.getWidth();
        int tickerWidth = textWidth + 4;

        int height = info.getHeight();
        int activeX = GUIMisc.getActiveX() + GUIMisc.getActiveWidth() - tickerWidth - 2;
        g.setColor(GUIMisc.getItemBgColor());
        g.fillRoundRect(activeX, activeY,
                tickerWidth, height + 3, 2, 2);

        g.setColor(GUIMisc.getDialogColor());
        g.drawRoundRect(activeX, activeY,
                tickerWidth, height + 3, 2, 2);
        info.paintInternal(g, activeX + 2, activeY + 1, false, false);
    }
}
