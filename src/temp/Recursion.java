package temp;

import java.time.LocalDate;

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
}
