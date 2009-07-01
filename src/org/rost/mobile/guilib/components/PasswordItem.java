/*
 * PasswordItem.java
 *
 * Created on November 14, 2006, 11:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import org.rost.mobile.guilib.core.AdvTextBox;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author kostya
 */
public class PasswordItem extends TextBoxItem {

    String passValue = "";

    /** Creates a new instance of PasswordItem */
    public PasswordItem(String text) {
        super(text);
    }

    public Object getValue() {
        return passValue;
    }

    public void setValue(String s) {
        value.clear();
        passValue = s;
        String starStr = "";
        for (int i = 0; i < passValue.length(); i++) {
            starStr += "*";
        }
        value.addText(starStr, false, -1);
    }

    public void click() {
        AdvTextBox tb = new AdvTextBox("", this);
        GUIStore.getDisplay().setCurrent(tb);
    }
}
