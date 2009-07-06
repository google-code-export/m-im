/*
 * SelectableList.java
 *
 * Created on November 12, 2006, 10:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components.layers;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.guilib.core.ItemInterface;
import org.rost.mobile.guilib.core.LayerInterface;

/**
 *
 * @author kostya
 */
public class SelectableList extends LayerInterface {

    /** Creates a new instance of SelectableList */
    Vector items = new Vector();
    protected int startShowing = 0;
    int endShowing = 0;
    protected int currentSelected = 0;
    int height = -1;

    public SelectableList() {
    }

    public int getSelectedIndex() {
        return currentSelected;
    }

    public void setSelectedIndex(int index) {
        currentSelected = index;
    }

    public void addItem(ItemInterface item) {
        items.addElement(item);
        notifyChanged();
    }

    public void addItem(ItemInterface item, int pos) {
        if (pos == items.size()) {
            items.addElement(item);
        } else {
            items.insertElementAt(item, pos);
        }
        notifyChanged();
    }

    void fillWithBackground(Graphics g, int y) {
        g.setColor(GUIMisc.getBgColor());
        g.fillRect(GUIMisc.getActiveX(), GUIMisc.getActiveY() + y, GUIMisc.getActiveWidth(), GUIMisc.getActiveHeight() - y);
    }

    public void paintCustom(Graphics g) {
        if (height == -1) {
            height = GUIMisc.getActiveHeight();
        }
        int i = startShowing;
        int y = 0;
        do {
            if (i >= items.size()) {
                //No items to show. Try to shift upward
                if (startShowing == 0) {
                    //No way. Fill downward with background. No scrollbar 
                    fillWithBackground(g, y);
                    return;
                } else {
                    //Start calc shift
                    int j = items.size() - 1;
                    int size = 0;
                    while (j >= 0) {
                        ItemInterface ii = (ItemInterface) items.elementAt(j);
                        if (size + ii.getHeight() > height) {
                            //Restart painting
                            if (j + 1 == startShowing) {
                                fillWithBackground(g, y);
                                GUIStore.getManager().setScrollBarData(items.size(), items.size() - j - 1, startShowing);
                                return;
                            }
                            startShowing = j + 1;
                            i = startShowing;
                            y = 0;
                            break;
                        } else {
                            size += ii.getHeight();
                            j--;
                            if (j == -1) {
                                startShowing = 0;
                            }
                        }
                    }
                    i = startShowing;
                    y = 0;
                }
            }
            ItemInterface ii = (ItemInterface) items.elementAt(i);
            if (ii.getHeight() + y < height) {
                if (currentSelected == i) {
                    ii.paintSelected(g, GUIMisc.getActiveX(), GUIMisc.getActiveY() + y);
                } else {
                    ii.paint(g, GUIMisc.getActiveX(), GUIMisc.getActiveY() + y);
                }
                endShowing = i;
                y += ii.getHeight();
            } else {
                if (endShowing < currentSelected) {
                    startShowing++;
                    paintCustom(g);
                    return;
                }
                g.setClip(GUIMisc.getActiveX(), GUIMisc.getActiveY(), GUIMisc.getActiveWidth(), GUIMisc.getActiveHeight());
                ii.paint(g, GUIMisc.getActiveX(), GUIMisc.getActiveY() + y);
                g.setClip(0, 0, GUIMisc.getScWidth(), GUIMisc.getScHeight());
//                fillWithBackground(g, y);
                GUIStore.getManager().setScrollBarData(items.size(), i - startShowing - 2, startShowing);
                return;
            }
            i++;
        } while (i <= items.size());
    }

    public boolean processKeyPress(int keyCode) {
        switch (keyCode) {
            case -2:
                //Down
                int i = 0;
                for (i = currentSelected + 1; i < items.size(); i++) {
                    ItemInterface ii = (ItemInterface) items.elementAt(i);
                    if (ii.canBeSelected()) {
                        currentSelected = i;
                        break;
                    }
                }
                if (currentSelected > endShowing) {
                    startShowing += (currentSelected - endShowing);
                }
                notifyChanged();

                return true;
            case -1:
                //Up
                for (i = currentSelected - 1; i > -1; i--) {
                    ItemInterface ii = (ItemInterface) items.elementAt(i);
                    if (ii.canBeSelected()) {
                        currentSelected = i;
                        break;
                    }
                }
                if (currentSelected < startShowing) {
                    startShowing = currentSelected;
                }
                notifyChanged();

                return true;
                
            case -4:
            	// Right (next)
            	return selectCommandClick();
        }
        try {
            ItemInterface ii = (ItemInterface) items.elementAt(currentSelected);
            return ii.processKeyCode(keyCode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public void removeItem(int n) {
        if (n >= items.size()) {
            return;
        }
        items.removeElementAt(n);
        if (currentSelected >= items.size()) {
            currentSelected = items.size() - 1;
        }
        if (currentSelected < 0) {
            currentSelected = 0;
        }
        notifyChanged();
    }

    public boolean selectCommandClick() {
        if (currentSelected > -1 && currentSelected < items.size()) {
            ItemInterface ii = (ItemInterface) items.elementAt(currentSelected);
            ii.click();
            notifyChanged();
            return true;
        }
        return false;
    }

    public int itemCount() {
        return items.size();
    }

    public ItemInterface getItem(int n) {
        if (n > -1 && n < items.size()) {
            return (ItemInterface) items.elementAt(n);
        }
        return null;
    }

    public ItemInterface getSelectedItem() {
        return getItem(currentSelected);
    }

    public String getItemValue(int n) {
        ItemInterface ii = getItem(n);
        if (ii != null) {
            return (String) ii.getValue();
        }
        return "";
    }

    public String getSelectedValue() {
        ItemInterface ii = getSelectedItem();
        if (ii != null) {
            return (String) ii.getValue();
        }
        return "";
    }

    public void clear() {
        items.removeAllElements();
        startShowing = 0;
        currentSelected = 0;
        endShowing = 0;
    }
}
