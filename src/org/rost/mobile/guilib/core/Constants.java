package org.rost.mobile.guilib.core;

public class Constants {

	public static final boolean LOGGING = true; // Global Variable for turning logging on and off for debug or production versions

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
