package org.rost.mobile.mgtalk.utils;

import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.rost.mobile.guilib.core.Constants;

import com.google.code.mim.Log;

/**
 *
 */
/**
 * @author Vorobev
 *
 */
public class XmlNode {

    private Vector childs;
    private Hashtable attributes;
    private String name;
    private String value;
    private InputStreamReader reader = null;
    private String buff;
    int pos = 0;

    public XmlNode() {
        super();
        attributes = new Hashtable();
        childs = new Vector();
    }

    public int getNextCharacter() throws Exception {
        int i = -1;
        int j = reader.read();
        if (j == -1) {
            throw new Exception("InputStreamReader cannot read data");
        }
        {
            j &= 0xff;
            //boolean flag = false;
            switch (j >> 4) {
                case 8: // '\b'
                case 9: // '\t'
                case 10: // '\n'
                case 11: // '\013'
                default:
                    break;

                case 0: // '\0'
                case 1: // '\001'
                case 2: // '\002'
                case 3: // '\003'
                case 4: // '\004'
                case 5: // '\005'
                case 6: // '\006'
                case 7: // '\007'
                    i = j;
                    break;

                case 12: // '\f'
                case 13: // '\r'
                    i = j & 0x1f;
                    i <<= 6;
                    int k = reader.read();
                    if ((k & 0xc0) != 128) {
                        throw new Exception("Bad UTF-8 Encoding encountered");
                    }
                    i += k & 0x3f;
                    break;

                case 14: // '\016'
                    i = j & 0xf;
                    i <<= 6;
                    int l = reader.read();
                    if ((l & 0xc0) != 128) {
                        throw new Exception("Bad UTF-8 Encoding encountered");
                    }
                    i += l & 0x3f;
                    i <<= 6;
                    l = reader.read();
                    if ((l & 0xc0) != 128) {
                        throw new Exception("Bad UTF-8 Encoding encountered");
                    }
                    i += l & 0x3f;
                    break;
            }
        }
        return i;
    }

    private char nextChar() throws Exception {
        try {
            if (pos > buff.length() - 1) {
                int ccc;
                boolean ok = true;
                do {
                    ok = true;
                    ccc = getNextCharacter();
                    if (ccc == 10) {
                        ok = false;
                    }
                } while (!ok);
                buff += (char) ccc;
                if (Constants.LOGGING) {
                	Log.info("nextChar() error: " + (char) ccc);
                }
            }
            char ch = buff.charAt(pos++);
            if (ch == -1) {
            	if (Constants.LOGGING) {
                	Log.info("conn closed");
            	}
                throw new Exception("Conn closed");
            }
            return ch;
        } catch (Exception e) {
            throw new Exception("nextChar buff = " + buff + ", pos = " + pos + ": " + e.toString());
        }
    }

    public void init(String stBuff, InputStreamReader s) throws Exception {
        reader = s;
        buff = stBuff;
        try {
            char ch = nextChar();
            while (ch == ' ') {
                ch = nextChar();
            }
            if (ch != '<') {
                return;
            }
            if (nextChar() == '?') {
                //Read header
                while (nextChar() != '>') {
                }
                while (nextChar() != '>') {
                }

                nextChar();
            } else {
                pos--;
            }
            String n = "";
            n += nextChar();
            while ((ch = nextChar()) != '>') {
                n += ch;
            }
            boolean hasEnd = false;
            if (n.charAt(n.length() - 1) == '/') {
                hasEnd = true;
                n = n.substring(0, n.length() - 1);
            }
            if (n.indexOf(' ') != -1) {
                String attrs = n.substring(n.indexOf(' '), n.length()).trim() + ' ';
                n = n.substring(0, n.indexOf(' '));
                for (; attrs.length() > 1;) {
                    String aName = attrs.substring(0, attrs.indexOf('='));
                    attrs = attrs.substring(attrs.indexOf('=') + 1, attrs.length());
                    char b = attrs.charAt(0);
                    String aValue = attrs.substring(1, attrs.indexOf(b, 1));
                    attrs = attrs.substring(attrs.indexOf(b, 1) + 2, attrs.length());
                    attributes.put(aName, aValue);
                }
            }
            name = n;
            if (!hasEnd) {
                ch = nextChar();
                if (ch == '<') {
                    pos--;
                    while ((ch = nextChar()) == '<') {
                        if (nextChar() == '/') {
                            while ((ch = nextChar()) != '>') {
                            }
                            break;
                        }
                        pos--;
                        XmlNode x = new XmlNode();
                        x.init("<" + nextChar(), s);
                        childs.addElement(x);
                    }
                } else {
                    value = "" + ch;
                    while ((ch = nextChar()) != '<') {
                        value += ch;
                    }
                    while ((ch = nextChar()) != '>') {
                    }
//					setValue(value);
                }
            }
        //Read data
        } catch (Exception e) {
            throw new Exception("Could not read: " + e);
        }
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        if (value == null) {
            return "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean hasChild(String n) {
        for (int i = 0; i < childs.size(); i++) {
            XmlNode x = (XmlNode) childs.elementAt(i);
            if (x.getName().equals("n")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasValueOfChild(String n) {
        for (int i = 0; i < childs.size(); i++) {
            XmlNode x = (XmlNode) childs.elementAt(i);
            if (x.getValue().equals(n)) {
                return true;
            }
        }
        return false;
    }

    public String childValue(String n) {
        for (int i = 0; i < childs.size(); i++) {
            XmlNode x = (XmlNode) childs.elementAt(i);
            if (x.getName().equals(n)) {
                return x.getValue();
            }
        }
        return "";
    }

    public XmlNode child(String n) {
        for (int i = 0; i < childs.size(); i++) {
            XmlNode x = (XmlNode) childs.elementAt(i);
            if (x.getName().equals(n)) {
                return x;
            }
        }
        return new XmlNode();
    }

    public String getAttr(String a) {
        String val = (String) attributes.get(a);
        if (val == null) {
            return "";
        }
        return val;
    }

    public String toString(int level) {
        String pr = "";
        for (int i = 0; i < level; i++) {
            pr += "\t";
        }
        String res = getName();
        for (Enumeration e = attributes.keys(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            res += (" " + name + "=" + attributes.get(name));
        }
        res += " == " + getValue();
        for (int i = 0; i < childs.size(); i++) {
            res += ("\n" + pr + "\t" + ((XmlNode) childs.elementAt(i)).toString(level + 1));
        }
        return pr + res;
    }

    public String toString() {
        return toString(0);
    }

    /**
     * @return Returns the childs.
     */
    public Vector getChilds() {
        return childs;
    }

    /**
     * @param childs The childs to set.
     */
    public void setChilds(Vector childs) {
        this.childs = childs;
    }
}
