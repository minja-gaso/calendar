import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Demo
{
	private static String urlStr = "http://scottandwhite.photobooks.com/WebService/Service.asp?type=form&FormData=Practices";
	public static void main(String[] args)
	{
		org.jsoup.nodes.Document externalDocument;
		try
		{
			externalDocument = (org.jsoup.nodes.Document) Jsoup.connect(urlStr).get();
			String xmlString = externalDocument.toString();
			
			JAXBContext jc = JAXBContext.newInstance(String.class);
	        Unmarshaller unmarshaller = jc.createUnmarshaller();
	        StreamSource xmlSource = new StreamSource(new StringReader(xmlString));
	        JAXBElement<String> je = unmarshaller.unmarshal(xmlSource, String.class);
	        System.out.println("val: " + je.getValue() + "\n ...");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
}
