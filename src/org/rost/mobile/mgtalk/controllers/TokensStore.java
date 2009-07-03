/*
 * TokensStore.java
 *
 * Created on November 19, 2006, 12:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

/**
 *
 * @author kostya
 */
class Token {

    protected String user = "";
    protected String token = "";

    public void setSource(String source) {
        user = source.substring(0, source.indexOf(";"));
        token = source.substring(source.indexOf(";") + 1);
    }

    public Token(String source) {
        setSource(source);
    }

    public Token(byte data[]) {
        try {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
            setSource(is.readUTF());
            is.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

public class TokensStore {

    /** Creates a new instance of TokensStore */
    static String STORE_NAME = "MGTalkTokens";

    public TokensStore() {
    }

    public String getTokenForUser(String user) {
        try {
            RecordStore store = RecordStore.openRecordStore(STORE_NAME, true);
            for (RecordEnumeration e = store.enumerateRecords(null, null, false); e.hasNextElement();) {
                int id = e.nextRecordId();
                Token token = new Token(store.getRecord(id));
                if (token.getUser().toLowerCase().equals(user.toLowerCase())) {
                    store.closeRecordStore();
                    return token.getToken();
                }
            }
            store.closeRecordStore();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    public void clearTokensForUser(String user) {
        try {
            RecordStore store = RecordStore.openRecordStore(STORE_NAME, true);
            for (RecordEnumeration e = store.enumerateRecords(null, null, false); e.hasNextElement();) {
                int id = e.nextRecordId();
                Token token = new Token(store.getRecord(id));
                if (token.getUser().toLowerCase().equals(user.toLowerCase())) {
                    store.deleteRecord(id);
                    store.closeRecordStore();
                    return;
                }
            }
            store.closeRecordStore();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void addTokenForUser(String user, String token) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);
            os.writeUTF(user + ";" + token);
            os.flush();
            RecordStore store = RecordStore.openRecordStore(STORE_NAME, true);
            store.addRecord(baos.toByteArray(), 0, baos.size());
            store.closeRecordStore();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
