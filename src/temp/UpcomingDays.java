package temp;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import org.joda.time.Period;

public class UpcomingDays
{
	public static void main(String[] args)
	{
		boolean limit = false;
		int maxRecurrences = 3; 
		
		boolean monthlyRecur = true;
		int everyNthMonth = 2;
		java.util.List<LocalDate> monthsToRecurOn = new java.util.ArrayList<LocalDate>();
		
		boolean weeklyRecur = false;
		int everyNthWeek = 2;
		java.util.List<LocalDate> weeksToRecurOn = new java.util.ArrayList<LocalDate>();
		
		java.util.List<String> weekDaysToRecurOn = new java.util.ArrayList<String>();	
		weekDaysToRecurOn.add("Monday");
		weekDaysToRecurOn.add("Friday");
		
		LocalDate startDate = LocalDate.now();
		LocalDate currentDate = startDate;
		LocalDate endDate = LocalDate.parse("2016-11-16");

		java.util.List<LocalDate> recurOn = null;
		if(monthlyRecur)
		{
			monthsToRecurOn.add(currentDate);
			while(currentDate.plusWeeks(everyNthMonth).isBefore(endDate))
			{
				currentDate = currentDate.plusWeeks(everyNthMonth);
				monthsToRecurOn.add(currentDate);
			}
			
			recurOn = monthsToRecurOn;
		}
		else if(weeklyRecur)
		{
			weeksToRecurOn.add(currentDate);
			while(currentDate.plusWeeks(everyNthWeek).isBefore(endDate))
			{
				currentDate = currentDate.plusWeeks(everyNthWeek);
				weeksToRecurOn.add(currentDate);
			}	
//			printWeekly(weeksToRecurOn, endDate, limit, maxRecurrences);	
		}
	}
	
//	public static void printWeekly(java.util.List<LocalDate> recurOn, LocalDate endDate, boolean limit, int maxRecurrences)
//	{
//		int count = 0;
//		for(int index = 0; index < recurOn.size(); index++)
//		{
//			int nextIndex = index + 1;
//			LocalDate date = recurOn.get(index);
//			LocalDate nextDate = endDate;
//			if(recurOn.size() > nextIndex && recurOn.get(nextIndex) != null)
//			{
//				nextDate = recurOn.get(index + 1);
//			}
//			
//			System.out.println("*** " + date + " ***");
//			
//			while(date.isBefore(nextDate))
//			{
//				String currentDayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
//				if(daysToRecurOn.contains(currentDayOfWeek))
//				{
//					/*
//					 * if limit by max occurrences
//					 */
//					if(limit && count < maxRecurrences)
//					{
//						System.out.println(date + " (" + currentDayOfWeek + ")");
//						count++;
//					}
//					/*
//					 * if limit by end date
//					 */
//					else
//					{
//						System.out.println(date + " (" + currentDayOfWeek + ")");
//					}
//				}
//				
//				date = date.plusDays(1);
//			}
//		}
//	}
}
