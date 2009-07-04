package com.google.code.mim;

import java.util.Calendar;

/**
 * Basic logging class to centralise calls to System.out. This allows them to be
 * compiled out using if (Constants.LOGGING) checks to further optimise the bytecode size
 */
public class Log {

	private Log() {
		super();
	}

	public static void debug(String s) {
		System.out.println(getTS() + s);
	}
	
	public static void info(String s) {
		System.out.println(getTS() + s);
	}

	public static void error(String s, Exception e) {
		System.out.println(getTS() + s + ":" + e.getMessage());
	}
	
	private static String getTS() {
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		StringBuffer sb = new StringBuffer(10);
		sb.append(h);
		sb.append(':');
		sb.append(m);
		sb.append('.');
		sb.append(s);
		sb.append(':');
		sb.append(' ');
		return sb.toString();
	}
	
}
