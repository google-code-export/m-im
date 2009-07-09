/*
 * TextSplitter.java
 *
 * Created on November 12, 2006, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.guilib.core;

import java.util.Vector;
import javax.microedition.lcdui.Font;

/**
 *
 * @author kostya
 */
public class TextSplitter {

    private static boolean canSplit(char c, char nextc) {
        return (nextc < 0 || nextc > 127) || (c < 0 || c > 127) || delims.indexOf(c) > -1;
    }

    public TextSplitter() {
    }
    static String delims = ",.\n:-+;!? ";

    /*
     * This method requires a unit test as it's throwing lots of ArrayIndexOutOfBoundsExceptions in the Nokia 6230i emulator
     */
    public static Vector split(String str, Font font, int width) {
        Vector v = new Vector();
        int lastIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            try {
                final char currentChar = str.charAt(i);
                final char nextChar = i + 1 < str.length() ? str.charAt(i + 1) : '\0';
                if (canSplit(currentChar, nextChar) || i == str.length() - 1) {
                    int k = i + 1;
                    if (k < str.length()) {
                        while (str.charAt(k) == ' ' && k < str.length()) {
                            k++;
                        }
                    }
                    String slice = str.substring(lastIndex, k);
                    lastIndex = k;
                    i = k - 1;
                    if (font.stringWidth(slice) > width) {
                        while (font.stringWidth(slice) > width) {
                            k = 0;
                            while (font.substringWidth(slice, 0, k) < width) {
                                k++;
                            }
                            String subSlice = slice.substring(0, k - 1);
                            //                            System.out.println("subSlice = ["+subSlice+"]");
                            v.addElement(subSlice);
                            slice = slice.substring(k - 1);
                        }
                    }
                    //                    System.out.println("slice = ["+slice+"]");
                    v.addElement(slice);
                }
            } catch (Throwable t) {
//		    t.printStackTrace();
                }

        }
        return v;
    }
}

