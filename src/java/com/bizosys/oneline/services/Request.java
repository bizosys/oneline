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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bizosys.oneline.util.XmlUtils;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;

/**
 * Starts from client
 * @author Abinash
 *
 */
public class Request {

	private final static Logger LOG = Logger.getLogger(Request.class);
	
	/** Request business feature information */
	public String serviceId = ""; 
	public String action = "";
	public Map<String, String> mapData;
	public String clientIp = null;
	public Object user = null;

	public Request(String serviceId, String action, Map<String, String> data) {
		this.serviceId = serviceId;
		this.action = action;
		this.mapData = data;
	}

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return
	 * @throws InvalidRequestException
	 */
	private String getMapValue(String key, boolean strict ) 
	throws InvalidRequestException {
		String value = null;
		if ( (null != this.mapData) && (null != key) ) value = this.mapData.get(key);
		if ( (null == value) && strict ) {
			throw new InvalidRequestException(key);
		}
		return value;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	/**
	 * From the incoming xml data, creates an object directly. 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Object
	 * @throws InvalidRequestException
	 */
	public Object getObject (String key, boolean strict ) throws InvalidRequestException {
		String value = this.getString(key,strict, true, false);
		if ( null == value ) return null;
	
		try {
			return XmlUtils.xstream.fromXML(value);
		} catch (Exception ex) {
			LOG.error("Error in creating object from xml : " + value, ex);
			throw new InvalidRequestException(value, ex);
		}
	}	

	/**
	 * From the incoming xml data, creates an object directly. 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Object
	 * @throws InvalidRequestException
	 */
	public List getList(String key, boolean strict, Class classToFill) throws InvalidRequestException {
		
		Integer recordCount = this.getInteger(key + "T", strict);
		
		if (recordCount == null || recordCount == 0)
		{
			return Collections.EMPTY_LIST;
		}
		
		List<Object> records = new ArrayList<Object>(recordCount);
		
		for(int index = 1; index <= recordCount; index++ )
		{
			records.add(this.getObject(key + index, strict, classToFill));
		}
		
		return records;
	}	

	/**
	 * From the incoming xml data, creates an object directly. 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Object
	 * @throws InvalidRequestException
	 */
	public List getList(String key, boolean strict) throws InvalidRequestException {
		return this.getList(key, strict, null);
	}
		
	/**
	 * From the incoming xml data, creates an object directly. 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Object
	 * @throws InvalidRequestException
	 */
	public Object getObject (String key, boolean strict, Class classToFill) throws InvalidRequestException {
		String value = this.getString(key,strict, true, false);
		if ( null == value ) return null;
		if (classToFill == null) return value;
		
		try {
			return com.bizosys.oneline.util.XmlUtils.xstream.fromXML(value, classToFill.newInstance());
		} catch (Exception ex) {
			LOG.error("Error in creating object from xml : " + value, ex);
			throw new InvalidRequestException(value, ex);
		}
	}	

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Short
	 * @throws InvalidRequestException
	 */
	public Short getShort(String key, boolean strict ) throws InvalidRequestException {
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return Short.parseShort(value);
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Boolen
	 * @throws InvalidRequestException
	 */
	public Boolean getBoolean(String key, boolean strict ) throws InvalidRequestException {
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return Boolean.parseBoolean(value);
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}	
	
	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Integer
	 * @throws InvalidRequestException
	 */
	public Integer getInteger(String key, boolean strict ) throws InvalidRequestException {
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return Integer.parseInt(value);
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}

	public int getInteger(String key, int defaultVal ) throws InvalidRequestException {
		String value = getMapValue(key,false);
		if ( null == value ) return defaultVal;
	
		try {
			return Integer.parseInt(value);
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}
	

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Timestamp
	 * @throws InvalidRequestException
	 */
	public Timestamp getDate(String key, boolean strict ) throws InvalidRequestException {
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return (Timestamp) new SqlTimestampConverter().fromString(value);
		} catch (Exception ex) {
			throw new InvalidRequestException(value, ex);
		}
	}

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Timestamp
	 * @throws InvalidRequestException
	 */
	public Date getDateTime(String key, boolean strict ) throws InvalidRequestException {
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return new Date(new Long(value));
		} catch (Exception ex) {
			throw new InvalidRequestException(value, ex);
		}
	}
	
	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Long
	 * @throws InvalidRequestException
	 */
	public Long getLong(String key, boolean strict ) throws InvalidRequestException {
		
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return Long.parseLong(value);
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Double
	 * @throws InvalidRequestException
	 */
	public Double getDouble(String key, boolean strict ) throws InvalidRequestException {
		
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return Double.parseDouble(value);
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return Float
	 * @throws InvalidRequestException
	 */
	public Float getFloat(String key, boolean strict ) throws InvalidRequestException {
		
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
	
		try {
			return Float.parseFloat(value);
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}    

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @return BigDecimal
	 * @throws InvalidRequestException
	 */
	public BigDecimal getBigDecimal(String key, boolean strict ) throws InvalidRequestException {
	
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
		try {
			BigDecimal decimal = new BigDecimal(value.toString());
			return decimal;
		} catch ( NumberFormatException ex) {
			throw new InvalidRequestException(value, ex);
		}
	}        

	/**
	 * 
	 * @param key The map key in which this is passed.
	 * @param strict Is null allowed
	 * @param trim Should it be trimmed.
	 * @param emptyAllowed Is empty expected.
	 * @return String
	 * @throws InvalidRequestException
	 */
	public String getString (String key, boolean strict, boolean trim,
		boolean emptyAllowed ) throws InvalidRequestException {
	
		String value = getMapValue(key,strict);
		if ( null == value ) return null;
		if ( trim ) value = value.trim();
		if ( !emptyAllowed && "".equals(value) ) {
			throw new InvalidRequestException(value);
		}
		return value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("\n\n\n");
		sb.append("[serviceId=" ).append(this.serviceId);
		sb.append("][action=").append(this.action).append("]\n" + "[data=");
		sb.append(mapData).append(']');
		return sb.toString();
	}    	
}