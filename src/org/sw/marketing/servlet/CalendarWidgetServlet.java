package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Element;
import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.category.CalendarCategoryDAO;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;
import org.sw.marketing.dao.calendar.event.CalendarEventTagDAO;
import org.sw.marketing.dao.calendar.skin.CalendarSkinDAO;
import org.sw.marketing.data.calendar.Data;
import org.sw.marketing.data.calendar.Data.Calendar;
import org.sw.marketing.data.calendar.Environment;
import org.sw.marketing.data.calendar.Skin;
import org.sw.marketing.data.calendar.Data.Calendar.Category;
import org.sw.marketing.data.calendar.Data.Calendar.CurrentView;
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.data.calendar.Data.Calendar.Event.Tag;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

@WebServlet("/widget/*")
public class CalendarWidgetServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{ 
		boolean prettyUrl = false;
		long calendarID = 0;
		String prettyUrlStr = null;
		try
		{
			calendarID = Long.parseLong(request.getPathInfo().substring(1));
		}
		catch(NumberFormatException e)
		{
			prettyUrl = true;
			prettyUrlStr = request.getPathInfo().substring(1);
		}
		
		CalendarDAO calendarDAO = DAOFactory.getCalendarDAO();
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();
		CalendarCategoryDAO categoryDAO = DAOFactory.getCalendarCategoryDAO();
		
		Data data = new Data();
		Calendar calendar = null;
		
		if (prettyUrl)
		{
			calendar = calendarDAO.getCalendarByPrettyUrl(prettyUrlStr);
		}
		else
		{
			calendar = calendarDAO.getCalendar(calendarID);
		}
		
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
			int month = localDate.getMonthValue();
			CurrentView currentView = new CurrentView();
			currentView.setStartDay(firstDay);
			currentView.setTotalDays(totalDays);
			currentView.setMonth(month);
			calendar.setCurrentView(currentView);
			
			java.util.List<Category> categories = categoryDAO.getCategories(calendar.getId());
			if(categories !=  null)
			{
				calendar.getCategory().addAll(categories);
			}
			
			data.getCalendar().add(calendar);
		}
		else
		{
			response.getWriter().println("The calendar you are looking for could not be found.");
			return;
		}
		
		Environment environment = new Environment();
		environment.setScreenName("LIST");		
		data.setEnvironment(environment);

		/*
		 * generate output
		 */
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("assetXslUrl"));
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.calendar", data);
		String xslScreen = getServletContext().getInitParameter("assetXslPath") + "widget/1.xsl";
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		htmlStr = htmlStr.replaceAll("\"", "\\\"");
		
		System.out.println(xmlStr);
		response.setCharacterEncoding("utf-8");
		System.out.println(htmlStr);
		response.getWriter().println(htmlStr + "...\"");
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
