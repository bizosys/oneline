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

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.plugin.Pluggable;

/**
 * An extension point interface for writting sensor point solutions .
 *
 * <p>Sensors are integrated in the following ways:
 *   <lu>
 *     <li>An icon to show in the application listing</li>
 *     <li>Sensor Tags for application facetted browsing</li>
 *     <li>The RSL file for showing the application, inbox activity details
 *         and immersive workplace information clustering </li>
 *     <li>On clicking a button, the action is passed to the sensor.
 *         Sensor processes the request and gives back the response as XML.</li>
 *     <li>
 *     <li>The sensor will access the Core services.</li>
 *  </lu>    
 * </p>
 *
 * <p>TODO: The sensor can access the database directly. We need to restrict this. </p>
 *
 * @author abinash
 * @version $$
 */
public interface Service extends Pluggable  {

  /** The name of the extension point. */
  public final static String X_POINT_ID = Service.class.getName();

  /**
   * The web requests comes here. Crawler the request context the action 
   * <code>RequestContext.action</code> helps in deciding category to process.
   * 
   * <p>Arguments to this method are all about performing an operation in a 
   * given context with necessary provided information and creating some  
   * result as output. </p>
   * 
   * <p>This method must be thread-safe (many threads may invoke
   * it concurrently on the same instance of a request processing).</p>
   */
  public void process(Request context, Response response);

  /**
   * Configuration object is given for sensor to set itself up. 
   * configuration object is created from default.xml and site.xml combined
   * @param config
   */
  public boolean init(Configuration config, ServiceMetaData metadata);
  public String getName();
  public void stop();
  
}

