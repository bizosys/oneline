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
package com.bizosys.oneline.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class XmlUtils extends DefaultHandler {
	
	/** XPP driver needs xpp.jar in the classpath */
	public static XStream xstream = new XStream(new XppDriver());
	
	
	StringBuilder sb = new StringBuilder(100);
	HashMap<String, Object[]> xmlMap = null;
	
	String currentNode = null;
	
	public XmlUtils () {}
	
	public HashMap<String, Object[]> toMap( String xmlSnippet) {
		
		if ( null == xmlSnippet) return null;
		if ( "".equals(xmlSnippet.trim())) return null;
		
		if ( ! xmlSnippet.startsWith("<?xml")) xmlSnippet = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlSnippet;
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			ByteArrayInputStream ba = new ByteArrayInputStream(xmlSnippet.getBytes());
			
			this.xmlMap = new HashMap<String, Object[]>(20);
			this.sb.delete(0, sb.capacity());
			sp.parse(ba, this);
			return this.xmlMap;
			
		} catch(SAXException se) {
			se.printStackTrace();
			throw new IllegalArgumentException(se.getMessage());
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
			throw new IllegalArgumentException(pce.getMessage());
		} catch (IOException ie) {
			ie.printStackTrace();
			throw new IllegalArgumentException(ie.getMessage());
		}
	}

	public String toString(String xmlSnippet) throws IllegalArgumentException {
		Map<String, Object[] > res = toMap(xmlSnippet);
		StringBuilder sb = new StringBuilder(100);
		boolean isFirst = true;
		for (String key : res.keySet()) {
			String[] values = (String[]) res.get(key);
			
			if ( isFirst ) isFirst = false; 
			else sb.append('\n');
			sb.append(key).append('=').append(arrayToString(values, ',') );
		}
		return sb.toString();
	}

	
	@Override
	public void startElement(String uri, String localName, 
			String qName, Attributes attributes) throws SAXException {
		
		if ( null == currentNode ) currentNode =  qName;
		else currentNode = currentNode + "." + qName;
	}
	
	@Override
	public void endElement(String uri, String localName,String qName) 
	throws SAXException {

		String appendedQName = "." + qName;
		String val = this.sb.toString();
		
		if ( currentNode.endsWith(appendedQName)) {
			if ( this.xmlMap.containsKey(currentNode)) {
				String [] existingVal = (String[]) this.xmlMap.get(currentNode);
				int totalElements = existingVal.length;
				String [] newVal = new String[totalElements + 1];
				System.arraycopy(existingVal, 0, newVal, 0, totalElements);
				newVal[totalElements] = val;
				this.xmlMap.put(currentNode, newVal);
			} else {
				if (val.length() != 0 ) 
					this.xmlMap.put(currentNode, new String[]{val} );
			}
			currentNode = currentNode.substring(0, currentNode.length() - appendedQName.length());
		} else {
			currentNode = currentNode.substring(0, qName.length());
		}
		this.sb.delete(0, sb.capacity());
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		sb.append(ch, start, length);
	}
	
	public static String toXml(Map data, String keyElement, String valueElement)
	{
		String keyStart = "<" + keyElement + ">";
		String keyEnd = "</" + keyElement + ">";
		String valueStart = "<" + valueElement + ">";
		String valueEnd = "</" + valueElement + ">";
		StringBuilder sb = new StringBuilder(1000);
		for (Object key : data.keySet())
		{
			sb.append("<e>").append(keyStart).append(key).append(keyEnd)
				.append(valueStart).append(data.get(key)).append(valueEnd).append("</e>");
		}
		return sb.toString();
	}
	
	public static String toXml(Collection<String> data, String keyElement)
	{
		String keyStart = "<" + keyElement + ">";
		String keyEnd = "</" + keyElement + ">";
		StringBuilder sb = new StringBuilder(1000);
		for (String key : data)
		{
			sb.append("<e>").append(keyStart).append(key).append(keyEnd).append("</e>");
		}
		return sb.toString();
	}
	
	  public static String arrayToString(String[] strs, char delim) {
		    if (strs == null || strs.length == 0) { return ""; }
		    StringBuffer sbuf = new StringBuffer();
		    sbuf.append(strs[0]);
		    for (int idx = 1; idx < strs.length; idx++) {
		      sbuf.append(delim);
		      sbuf.append(strs[idx]);
		    }
		    return sbuf.toString();
		 }
	
}