package temp;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class WeeklyRecurrence
{
	public static void main(String[] args)
	{
		LocalDate startDate = LocalDate.now();
		LocalDate currentDate = startDate;		
		LocalDate endDate = LocalDate.parse("2016-03-31");
		
		java.util.List<String> dayOfWeekAcceptedList = new java.util.ArrayList<String>();
		dayOfWeekAcceptedList.add("MONDAY");
		dayOfWeekAcceptedList.add("TUESDAY");
		
		boolean limitEnabled = true;
		int limit = 5;
		int counter = 0;
		
		while(currentDate.isBefore(endDate) || currentDate.isEqual(endDate))
		{
			String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
			if(dayOfWeekAcceptedList.contains(dayOfWeek))
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
			currentDate = currentDate.plusDays(1);
		}
	}
}
