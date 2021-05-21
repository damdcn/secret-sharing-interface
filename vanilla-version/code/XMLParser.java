package code;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLParser {
	
	private XPath xpath;
	private Document doc;
	
	public XMLParser() {
		this.xpath = XPathFactory.newInstance().newXPath();
		 try {
			this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("file:./src/xmldata/data.xml");
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Bloc catch g�n�r� automatiquement
			e.printStackTrace();
		}
	}
	
	public Coord getTopLeftCoords(int id_pers) {
		int x = 0, y = 0;
		try {
			x = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='TL']/@x", doc));
			y = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='TL']/@y", doc));
		} catch (NumberFormatException | XPathExpressionException e) {
			// TODO Bloc catch g�n�r� automatiquement
			e.printStackTrace();
		}
		
		return new Coord(x, y);
	}
	
	public Coord getBotRightCoords(int id_pers) {
		int x = 0, y = 0;
		try {
			x = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='BR']/@x", doc));
			y = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='BR']/@y", doc));
		} catch (NumberFormatException | XPathExpressionException e) {
			// TODO Bloc catch g�n�r� automatiquement
			e.printStackTrace();
		}
		
		return new Coord(x, y);
	}
	
	public int getRegionWidth(int id_pers) {
		int x1 = 0, x2 = 0;
		try {
			x1 = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='TL']/@x", doc));
			x2 = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='BR']/@x", doc));
		} catch (NumberFormatException | XPathExpressionException e) {
			// TODO Bloc catch g�n�r� automatiquement
			e.printStackTrace();
		}
		return x2-x1;
	}
	
	public int getRegionHeight(int id_pers) {
		int y1 = 0, y2 = 0;
		try {
			y1 = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='TL']/@y", doc));
			y2 = Integer.parseInt(xpath.evaluate("/persons/person[@id='"+id_pers+"']/points[@position='BR']/@y", doc));
		} catch (NumberFormatException | XPathExpressionException e) {
			// TODO Bloc catch g�n�r� automatiquement
			e.printStackTrace();
		}
		return y2-y1;
	}
}
