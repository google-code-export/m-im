/*
 * TextBoxItem.java
 *
 * Created on 13 Ноябрь 2006 г., 15:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import org.rost.mobile.guilib.core.AdvTextBox;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.ItemInterface;
import org.rost.mobile.guilib.core.BaseMidlet;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author Kostya
 */
public class TextBoxItem implements ItemInterface {

    int width = 0;
    /** Creates a new instance of TextBoxItem */
    StaticRichText caption;
    StaticRichText value;
    int valueWidth = width - 7;

    public TextBoxItem() {
        this(GUIMisc.getActiveWidth());
    }

    public TextBoxItem(int width) {
        this.width = width;
        valueWidth = width - 7;
        caption = new StaticRichText(width - 2);
        value = new StaticRichText(valueWidth);
    }

    public TextBoxItem(String s) {
        this();
        getCaption().addText(s);
    }

    public StaticRichText getCaption() {
        return caption;
    }

    public void paintInternal(boolean selected, Graphics g, int x, int y) {
        g.setColor(GUIMisc.getItemBgColor());
        g.fillRect(x, y, width, getHeight());
        caption.paint(g, x + 1, y, false);
        int startY = y + caption.getHeight();
        int valueHeight = value.getHeight();
        g.setColor(0x000000);
        g.drawRect(x + 2, startY + 1, width - 4, valueHeight + 3);
        g.setColor(0xcccccc);
        g.drawRect(x + 3, startY + 2, width - 6, valueHeight + 1);
        if (selected) {
            value.paintSelected(g, x + 4, startY + 3, false);
        } else {
            value.paint(g, x + 4, startY + 3, false);
        }
    }

    public void paint(Graphics g, int x, int y) {
        paintInternal(false, g, x, y);
    }

    public void paintSelected(Graphics g, int x, int y) {
        paintInternal(true, g, x, y);
    }

    public Object getValue() {
        return value.getValue();
    }

    public void setValue(String s) {
        value.clear();
        value.addText(s, false, -1);
    }

    public boolean canBeSelected() {
        return true;
    }

    public int getHeight() {
        int height = caption.getHeight() + 7 + value.getHeight();
        return height;
    }

    public void click() {
        AdvTextBox tb = new AdvTextBox((String) value.getValue(), this);
        GUIStore.getDisplay().setCurrent(tb);
    //Edit text here
    }

    public boolean processKeyCode(int keyCode) {
        return false;
    }
}
