/*
 * SimpleLayout.java
 *
 * Created on November 12, 2006, 10:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components.layers;

import javax.microedition.lcdui.Graphics;
import org.rost.mobile.guilib.components.*;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.LayerInterface;

/**
 *
 * @author kostya
 */
public class SimpleLayer extends LayerInterface {

    /** Creates a new instance of SimpleLayout */
    public SimpleLayer() {
        item1 = new StaticRichText(GUIMisc.getActiveWidth());
        item1.addText("Hi korea", true, -1);
    }
    StaticRichText item1;

    public void paintCustom(Graphics g) {
        item1.paint(g, GUIMisc.getActiveX(), GUIMisc.getActiveY());
    }
}
