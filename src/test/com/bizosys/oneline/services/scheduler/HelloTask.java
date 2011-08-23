package com.bizosys.oneline.services.scheduler;

import com.bizosys.oneline.ApplicationFault;
import com.bizosys.oneline.SystemFault;
import com.bizosys.oneline.services.batch.BatchTask;

	public class HelloTask implements BatchTask {

		public String jobName = "HelloTask";
		private String msg = "Hello";
		
		public HelloTask(String msg) {
			this.msg = msg;
		}
		
		public String getJobName() {
			
			return this.jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public Object process() throws ApplicationFault, SystemFault{
			System.out.println(this.jobName + ":" + msg);
			return 0;
		}
}
