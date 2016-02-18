package org.sw.marketing.servlet.json;

import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.category.CalendarCategoryDAO;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;
import org.sw.marketing.data.calendar.Data;
import org.sw.marketing.data.calendar.Data.Calendar;
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.data.calendar.Data.Calendar.Category;
import org.sw.marketing.data.calendar.Data.Calendar.CurrentView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/api/events/all.json")
public class ApiCalendarEventsServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String paramCalendarID = request.getParameter("calendarID");
		System.out.println(paramCalendarID);
		long calendarID = 0;
		try
		{
			calendarID = Long.parseLong(paramCalendarID);
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		CalendarDAO calendarDAO = DAOFactory.getCalendarDAO();
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();
		CalendarCategoryDAO categoryDAO = DAOFactory.getCalendarCategoryDAO();
		
		Data data = new Data();
		Calendar calendar = calendarDAO.getCalendar(calendarID);
		
		if(calendar != null)
		{
			java.util.List<Event> events = eventDAO.getCalendarEvents(calendar.getId());
			if(events != null)
			{
				calendar.getEvent().addAll(events);
			}
			
			LocalDate localDate = LocalDate.now().withDayOfMonth(1);
			int firstDay = localDate.getDayOfWeek().getValue() + 1;
			int totalDays = localDate.getMonth().maxLength();
			CurrentView currentView = new CurrentView();
			currentView.setStartDay(firstDay);
			currentView.setTotalDays(totalDays);
			calendar.setCurrentView(currentView);
			
			java.util.List<Category> categories = categoryDAO.getCategories(calendar.getId());
			if(categories !=  null)
			{
				calendar.getCategory().addAll(categories);
			}
			
			data.getCalendar().add(calendar);
			
			response.setContentType("application/json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(data);
			response.getWriter().println(json);
		}
		else
		{
			response.getWriter().println("The calendar you are looking for could not be found.");
			return;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

}
