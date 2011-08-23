/*
* Copyright 2010 The Apache Software Foundation
*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
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
package com.bizosys.oneline.plugin;

import java.net.URL;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.services.Request;
import com.bizosys.oneline.services.Response;
import com.bizosys.oneline.services.Service;
import com.bizosys.oneline.services.ServiceMetaData;
import com.bizosys.oneline.util.StringUtils;

/**
 * Creates and caches {@link Service} plugins. Sensor plugins should define
 * the attribute "sensorLogo - The logo file" , 
 * "sensorTags - Tags for facetted browsing", "sensorRsl - The Flex component" 
 * Configuration object is used for caching. Cache key is constructed
 * from appending sensor name (eg. leave) to constant
 * {@link Service#X_POINT_ID}.
 */
public class PluginService implements Service {

	private static Logger LOG = Logger.getLogger(PluginService.class);
	
	 static PluginService instance = null;
	 public static PluginService getInstance() {
		 if ( null != instance) return instance;
		 synchronized (PluginService.class) {
			 if ( null != instance) return instance;
			 instance = new PluginService();
		 }
		 return instance;
	 }

    private Configuration conf = null;
	private ExtensionPoint extensionPoint;
    private Hashtable<String, ServiceMetaData> sensorTOC = null;
	
	public boolean init(Configuration conf, ServiceMetaData metadata) {
		this.conf = conf;
	    this.extensionPoint = PluginRepository.get(conf).getExtensionPoint(
	    		Service.X_POINT_ID);
	    if (this.extensionPoint != null) return true;
	    
	    LOG.fatal("x-point " + Service.X_POINT_ID + " not found.");
	    return false;
	}
	
	public void process(Request context, Response response) {
	}    
	
	public void stop() {
	}
	
    /**
     * Returns the appropriate {@link Service} for a sensor name.
     * 
     * @param sensorName String
     * @return The appropriate {@link Service} implementation for a given {@link URL}.
     * @throws PluginNotFound
     *           when Sensor can not be found for serviceId
     */
	public Service getPlugin(String sensorId) 
	{
      try {
          
    	  if (sensorId == null) throw new PluginNotFound(sensorId);
          String cacheId = Service.X_POINT_ID + sensorId;

          if (conf.getObject(cacheId) != null) {
        	  return (Service) conf.getObject(cacheId);
          } 
          else 
          {
        	  ServiceMetaData metadata = this.getMetaData(sensorId);
        	  if (metadata == null) {
        		  LOG.fatal("Sensor id is absent.");
        		  throw new PluginNotFound(sensorId);
        	  }
        	  
        	  Extension extension = findExtension(sensorId);
        	  if (extension == null) throw new PluginNotFound(sensorId);
        	  Service sensor = (Service) extension.getExtensionInstance();
        	  sensor.init(this.conf, this.getMetaData(sensorId));
        	  conf.setObject(cacheId, sensor);
        	  return sensor;
          }
      } 
      catch (PluginRuntimeException e) {
    	  throw new PluginNotFound(sensorId + e.toString());
      }
    } 
	

	public ServiceMetaData getMetaData(String sensorId) {
		if ( sensorTOC != null ) return  sensorTOC.get(sensorId);
		this.buildTOC();
		return sensorTOC.get(sensorId);
	}
	
	/** We will do an lazy loading of this. */
	public Hashtable<String, ServiceMetaData> getTOC() {
		if ( sensorTOC != null ) return sensorTOC;
		this.buildTOC();
		return sensorTOC;
	}
	
	private synchronized void buildTOC() {
		if ( null != this.sensorTOC ) return ;

		Extension[] extensions = this.extensionPoint.getExtensions();
		int totalExt = extensions.length;
		this.sensorTOC = new Hashtable<String,ServiceMetaData>(totalExt);
        for (int i = 0; i < totalExt; i++) {
            Extension extension = extensions[i];
    		ServiceMetaData metaData = new  ServiceMetaData();
    		metaData.id = extension.getId();
    		metaData.name = extension.getAttribute("name");
    		metaData.iconName = extension.getAttribute("logo");
    		metaData.tags = extension.getAttribute("tags");
    		metaData.url = extension.getAttribute("url");
            this.sensorTOC.put(metaData.id, metaData);
        }
	}

    private Extension findExtension(String id) throws PluginRuntimeException {

        Extension[] extensions = this.extensionPoint.getExtensions();

        for (int i = 0; i < extensions.length; i++) {
          Extension extension = extensions[i];
          if ( id.equals(extension.getId()) ) return extension;
        }
        return null;
      }  

    /**
     * Loads all necessary dependencies for sensor plugin, and then runs 
     * the <code>Sensor.processRequest()</code> method.
     * 
     * @param args
     *          plugin ID (needs to be activated in the configuration).
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
      Configuration conf = new Configuration();
      PluginService plugin = PluginService.getInstance();
      plugin.init(conf, null);
      
      System.out.println("Getting the TOC");
      Hashtable<String, ServiceMetaData> toc = plugin.getTOC();
      java.util.Enumeration<String> sensorIds = toc.keys();
      String sensorId = StringUtils.Empty;
      while ( sensorIds.hasMoreElements() ) {
    	  sensorId = sensorIds.nextElement();
          System.out.println("Sensor ID:" + sensorId);
    	  ServiceMetaData metaData = toc.get(sensorId);
    	  System.out.println(metaData);
      }
            
      System.out.println("Getting meta data for one sensor = " + sensorId);
      ServiceMetaData metaData = plugin.getMetaData(sensorId);
      System.out.println(metaData);
      
      System.out.println("Getting the sensor for " + sensorId);
      Service sensor1 = plugin.getPlugin(sensorId);
      sensor1.process(null, null);
      
      System.out.println("Instantiating again sensor for " + sensorId);
      plugin.getPlugin(sensorId);
    }

	public String getName() {
		return "PluginService";
	}
}
