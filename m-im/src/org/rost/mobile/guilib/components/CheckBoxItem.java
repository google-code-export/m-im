/*
 * CheckBoxItem.java
 *
 * Created on November 12, 2006, 2:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author kostya
 */
public class CheckBoxItem extends StaticRichText {

    boolean selected;
    /** Creates a new instance of CheckBoxItem */
    Image selImage = null;
    Image unSelImage = null;
    static String BASEURL = "/org/rost/mobile/guilib/components/res/";
    static String CHECKED = "checked.png";
    static String UNCHECKED = "unchecked.png";
    static String SELECTED = "selected.png";
    static String UNSELECTED = "unselected.png";

    public CheckBoxItem(String text) {
        this(text, false, GUIMisc.getActiveWidth(), false);
    }

    public CheckBoxItem(String text, boolean radio) {
        this(text, false, GUIMisc.getActiveWidth(), radio);
    }

    public CheckBoxItem(String text, boolean selected, int width) {
        this(text, selected, width, false);
    }

    public CheckBoxItem(String text, boolean selected, int width, boolean radio) {
        super(width);
        this.selected = selected;
        try {
            if (radio) {
                unSelImage = Image.createImage(BASEURL + UNSELECTED);
                selImage = Image.createImage(BASEURL + SELECTED);
            } else {
                unSelImage = Image.createImage(BASEURL + UNCHECKED);
                selImage = Image.createImage(BASEURL + CHECKED);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        addImage(getImage());
        addText(text, false, -1);
    }

    Image getImage() {
        if (selected) {
            return selImage;
        }
        return unSelImage;
    }

    public void setSelected(boolean s) {
        selected = s;
        ((StaticRichTextItem) items.elementAt(0)).setItem(getImage());
    }

    public void click() {
        super.click();
        setSelected(!selected);
        GUIStore.getCanvas().repaint();
    }

    public Object getValue() {
        return selected ? "true" : "false";
    }

    public boolean isSelected() {
        return selected;
    }
}
