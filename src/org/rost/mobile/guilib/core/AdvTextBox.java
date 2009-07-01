/*
 * AdvTextBox.java
 *
 * Created on 13 Ноябрь 2006 г., 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import org.rost.mobile.guilib.components.TextBoxItem;
import org.rost.mobile.guilib.core.BaseCanvas;
import org.rost.mobile.guilib.core.GUIStore;

/**
 *
 * @author Kostya
 */
public class AdvTextBox extends TextBox implements CommandListener {

    /** Creates a new instance of AdvTextBox */
    Command ok;
    Command cancel;
    TextBoxItem box;

    public AdvTextBox(String text, TextBoxItem callback) {
        super("Input text:", text, 65536, 0);
        box = callback;
        ok = new Command("Ok", Command.OK, 0);
        cancel = new Command("Cancel", Command.CANCEL, 0);
        addCommand(ok);
        addCommand(cancel);
        setCommandListener(this);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command.equals(ok)) {
            box.setValue(getString());
            GUIStore.getDisplay().setCurrent(GUIStore.getCanvas());
            return;
        }
        if (command.equals(cancel)) {
            GUIStore.getDisplay().setCurrent(GUIStore.getCanvas());
        }
    }
}
