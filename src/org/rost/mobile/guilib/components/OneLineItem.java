/*
 * OneLineItem.java
 *
 * Created on 13 Ноябрь 2006 г., 11:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.ItemActionListener;
import org.rost.mobile.guilib.core.ItemInterface;

/**
 *
 * @author Kostya
 */
public class OneLineItem implements ItemInterface {

    /** Creates a new instance of OneLineItem */
    int width;
    int height = 0;
    StaticImage leftImage = null;
    StaticImage rightImage = null;
    StaticText centerText = null;
    String text = "";

    public OneLineItem(int width) {
        this.width = width;
    }

    public OneLineItem() {
        this(GUIMisc.getActiveWidth());
    }

    public void setLeftImage(Image image) {
        if (image == null) {
            leftImage = null;
        } else {
            leftImage = new StaticImage();
            leftImage.setItem(image);
            if (image.getHeight() > height) {
                height = image.getHeight();
            }
        }
    }

    public void setRightImage(Image image) {
        if (image == null) {
            rightImage = null;
        } else {
            rightImage = new StaticImage();
            rightImage.setItem(image);
            rightImage.setX(width - image.getWidth());
            if (image.getHeight() > height) {
                height = image.getHeight();
            }
        }
    }

    public void setText(String s) {
        text = s;
        int freeWidth = width;
        if (leftImage != null) {
            freeWidth -= ((Image) leftImage.getItem()).getWidth();
        } else {
            freeWidth -= 2;
        }
        if (rightImage != null) {
            freeWidth -= ((Image) rightImage.getItem()).getWidth();
        }
        int threeDots = GUIMisc.getNormalFont().stringWidth("...");
        freeWidth -= threeDots;
        while (GUIMisc.getNormalFont().stringWidth(s) > freeWidth) {
            s = s.substring(0, s.length() - 2);
        }
        centerText = new StaticText();
        centerText.setFont(GUIMisc.getNormalFont());
        centerText.setItem(s.equals(text) ? s : s + "...");
        if (GUIMisc.getNormalFont().getHeight() > height) {
            height = GUIMisc.getNormalFont().getHeight();
        }
    }

    public void paint(Graphics g, int x, int y) {
        g.setColor(GUIMisc.getItemBgColor());
        paintInternal(g, x, y);
    }

    public void paintSelected(Graphics g, int x, int y) {
        g.setColor(GUIMisc.getItemBgActColor());
        paintInternal(g, x, y);
    }

    void paintInternal(Graphics g, int x, int y) {
        g.fillRect(x, y, width, height);
        g.setColor(GUIMisc.getTextColor());
        int dx = 0;
        if (leftImage != null) {
            leftImage.paint(g, x, y);
            dx = ((Image) leftImage.getItem()).getWidth();
        } else {
            dx = 2;
        }
        if (rightImage != null) {
            rightImage.paint(g, x, y);
        }
        if (centerText != null) {
            centerText.paint(g, x + dx, y);
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
    ItemActionListener listener;

    public void setItemActionListener(ItemActionListener listener) {
        this.listener = listener;
    }

    public void click() {
        if (listener != null) {
            listener.actionPerformed();
        }
    }

    public boolean processKeyCode(int keyCode) {
        return false;
    }
}
