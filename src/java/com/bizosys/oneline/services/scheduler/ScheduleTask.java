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
package com.bizosys.oneline.services.scheduler;

import java.text.ParseException;
import java.util.Date;
import java.util.TimerTask;

import org.quartz.CronExpression;

import com.bizosys.oneline.ApplicationFault;
import com.bizosys.oneline.SystemFault;
import com.bizosys.oneline.services.batch.BatchTask;


/**
 * 	Note, don't use the LOG. This is bloating the serialization piece.
 * private Logger LOG = Logger.getLogger(SchedulerTask.class);
 * @author karan
 */
public class ScheduleTask extends TimerTask {

	public CronExpression cron = null;
	public Date startDate = null;
	public Date endDate = null;
	public BatchTask task = null;

	/**
	 * Need for the XML serialization
	 */
	public ScheduleTask() {
		
	}
	
	public ScheduleTask(BatchTask task, String cronExpr, 
		Date startDate,Date endDate ) 
		throws ApplicationFault, SystemFault {

		this.task = task;
		this.startDate = startDate;
		this.endDate = endDate;
		
		try {
			this.cron = new CronExpression(cronExpr);
		} catch (java.text.ParseException ex) {
			throw new ApplicationFault("Scheduler : Invalid Expression " + cronExpr, ex);
		}
		if ( ! SchedulerService.getInstance().putTask(this) )
			SchedulerLog.l.warn("ScheduleTask : Unsucessful Scheduling > " + this.task.getJobName()); 
		
	}
	
	public void schedule() throws ApplicationFault, ApplicationFault {
		SchedulerService.getInstance().putTaskNextTime(this.task.getJobName());
	}	
	
	/**
	 *	Seconds - Mandatory, 0-59, [* / , -]
	 *	Minutes - Mandatory, 0-59 [* / , -]
	 *	Hours - Mandatory, 0-23 [* / , -]
	 *	Day of month - Mandatory, 1-31 [* / , - ? L W C] 
	 *	Month - Mandatory, 1-12 or JAN-DEC [* / , -]
	 *	Day of week - Mandatory, 1-7 or SUN-SAT , [* / , - ? L C #] 
	 *	Year - Optional,  1970-2099 , [* / , -]
	 * 
	 *  Asterisk (*) The asterisk indicates that any value for the field is acceptable.  
	 *  Ex.	Using an asterisk in the 5th field (month) would indicate every month.
	 *  
	 *  Forward slashes (/) Forward slashes are used to describe increments. 
	 *  Ex. 0/15 in the 1st field (seconds) would indicate the 0th second of the minute and every 15 seconds thereafter.
	 *  
	 *  Commas (,) Commas are used to separate items of a list. 
	 *  Ex, using "MON,WED,FRI" in the 6th field (day of week) would mean Mondays, Wednesdays and Fridays.
	 *  
	 *  Hyphen (-) Hyphens denote ranges. 
	 *  Ex, 2000-2010 would indicate every year between 2000 and 2010 CE inclusive.
	 *  
	 *  Question mark (?) : The '?' character is allowed for the day-of-month and day-of-week fields.
	 *  It is used to specify 'no specific value'. This is useful when you need to specify something in one of the two fields, but not the other.
	 *  
	 *  Every second: * * * * * ?
	 *  Every minute: 0 * * * * ?
	 *  23:00 every weekday night: 0 0 23 ? * MON-FRI
	 *  
	 *  In 2003 CE on the 11th to 26th of each month in January to June every third minute starting from 2 past 1am, 9am and 10pm
	 *  0 2/3 1,9,22 11-26 1-6 ? 2003
	 */
	public void setCron(String cronExpr) throws ApplicationFault {
		try {
			this.cron = new CronExpression(cronExpr);
		} catch (ParseException ex) {
			throw new ApplicationFault(cronExpr, ex);
		}
	}
	
	public Date getNextWindow() throws ApplicationFault {
		if ( null == this.cron)
			throw new ApplicationFault("Cron Expression is not set :" + this.toString());
		
		Date curDate = new Date();
		if ( null != this.startDate) {
			//Startdate is in future
			if ( curDate.before(this.startDate) ) {
				curDate = this.startDate;
			}
		}
		Date nextWindow = this.cron.getNextValidTimeAfter(curDate);
		return nextWindow;
	}	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SchedulerTask => ");
		sb.append("jobName : ").append(this.task.getJobName());
		sb.append(" cron : ").append(cron);
		sb.append(" task : ").append(task.toString());
		sb.append(" cron : ").append(cron);
		sb.append(" startDate : ").append(startDate);
		sb.append(" endDate : ").append(endDate);
		return sb.toString();
	}
	
	public void refresh(ScheduleTask task) {
		this.startDate = task.startDate;
		this.endDate = task.endDate;
		this.cron = task.cron;
	}
	
	public void purge() {
		
	}
	
	@Override
	public void run() {
		try {
			this.task.process();
			SchedulerService.getInstance().putTaskNextTime(this.task.getJobName());
			
		} catch (Exception ex) {
			SchedulerLog.l.fatal("scheduler.TransientTask >>" , ex);
		}
	}
	public ScheduleTask clone() {
		ScheduleTask tt = new ScheduleTask();
		tt.task = this.task ;
		tt.startDate = this.startDate ;
		tt.endDate = this.endDate ;
		tt.cron = this.cron;
		
		return tt;
	}
}
