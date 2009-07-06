/*
 * ReadOnlyTextItem.java
 *
 * Created on November 14, 2006, 11:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.components;

/**
 *
 * @author kostya
 */
public class ReadOnlyTextItem extends StaticRichText {

    /** Creates a new instance of ReadOnlyTextItem */
    public boolean canBeSelected() {
        return false;
    }
}
