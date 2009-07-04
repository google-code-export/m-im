/*
 * UnselectableList.java
 *
 * Created on 13 Ноябрь 2006 г., 18:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components.layers;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.ItemInterface;
import org.rost.mobile.guilib.core.LayerInterface;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author Kostya
 */
public class UnselectableList extends LayerInterface {

    /** Creates a new instance of UnselectableList */
    public UnselectableList() {
    }
    int height = 0;
    int activeY = 0;
    int currentPosition = 0;
    int currentHeight = 0;
    int step = GUIMisc.getNormalFont().getHeight();
    Vector items = new Vector();

    public void pushItemFront(ItemInterface item) {
        items.insertElementAt(item, 0);
        currentHeight += item.getHeight();
    }

    public void pushItemBack(ItemInterface item) {
        items.addElement(item);
        currentHeight += item.getHeight();
    }

    public void setCurrentPosition(int p) {
        currentPosition = p;
    }

    public void setYCredentials(int height, int activeY) {
        this.height = height;
        this.activeY = activeY;
    }

    public void paintCustom(Graphics g) {
        if (height == 0) {
            height = GUIMisc.getActiveHeight();
        }
        if (activeY == 0) {
            activeY = GUIMisc.getActiveY();
        }
        g.setClip(GUIMisc.getActiveX(), activeY, GUIMisc.getActiveWidth(), height);
        g.translate(0, -currentPosition);
        int h = 0;
        for (int i = 0; i < items.size(); i++) {
            ItemInterface ii = (ItemInterface) items.elementAt(i);
            if (h + ii.getHeight() > currentPosition) {
                //Need draw
                ii.paint(g, GUIMisc.getActiveX(), activeY + h);
            }
            h += ii.getHeight();
            if (h > height + currentPosition) {
                break;
            }
        }

        g.translate(0, currentPosition);
        if (h <= currentHeight) {
            fillWithBackground(g, h - currentPosition);
        }
        g.setClip(0, 0, GUIMisc.getScWidth(), GUIMisc.getScHeight());
        if (currentPosition > 0 || currentHeight > h) {
            GUIStore.getManager().setScrollBarData(currentHeight, h - currentPosition, currentPosition);
        }
    }

    void fillWithBackground(Graphics g, int y) {
        g.setColor(GUIMisc.getBgColor());
        g.fillRect(GUIMisc.getActiveX(), activeY + y, GUIMisc.getActiveWidth(), height - y);
    }

    public boolean processKeyPress(int keyCode) {
        switch (keyCode) {
            case -2:
                //Down
                if (height + currentPosition < currentHeight) {
                    currentPosition += step;
                }
                notifyChanged();
                return true;
            case -1:
                //Up
                if (currentPosition > 0) {
                    currentPosition -= step;
                }
                notifyChanged();
                return true;
        }
        return false;
    }

    public void clear() {
        items.removeAllElements();
        setCurrentPosition(0);
        currentHeight = 0;
    }

    public Vector getItems() {
        return items;
    }
}
