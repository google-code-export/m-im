/*
 * LayoutManager.java
 *
 * Created on November 12, 2006, 8:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author kostya
 */
public final class LayerManager implements LayerChangeListener {

    /** Creates a new instance of LayoutManager */
    Canvas canvas;
    Vector layers = new Vector();

    public LayerManager(Canvas c) {
        canvas = c;
    }

    public void pushBack(LayerInterface layer) {
        layer.addChangeListener(this);
        layers.insertElementAt(layer, 0);
        layer.refreshView();
    }

    public void pushFront(LayerInterface layer) {
        layer.addChangeListener(this);
        layers.addElement(layer);
        layer.refreshView();
    }

    public void push(LayerInterface layer) {
        for (int i = layers.size() - 1; i > -1; i--) {
            LayerInterface li = (LayerInterface) layers.elementAt(i);
            if (li.isMain()) {
                removeLayer(li);
            }
        }
        pushBack(layer);
    }
    int totalScrollItems;
    int itemsShowed;
    int itemsBefore;

    public void setScrollBarData(int totalScrollItems, int itemsShowed, int itemsBefore) {
        this.totalScrollItems = totalScrollItems;
        this.itemsShowed = itemsShowed;
        this.itemsBefore = itemsBefore;
    }

    synchronized public void repaint(Graphics g) {
        GUIMisc.drawHeader(g);
        totalScrollItems = 0;
        itemsShowed = 0;
        itemsBefore = 0;
        for (int i = 0; i < layers.size(); i++) {
            LayerInterface li = (LayerInterface) layers.elementAt(i);
            li.paint(g);
        }
        GUIMisc.drawScrollBar(g);
        if (totalScrollItems > 0) {
            //Draw scrollbar widget
            int i = itemsBefore * GUIMisc.getActiveHeight() / totalScrollItems;
            int j = itemsShowed * GUIMisc.getActiveHeight() / totalScrollItems;
            g.setColor(GUIMisc.getScrollBarColor());
            g.fillRect(GUIMisc.getActiveX() + GUIMisc.getActiveWidth() + 2,
                    GUIMisc.getActiveY() + i,
                    GUIMisc.getScWidth() - GUIMisc.getActiveX() - GUIMisc.getActiveWidth() - 2,
                    j);
        }
    }

    public void actionPerformed() {
        canvas.repaint();
    }

    public void notifyChanged() {
        canvas.repaint();
    }

    public void removeLayer(LayerInterface i) {
        i.layerRemoved();
        layers.removeElement(i);
    }

    public int getLayerCount() {
        return layers.size();
    }

    public LayerInterface getLayerAt(int i) {
        return (LayerInterface) layers.elementAt(i);
    }

    public void keyPressed(int keyCode) {
        switch (keyCode) {
            case -59:
                keyCode = -1;
                break;
            case -61:
                keyCode = -3;
                break;
            case -62:
                keyCode = -4;
                break;
            case -60:
                keyCode = -2;
                break;
            case -26:
                keyCode = -5;
                break;
            case -1:
                if (GUIStore.getCanvas().getKeyName(keyCode).startsWith("Soft")) {
                    keyCode = -6;
                }
                break;
            case -4:
                if (GUIStore.getCanvas().getKeyName(keyCode).startsWith("Soft")) {
                    keyCode = -7;
                }
                break;
        }
        for (int i = layers.size() - 1; i >= 0; i--) {
            LayerInterface li = (LayerInterface) layers.elementAt(i);
            boolean ended = false;
            switch (keyCode) {
                case -6:
                    if (li.leftCommandClick()) {
                        ended = true;
                    }
                    break;
                case -7:
                    if (li.rightCommandClick()) {
                        ended = true;
                    }
                    break;
                case -5:
                    if (li.selectCommandClick()) {
                        ended = true;
                    }
                    break;
                default:
                    if (li.processKeyPress(keyCode)) {
                        ended = true;
                    }
            }
            if (ended) {
                break;
            }
        }

    }
}
