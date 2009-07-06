/*
 * GUIMisc.java
 *
 * Created on 11 Ноябрь 2006 г., 12:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Kostya
 */
public class GUIMisc {

    protected static int bgColor = 0xC4F3C2;
    protected static int hColor = 0x78db94;
    protected static int textColor = 0x000000;
    protected static int itemBgColor = 0xffffff;
    protected static int itemBgActColor = 0xBAE1EB;
    protected static int scrollBarColor = 0xCCCCCC;
    protected static int dialogColor = 0xCC0000;
    static Font normalFont = null;
    static Font boldFont = null;
    protected static int boldHeight = 0;
    protected static int normalHeight = 0;
    static int scWidth = 0;
    static int scHeight = 0;
    protected static int activeX = 0;
    protected static int activeY = 0;
    protected static int activeHeight = 0;
    protected static int activeWidth = 0;
    protected static int messageItemWidth = 0;
    protected static int captionWidth = 0;

    /**
     * Creates a new instance of GUIMisc
     */
    public GUIMisc() {
    }

    public static void initialize(Canvas c) {
        normalFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        normalHeight = normalFont.getHeight();
        boldFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
        boldHeight = boldFont.getHeight();
        scWidth = c.getWidth();
        scHeight = c.getHeight();
        activeY = boldHeight + 2;
        activeHeight = scHeight - 2 * activeY;
        activeWidth = scWidth - 5; //Scroll
        messageItemWidth = activeWidth - 4;
        captionWidth = scWidth - 4;
    }

    public static void drawScrollBar(Graphics g) {
        g.setColor(getItemBgColor());
        g.fillRect(getActiveX() + getActiveWidth(), getActiveY(), getScWidth() - getActiveX() - getActiveWidth(), getActiveHeight());
        g.setColor(0xCCCCCC);
        g.drawLine(getActiveX() + getActiveWidth(), getActiveY(), getActiveX() + getActiveWidth(), getActiveY() + getActiveHeight());
    }

    public static void drawHeader(Graphics g) {
        g.setColor(hColor);
        g.fillRect(0, 0, scWidth, boldHeight);
        g.setColor(0xd7f0de);
        g.drawLine(0, boldHeight, scWidth, boldHeight);
        g.setColor(0x6cb481);
        g.drawLine(0, boldHeight + 1, scWidth, boldHeight + 1);
    }

    public static void drawFooter(Graphics g) {
        g.setColor(hColor);
        g.fillRect(0, scHeight - boldHeight, scWidth, boldHeight);
        g.setColor(0xd7f0de);
        g.drawLine(0, scHeight - boldHeight - 1, scWidth, scHeight - boldHeight - 1);
        g.setColor(0x6cb481);
        g.drawLine(0, scHeight - boldHeight - 2, scWidth, scHeight - boldHeight - 2);
    }

    public static String cutStringToWidth(String s, Font font, int width) {
        String str = s;
        if (font.stringWidth(str) < width) {
            return str;
        }
        int threeDots = font.stringWidth("...");
        while (font.stringWidth(str) > width - threeDots) {
            str = str.substring(0, str.length() - 2);
        }
        return str + "...";
    }

    public static void drawCaptionOnHeader(String caption, Graphics g) {
        g.setColor(getTextColor());
        String str = cutStringToWidth(caption, getBoldFont(), captionWidth);
        g.setFont(getBoldFont());
        g.drawString(str, captionWidth / 2, 0, Graphics.TOP | Graphics.HCENTER);
    }

    public static void fillBackground(Graphics g) {
        g.setColor(bgColor);
        g.fillRect(0, 0, scWidth, scHeight);
        drawHeader(g);
        drawFooter(g);
    }

    public static void drawLeftCommand(Graphics g, String val) {
        g.setColor(textColor);
        g.setFont(getBoldFont());
        //int tw = getBoldFont().stringWidth(val);
        g.drawString(val, 2, scHeight, Graphics.LEFT | Graphics.BOTTOM);
    }

    public static void drawRightCommand(Graphics g, String val) {
        g.setColor(textColor);
        g.setFont(getBoldFont());
        int tw = getBoldFont().stringWidth(val);
        g.drawString(val, scWidth - tw - 2, scHeight, Graphics.LEFT | Graphics.BOTTOM);
    }

    public static Font getNormalFont() {
        return normalFont;
    }

    public static Font getBoldFont() {
        return boldFont;
    }

    public static int getTextColor() {
        return textColor;
    }

    public static int getScWidth() {
        return scWidth;
    }

    public static int getScHeight() {
        return scHeight;
    }

    public static int getBgColor() {
        return bgColor;
    }

    public static int getHColor() {
        return hColor;
    }

    public static int getItemBgColor() {
        return itemBgColor;
    }

    public static int getItemBgActColor() {
        return itemBgActColor;
    }

    public static int getActiveX() {
        return activeX;
    }

    public static int getActiveY() {
        return activeY;
    }

    public static int getActiveHeight() {
        return activeHeight;
    }

    public static int getActiveWidth() {
        return activeWidth;
    }

    public static int getScrollBarColor() {
        return scrollBarColor;
    }

    public static int getDialogColor() {
        return dialogColor;
    }

    public static int getMessageItemWidth() {
        return messageItemWidth;
    }
}
