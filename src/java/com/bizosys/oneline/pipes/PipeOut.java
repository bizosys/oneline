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
package com.bizosys.oneline.pipes;

import com.bizosys.oneline.ApplicationFault;
import com.bizosys.oneline.SystemFault;
import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.plugin.Pluggable;

public interface PipeOut extends Pluggable  {

	  /** The name of the extension point. */
	  public final static String X_POINT_ID = PipeOut.class.getName();

	  /**
	   * Get a instance or can reuse the this instance
	   * @return	Pipe out
	   */
	  public PipeOut getInstance();
	  
	  /**
	   * Gets initialized only once.
	   * @param conf	Configuration File
	   * @throws ApplicationFault	Application Fault
	   * @throws SystemFault	System Fault
	   */
	  public void init(Configuration conf) throws ApplicationFault, SystemFault;
	  
	  /**
	   * Visit a pipe
	   * @param data	This object gets passed through all the pipes
	   * @param multiWriter	Is multiple writers are in action
	   * @throws ApplicationFault	Application Fault
	   * @throws SystemFault	System Fault
	   */
	  public void visit(Object data, boolean multiWriter) throws ApplicationFault, SystemFault;
	  
	  /**
	   * Commits the changes. (This helps multiple objects to be collapsed and commited in batch mode. 
	   * @param multiWriter	Is multiple writers are in action
	   * @throws ApplicationFault	Application Fault
	   * @throws SystemFault	System Fault
	   */
	  public void commit(boolean multiWriter) throws ApplicationFault, SystemFault;
	  
	  /**
	   * Get the Pipe Name
	   * @return
	   */
	  public String getName();	  
}
