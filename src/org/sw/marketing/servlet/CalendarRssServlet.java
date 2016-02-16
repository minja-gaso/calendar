package org.sw.marketing.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.digester.rss.Channel;
import org.apache.commons.digester.rss.Item;
import org.jsoup.Jsoup;
import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.category.CalendarCategoryDAO;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;
import org.sw.marketing.data.calendar.Data;
import org.sw.marketing.data.calendar.Data.Calendar;
import org.sw.marketing.data.calendar.Data.Calendar.Category;
import org.sw.marketing.data.calendar.Data.Calendar.CurrentView;
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.data.calendar.Data.Environment;
import org.sw.marketing.transformation.TransformerHelper;

@WebServlet("/rss/*")
public class CalendarRssServlet extends HttpServlet
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
		catch (NumberFormatException e)
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

		if (calendar != null)
		{
			java.util.List<Event> events = eventDAO.getCalendarEvents(calendar.getId());
			if (events != null)
			{
				calendar.getEvent().addAll(events);
			}

			java.util.List<Category> categories = categoryDAO.getCategories(calendar.getId());
			if (categories != null)
			{
				calendar.getCategory().addAll(categories);
			}

			data.getCalendar().add(calendar);
			
			Environment environment = new Environment();
			environment.setServerName(getBaseUrl(request));
			data.setEnvironment(environment);
			

			TransformerHelper transformerHelper = new TransformerHelper();
			String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.calendar", data);
			System.out.println(xmlStr);

			/*
			 * Begin RSS generation process
			 */
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
			String today = formatter.format(new Date());
			String channelLink = environment.getServerName() + "/calendar/list/" + calendar.getPrettyUrl();
			
			Channel channel = new Channel();
			channel.setVersion(2.0);
			channel.setTitle(calendar.getTitle());
			channel.setCopyright("2016 Baylor Scott and White");
			channel.setDescription("List of upcoming events.");
			channel.setLink(channelLink);
			channel.setLanguage("en");
			channel.setPubDate(today);

			for(Event event : calendar.getEvent())
			{
				String itemLink = environment.getServerName() + "/calendar/detail/" + calendar.getId() + "?eventID=" + event.getId();
				String itemDescription = Jsoup.parse(event.getDescription()).text();
				
				Item item = new Item();
				item.setTitle(event.getTitle());
				item.setLink(itemLink);
				item.setDescription(itemDescription);
				channel.setPubDate(event.getStartDate().toString());
				channel.addItem(item);
			}
			StringWriter rssStr = new StringWriter();
			channel.render(rssStr);
			
			response.setContentType("application/rss+xml");
			response.getWriter().println(rssStr);
		}
		else
		{
			response.getWriter().println("The calendar you are looking for could not be found.");
			return;
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}
	
	/**
	 * NOT UNIT TESTED Returns the base url (e.g, <tt>http://myhost:8080/myapp</tt>) suitable for
	 * using in a base tag or building reliable urls.
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
		{
			return request.getScheme() + "://" + request.getServerName();	
		}
		else
		{

			return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		}
	}
}
