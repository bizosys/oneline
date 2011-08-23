package com.bizosys.oneline.services.example;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.services.Request;
import com.bizosys.oneline.services.Response;
import com.bizosys.oneline.services.Service;
import com.bizosys.oneline.services.ServiceMetaData;

public class HelloWorld implements Service {

	/**
	 * Any initialization necessary for the service.
	 * Remember, this is a Singleton class. So all class variables
	 * will be visible across requests
	 */
	public boolean init(Configuration config, ServiceMetaData metadata) {
		return true;
	}

	/**
	 * A particular request serving.
	 */
	public void process(Request context, Response response) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n\n\n\n\nInside Hello World Service");
		sb.append("\nAction :" + context.action);
		sb.append("\nServiceId :" + context.serviceId);
		sb.append("\nArg Names :" + context.mapData.keySet().toString());
		sb.append("\nArg Values :" + context.mapData.values().toString());
		response.writeText(sb.toString());
	}

	/**
	 * Any clean up job during service stopping. 
	 */
	public void stop() {
	}

	/**
	 * This is not critical for the application to run. 
	 */
	public String getName() {
		return "ExampleService";
	}

}
