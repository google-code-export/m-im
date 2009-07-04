/*
 * MenuItem.java
 *
 * Created on 13 Ноябрь 2006 г., 13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import org.rost.mobile.guilib.components.layers.Menu;
import org.rost.mobile.guilib.core.ItemActionListener;

/**
 *
 * @author Kostya
 */
public class MenuItem extends OneLineItem {

    /** Creates a new instance of MenuItem */
    ItemActionListener listener;

    public MenuItem(String text) {
        super(Menu.getMenuWidth());
        setText(text);
    }

    public void setItemActionListener(ItemActionListener listener) {
        this.listener = listener;
    }

    public void click() {
        if (listener != null) {
            listener.actionPerformed();
        }
    }
}
