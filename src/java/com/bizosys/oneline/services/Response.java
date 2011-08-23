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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bizosys.oneline.util.XmlUtils;

public class Response { 

	private PrintWriter out = null;
    public String stamp = "";
    public String xsl = "";
    public Object data = null;
    private List<String> errorMessages = null;
    public Boolean isError = false;

    private static final Logger LOG = Logger.getLogger(Response.class);
    
    public static final void register (String name, Class value) {
    	XmlUtils.xstream.alias(name, value);
    }

    private Response() 
    {
    }
    
    public PrintWriter getWriter() {
    	return this.out;
    }
    
    public Response(PrintWriter out) {
    	this.out = out;
    }

    public boolean hasNoErrors()
    {
    	return !(this.isError);
    }
    
    public void writeText(String result) {
    	if ( LOG.isInfoEnabled()) LOG.info("The result:" + result );
    	out.println(result);
    }

    public void writeXmlString(String xmlText) {
    	if ( LOG.isInfoEnabled()) LOG.info("The result:" + xmlText );
    	writeHeader();
    	out.println(xmlText);
    	writeFooter();
    }

    public void writeXmlString(List serializeL) {
    	writeHeader();
    	if ( null != serializeL ){ 
	    	int serializeT = serializeL.size();
	    	for ( int i=0; i<serializeT; i++ ) {
	        	out.println(serializeL.get(i));
	        	if ( LOG.isDebugEnabled()) LOG.debug("\n" + serializeL.get(i) + "\n");
	    	}
    	}
    	writeFooter();
    }

    
    public void writeXML(Object aObject) {
    	writeHeader();
    	String xmlData = XmlUtils.xstream.toXML(aObject);
    	out.println(xmlData);
    	writeFooter();
    	
    	if ( LOG.isDebugEnabled()){
    		LOG.debug("\n" + xmlData + "\n");
    	}
    }

    public void writeXMLList(List serializeL) {
    	writeHeader();
    	if ( null != serializeL ){ 
	    	int serializeT = serializeL.size();
	    	for ( int i=0; i<serializeT; i++ ) {
	        	String listXML = XmlUtils.xstream.toXML(serializeL.get(i));
				out.println(listXML);
	        	if ( LOG.isDebugEnabled()) LOG.debug("\n" + listXML + "\n");
	    	}
    	}
    	writeFooter();
    }
    
    public void writeXMLArray(String[] xmlStrings, String tag) {
    	writeHeader();
    	StringBuilder sb = new StringBuilder(100);
    	if ( null != xmlStrings ){ 
	    	int serializeT = xmlStrings.length;
	    	
	    	for ( int i=0; i<serializeT; i++ ) {
	        	sb.append('<').append(tag).append('>');
	        	sb.append(xmlStrings[i]);
	        	sb.append("</").append(tag).append('>');
	        	out.println('<');
	    		out.println(sb.toString());
        		LOG.debug(sb.toString());
        		sb.delete(0, sb.capacity());
	    	}
    	}
    	writeFooter();
    }

    public void error(String message, Exception e) {
		
    	this.error(message);
		LOG.error(message, e);
	}
    
    public String getError() {
    	boolean hasError = true;
    	int errorsT = 0;
    	
    	if ( null == this.errorMessages ) hasError = false;
    	if ( hasError ) {
    		errorsT = this.errorMessages.size();
    		if ( 0 == errorsT ) hasError = false;
    	}
    	
    	if ( hasError ) {
			StringBuffer errors = new StringBuffer();
			for ( int i=0; i< errorsT; i++ ) {
				errors.append("<msg>").append(this.errorMessages.get(i)).append("</msg>");
			}
			return errors.toString();
    	}
    	return "NO_MESSAGE";
    }

    public void error(String message) {
    	if ( null == this.errorMessages ) {
    		this.errorMessages = new ArrayList<String>(2);
    		this.isError = true;
    	}
		this.errorMessages.add(message);
    }
    
    public void writeHeader() {
    	StringBuilder sb = new StringBuilder(100);
    	boolean isXsl = ! ( null == this.xsl || "".equals(this.xsl));
    	if ( isXsl ) {
    		sb.append("<?xml version=\"1.0\" ?>");
    		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
    		sb.append(this.xsl);
    		sb.append("\"?>");
    	}
    	LOG.debug("The message stamp = " + this.stamp);
    	boolean isStamp = ! ( null == this.stamp || "".equals(this.stamp));
    	if ( isStamp ) {
    		sb.append("<result>");
    	} else {
    		sb.append("<result msgid=\"").append(this.stamp).append("\" >");
    	}
    	out.print(sb.toString());
    	sb.delete(0, sb.capacity());
    }
    public void writeFooter() {
    	out.print("</result>");
    	boolean isXsl = ! ( null == this.xsl || "".equals(this.xsl));
    	if ( isXsl) out.print("</xsl:template></xsl:stylesheet>");    	
    }
}
	