package org.quartz;

public class CronExpressionTest
{

	public static void main(String[] args) throws Exception
	{
		CronExpressionTest test = new CronExpressionTest();
		test.testNextMinute();
	}
	
	private void testNextMinute()
	{
		String expr = CronExpression.nextMinute();
		System.out.println("Cron expression: " + expr + " is valid = " + CronExpression.isValidExpression(expr));
	}
}
