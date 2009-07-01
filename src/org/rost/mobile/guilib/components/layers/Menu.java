/*
 * Menu.java
 *
 * Created on 13 Ноябрь 2006 г., 13:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components.layers;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import org.rost.mobile.guilib.components.MenuItem;
import org.rost.mobile.guilib.components.SubMenuItem;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.LayerInterface;
import org.rost.mobile.guilib.core.BaseCanvas;

/**
 *
 * @author Kostya
 */
public class Menu extends LayerInterface {

    /** Creates a new instance of Menu */
    int menuLevel;
    int width = 0;
    int height = 0;
    Vector items = new Vector();
    int currentSelected = 0;
    static int heightInc = 8;
    static int widthInc = 7;
    static int menuWidth = GUIMisc.getActiveWidth() * 2 / 3 - widthInc;
    static int menuShift = (GUIMisc.getActiveWidth() - menuWidth) / 3;

    public static int getMenuWidth() {
        return menuWidth;
    }

    public Menu(int menuLevel) {
        this(menuLevel, "Select", "Cancel");
    }

    public Menu(int menuLevel, String leftMenu, String rightMenu) {
        this.menuLevel = menuLevel;
        setLeftCommand(leftMenu);
        setRightCommand(rightMenu);
    }

    public void addMenuItem(MenuItem item) {
        items.addElement(item);
        height += item.getHeight();
    }

    public void paintCustom(Graphics g) {
        //Draw border

        int startY = GUIMisc.getActiveY() + GUIMisc.getActiveHeight() - height - menuShift * menuLevel - heightInc;
        int startX = GUIMisc.getActiveX() + menuShift * menuLevel;
        if (startY < GUIMisc.getActiveY()) {
            startY = GUIMisc.getActiveY();
        }
        g.setColor(0x000000);
        g.drawRect(startX + 1, startY + 1, menuWidth + 5, height + 5);
        g.setColor(0x676767);
        g.drawRect(startX, startY, menuWidth + 7, height + 7);
        g.setColor(0xcccccc);
        g.drawLine(startX + menuWidth + 8, startY + 2, startX + menuWidth + 8, startY + height + 8);
        g.drawLine(startX + 2, startY + height + 8, startX + menuWidth + 8, startY + height + 8);
        g.setColor(GUIMisc.getItemBgColor());
        g.fillRect(startX + 2, startY + 2, menuWidth + 4, height + 4);
        int dy = 0;
        for (int i = 0; i < items.size(); i++) {
            MenuItem mi = (MenuItem) items.elementAt(i);
            if (currentSelected == i) {
                mi.paintSelected(g, startX + 4, startY + 4 + dy);
            } else {
                mi.paint(g, startX + 4, startY + 4 + dy);
            }
            dy += mi.getHeight();
        }
    }

    public boolean processKeyPress(int keyCode) {
        switch (keyCode) {
            case -2:
                //Down
                if (currentSelected < items.size() - 1) {
                    currentSelected++;
                } else {
                    currentSelected = 0;
                }
                notifyChanged();

                return true;
            case -1:
                //Up
                if (currentSelected > 0) {
                    currentSelected--;
                } else {
                    currentSelected = items.size() - 1;
                }
                notifyChanged();

                return true;
            case -3:
                closeMenu();
                return true;
            default:
                MenuItem mi = (MenuItem) items.elementAt(currentSelected);
                mi.processKeyCode(keyCode);
        }
        return false;
    }

    void closeMenu() {
        GUIStore.getManager().removeLayer(this);
        notifyChanged();
    }

    public void selectItem() {
        MenuItem mi = (MenuItem) items.elementAt(currentSelected);
        if (!(mi instanceof SubMenuItem)) {
            closeAllMenus();
        }
        mi.click();
    }

    public boolean leftCommandClick() {
        selectItem();
        return true;
    }

    public boolean rightCommandClick() {
        closeMenu();
        return true;
    }

    public boolean selectCommandClick() {
        selectItem();
        return true;
    }

    private void closeAllMenus() {
        for (int i = GUIStore.getManager().getLayerCount() - 1; i > -1; i--) {
            if (GUIStore.getManager().getLayerAt(i) instanceof Menu) {
                GUIStore.getManager().removeLayer(GUIStore.getManager().getLayerAt(i));
            }
        }
        notifyChanged();
    }

    public void refreshView() {
        currentSelected = 0;
    }
}
