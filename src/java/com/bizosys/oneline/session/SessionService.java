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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.plugin.Extension;
import com.bizosys.oneline.plugin.ExtensionPoint;
import com.bizosys.oneline.plugin.PluginRepository;
import com.bizosys.oneline.plugin.PluginRuntimeException;
import com.bizosys.oneline.services.Request;
import com.bizosys.oneline.services.Response;
import com.bizosys.oneline.services.Service;
import com.bizosys.oneline.services.ServiceMetaData;

/** 
 * Creates and caches {@link Session} handlers. Session handlers should define
 * the attribute "onStart - true/false" , 
 * "onFinish - true/false.  
 * Configuration object is used for caching. Cache key is constructed
 * from appending handler name (eg. perf) to constant
 * {@link Session#X_POINT_ID}.
 */
public class SessionService implements Service{

	private static Logger LOG = Logger.getLogger(SessionService.class);
	private static final String START_MONITOR = "OnStart";
	private static final String FINISH_MONITOR = "OnFinish";
	
    private Map<String, Session> allMonitors = new HashMap<String, Session>();
    private List<Session> startMonitors;
    private List<Session> finishMonitors;
    
    private Configuration conf;
    
    private static SessionService instance = null;
    public static SessionService getInstance() {
    	if ( null != instance) return instance;
    	synchronized (SessionService.class) {
    		if ( null != instance) return instance;
    		instance = new SessionService();
		}
    	return instance;
    }
    
	public boolean init(Configuration conf, ServiceMetaData meta) {
		this.conf = conf;
		ExtensionPoint extensionPoint = PluginRepository.get(conf).getExtensionPoint(
	    		Session.X_POINT_ID);
	    if (extensionPoint == null) {
		  String errorMsg = "x-point " + Session.X_POINT_ID + " not found.";
		  LOG.fatal(errorMsg);
	      return false;
	    }
	    
	    //Load the start and finish Monitors; They are cashed in the class level.
	    startMonitors = this.getStartMonitors(extensionPoint);
	    finishMonitors = this.getFinishMonitors(extensionPoint);
	    allMonitors.clear();
	    return true;
	}

	public Map<String, Object> executeOnStart( Request request, Response response) {
		
		if ( null == startMonitors) return null;
		int startMonitorT = startMonitors.size();
		Map<String, Object> startInfoL = new HashMap<String, Object>(startMonitorT);
		for ( int i=0; i<startMonitorT; i++ ) {
			Session startMon = this.startMonitors.get(i); 
			Object startInfo = startMon.onStart(request, response);
			if ( null != startInfo ) startInfoL.put(startMon.getClazz(), startInfo);
		}
		return startInfoL;
	}
	
	public void executeOnFinish(Map<String, Object> startInfoL, 
			Request context, Response response) {
		
		if ( null == finishMonitors) return;
		int finishMonitorT = finishMonitors.size();
		for ( int i=0; i<finishMonitorT; i++ ) {
			Session finishMon = this.finishMonitors.get(i); 
			finishMon.onFinish(startInfoL.get(finishMon.getClazz()), context, response);
		}
	}
	
	/**
	 * Get all the start session service monitors instances
	 * @return List<Session> Start monitors
	 */
	private List<Session> getStartMonitors(ExtensionPoint extensionPoint) {
		List<Session> startMonitors = findExtension(START_MONITOR, extensionPoint);
		return startMonitors;
	}
	
	/**
	 * Get all the end session service monitors instances
	 * @return List<Session> End monitors
	 */
	private List<Session> getFinishMonitors(ExtensionPoint extensionPoint) {
		List<Session> finishMonitors = findExtension(FINISH_MONITOR, extensionPoint);
		return finishMonitors;
	}

	private List<Session> findExtension(String monitorType, ExtensionPoint extensionPoint) {

		List<Session> monitors = new ArrayList<Session>();
		
		Extension[] extensions = extensionPoint.getExtensions();

        for (int i = 0; i < extensions.length; i++) {
          Extension extension = extensions[i];
          String definedType = extension.getAttribute(monitorType);
          if ( null == definedType) { 
    		  if (LOG.isDebugEnabled()) {
    			  LOG.debug("findExtension ; Defined type=NULL ; monitorType=" + monitorType);
    			  LOG.debug("findExtension ; Defined type=NULL ; extensionId=" + extension.getId());
    		  }
        	  throw new IllegalArgumentException( extension.getId() + "-" +
        			  START_MONITOR + " AND " + FINISH_MONITOR + "parameter not defined.");
          }
          if ( "true".equals( definedType.toLowerCase() ) ) {
        	  try {
        		  
        		  if ( allMonitors.containsKey(extension.getClazz())) {
        			  monitors.add(allMonitors.get(extension.getClazz()) );
        		  } else {
        			  
            		  Object instance = extension.getExtensionInstance();
            		  Session aSession = (Session) instance ;
            		  aSession.setConfig(this.conf);
                	  monitors.add((Session)instance );
                	  allMonitors.put( extension.getClazz(),(Session)instance );
        		  }
        		  
        	  } catch (  PluginRuntimeException ex) {
        		  if (LOG.isDebugEnabled()) {
        			  LOG.debug("findExtension ; Minitor instantiation");
        			  throw new IllegalArgumentException("Monitor not initialized.",ex);
        		  }
        	  }
          }
        }
        return monitors;
      }

	public void process(Request context, Response response) {
	}

	public void stop() {
	}

	public String getName() {
		return "SessionService";
	}  
}
