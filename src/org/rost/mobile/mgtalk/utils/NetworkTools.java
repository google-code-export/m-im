/*
 * NetworkTools.java
 *
 * Created on November 16, 2006, 11:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.utils;

/**
 *
 * @author kostya
 */
public class NetworkTools {

    /** Creates a new instance of NetworkTools */
    public NetworkTools() {
    }

    public static String XstringToUTF(String s) {
        int i = 0;
        StringBuffer stringbuffer = new StringBuffer();

        for (int j = s.length(); i < j; i++) {
            int c = (int) s.charAt(i);
            if ((c >= 1) && (c <= 0x7f)) {
                stringbuffer.append((char) c);
            }
            if (((c >= 0x80) && (c <= 0x7ff)) || (c == 0)) {
                stringbuffer.append((char) (0xc0 | (0x1f & (c >> 6))));
                stringbuffer.append((char) (0x80 | (0x3f & c)));
            }
            if ((c >= 0x800) && (c <= 0xffff)) {
                stringbuffer.append(((char) (0xe0 | (0x0f & (c >> 12)))));
                stringbuffer.append((char) (0x80 | (0x3f & (c >> 6))));
                stringbuffer.append(((char) (0x80 | (0x3f & c))));
            }
        }

        return stringbuffer.toString();
    }

    public static byte[] md5It(String s) {
        byte bb[] = new byte[16];
        try {
            MD5 md2 = new MD5(s.getBytes());
            return md2.doFinal();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bb;
    }

    public static byte[] md5It(byte[] s, int l) {
        byte bb[] = new byte[16];
        try {
            byte tmp[] = new byte[l];
            for (int i = 0; i < l; i++) {
                tmp[i] = s[i];
            }
            MD5 md2 = new MD5(tmp);
            return md2.doFinal();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bb;
    }

    public static String getStatusShortNameByNumber(int i) {
        String show = "";
        if (i == 1) {
            show = "away";
        }
        if (i == 2) {
            show = "xa";
        }
        if (i == 3) {
            show = "dnd";
        }
        return show;
    }

    public static String replace(String src, String what, String to) {
        int f = -to.length();
        while ((f = src.indexOf(what, f + to.length())) != -1) {
            src = src.substring(0, f) + to + src.substring(f + what.length(), src.length());
        }
        return src;
    }

    public static String toXML(String source) {
        source = replace(source, "&", "&amp;");
        source = replace(source, "<", "&lt;");
        source = replace(source, ">", "&gt;");
        source = replace(source, "\"", "&quot;");
        return source;
    }
}
