package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.data.calendar.Data.Calendar.Event.Tag;
import org.sw.marketing.data.calendar.Data.Calendar.Search;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;
import org.sw.marketing.util.SkinReader;

@WebServlet("/search/*")
public class CalendarSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		CalendarDAO calendarDAO = DAOFactory.getCalendarDAO();
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();
		CalendarCategoryDAO categoryDAO = DAOFactory.getCalendarCategoryDAO();
		CalendarEventTagDAO tagsDAO = DAOFactory.getCalendarEventTagDAO();
		
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
			Search search = new Search();
			search.setFkCalendarId(calendarID);
			String paramSearchType = request.getParameter("searchType");
			if(paramSearchType == null)
			{
				paramSearchType = "keyword";
			}
			String paramSearchKeyword = request.getParameter("searchKeyword");
			if(paramSearchKeyword == null)
			{
				paramSearchKeyword = "";
			}
			
			/*
			 * get and display data
			 */
			java.util.List<Long> eventIDs = null;
			java.util.List<Event> events = null;
			if(paramSearchType.equals("category"))
			{
				String paramCategoryId = request.getParameter("categoryId");
				long categoryID = Long.parseLong(paramCategoryId);
				search.setCategoryId(categoryID);
				
				events = eventDAO.getCalendarEventsByCategory(search);
				if(events != null)
				{
					calendar.getEvent().addAll(events);
				}
			}
			if(paramSearchType.equals("tag"))
			{
				String paramTagId = request.getParameter("tagId");
				String paramTagName = request.getParameter("tagName");
				long tagId = Long.parseLong(paramTagId);
				search.setTagId(tagId);
			
				eventIDs = eventDAO.getMatchedEventsForTag(search);
				if(eventIDs != null)
				{
					java.util.Iterator<Long> eventIdIt = eventIDs.iterator();
					while(eventIdIt.hasNext())
					{
						long eventID = eventIdIt.next();
						Event event = eventDAO.getCalendarEvent(eventID);
						java.util.List<Tag> tags = tagsDAO.getTags(eventID);
						if(tags != null)
						{
							event.getTag().addAll(tags);
						}
						calendar.getEvent().add(event);
					}
				}
			}
			else
			{
				eventIDs = eventDAO.getMatchedEventsForSearch(paramSearchKeyword);
				if(eventIDs != null)
				{
					java.util.Iterator<Long> eventIdIt = eventIDs.iterator();
					while(eventIdIt.hasNext())
					{
						long eventID = eventIdIt.next();
						Event event = eventDAO.getCalendarEvent(eventID);
						calendar.getEvent().add(event);
					}
				}
				search.setQuery(paramSearchKeyword);				
			}
			
			calendar.setSearch(search);
			
			java.util.List<Category> categories = categoryDAO.getCategories(calendar.getId());
			if(categories != null)
			{
				calendar.getCategory().addAll(categories);
			}
			
			data.getCalendar().add(calendar);
			
			Environment environment = new Environment();
			environment.setScreenName("SEARCH");			
			data.setEnvironment(environment);
			
			/*
			 * generate output
			 */
			TransformerHelper transformerHelper = new TransformerHelper();
			transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("assetXslUrl"));
			String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.calendar", data);
			String xslScreen = getServletContext().getInitParameter("assetXslPath") + "search.xsl";
			String xslStr = ReadFile.getSkin(xslScreen);
			String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
			
			String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox_1col.html";
			String skinHtmlStr = null;
			
			CalendarSkinDAO skinDAO = DAOFactory.getCalendarSkinDAO();
			String paramSkinID = request.getParameter("skinID");
			long skinID = calendar.getFkSkinId();
			if(paramSkinID != null)
			{
				try
				{
					skinID = Long.parseLong(paramSkinID);
				}
				catch(NumberFormatException e)
				{
					//
				}
			}
			
			
			Skin skin = null;
			if(skinID > 0)
			{
				skin = skinDAO.getSkin(skinID);
			}
			
			if(skin != null)
			{
				skinHtmlStr = skin.getSkinHtml();
			}
			else
			{
				skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
			}
			skinHtmlStr = skinHtmlStr.replace("{TITLE}", calendar.getTitle());
			skinHtmlStr = skinHtmlStr.replace("{CONTENT}", htmlStr);
			
			if(skin != null)
			{
				Element styleElement = new Element(org.jsoup.parser.Tag.valueOf("style"), "");
				String skinCss = skin.getSkinCssOverrides() + skin.getCalendarCss();
				styleElement.text(skinCss);
				String styleElementStr = styleElement.toString();
				styleElementStr = styleElementStr.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
				skinHtmlStr = skinHtmlStr.replace("{CSS}", styleElementStr);			
			}
			
			System.out.println(xmlStr);
			response.getWriter().println(skinHtmlStr);
		}
		else
		{
			response.getWriter().println("The calendar does not exist.");
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
