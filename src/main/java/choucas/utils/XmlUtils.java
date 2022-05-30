/**
 * Package choucas.utils
 * Provides configuration and utility classes and methods 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlObject.Factory;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.datahandler.generator.GenericXMLDataGenerator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class provides utility method for Xml Objects
 *
 * @author Eric Gouardères
 * @date August 2021
 */

public class XmlUtils {
	  public static String getData(XmlObject data) {
		  String response="";
		  GenericXMLDataBinding xmlDataBinding = new GenericXMLDataBinding(data);
		  GenericXMLDataGenerator XmlGen = new GenericXMLDataGenerator();
		  InputStreamReader input;
		try {
			input = new InputStreamReader(XmlGen.generateStream(xmlDataBinding, "", ""));
			 BufferedReader in = new BufferedReader(input);
			 String line = null;
			 while ((line = in.readLine()) != null) {
				 response += line;
				  }
			 in.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		  return response;
	  }
	  
	  public static Document readFile(String filePath) throws ParserConfigurationException, SAXException, IOException   {
		  File xmlFile = new File(filePath);
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
		  return (db.parse(xmlFile));
	  }

}
