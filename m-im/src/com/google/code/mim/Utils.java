/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.mim;

/**
 *
 * @author yaochunlin
 */
public class Utils {
    public static String statusIDtoString(int statusID) {
        switch (statusID) {
            case 1:
                return "away";
            case 2:
                return "xa";
            case 3:
                return "dnd";
        }
        return "";
    }

        public static int statusStringToNumber(String str) {
        if (str.equals(XMPP.AWAY)) {
            return 1;
        }
        if (str.equals(XMPP.NA)) {
            return 2;
        }
        if (str.equals("xa")) {
            return 2;
        }
        if (str.equals("busy")) {
            return 3;
        }
        if (str.equals(XMPP.DND)) {
            return 3;
        }
        return 0;
    }

}
