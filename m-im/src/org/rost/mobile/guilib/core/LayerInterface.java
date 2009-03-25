/*
 * LayerInterface.java
 *
 * Created on November 12, 2006, 7:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author kostya
 */
abstract public class LayerInterface {

    LayerChangeListener listener = null;
    protected String leftCommand = "";
    protected String rightCommand = "";
    protected String caption = "";

    public void addChangeListener(LayerChangeListener listener) {
        this.listener = listener;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void notifyChanged() {
        if (listener != null) {
            listener.actionPerformed();
        }
    }

    public void paint(Graphics g) {
        if ((!leftCommand.equals("")) || (!rightCommand.equals(""))) {
            GUIMisc.drawFooter(g);
            if (!leftCommand.equals("")) {
                GUIMisc.drawLeftCommand(g, leftCommand);
            }
            if (!rightCommand.equals("")) {
                GUIMisc.drawRightCommand(g, rightCommand);
            }
        }
        if (!caption.equals("")) {
            //Draw caption
            GUIMisc.drawHeader(g);
            GUIMisc.drawCaptionOnHeader(caption, g);
        }
        paintCustom(g);
    }

    abstract public void paintCustom(Graphics g);

    public boolean processKeyPress(int keyCode) {
        return false;
    }

    public boolean leftCommandClick() {
        return false;
    }

    public boolean rightCommandClick() {
        return false;
    }

    public boolean selectCommandClick() {
        return false;
    }

    public String getLeftCommand() {
        return leftCommand;
    }

    public void setLeftCommand(String leftCommand) {
        this.leftCommand = leftCommand;
    }

    public String getRightCommand() {
        return rightCommand;
    }

    public void setRightCommand(String rightCommand) {
        this.rightCommand = rightCommand;
    }

    public void refreshView() {
    }

    public boolean isMain() {
        return false;
    }

    public void layerRemoved() {
    }
}
