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


/**
 *  MIN Minute field 0 to 59
 *  HOUR Hour field 0 to 23
 *  DOM Day of Month 1-31
 *  MON Month field 1-12
 *  DOW Day Of Week 0-6
 *  CMD Command Any command to be executed 
 * @author karan
 *
 */
public class ExpressionBuilder {
	
	public static String EVERY_SECOND = "* * * * * ?";
	public static String EVERY_MINUTE = "0 * * * * ?";
	public static String WEEKDAY_MIDNIGHT = "59 59 23 ? * MON_FRI";
	public static int JAN = 1;
	public static int FEB = 2;
	public static int MAR = 3;
	public static int APR = 4;
	public static int MAY = 5;
	public static int JUN = 6;
	public static int JUL = 7;
	public static int AUG = 8;
	public static int SEP = 9;
	public static int OCT = 10;
	public static int NOV = 11;
	public static int DEC = 12;
	public static int SUN = 1;
	public static int MON = 2;
	public static int TUE = 3;
	public static int WED = 4;
	public static int THU = 5;
	public static int FRI = 6;
	public static int SAT = 7;
	
	
	

	private String runAt(int time, int minLimit, int maxLimit) {
		if ( time < minLimit || time > maxLimit ) return null;
		return new Integer(time).toString();
	}

	/**
	 * Run 8, 22, and 47 minutes = 8,22,47  
	 */
	public static String runAt(int[] times, int minLimit, int maxLimit) {
		StringBuilder sb = new StringBuilder();
		boolean isStart = true;
		for (int min : times) {
			if ( min < minLimit || min > maxLimit ) return null;
			if ( isStart ) isStart = false;
			else sb.append(',');
			sb.append(min);
		}
		return sb.toString();
	}
	
	public static String runInterval(int interval, int maxLimit) {
		if ( interval > maxLimit) return null;
		return "*/" + interval;
	}
	
	/**
	 * Run from 09-18
	 */
	public static String runFromTo(int start, int end, int minLimit, int maxLimit) {
		if ( start < minLimit|| start > maxLimit || end < minLimit || end > maxLimit || start > end ) return null;
		StringBuilder sb = new StringBuilder();
		sb.append(start).append('-').append(end);
		return sb.toString();
	}	

	public String seconds = "*";  // 0-59 * / , -
	public String minutes = "*";  // 0-59 * / , -
	public String hours = "*";    // 0-23 * / , - 
	public String dates = "*";    // 1-31 * / , - ? L W C  
	public String months = "*";   // 1-12 or JAN-DEC * / , - 
	public String weekDays = "?"; // 1-7 or SUN-SAT * / , - ? L C # 
	public String years = "*";    // 1970-2099 * / , -  
	
	public boolean setSecond(int time, boolean isInterval) {
		if ( isInterval ) this.seconds = runInterval(time, 59);
		else this.seconds = runAt(time, 0, 59);
		if ( null == this.seconds) return false;
		return true;
	}

	public boolean setSeconds(int[] times) {
		this.seconds = runAt(times, 0, 59);
		if ( null == this.seconds) return false;
		return true;
	}
	
	public boolean setSeconds(int start, int end) {
		this.seconds = runFromTo(start, end, 0,  59);
		if ( null == this.seconds) return false;
		return true;
	}	

	/**
	 * Minutes settings
	 * @param time
	 * @param isInterval
	 */
	public boolean setMinute(int time, boolean isInterval) {
		if ( isInterval ) this.minutes = runInterval(time,59);
		else this.minutes = runAt(time, 0, 59);
		if ( null == this.minutes) return false;
		return true;
	}

	public boolean setMinutes(int[] times) {
		this.minutes = runAt(times, 0, 59);
		if ( null == this.minutes) return false;
		return true;
	}
	
	public boolean setMinutes(int start, int end) {
		this.minutes = runFromTo(start, end, 0, 59);
		if ( null == this.minutes) return false;
		return true;
	}
	
	/**
	 * Hours settings
	 * @param time
	 * @param isInterval
	 */
	
	public boolean setHour(int time, boolean isInterval) {
		if ( isInterval ) this.hours = runInterval(time, 23);
		else this.hours = runAt(time,0,  23);
		if ( null == this.hours) return false;
		return true;
	}

	public boolean setHours(int[] times) {
		this.hours = runAt(times, 0, 23);
		if ( null == this.hours) return false;
		return true;
		
	}
	
