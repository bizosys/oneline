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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bizosys.oneline.services.FrontController;
import com.bizosys.oneline.services.Request;
import com.bizosys.oneline.services.Response;
import com.bizosys.oneline.util.StringUtils;

public class RestServlet extends HttpServlet {

	private static final long serialVersionUID = 4L;
	private final static Logger LOG = Logger.getLogger(RestServlet.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		LOG.info("XMLServlet initializing.");		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException {
		this.doProcess(req, res);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException {
		this.doProcess(req, res);
	}
	
	private void doProcess(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException {

//		Calendar.getInstance(req.getLocale()).getTime().getTime();
		
		res.setContentType("text/html");
		res.setCharacterEncoding (req.getCharacterEncoding() );
		
		LOG.info("\n\n\n A web request has entered server.\n");

		/**
		 * Store all the parameters in the Sensor request object
		 */
		Enumeration reqKeys = req.getParameterNames();
		Map<String, String> data = new HashMap<String, String>();

		while (reqKeys.hasMoreElements()) {
			String key = (String) reqKeys.nextElement();
			String value = req.getParameter(key);
			data.put(key, value);
		}

		String sensor = req.getParameter("service");
		String action = req.getParameter("action");
		sensor = (null == sensor) ? StringUtils.Empty : sensor.trim();
		action = (null == action) ? StringUtils.Empty : action.trim();
		
		if ( (sensor.length() == 0) || ( action.length() == 0 )) {
			String errorMsg =  StringUtils.FatalPrefix + "Sensor [" + sensor + "] or action [" + action + "] are missing." ; 
			LOG.fatal(errorMsg);
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMsg);
		}

		Request sensorReq = new Request(sensor, action, data);
		sensorReq.clientIp = req.getRemoteAddr();
		if ( LOG.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder(100);
			sb.append("Sensor name=").append(sensor).append(": action=").append(action);
			LOG.info(sb.toString());
			sb = null;
		}
		Object whoObj = req.getAttribute("__who");
		if ( null == whoObj) sensorReq.user = null;
		else sensorReq.user = whoObj;
		
		/**
		 * Initiate the sensor response, putting the stamp on it and xsl. 
		 */
		PrintWriter out = res.getWriter();
		Response sensorRes = new Response(out);
		String stamp = req.getParameter("stamp");
		sensorRes.stamp = (null == stamp) ? StringUtils.Empty : stamp;
		String xsl = req.getParameter("xsl");
		sensorRes.xsl = (null == xsl) ? StringUtils.Empty : xsl;
		
		try {
			FrontController controller = FrontController.getInstance(); 

			LOG.debug("Front controller processing START");
			controller.process(sensorReq, sensorRes);
			LOG.debug("Front controller processing END");
			
			if ( sensorRes.isError) {
				sensorRes.writeHeader();
				out.write("<error>" +  sensorRes.getError() + "</error>");
				sensorRes.writeFooter();
			}
			
		} catch (Exception ex) {
			res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Please contact the admin. Server has failed to process your request.");
			LOG.error("Error in processing request", ex);
		} finally {
			out.flush();
			out.close();
		}
	}

}
