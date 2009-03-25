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

    public TextSplitter() {
    }
    static char[] delims = {',', '.', '\n', ':', '-', '+', ';', '!', '?', ' '};

    public static Vector split(String str, Font font, int width) {
        Vector v = new Vector();
        int lastIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            for (int j = 0; j < delims.length; j++) {
                try {
                    if (str.charAt(i) == delims[j]) {
                        int k = i + 1;
                        if (k < str.length()) {
                            while (str.charAt(k) == ' ' && k < str.length()) {
                                k++;
                            }
                        }
                        String slice = str.substring(lastIndex, k);
                        lastIndex = k;
                        i = k;
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
        }
        String slice = str.substring(lastIndex);
//        System.out.println("slice = ["+slice+"]");
        v.addElement(slice);

        return v;
    }
}

