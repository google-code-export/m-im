package org.rost.mobile.guilib.core;

public class Constants {

	public static final boolean LOGGING = false; // Global Variable for turning logging on and off for debug or production versions

	public static final boolean DEFAULT_KEEPALIVE = true;
	public static final boolean DEFAULT_XMPP_PING = true;
	
	public static final String DEFAULT_PROFILE_NAME = "Google";
	public static final String DEFAULT_HOST         = "talk.google.com";
	public static final String DEFAULT_NONSSL_PORT  = "5222";
	public static final String DEFAULT_SSL_PORT     = "5223";
	public static final String DEFAULT_RESOURCE     = "Mobile";
	public static final String DEFAULT_USERNAME     = "@gmail.com";
	public static final String DEFAULT_STATUS_MSG   = "m-im on my phone";	
	
	public static final int    DEFAULT_HISTORY_LEN  = 20;
	public static final int    DEFAULT_VOLUME       = 9;
	public static final int    DEFAULT_VIBR_TIME    = 8; //default 800ms
	public static final int    DEFAULT_STATUS_ID    = 0;

	public static final int screenWidth;
	public static final int screenHeight;
	
	static {
		//screenWidth = Display.getDisplay().getCurrent().getWidth();
		screenWidth = GUIStore.getCanvas().getWidth();
		screenHeight = GUIStore.getCanvas().getHeight();
	}

	public static void init() {		
	}
	
}
