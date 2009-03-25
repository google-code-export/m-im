/*
 * UserJID.java
 *
 * Created on 18 Ноябрь 2006 г., 13:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.model;

/**
 *
 * @author Kostya
 */
public class UserJID {

    protected String fullJID = "";
    protected String status = "";
    protected int statusID = 0;

    /** Creates a new instance of UserJID */
    public UserJID() {
    }

    public UserJID(String fullJID, String status, int statusID) {
        setFullJID(fullJID);
        setStatus(status);
        setStatusID(statusID);
    }

    public String getFullJID() {
        return fullJID;
    }

    public void setFullJID(String fullJID) {
        this.fullJID = fullJID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }
}
