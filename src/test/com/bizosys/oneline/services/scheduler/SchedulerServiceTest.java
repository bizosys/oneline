package com.bizosys.oneline.services.scheduler;

import java.util.Date;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.services.batch.BatchTask;
import com.bizosys.oneline.services.scheduler.ExpressionBuilder;
import com.bizosys.oneline.services.scheduler.ScheduleTask;
import com.bizosys.oneline.services.scheduler.SchedulerService;

public class SchedulerServiceTest {
	public static void main(String[] args) throws Exception {
		try {
			SchedulerService.getInstance().init(new Configuration(), null);

			ExpressionBuilder expr = new ExpressionBuilder();
			expr.setSecond(5, true);
			
			BatchTask mytask1 = new HelloTask("Abinash");
			mytask1.setJobName("DEVEL1");

			BatchTask mytask2 = new HelloTask("Sunil");
			mytask2.setJobName("DEVEL2");
			
			long startTime = new Date().getTime();
			new ScheduleTask(mytask1, expr.getExpression(), new Date(startTime), new Date(startTime + 1000000));
			System.out.println("Job 1 Scheduled");
			new ScheduleTask(mytask2, expr.getExpression(), new Date(startTime), new Date(startTime + 1000000));
			System.out.println("Job 2 Scheduled");
			
			//It should execute 3 times
			try { Thread.sleep(180*1000); } catch (Exception ex) {};
			//assertTrue(mytask.timesExecuted > 1);

			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

}
