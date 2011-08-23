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
package com.bizosys.oneline.web;

import org.apache.log4j.Logger;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.services.ServiceFactory;

public class ServerListener
{
	private final static Logger LOG = Logger.getLogger(ServerListener.class);
	
    public void startup()
    {
    	LOG.info("> Starting Oneline Services");
		this.initServices();
    }

    private void initServices()
    {
	      Configuration conf = new Configuration();
	      ServiceFactory serviceFactory = ServiceFactory.getInstance();
	      boolean sucess = serviceFactory.init(conf, null);
    	  System.out.println("\n\n\n\n###############################");
    	  System.out.println("******Service start up Status :" + sucess);
    	  System.out.println("###############################\n\n\n\n");
    }
    
    /**
     * Initiate all services and then makes an execution.
     * 
     */
    public static void main(String[] args) throws Exception {
		ServerListener listener = new ServerListener();
		listener.startup();
    }      
}