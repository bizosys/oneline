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

public class InvalidRequestException extends RuntimeException {

	private static final long serialVersionUID = 2L;
	public String errorField = "";
	
	public InvalidRequestException() {
		super();
	}

	public InvalidRequestException(String message, Throwable exception) {
		super(message, exception);
		this.errorField = message;
	}

	public InvalidRequestException(String message) {
		super(message);
		this.errorField = message;
	}

	public InvalidRequestException(Throwable message) {
		super(message);
	}
}
