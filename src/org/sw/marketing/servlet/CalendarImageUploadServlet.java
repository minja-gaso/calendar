package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;
import org.sw.marketing.data.calendar.Data;
import org.sw.marketing.data.calendar.Data.Calendar;
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

@WebServlet("/calendarEventImageUploadServlet")
public class CalendarImageUploadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String paramCalendarID = request.getParameter("CALENDAR_ID");
		long calendarID = 0;
		try
		{
			calendarID = Long.parseLong(paramCalendarID);
		}
		catch (NumberFormatException e)
		{
			calendarID = 0;
		}

		String paramEventID = request.getParameter("EVENT_ID");
		long eventID = 0;
		try
		{
			eventID = Long.parseLong(paramEventID);
		}
		catch (NumberFormatException e)
		{
			eventID = 0;
		}

		CalendarDAO calendarDAO = DAOFactory.getCalendarDAO();
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();

		Data data = new Data();
		Calendar calendar = calendarDAO.getCalendar(calendarID);

		if (calendar != null)
		{
			Event event = eventDAO.getCalendarEvent(eventID);
			if (event != null)
			{
				calendar.getEvent().add(event);
			}
			data.getCalendar().add(calendar);
		}

		/*
		 * generate output
		 */
		TransformerHelper transformerHelper = new TransformerHelper();
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.calendar", data);
		String xslScreen = getServletContext().getInitParameter("assetXslPath") + "event_image_upload.xsl";
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));

		System.out.println(xmlStr);
		response.getWriter().println(htmlStr);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String paramCalendarID = request.getParameter("CALENDAR_ID");
		long calendarID = 0;
		try
		{
			calendarID = Long.parseLong(paramCalendarID);
		}
		catch (NumberFormatException e)
		{
			calendarID = 0;
		}

		String paramEventID = request.getParameter("EVENT_ID");
		long eventID = 0;
		try
		{
			eventID = Long.parseLong(paramEventID);
		}
		catch (NumberFormatException e)
		{
			eventID = 0;
		}

		CalendarDAO calendarDAO = DAOFactory.getCalendarDAO();
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();

		Event event = eventDAO.getCalendarEvent(eventID);
		
		try
		{
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			if(items != null)
			{
				for (FileItem item : items)
				{
					if (item.isFormField())
					{
						/*
						 * process standard form fields
						 */
						String fieldName = item.getFieldName();
						String fieldValue = item.getString();
						
						if(fieldName.equals("EVENT_IMAGE_DESCRIPTION"))
						{
							event.setFileDescription(fieldValue);
						}
					}
					else
					{
						/*
						 * process file form field
						 */
						String fieldName = item.getFieldName();
						String fileName = FilenameUtils.getName(item.getName());
						event.setFileName(fileName);
						InputStream fileContent = item.getInputStream();
						
						String uploadPath = getServletContext().getInitParameter("uploadsPath");
						// + request.getParameter("CALENDAR_ID") + java.io.File.separator + "event" + 
						// java.io.File.separator + request.getParameter(arg0);
						
						String calendarUploadPath = uploadPath + request.getParameter("CALENDAR_ID");
						java.io.File calendarUploadPathFile = new java.io.File(calendarUploadPath);
						if(!calendarUploadPathFile.exists())
						{
							calendarUploadPathFile.mkdir();
						}
						
						String eventUploadPath = calendarUploadPath + java.io.File.separator + request.getParameter("EVENT_ID");
						java.io.File eventUploadPathFile = new java.io.File(eventUploadPath);
						if(!eventUploadPathFile.exists())
						{
							eventUploadPathFile.mkdir();
						}
						
						String fileUploadPath = eventUploadPath + java.io.File.separator + event.getFileName();
						java.io.File fileSave = new java.io.File(fileUploadPath);
						try
						{
							item.write(fileSave);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				eventDAO.updateCalendarEvent(event);
			}
		}
		catch (FileUploadException e)
		{
			throw new ServletException("Cannot parse multipart request.", e);
		}

		process(request, response);
	}
}
