/*
 * Copyright (c) 2009, Chunlin Yao
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <copyright holder> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <copyright holder> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
