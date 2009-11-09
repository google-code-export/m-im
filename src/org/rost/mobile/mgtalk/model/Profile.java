/*
 * Profile.java
 *
 * Created on November 14, 2006, 4:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.rost.mobile.mgtalk.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.rost.mobile.guilib.core.Constants;
import org.rost.mobile.mgtalk.AppStore;

/**
 *
 * @author kostya
 */
public class Profile {

    /** Creates a new instance of Profile */
    protected String userName = Constants.DEFAULT_USERNAME;
    protected String displayName = "";
    protected String password = "";
    protected String host = Constants.DEFAULT_HOST;
    protected String port = Constants.DEFAULT_NONSSL_PORT;
    protected boolean SSL = false;
    protected String resource = Constants.DEFAULT_RESOURCE; // set to the mobile device name if available (see constructor)
    protected String status = Constants.DEFAULT_STATUS_MSG;
    protected boolean statusActive = false;
    protected String name = Constants.DEFAULT_PROFILE_NAME;
    protected boolean autoConnect = false;
    protected boolean autoReconnect = true;
    protected boolean keepalive = Constants.DEFAULT_KEEPALIVE;
    protected boolean xmppPing = Constants.DEFAULT_XMPP_PING;
    protected boolean google = true;
    protected boolean sortByName = false;
    protected boolean moveChattersTop = true;
    protected int statusID = Constants.DEFAULT_STATUS_ID;//
    protected int volume = Constants.DEFAULT_VOLUME;
    protected boolean vibrate = true;
    protected int vibrateTime = Constants.DEFAULT_VIBR_TIME;
    protected int historyLength = Constants.DEFAULT_HISTORY_LEN;
    protected boolean smiles = false;
    protected boolean showOffline = false;
    protected String fullJID = "";
    protected int id = -1;

    public Profile(boolean initWithGlobalValues) {
    	String meplatform = System.getProperty("microedition.platform");
    	if (meplatform != null && meplatform.length() > 0) {
    		int index = meplatform.indexOf('/');
    		if (index == -1) { index = meplatform.length(); }
    		resource = meplatform.substring(0, index);
    	}
    	
    	if (initWithGlobalValues) {
    		GlobalPrefs prefs = AppStore.getGlobalPrefs();
    		this.displayName = prefs.getDisplayName();
    		this.volume = prefs.getVolume();
    		this.vibrate = prefs.isVibrate();
    		this.vibrateTime = prefs.getVibrateTime();
    	}
    	
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        try {
            os.writeUTF(userName);
            os.writeUTF(displayName);
            os.writeUTF(password);
            os.writeUTF(host);
            os.writeUTF(port);
            os.writeUTF(resource);
            os.writeUTF(status);
            os.writeBoolean(statusActive);
            os.writeUTF(name);
            os.writeBoolean(autoConnect);
            os.writeBoolean(autoReconnect);
            os.writeBoolean(keepalive);
            os.writeBoolean(xmppPing);
            os.writeBoolean(google);
            os.writeBoolean(SSL);
            os.writeBoolean(sortByName);
            os.writeBoolean(moveChattersTop);
            os.writeInt(statusID);
            os.writeInt(volume);
            os.writeBoolean(vibrate);
            os.writeInt(vibrateTime);
            os.writeInt(historyLength);
            os.writeBoolean(smiles);
            os.writeBoolean(showOffline);
            os.flush();
            for (int i = baos.size(); i < 512; i++) {
                os.writeInt(0);
            }
            os.close();
            return baos.toByteArray();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new byte[512];
        }
    }

    public boolean fromByteArray(byte data[]) {
        try {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
            userName = is.readUTF();
            displayName = is.readUTF();
            password = is.readUTF();
            host = is.readUTF();
            port = is.readUTF();
            resource = is.readUTF();
            status = is.readUTF();
            statusActive = is.readBoolean();
            name = is.readUTF();
            autoConnect = is.readBoolean();
            autoReconnect = is.readBoolean();
            keepalive = is.readBoolean();
            xmppPing = is.readBoolean();
            google = is.readBoolean();
            SSL = is.readBoolean();
            sortByName = is.readBoolean();
            moveChattersTop = is.readBoolean();
            statusID = is.readInt();
            volume = is.readInt();
            vibrate = is.readBoolean();
            vibrateTime = is.readInt();
            historyLength = is.readInt();
            smiles = is.readBoolean();
            showOffline = is.readBoolean();

            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

	public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public boolean isKeepalive() {
		return keepalive;
	}

	public void setKeepalive(boolean keepalive) {
		this.keepalive = keepalive;
	}

	public boolean isXmppPing() {
		return xmppPing;
	}

	public void setXmppPing(boolean xmppPing) {
		this.xmppPing = xmppPing;
	}

    public boolean isGoogle() {
        return google;
    }

    public void setGoogle(boolean google) {
        this.google = google;
    }

    public boolean isSSL() {
        return SSL;
    }

    public void setSSL(boolean SSL) {
        this.SSL = SSL;
    }

    public boolean isSortByName() {
        return sortByName;
    }

    public void setSortByName(boolean sortByName) {
        this.sortByName = sortByName;
    }

    public boolean isMoveChattersTop() {
        return moveChattersTop;
    }

    public void setMoveChattersTop(boolean moveChattersTop) {
        this.moveChattersTop = moveChattersTop;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	public int getVibrateTime() {
		return vibrateTime;
	}

	public void setVibrateTime(int vibrateTime) {
		this.vibrateTime = vibrateTime;
	}

    public int getHistoryLength() {
        return historyLength;
    }

    public void setHistoryLength(int historyLength) {
        this.historyLength = historyLength;
    }

    public boolean isStatusActive() {
        return statusActive;
    }

    public void setStatusActive(boolean statusActive) {
        this.statusActive = statusActive;
    }

    public boolean isSmiles() {
        return smiles;
    }

    public void setSmiles(boolean smiles) {
        this.smiles = smiles;
    }

    public boolean isShowOffline() {
        return showOffline;
    }

    public void setShowOffline(boolean showOffline) {
        this.showOffline = showOffline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullJID() {
        return fullJID.equals("") ? userName : fullJID;
    }

    public void setFullJID(String fullJID) {
        this.fullJID = fullJID;
    }
    
}
