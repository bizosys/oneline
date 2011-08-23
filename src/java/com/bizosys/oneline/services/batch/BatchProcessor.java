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
package com.bizosys.oneline.services.batch;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.services.Request;
import com.bizosys.oneline.services.Response;
import com.bizosys.oneline.services.Service;
import com.bizosys.oneline.services.ServiceMetaData;

/**
 * @author karan
 */
public class BatchProcessor implements Service ,Runnable {

	private final static Logger LOG = Logger.getLogger(BatchProcessor.class);
	private static BatchProcessor instance = null;
	
	public static BatchProcessor getInstance() {
		if ( null != instance) return instance;
		synchronized (BatchProcessor.class) {
			if ( null != instance ) return instance;
			BlockingQueue<BatchTask> msgQueue = new LinkedBlockingQueue<BatchTask>();
			instance = new BatchProcessor(msgQueue);
			Thread offlineThread = new Thread(instance);
			offlineThread.setDaemon(true);
			offlineThread.start();
		}
		return instance;
	}
	
	BlockingQueue<BatchTask> blockingQueue = null; 
	
	private BatchProcessor () {}
	
	private BatchProcessor ( BlockingQueue<BatchTask> blockingQueue){
		this.blockingQueue = blockingQueue;
	}

	public void addTask(BatchTask task) {
		if ( null == task ) return;
		if ( LOG.isDebugEnabled() ) LOG.debug("BatchProcessor >  A new task is lunched > " + task.getJobName());
		
		blockingQueue.add(task); 
	}
	
	public int getQueueSize() {
		if ( null == blockingQueue) return 0;
		else return blockingQueue.size();
	}
	
	/**
	 * Takes a transaction from the queue and apply this in the database.
	 */
	public void run() {
		LOG.info("BatchProcessor > Batch processor is ready to take jobs.");
		while (true) {
			BatchTask offlineTask = null;
			try {
				offlineTask = this.blockingQueue.take(); //Request blocks here 
				if ( LOG.isInfoEnabled() ) LOG.info(
					"BatchProcessor > Taken from the Queue for processing - " + offlineTask.getJobName());
				offlineTask.process();
				
			} catch (InterruptedException ex) {
				LOG.warn("Batch Interrupted", ex);
				Iterator<BatchTask> queueItr = this.blockingQueue.iterator();
				while ( queueItr.hasNext() ) LOG.fatal("BatchProcessor > " + queueItr.next());
				break;
			} catch (Exception ex) {
				LOG.fatal("BatchProcessor > ",  ex);
				if ( null != offlineTask) LOG.fatal("BatchProcessor > " + offlineTask.toString());
			}
		}
	}

	public boolean init(Configuration config, ServiceMetaData metadata) {
		BatchProcessor.getInstance();
		return true;
	}

	public void process(Request context, Response response) {
	}

	public void stop() {
	}

	public String getName() {
		return "BatchProcessor";
	}
	
}