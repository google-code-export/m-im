/*
 * ContactListItem.java
 *
 * Created on November 18, 2006, 9:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.ItemActionListener;
import org.rost.mobile.guilib.core.ItemInterface;

/**
 *
 * @author kostya
 */
public class ContactListItem implements ItemInterface {

    Image icon;
    StaticRichText name;
    StaticRichText status;
    int width = GUIMisc.getActiveWidth();
    int height = 0;
    boolean showStatus = false;
    boolean showStatusLine = false;
    ItemActionListener listener;

    public void setItemActionListener(ItemActionListener listener) {
        this.listener = listener;
    }

    public void click() {
        if (listener != null) {
            listener.actionPerformed();
        }
    }

    /** Creates a new instance of ContactListItem */
    public ContactListItem(Image icon, String name, String status, boolean showStatusLine) {
        this.showStatusLine = showStatusLine;
        setInfo(icon, name, status);
    }

    public void setInfo(Image icon, String name, String status) {
        setInfo(icon, name, status, false);
    }

    public void setInfo(Image icon, String name, String status, boolean fullStatus) {
        this.icon = icon;
        this.name = new StaticRichText(width - icon.getWidth());
        this.status = new StaticRichText();
        this.name.addText(GUIMisc.cutStringToWidth(name, GUIMisc.getBoldFont(), width - icon.getWidth()), true, -1);
        if (showStatusLine) {
	        this.status.addText(fullStatus ? status : GUIMisc.cutStringToWidth(status, GUIMisc.getNormalFont(), width));
    	    showStatus = !status.equals("");
	    }
        height = Math.max(icon.getHeight(), this.name.getHeight()) + (showStatusLine && showStatus ? this.status.getHeight() : 0);
    }

    public void paint(Graphics g, int x, int y) {
        paintInternal(g, x, y, false);
    }

    public void paintSelected(Graphics g, int x, int y) {
        paintInternal(g, x, y, true);
    }

    void paintInternal(Graphics g, int x, int y, boolean selected) {
        g.setColor(selected ? GUIMisc.getItemBgActColor() : GUIMisc.getItemBgColor());
        g.fillRect(x, y, width, height);
        g.drawImage(icon, x, y, Graphics.TOP | Graphics.LEFT);
        if (selected) {
            name.paintSelected(g, x + icon.getWidth(), y);
        } else {
            name.paint(g, x + icon.getWidth(), y);
        }
        if (!showStatusLine || !showStatus) {
            return;
        }
        if (selected) {
            status.paintSelected(g, x, y + Math.max(icon.getHeight(), name.getHeight()));
        } else {
            status.paint(g, x, y + Math.max(icon.getHeight(), name.getHeight()));
        }
    }

    public Object getValue() {
        return "";
    }

    public boolean canBeSelected() {
        return true;
    }

    public int getHeight() {
        return height;
    }

    public boolean processKeyCode(int keyCode) {
        return false;
    }
}
