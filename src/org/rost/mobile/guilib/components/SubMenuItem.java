/*
 * SubMenuItem.java
 *
 * Created on 13 Ноябрь 2006 г., 14:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

import javax.microedition.lcdui.Image;
import org.rost.mobile.guilib.components.layers.Menu;
import org.rost.mobile.guilib.core.BaseCanvas;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author Kostya
 */
public class SubMenuItem extends MenuItem {

    /** Creates a new instance of SubMenuItem */
    Menu menu;

    public SubMenuItem(Menu subMenu, String text) {
        super(text);
        menu = subMenu;
        try {
            Image image = Image.createImage("/org/rost/mobile/guilib/components/res/menu_more.png");
            setRightImage(image);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void click() {
        GUIStore.getManager().pushFront(menu);
        menu.notifyChanged();
    }

    public boolean processKeyCode(int keyCode) {
        if (keyCode == -4) {
            //Right key pressed
            click();
            return true;
        }
        return false;
    }
}
