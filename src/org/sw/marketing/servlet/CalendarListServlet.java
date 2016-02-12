package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;
import org.sw.marketing.data.calendar.Data;
import org.sw.marketing.data.calendar.Data.Calendar;
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;
import org.sw.marketing.util.SkinReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/list/*")
public class CalendarListServlet extends HttpServlet
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
			System.out.println("EVENTS:" + events);
			if(events != null)
			{
				calendar.getEvent().addAll(events);
			}
			data.getCalendar().add(calendar);
		}
		else
		{
			response.getWriter().println("The calendar you are looking for could not be found.");
			return;
		}

		/*
		 * generate output
		 */
		TransformerHelper transformerHelper = new TransformerHelper();
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.calendar", data);
		String xslScreen = getServletContext().getInitParameter("assetXslPath") + "list.xsl";
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox_1col.html";
		String skinHtmlStr = null;

		String skinUrl = calendar.getSkinUrl();
		String skinCssSelector = calendar.getSkinSelector();

		if (skinUrl.length() > 0 && skinCssSelector.length() > 0)
		{
			skinHtmlStr = SkinReader.getSkinByUrl(calendar.getSkinUrl(), calendar.getSkinSelector());
		}
		else
		{
			skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		}

		skinHtmlStr = skinHtmlStr.replace("{TITLE}", calendar.getTitle());
		skinHtmlStr = skinHtmlStr.replace("{CONTENT}", htmlStr);
		
		System.out.println(xmlStr);
		response.getWriter().println(skinHtmlStr);
		
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String json = gson.toJson(data);
//		System.out.println(json);
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