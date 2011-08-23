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
package com.bizosys.oneline.services;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.plugin.PluginService;
import com.bizosys.oneline.session.SessionService;
import com.bizosys.oneline.util.StringUtils;

public class FrontController  implements Service {
	
	 private final static Logger LOG = Logger.getLogger(FrontController.class);
	 
	 static FrontController instance = null;
	 public static FrontController getInstance() {
		 if ( null != instance) return instance;
		 synchronized (FrontController.class) {
			 if ( null != instance) return instance;
			 instance = new FrontController();
		 }
		 return instance;
	 }
	    
	 public void process(Request request, Response response)
	 {
		 LOG.info("> Front Controller - Enter");
		 if ( LOG.isDebugEnabled()) LOG.debug(request.toString() );

		 LOG.info("> Processing session Handlers - OnStart");
		 SessionService ss = SessionService.getInstance();

		 Map<String, Object> startInfoL = ss.executeOnStart(request,response);
		 
		 /**
		  * The request dispatched to the sensor for further processing.
		  */
		 if ( LOG.isInfoEnabled()) LOG.info("> Resolving service:" + request.serviceId);
		 Service plugin = PluginService.getInstance().getPlugin(request.serviceId);
		 if ( null == plugin) {
			 LOG.fatal(StringUtils.FatalPrefix + "Missing service:" + request.serviceId);
			 response.error(request.serviceId + " Service is not available." ) ;
		 } else {
			 LOG.info("> Processing service.");
			 plugin.process(request, response);
		 }

		 LOG.info("> Processing session Handlers - OnFinish");
		 ss.executeOnFinish(startInfoL,request,response);

		 LOG.debug("> Front Controller - Exit");
    }

	public boolean init(Configuration config, ServiceMetaData metadata) {
		return true;
	}

	public void stop() {
		
	}

	public String getName() {
		return "FrontController";
	}

}