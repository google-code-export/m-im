/*
 * BaseCanvas.java
 *
 * Created on 11 Ноябрь 2006 г., 12:25
 */
package org.rost.mobile.guilib.core;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDletStateChangeException;
import org.rost.mobile.guilib.components.CheckBoxItem;
import org.rost.mobile.guilib.components.OneLineItem;
import org.rost.mobile.guilib.components.TextBoxItem;
import org.rost.mobile.guilib.components.layers.SelectableList;
import org.rost.mobile.guilib.components.layers.SimpleLayer;
import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.guilib.components.layers.UnselectableList;

/**
 *
 * @author  Kostya
 * @version
 */
public class BaseCanvas extends Canvas {

    /**
     * constructor
     */
    LayerManager manager;
    SelectableList list;

    public BaseCanvas() {
        setFullScreenMode(true);
        GUIMisc.initialize(this);
        manager = new LayerManager(this);
        GUIStore.setManager(manager);
    }
    /**
     * paint
     */
    boolean started = false;

    public void paint(Graphics g) {
        if (!started) {
            GUIMisc.initialize(this);
            GUIMisc.fillBackground(g);
            started = true;
        }
        GUIStore.getManager().repaint(g);
    }

    /**
     * Called when a key is pressed.
     */
    protected void keyPressed(int keyCode) {
        GUIStore.getManager().keyPressed(keyCode);

    }

    protected void keyRepeated(int keyCode) {
        GUIStore.getManager().keyPressed(keyCode);
    }
}
