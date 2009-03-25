/*
 * RichText.java
 *
 * Created on 11 Ноябрь 2006 г., 15:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.ItemActionListener;
import org.rost.mobile.guilib.core.ItemInterface;
import org.rost.mobile.guilib.core.TextSplitter;

abstract class StaticRichTextItem {

    public StaticRichTextItem() {
    }
    protected int x = 0;
    protected int y = 0;
    protected Object item;

    public int getX() {
        return x;
    }

    abstract public int getWidth();

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    abstract void paint(Graphics g, int dx, int dy);
}

class StaticImage extends StaticRichTextItem {

    void paint(Graphics g, int dx, int dy) {
        g.drawImage((Image) item, dx + x, dy + y, Graphics.TOP | Graphics.LEFT);
    }

    public Object getItem() {
        return item;
    }

    public int getWidth() {
        return ((Image) item).getWidth();
    }
}

class StaticText extends StaticRichTextItem {

    protected int textColor = GUIMisc.getTextColor();
    Font font = null;

    public void setTextColor(int color) {
        textColor = color;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    void paint(Graphics g, int dx, int dy) {
        g.setColor(textColor);
        if (font != null) {
            g.setFont(font);
        }
        g.drawString((String) item, dx + x, dy + y, Graphics.TOP | Graphics.LEFT);
    }

    public int getWidth() {
        Font f = (font == null ? GUIMisc.getNormalFont() : font);
        return f.stringWidth((String) item);
    }
}

/**
 *
 * @author Kostya
 */
public class StaticRichText implements ItemInterface {

    /** Creates a new instance of RichText */
    ItemActionListener listener;

    public void setItemActionListener(ItemActionListener listener) {
        this.listener = listener;
    }

    public void click() {
        if (listener != null) {
            listener.actionPerformed();
        }
    }
    protected int width;
    Vector items = new Vector();
    int currentHeight = 0;
    int currentWidth = 0;
    int lineHeight = 0;

    public StaticRichText() {
        this(GUIMisc.getActiveWidth());
    }

    public StaticRichText(int width) {
        this.width = width;
    }

    public void clear() {
        items.removeAllElements();
        currentHeight = 0;
        currentWidth = 0;
        lineHeight = 0;
    }

    public void addText(String text) {
        addText(text, false, -1);
    }

    public void addText(String text, boolean bold, int color) {
        Font font = bold ? GUIMisc.getBoldFont() : GUIMisc.getNormalFont();
        Vector v = TextSplitter.split(text, font, width);
        for (int i = 0; i < v.size(); i++) {
            String slice = (String) v.elementAt(i);
            if (font.stringWidth(slice) + currentWidth > width) {
                currentHeight += lineHeight;
                currentWidth = 0;
                lineHeight = 0;
            }
            StaticText item = new StaticText();
            item.setItem(slice);
            item.setX(currentWidth);
            item.setY(currentHeight);
            if (color != -1) {
                item.setTextColor(color);
            }
            item.setFont(font);
            items.addElement(item);
            currentWidth += font.stringWidth(slice);
            if (font.getHeight() > lineHeight) {
                lineHeight = font.getHeight();
            }
        }
    }

    public void addImage(Image image) {
        if (image.getWidth() + currentWidth > width) {
            currentHeight += lineHeight;
            currentWidth = 0;
            lineHeight = 0;
        }
        StaticImage item = new StaticImage();
        item.setItem(image);
        item.setX(currentWidth);
        item.setY(currentHeight);
        items.addElement(item);
        currentWidth += image.getWidth();
        if (image.getHeight() > lineHeight) {
            lineHeight = image.getHeight();
        }

    }

    public void paint(Graphics g, int x, int y) {
        paint(g, x, y, false);
    }

    public void paint(Graphics g, int x, int y, boolean line) {
        g.setColor(GUIMisc.getItemBgColor());
        paintInternal(g, x, y, line);
    }

    public void paintSelected(Graphics g, int x, int y) {
        paintSelected(g, x, y, false);
    }

    public void paintSelected(Graphics g, int x, int y, boolean line) {
        g.setColor(GUIMisc.getItemBgActColor());
        paintInternal(g, x, y, line);
    }

    void paintInternal(Graphics g, int x, int y, boolean line) {
        paintInternal(g, x, y, line, true);
    }

    public void paintInternal(Graphics g, int x, int y, boolean line, boolean fill) {
        if (fill) {
            g.fillRect(x, y, width, getHeight());
        }
        for (int i = 0; i < items.size(); i++) {
            ((StaticRichTextItem) items.elementAt(i)).paint(g, x, y);
        }
        if (line) {
            g.setColor(GUIMisc.getTextColor());
            g.drawLine(x, y + currentHeight + lineHeight, x + width, y + currentHeight + lineHeight);
        }
    }

    public int getWidth() {
        int maxWidth = 0;
        for (int i = 0; i < items.size(); i++) {
            StaticRichTextItem item = (StaticRichTextItem) items.elementAt(i);
            if (item.getX() + item.getWidth() > maxWidth) {
                maxWidth = item.getX() + item.getWidth();
            }
        }
        return maxWidth;
    }

    public Object getValue() {
        String s = "";
        for (int i = 0; i < items.size(); i++) {
            s += ((StaticRichTextItem) items.elementAt(i)).getItem();
        }
        return s;
    }

    public boolean canBeSelected() {
        return true;
    }

    public int getHeight() {
        return ((currentHeight + lineHeight) == 0 ? GUIMisc.getNormalFont().getHeight() : (currentHeight + lineHeight)) + 1;
    }

    public boolean processKeyCode(int keyCode) {
        return false;
    }
}
