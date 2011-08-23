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
package com.bizosys.oneline.services.async;

import java.util.concurrent.*;

import org.apache.log4j.Logger;

import com.bizosys.oneline.SystemFault;
import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.services.Request;
import com.bizosys.oneline.services.Response;
import com.bizosys.oneline.services.Service;
import com.bizosys.oneline.services.ServiceMetaData;
import com.bizosys.oneline.services.batch.BatchProcessor;

public class AsyncProcessor implements Service {

	private final static Logger l = Logger.getLogger(BatchProcessor.class);
	
	private int poolSize = 40; //the number of threads to keep in the pool, even if they are idle. 
    private int maxPoolSize = 100; //the maximum number of threads to allow in the pool.
    private long keepAliveTime = 60; //(sec) when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
    private ThreadPoolExecutor threadPool = null;
    // The queue to use for holding tasks before they are executed. This queue will hold only the Runnable tasks submitted by the execute method. 
    private BlockingQueue<Runnable> queue = null;
    
    private static AsyncProcessor thisInstance = null;
    public static AsyncProcessor getInstance() {
    	if (null != thisInstance) return thisInstance;
    	synchronized (AsyncProcessor.class) {
    		if (null != thisInstance) return thisInstance;
    		thisInstance = new AsyncProcessor();
		}
    	return thisInstance;
    }
    
    /**
     * Provate constructor so no body can use it.
     */
    private AsyncProcessor() {
    }
 
    public void runTask(Runnable task) throws SystemFault {
        if ( null == threadPool) throw new SystemFault ("AsyncProcessor is not initialized.");
    	threadPool.execute(task);
    }

    public String getSummary() {
    	if ( null == threadPool) return "AsyncProcessor is not initialized.";
    	StringBuilder sb = new StringBuilder();
    	sb.append("Completed Tasks=");
    	sb.append(threadPool.getTaskCount());
    	sb.append(". Remaining Tasks:");
    	sb.append(queue.size());
    	return sb.toString();
    }
    
    public long getCompletedTasks() {
    	if ( null == threadPool) return 0;
    	return threadPool.getTaskCount();
    }
    
    public long getRemainingTasks() {
    	if ( null == threadPool) return 0;
    	return queue.size();
   }
    
    public ThreadPoolExecutor getThreadPool() {
    	if ( null == threadPool) return null;
        l.info("AsyncProcessor > Threadpool size:" + threadPool.getActiveCount());
    	return this.threadPool;
    }
    
    
	public boolean init(Configuration config, ServiceMetaData metadata) {
    	this.poolSize = config.getInt("async.workers.init",40);

    	this.maxPoolSize = config.getInt("async.workers.max",this.poolSize * 10);
    	this.queue = new LinkedBlockingQueue<Runnable>(this.maxPoolSize * 3);    	
    	
    	l.debug("Initializing the AsyncProcessor thread pool");
    	threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
                keepAliveTime, TimeUnit.SECONDS, queue);
    	if ( l.isInfoEnabled())
    		l.info("AsyncProcessor: Thread pool is initialized with size " + poolSize );
    	return true;
	}

	public void process(Request context, Response response) {
	}

	public void stop() {
		 try { this.queue.clear(); } catch (Exception ex) {l.fatal(ex);};
		 try { threadPool.shutdown(); } catch (Exception ex) {l.fatal(ex);};
	}
	
    @Override
    public String toString() {
    	 StringBuilder sb = new StringBuilder();
    	 sb.append("Thread Pool Task count.." );
    	 sb.append(threadPool.getTaskCount());
    	 sb.append(" , Queue Size before assigning the task..");
    	 sb.append(queue.size());
    	 return sb.toString();
    }

	public String getName() {
		return "AsyncProcessor";
	}
}

