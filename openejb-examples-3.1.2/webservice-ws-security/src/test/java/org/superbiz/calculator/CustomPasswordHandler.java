/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.superbiz.calculator;

import org.apache.ws.security.WSPasswordCallback;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class CustomPasswordHandler implements CallbackHandler {
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        
        if(pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {
            // TODO get the password from the users.properties if possible
            pc.setPassword("waterfall");
            
        } else if(pc.getUsage() == WSPasswordCallback.DECRYPT) {
            pc.setPassword("serverPassword");
            
        } else if(pc.getUsage() == WSPasswordCallback.SIGNATURE) {
            pc.setPassword("serverPassword");
            
        }
        
        if ((pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN)
        	|| (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN_UNKNOWN)) {
            
            SecurityService securityService = SystemInstance.get()
		    .getComponent(SecurityService.class);
	    Object token = null;
	    try {
		securityService.disassociate();

		token = securityService.login(pc.getIdentifer(), pc.getPassword());
		securityService.associate(token);
		
	    } catch (LoginException e) {
		e.printStackTrace();
		throw new SecurityException("wrong password");
	    } finally {
	    }
	}
            

    }
}
