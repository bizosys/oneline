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

import com.bizosys.oneline.util.StringUtils;

public class ServiceMetaData { 

	public String id = StringUtils.Empty;
	public String name = StringUtils.Empty;
	public String iconName = StringUtils.Empty;
	public String url = StringUtils.Empty;
	public String tags = StringUtils.Empty;
	
	public String[] getTags() {
		return StringUtils.getStrings(tags);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("id:").append(id).append('-');
		sb.append("name:").append(name).append('-');
		sb.append("iconName:").append(iconName).append('-');
		sb.append("url:").append(url).append('-');
		sb.append("tags:").append(tags).append('-');
		return  sb.toString();  
	}
}