	public boolean setHours(int start, int end) {
		this.hours = runFromTo(start, end, 0, 23);
		if ( null == this.hours) return false;
		return true;
	}

	/**
	 * This is the day range.
	 * @param time
	 * @param isInterval
	 * @return
	 */
	public boolean setDate(int time, boolean isInterval) {
		this.weekDays = "?";
		if ( isInterval ) this.dates = runInterval(time, 31);
		else this.dates = runAt(time, 1, 31);
		if ( null == this.dates) return false;
		return true;
	}

	public boolean setDates(int[] times) {
		this.weekDays = "?";
		this.dates = runAt(times, 1, 31);
		if ( null == this.dates) return false;
		return true;
		
	}
	
	public boolean setDates(int start, int end) {
		this.weekDays = "?";
		this.dates = runFromTo(start, end, 1, 31);
		if ( null == this.dates) return false;
		return true;
	}
	
	public void setLastDateOfMonth() {
		this.dates = "L";
	}	
	
	/**
	 * This is the Month range.
	 * @param time
	 * @param isInterval
	 * @return
	 */
	public boolean setMonth(int time, boolean isInterval) {
		if ( isInterval ) this.months = runInterval(time, 12);
		else this.months = runAt(time, 1, 12);
		if ( null == this.months) return false;
		return true;
	}

	public boolean setMonths(int[] times) {
		this.months = runAt(times, 1, 12);
		if ( null == this.months) return false;
		return true;
		
	}
	
	public boolean setMonths(int start, int end) {
		this.months = runFromTo(start, end, 1, 12);
		if ( null == this.months) return false;
		return true;
	}	


	public boolean setWeekDay(int time, boolean isInterval) {
		this.dates = "?";
		if ( isInterval ) this.weekDays = runInterval(time, 7);
		else this.weekDays = runAt(time, 1, 7);
		if ( null == this.weekDays) return false;
		return true;
	}

	public boolean setWeekDays(int[] times) {
		this.dates = "?";
		this.weekDays = runAt(times,  1, 7);
		if ( null == this.weekDays) return false;
		return true;
	}

	public boolean setWeekDays(int start, int end) {
		this.dates = "?";
		this.weekDays = runFromTo(start, end,  1, 7);
		if ( null == this.weekDays) return false;
		return true;
	}		
	
	public void setFirstWeekDayOfMonth() {
		if ( ! (
			"*".equals(this.weekDays) || "?".equals(this.weekDays) ) ) {
			this.weekDays = this.weekDays + "#1"; 
		}
	}

	public void setSecondWeekDayOfMonth() {
		if ( ! (
			"*".equals(this.weekDays) || "?".equals(this.weekDays) ) ) {
			this.weekDays = this.weekDays + "#2"; 
		}
	}
	
	public void setThirdWeekDayOfMonth() {
		if ( ! (
				"*".equals(this.weekDays) || "?".equals(this.weekDays) ) ) {
			this.weekDays = this.weekDays + "#3"; 
		}
	}
	
	public void setLastWeekDayOfMonth() {
		if ( ! (
			"*".equals(this.weekDays) || "?".equals(this.weekDays) ) ) {
			this.weekDays = this.weekDays + "L"; 
		}
	}
	

	
	/**
	 * Year setting
	 * @param time
	 * @param isInterval
	 * @return
	 */
	public boolean setYear(int time, boolean isInterval) {
		if ( isInterval ) this.years = runInterval(time, 2099);
		else this.years = runAt(time, 1970, 2099);
		if ( null == this.years) return false;
		return true;
	}

	public boolean setYears(int[] times) {
		this.years = runAt(times, 1970, 2099);
		if ( null == this.years) return false;
		return true;
		
	}
	
	public boolean setYears(int start, int end) {
		this.years = runFromTo(start, end, 1970, 2099);
		if ( null == this.years) return false;
		return true;
	}		
	
	public String getExpression() {

		StringBuilder sb = new StringBuilder();
		sb.append(this.seconds).append(" ");
		sb.append(this.minutes).append(" ");
		sb.append(this.hours).append(" ");
		sb.append(this.dates).append(" ");
		sb.append(this.months).append(" ");
		if ( "*".equals(years)) {
			sb.append(this.weekDays);
		} else {
			sb.append(this.weekDays).append(" ");
			sb.append(this.years);
		}
		return sb.toString();
	}
}
