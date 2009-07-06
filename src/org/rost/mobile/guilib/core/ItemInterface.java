/*
 * ItemInterface.java
 *
 * Created on 11 Ноябрь 2006 г., 14:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Kostya
 */
public interface ItemInterface {

    /** Creates a new instance of ItemInterface */
    public void paint(Graphics g, int x, int y);

    public void paintSelected(Graphics g, int x, int y);

    public Object getValue();

    public boolean canBeSelected();

    public int getHeight();

    public void click();

    public boolean processKeyCode(int keyCode);
}
