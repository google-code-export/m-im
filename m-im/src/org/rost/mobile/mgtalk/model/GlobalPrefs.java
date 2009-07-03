package org.rost.mobile.mgtalk.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * Global Preferences which are used for each newly created profile.
 * Arguably Profile should have an "override" setting to inherit then change each of these
 * 
 * @author mmcnamee
 */
public class GlobalPrefs {
	
    private static final String STORE_NAME = "MIMGlobalPrefs";
    private static final int RECORDID = 1;

	private boolean soundEnabled = true;
	private int volume = 5;
	
	private boolean vibrate = true;
	private int vibrateTime = 8; //800ms

	private String displayName = "";
	
	public GlobalPrefs() {
		super();
	}

    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        try {
            os.writeUTF(displayName);
            os.writeBoolean(soundEnabled);
            os.writeInt(volume);
            os.writeBoolean(vibrate);
            os.writeInt(vibrateTime);
            os.flush();
            for (int i = baos.size(); i < 512; i++) {
                os.writeInt(0);
            }
            os.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[512];
        }
    }

    public boolean fromByteArray(byte data[]) {
        try {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
            displayName = is.readUTF();
            soundEnabled = is.readBoolean();
            volume = is.readInt();
            vibrate = is.readBoolean();
            vibrateTime = is.readInt();
            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

	public synchronized boolean isSoundEnabled() {
		return soundEnabled;
	}

	public synchronized void setSoundEnabled(boolean sounds) {
		this.soundEnabled = sounds;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public void save() {
		RecordStore store = null;
        try {
        	try {
        		store = RecordStore.openRecordStore(STORE_NAME, false);
        		store.closeRecordStore();
        		RecordStore.deleteRecordStore(STORE_NAME);
        		throw new RecordStoreNotFoundException("Deleted");
        	} catch (RecordStoreNotFoundException e) {
        		store = RecordStore.openRecordStore(STORE_NAME, true);
        	}
            byte[] prefsData = this.toByteArray();
            //store.addRecord(prefsData, 0, prefsData.length);
            store.addRecord(prefsData, 0, 512);
        } catch (Throwable t) {
        	System.out.println("Error saving global prefs!" + t);
        	t.printStackTrace();
        } finally {
        	if (store != null) {
        		try {
       				store.closeRecordStore();
				} catch (RecordStoreNotOpenException e) {
					e.printStackTrace();
				} catch (RecordStoreException e) {
					e.printStackTrace();
				}
        	}        	
        }
	}

	public void load() {
		RecordStore store = null;
        try {
    		store = RecordStore.openRecordStore(STORE_NAME, false);
    		// All ok, it exists, so we can load from it...
           	this.fromByteArray(store.getRecord(RECORDID));
    	} catch (RecordStoreNotFoundException e) {
    		// didn't exist, not really a problem - it will be created on save()
        } catch (Exception e) {
        	// oops, some other error!
        } finally {
        	if (store != null) {
        		try {
       				store.closeRecordStore();
				} catch (RecordStoreNotOpenException e) {
					e.printStackTrace();
				} catch (RecordStoreException e) {
					e.printStackTrace();
				}
        	}
        }
	}
}
