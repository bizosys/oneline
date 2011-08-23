/*
* Copyright 2010 Bizosys Technologies Limited
*
* Licensed to the Bizosys Technologies Limited (Bizosys) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The Bizosys licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.bizosys.oneline.session;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.plugin.Pluggable;
import com.bizosys.oneline.services.Request;
import com.bizosys.oneline.services.Response;

/**
 * An extension point interface for writting various handlers for 
 * processing the request. 
 *
 * <p>Handlers are integrated in the following ways:
 *   <lu>
 *     <li>During the start of the request</li>
 *     <li>During the end of request</li>
 *  </lu>    
 * </p>
 * This is where all session activity begins. We gather various pieces of
 * information from the client and server. 
 *   <lu>
 *     <li>Test to see if a session already exists and it's credentials are authentic. </li> 
 *     <li>Test the system load (if we're running on a system which makes such 
 *         information readily available) and halt if it's above an admin definable limit. </li> 
 *     <li>Monitor the usage pattern.</li>
 *     <li>Monitor the response time of processing</li>
 *     <li>Monitor the system resources.</li>
 *     <li>IP Black listing</li>
 * @author abinash
 */
public interface Session extends Pluggable {

	/** The name of the extension point. */
	  public final static String X_POINT_ID = Session.class.getName();

	  public String getClazz();
	  public void setConfig(Configuration config);
	  public Object onStart(Request requst, Response response);
	  public void onFinish(Object startInfo, Request requst, Response response);
}
