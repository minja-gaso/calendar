package temp;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class Demo
{
	public static void main(String[] args)
	{
		Recursion recursion = new Recursion();
		recursion.setStartDate(LocalDate.parse("2016-02-13"));
		recursion.setCurrentDate(recursion.getStartDate());
		recursion.setEndDate(LocalDate.parse("2016-03-01"));
		recursion.setLimitEnabled(true);
		recursion.setLimit(10);
		recursion.setInterval(2);
		
		java.util.List<String> recursions = new java.util.ArrayList<String>();
		recursions.add("SATURDAY");
		recursions.add("MONDAY");
		recursions.add("TUESDAY");
		recursion.setRecursions(recursions);
		
//		printWeekly(recursion);
		
		recursion = new Recursion();
		recursion.setStartDate(LocalDate.parse("2016-03-30"));
		recursion.setCurrentDate(recursion.getStartDate());
		recursion.setEndDate(LocalDate.parse("2016-09-01"));
		recursion.setLimitEnabled(true);
		recursion.setLimit(20);
		recursion.setInterval(2);
		
		printMonthly(recursion);
	}
	
	private static void printWeekly(Recursion recursion)
	{
		boolean limitEnabled = recursion.isLimitEnabled();
		int limit = recursion.getLimit();
		int interval = recursion.getInterval();
		int counter = 0;
		
		LocalDate startDate = recursion.getStartDate();
		LocalDate currentDate = recursion.getCurrentDate();
		LocalDate endDate = recursion.getEndDate();
		
		java.util.List<String> recursions = recursion.getRecursions();
		
		while(currentDate.isBefore(endDate) || currentDate.isEqual(endDate))
		{
			String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
			if(recursions.contains(dayOfWeek))
			{
				if(limitEnabled)
				{
					if(counter < limit)
					{
						System.out.println(currentDate);
						counter++;
					}
				}
				else
				{
					System.out.println(currentDate);
				}
			}
			
			String lastDayInRecursion = recursions.get(recursions.size() - 1);
			if(dayOfWeek.equals(lastDayInRecursion) && interval > 1)
			{
				currentDate = currentDate.plusWeeks(interval - 1).plusDays(1);
			}
			else
			{
				currentDate = currentDate.plusDays(1);	
			}		
		}
	}
	
	public static void printMonthly(Recursion recursion)
	{
		boolean limitEnabled = recursion.isLimitEnabled();
		int limit = recursion.getLimit();
		int interval = recursion.getInterval();
		int counter = 0;
		
		LocalDate startDate = recursion.getStartDate();
		LocalDate currentDate = recursion.getCurrentDate();
		LocalDate endDate = recursion.getEndDate();
		int dayOfMonth = currentDate.getDayOfMonth();
		
		while(currentDate.isBefore(endDate) || currentDate.isEqual(endDate))
		{
			if(limitEnabled)
			{
				if(counter < limit)
				{
					System.out.println(currentDate);
					counter++;
				}
			}
			else
			{
				System.out.println(currentDate);
			}
			
//			if(interval > 1)
//			{
//				currentDate = currentDate.plusMonths(interval - 1).plusDays(1);
//			}
//			else
//			{
//				currentDate = currentDate.plusDays(1);	
//			}		
			try
			{
				currentDate = currentDate.plusMonths(interval).withDayOfMonth(dayOfMonth);
			}
			catch(DateTimeException e)
			{
				currentDate = currentDate.plusMonths(interval + 1);
			}
		}
	}
}
