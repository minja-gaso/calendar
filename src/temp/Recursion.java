package temp;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class Recursion
{
	private LocalDate startDate = null;
	private LocalDate currentDate = null;		
	private LocalDate endDate = null;
	
	private java.util.List<String> recursions = null;
	
	private boolean limitEnabled = false;
	private int limit = 0;
	private int interval = 1;
	
	public LocalDate getStartDate()
	{
		return startDate;
	}
	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}
	public LocalDate getCurrentDate()
	{
		return currentDate;
	}
	public void setCurrentDate(LocalDate currentDate)
	{
		this.currentDate = currentDate;
	}
	public LocalDate getEndDate()
	{
		return endDate;
	}
	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}
	public java.util.List<String> getRecursions()
	{
		return recursions;
	}
	public void setRecursions(java.util.List<String> recursions)
	{
		this.recursions = recursions;
	}
	public boolean isLimitEnabled()
	{
		return limitEnabled;
	}
	public void setLimitEnabled(boolean limitEnabled)
	{
		this.limitEnabled = limitEnabled;
		this.setEndDate(LocalDate.parse("2099-12-31"));
	}
	public int getLimit()
	{
		return limit;
	}
	public void setLimit(int limit)
	{
		this.limit = limit;
	}	
	public int getInterval()
	{
		return interval;
	}
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
	
	public void printWeekly(Recursion recursion)
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
	
	public void printMonthly(Recursion recursion)
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
