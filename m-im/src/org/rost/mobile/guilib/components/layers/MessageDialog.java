/*
 * MessageDialog.java
 *
 * Created on November 13, 2006, 11:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components.layers;

import javax.microedition.lcdui.Graphics;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.ItemInterface;
import org.rost.mobile.guilib.core.LayerInterface;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author kostya
 */
public class MessageDialog extends LayerInterface {

    /** Creates a new instance of MessageDialog */
    ItemInterface item = null;

    public MessageDialog() {
    }

    public void setItem(ItemInterface item) {
        this.item = item;
    }

    public void paintCustom(Graphics g) {
        int height = item == null ? 0 : item.getHeight();
        g.setColor(GUIMisc.getItemBgColor());
        g.fillRoundRect(GUIMisc.getActiveX(),
                GUIMisc.getActiveY() + GUIMisc.getActiveHeight() - 5 - height,
                GUIMisc.getActiveWidth() - 1, height + 4, 2, 2);

        g.setColor(GUIMisc.getDialogColor());
        g.drawRoundRect(GUIMisc.getActiveX(),
                GUIMisc.getActiveY() + GUIMisc.getActiveHeight() - 5 - height,
                GUIMisc.getActiveWidth() - 1, height + 4, 2, 2);
        if (item != null) {
            item.paint(g, GUIMisc.getActiveX() + 2, GUIMisc.getActiveY() + GUIMisc.getActiveHeight() - 3 - height);
        }
    }

    public boolean selectCommandClick() {
        if (item != null) {
            item.click();
            notifyChanged();
            return true;
        }
        return false;
    }

    protected void closeDialog() {
        GUIStore.getManager().removeLayer(this);
        notifyChanged();
    }
}
