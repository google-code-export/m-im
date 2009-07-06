/*
 * TrackItem.java
 *
 * Created on November 14, 2006, 2:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.ItemInterface;

/**
 *
 * @author kostya
 */
public class TrackItem implements ItemInterface {

    /** Creates a new instance of TrackItem */
    int maxValue = 0;
    int currValue = 0;
    int width = 0;
    StaticRichText caption;
    static Image IMG = null;
    static String FNAME = "track.png";

    public TrackItem() {
        this(GUIMisc.getActiveWidth(), 10, 0);
    }

    public TrackItem(int width, int maxValue, int currValue) {
        this.width = width;
        setValues(maxValue, currValue);
        caption = new StaticRichText(width - 2);
        if (IMG == null) {
            try {
                IMG = Image.createImage(CheckBoxItem.BASEURL + FNAME);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void setValues(int maxValue, int currValue) {
        this.maxValue = maxValue;
        this.currValue = currValue;
        if (maxValue <= 0) {
            maxValue = 1;
        }
        if (currValue > maxValue) {
            currValue = maxValue - 1;
        }
    }

    public StaticRichText getCaption() {
        return caption;
    }

    public void paint(Graphics g, int x, int y) {
        paintInternal(false, g, x, y);
    }

    public void paintSelected(Graphics g, int x, int y) {
        paintInternal(true, g, x, y);
    }

    void paintInternal(boolean selected, Graphics g, int x, int y) {
        g.setColor(selected ? GUIMisc.getItemBgActColor() : GUIMisc.getItemBgColor());
        g.fillRect(x, y, width, getHeight());
        if (selected) {
            caption.paintSelected(g, x + 1, y, false);
        } else {
            caption.paint(g, x + 1, y, false);
        }
        g.setColor(GUIMisc.getTextColor());
        g.drawLine(x + 8, y + caption.getHeight() + 9, x + width - 8, y + caption.getHeight() + 9);

        g.drawImage(IMG, x + 2 + (width - 16) * currValue / (maxValue - 1), y + caption.getHeight() + 2, Graphics.TOP | Graphics.LEFT);
    }

    public Object getValue() {
        return "" + currValue;
    }

    public boolean canBeSelected() {
        return true;
    }

    public int getHeight() {
        return 15 + caption.getHeight();
    }

    public void click() {
    }

    public boolean processKeyCode(int keyCode) {
        switch (keyCode) {
            case -3:
            	return decrement();
            case -4:
            	return increment();
            case 105:
            	return increment();
            case 107:
            	return decrement();
            case Canvas.KEY_NUM3:
            	return increment();
            case Canvas.KEY_NUM6:
            	return increment();
            case Canvas.KEY_NUM9:
            	return increment();
            case Canvas.KEY_NUM1:
            	return decrement();
            case Canvas.KEY_NUM4:
            	return decrement();
            case Canvas.KEY_NUM7:
            	return decrement();
        }
        return false;
    }
    
    private boolean decrement() {
        if (currValue > 0) {
            currValue--;
        }
        GUIStore.getCanvas().repaint();
        return true;
    }
    
    private boolean increment() {
        if (currValue < maxValue - 1) {
            currValue++;
        }
        GUIStore.getCanvas().repaint();
        return true;
    }
    
}
