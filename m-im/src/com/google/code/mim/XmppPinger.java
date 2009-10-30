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



public class XmppPinger implements Runnable {

	//public static final int PING_EVERY_MS = 5 * 60 * 1000; // 5 mins
	//public static final int PING_EVERY_MS = 10 * 1000; // 10 seconds for debugging
	
	// IF LOGGING (Debug Build), ping server every 30 seconds otherwise every 5 minutes...
//	public static final int PING_EVERY_MS = Constants.LOGGING ? (30 * 1000) : (5 * 60 * 1000);
	
	private int pingCount = 0;
	private boolean running = false;
	
	public XmppPinger() {
		super();
	}

	public void run() {
//		running = true;
//		while (running) {
//			AppStore.getJxa().sendPing(++pingCount);
//			try {
//				Thread.sleep(PING_EVERY_MS);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
}
