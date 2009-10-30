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

import java.util.Vector;

/**
 * Interface class to implement events
 * 
 * @author Swen Kummer, Dustin Hass, Sven Jost
 * @version 2.0
 * @since 1.0
 */
public interface XmppListener {

    //public void onDebug(final String msg);
    /**
     * This event is sent when a parser or connection error occurs.
     */
    public void onConnFailed(final String msg);

    /**
     * This event occurs when the login/authentication process succeeds.
     */
    public void onAuth(final String responseJid);

    /**
     * This event occurs when the login/authentication process fails.
     *
     * @param message some error information
     */
    public void onAuthFailed(final String message);

    /**
     * This event is sent when a message arrives.
     *
     * @param from the jid of the sender
     * @param body the message text
     */
    public void onMessageEvent(final String from, final String body);

    /**
     * This event occurs when someone has removed you from his roster (o rly?)
     *
     * @param jid the jid of the remover
     */
    //public void onContactRemoveEvent(final String jid);
    /**
     * This event occurs for each contact in roster when the roster is queried.
     *
     * @param jid the jid of the contact
     * @param name the nickname of the contact
     * @param group the group in which the contact is saved
     * @param subscription the subscription status of the contact
     */
    public void onContactEvent(final String jid, final String name, final String group, final String subscription);

    public void onContactOverEvent();

    public void onSharedStatusEvent(String status, int show, Vector awayList, Vector busyList, Vector onlineList);

    /**
     * <p>
     * This event occurs when a presence message comes from jabber server. This
     * can also be your own jid. The presence can be one of the following:
     * </p>
     *
     * <ul>
     * <li><code>blank</code>: user is online</li>
     * <li>chat: user is free to chat</li>
     * <li>away: user is away</li>
     * <li>xa: user is not available (extended away).</li>
     * <li>dnd: user is busy (do not disturb).</li>
     * </ul>
     *
     * <p>
     * An offline user will send no status message at all.
     * </p>
     *
     * @param jid the JID of the contact that changed his status
     * @param status the display status
     */
    public void onStatusEvent(final String jid, final String show, final String status);

    /**
     * This event is sent when a subscription request arrives. This means
     * someone has allowed you to see his status.
     *
     * @param jid the jid of the one who wants to subscribe to you
     */
    public void onSubscribeEvent(final String jid);

    /**
     * This event is sent when a subscription remove event arrives. This means
     * someone has taken away your right to see his status.
     *
     * @param jid the jid of the one who removes your subscription
     */
    public void onUnsubscribeEvent(final String jid);
};
