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

import junit.framework.*;

/**
 * XMPPInputStream Tester.
 *
 * @author <Authors name>
 * @since <pre>10/30/2009</pre>
 * @version 1.0
 */
public class XMPPInputStreamTest extends TestCase {
    public XMPPInputStreamTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testStartSession() throws Exception {
        XMPPInputStream xis = new XMPPInputStream(this.getClass().getResourceAsStream("recv.txt"));
        try {
            xis.startStream();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(xis.supportGoogleToken);
        assertTrue(xis.supportTLS);
        
        xis.requireSuccess();
        xis.startStream();
        assertTrue(xis.supportBind);
        assertTrue(xis.supportSession);
        
        xis.readIQ();
        xis.parse();
    }

    public static Test suite() {
        return new TestSuite(XMPPInputStreamTest.class);
    }
}