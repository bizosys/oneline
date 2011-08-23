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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.plugin.PluginService;
import com.bizosys.oneline.services.async.AsyncProcessor;
import com.bizosys.oneline.services.batch.BatchProcessor;
import com.bizosys.oneline.services.scheduler.SchedulerService;
import com.bizosys.oneline.session.SessionService;
import com.bizosys.oneline.util.StringUtils;

public class ServiceFactory implements Service{

	private static Logger LOG = Logger.getLogger(ServiceFactory.class);
	private static ServiceFactory thisInstance = new ServiceFactory();
	public Configuration conf = null;
	
	private Map<String, Service> userServices = new HashMap<String, Service>(5);

	private ServiceFactory () {
	}

	public static ServiceFactory getInstance() {
		return ServiceFactory.thisInstance;
	}
	
	public boolean init(Configuration config, ServiceMetaData metadata) {
		
		this.conf = config;
		try {
			
			LOG.info("> Starting the Job processors");
			if ( ! BatchProcessor.getInstance().init(config, metadata) ) {
				LOG.fatal("BatchProcessor Service not started.");
				return false;
			}

			LOG.info("> Starting Scheduler service");
			if ( ! SchedulerService.getInstance().init(config, metadata) ) {
				LOG.fatal("SchedulerService not started.");
				return false;
			}
			
			LOG.info("> Starting AsyncProcessor service");
			if ( ! AsyncProcessor.getInstance().init(config, metadata) ) {
				LOG.fatal("AsyncProcessor not started.");
				return false;
			}

			LOG.info("> Starting Front controller service");
			if ( ! FrontController.getInstance().init(config, metadata) ) {
				LOG.fatal("Front controller service not started.");
				return false;
			}
			
			LOG.info("> Starting user services");
			String services = conf.get("services", "");
			String[] serviceL = StringUtils.getStrings(services);
			try {
				for (String serviceClazz : serviceL) {
					serviceClazz = serviceClazz.trim();
					LOG.info("> Starting service from clazz :" + serviceClazz);
					Service service = (Service) Class.forName(serviceClazz).newInstance();

					LOG.info("> Initializing Service:" + service.getName());
					if ( ! service.init(config, metadata) ) {
						LOG.fatal("User Service : " + service.getName() + " not started.");
						return false;
					}
					
					userServices.put(service.getName(), service);
					LOG.info("> Service Registered:" + service.getName());
				}
			} catch (Exception e) {
				LOG.fatal("Service starting failure.", e);
				return false;
			}		
			
			LOG.info("> Starting PlugIn service");
			if ( ! PluginService.getInstance().init(config, metadata) ) {
				LOG.fatal("PlugIn Service not started.");
				return false;
			}
			
				
			LOG.info("> Starting session service");
			if ( ! SessionService.getInstance().init(config, metadata) ) {
				LOG.fatal("SessionService not started.");
				return false;
			}
			
			LOG.info("> All services sucessfully started.");
			
			Runtime.getRuntime().addShutdownHook(
				new Thread( new Runnable() { 
					public void run() {
						LOG.debug("System is Shutting Down...");
						ServiceFactory.getInstance().stop();
					}
				} )
			);
			LOG.info("Shutdown thread is regisered.");
			return true;
		}
		catch (RuntimeException e) {
			LOG.error("Error in starting services", e);
			return false;
		}
	}

	public void process(Request context, Response response) {
	}

	public void stop() {
		FrontController.getInstance().stop();
		SessionService.getInstance().stop();
		SchedulerService.getInstance().stop();
		BatchProcessor.getInstance().stop();
		if ( null == userServices) return;
		for (String serviceName:userServices.keySet()) {
			LOG.info("Stopping the service " +  serviceName);
			try {  
				userServices.get(serviceName).stop(); 
			} catch (Exception e) {
				LOG.warn("Stopping Failure " +  serviceName, e);
			}
		}
	}

	public String getName() {
		return "ServiceFactory";
	}
	
}