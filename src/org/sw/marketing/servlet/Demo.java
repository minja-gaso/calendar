package org.sw.marketing.servlet;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Demo
{

	public static void main(String[] args)
	{
		Date startDate = new Date();
	    System.out.println("Date 1: " + startDate + " [START DATE]");
	    
	    Date currentDate = startDate;
	    for(int index = 0; index < 5; index++)
	    {
		    Calendar cal = Calendar.getInstance();
		    cal.set(Calendar.DAY_OF_MONTH);
		    cal.set(Calendar.MONTH, 2);
		    cal.set(Calendar.YEAR, 2016);
	    }

	    GregorianCalendar gregorianCalendar = new GregorianCalendar();
	    gregorianCalendar.setTime(startDate);
	    gregorianCalendar.add(Calendar.DATE, 5);
	    startDate.setTime(gregorianCalendar.getTime().getTime());
	    System.out.println("Date 2: " + startDate);	    
	    
	    Date endDate = new Date();
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.DAY_OF_MONTH, 22);
	    cal.set(Calendar.MONTH, 2);
	    cal.set(Calendar.YEAR, 2016);
	    endDate.setTime(cal.getTimeInMillis());

	    System.out.println("Date 3: " + endDate + " [END DATE]");
	}

}
