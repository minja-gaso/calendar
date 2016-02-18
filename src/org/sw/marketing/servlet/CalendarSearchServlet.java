package org.sw.marketing.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;

@WebServlet("/search/*")
public class CalendarSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
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
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();
		java.util.List<Long> eventIDs = eventDAO.getMatchedEventsForSearch("post-partum");
		System.out.println("SIZE:" + eventIDs.size());
		String path = request.getPathInfo();
		System.out.println("Path:" + path);
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
