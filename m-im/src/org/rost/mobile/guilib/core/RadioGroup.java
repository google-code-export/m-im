/*
 * RadioGroup.java
 *
 * Created on 14 Ноябрь 2006 г., 12:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import java.util.Vector;
import org.rost.mobile.guilib.components.CheckBoxItem;

/**
 *
 * @author Kostya
 */
public class RadioGroup implements ItemActionListener {

    /**
     * Creates a new instance of RadioGroup
     */
    Vector items = new Vector();

    public RadioGroup() {
    }

    public void addItem(CheckBoxItem item) {
        items.addElement(item);
        item.setItemActionListener(this);
    }

    public void actionPerformed() {
        for (int i = 0; i < items.size(); i++) {
            CheckBoxItem item = (CheckBoxItem) items.elementAt(i);
            item.setSelected(false);
        }
    }

    public void setValue(int value) {
        //System.out.println("Value = " + value);
        for (int i = 0; i < items.size(); i++) {
            CheckBoxItem item = (CheckBoxItem) items.elementAt(i);
            item.setSelected(value == i);
        }
    }

    public int getValue() {
        for (int i = 0; i < items.size(); i++) {
            CheckBoxItem item = (CheckBoxItem) items.elementAt(i);
            if (item.getValue().equals("true")) {
                return i;
            }
        }
        return -1;
    }
}
