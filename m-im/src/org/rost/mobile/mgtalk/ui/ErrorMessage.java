/*
 * ErrorMessage.java
 *
 * Created on 15 Ноябрь 2006 г., 16:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.ui;

import org.rost.mobile.guilib.components.StaticRichText;
import org.rost.mobile.guilib.components.layers.MessageDialog;
import org.rost.mobile.guilib.core.GUIMisc;
import org.rost.mobile.guilib.core.GUIStore;
import org.rost.mobile.mgtalk.i18n.i18n;

/**
 *
 * @author Kostya
 */
public class ErrorMessage extends MessageDialog {

    /** Creates a new instance of ErrorMessage */
    StaticRichText text;

    public ErrorMessage() {
        setRightCommand(i18n.getMessage("close"));
        text = new StaticRichText(GUIMisc.getMessageItemWidth());
        setItem(text);
    }

    public void showErrorMessage(String err) {
        setErrorMessage(err);
        refreshView();
        GUIStore.getManager().pushFront(this);
        GUIStore.getManager().notifyChanged();
    }
    String errMessage = "";

    public void setErrorMessage(String message) {
        errMessage = message;
    }

    public void refreshView() {
        text.clear();
        text.addText(errMessage, true, -1);
    }

    public boolean rightCommandClick() {
        GUIStore.getManager().removeLayer(this);
        GUIStore.getManager().notifyChanged();
        return true;
    }

    public boolean leftCommandClick() {
        return true;
    }
}
