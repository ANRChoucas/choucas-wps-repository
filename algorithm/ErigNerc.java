package choucas.erig.algorithm;

/**
 * Package algorithm.erig
 * Provides WPS processes (services) to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Author : Eric Gouardères
 * Project : LMAP/IPRA/CHOUCAS, march 2021
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;
 
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlObject.Factory;

import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.ComplexDataOutput;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Algorithm(version = "1.0.0", title = "Erig/Perdido", abstrakt = "Implementation of the Erig Web Service NERC")
public class ErigNerc extends AbstractAnnotatedAlgorithm {
	
	private static final Logger log = LoggerFactory.getLogger(ErigNerc.class);	
	
//	private List<XmlObject> complexInput;
    private String content=null, api_key=null, lang=null;

    private XmlObject complexOutput;
//    private String literalOutput;

//    @ComplexDataInput(binding = GenericXMLDataBinding.class, identifier = "complexInput", minOccurs = 0, maxOccurs = 1)
//    public void setComplexInput(List<XmlObject> complexInput) {
//        this.complexInput = complexInput;
//    }

    @LiteralDataInput(identifier = "api_key", minOccurs = 0, maxOccurs = 1)
    public void setLiteralKey(String literalInput) {
        this.api_key = literalInput;
    }
    
    @LiteralDataInput(identifier = "lang", minOccurs = 0, maxOccurs = 1)
    public void setLiteralLang(String literalInput) {
        this.lang = literalInput;
    }	
    
    @LiteralDataInput(identifier = "content", minOccurs = 1, maxOccurs = 1)
    public void setLiteralContent(String literalInput) {
        this.content = literalInput;
    }	
    
  @ComplexDataOutput(identifier = "complexOutput", binding = GenericXMLDataBinding.class)
  public XmlObject getComplexOutput() {
      return complexOutput;
  }

//  @LiteralDataOutput(identifier = "literalOutput")
//  public String getLiteralOutput() {
//  	return literalOutput;
//  }
	
    @Execute
    public void run() {
    	
        String url_base =  "http://erig.univ-pau.fr";
        String default_key = "choucas"; // YOUR API_KEY
        String default_lang = "French";
        String default_text = "Je visite les Pyrénées au sud de Toulouse.";
        String response = "";
        String separator = "****************";

    	
        log.debug("Running erig process");
        
        if (api_key == null) {
        	api_key = default_key;
        }
        
        if (lang == null) {
        	lang = default_lang;
        }

        if (content != null) {
        	
            try {
                System.out.println("\n" + separator);               
                System.out.println("Calling NERC...\nInput :\n" + content);
                response = callNERC(url_base, api_key, lang, content);
                System.out.println("\nREST Service Invoked Successfully...\nOutput :\n");
                
                complexOutput = Factory.parse(response);
                System.out.println(complexOutput);
                
                
            } catch (Exception e) {
                System.out.println("\nError while calling REST Service");
                e.printStackTrace();
            }

        }
        else {
        	System.out.println("No content");
            log.debug("No content");
        }

 //       log.debug("Finished erig process, literal output is : {}", literalOutput);
        log.debug("Finished erig process, complex output is : {}", complexOutput);
    }
    
    
    // Expanded named entity recognition and classification service
    protected static String callNERC(String api_url, String api_key, String lang, String content) throws JSONException, IOException
    {
    		api_url += "/PERDIDO/api/nerc/txt_xml/";
    		
        	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\""+lang+"\"}";
   		
    		return callServiceREST(api_url, request);
    }
        
	protected static String stringCleaning(String content)
	{
		return content.replaceAll("[\\n\\r]", "").replaceAll(">\\s*<","><");
	}
	
	protected static String callServiceREST(String api_url, String request) throws IOException, JSONException
    {
    	String response = "";
    		
    	JSONObject jsonObject = new JSONObject("{\"request\":"+request+"}");
    		
    	URL url = new URL(api_url);
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type","application/json");
		
			   
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(jsonObject.toString());
		out.close();
	 
		InputStreamReader input = new InputStreamReader(connection.getInputStream(),"UTF-8");
		BufferedReader in = new BufferedReader(input);
		
		String line = null;
			 
		while ((line = in.readLine()) != null) {
			response += line;
		}
			 
		in.close();
			 
		return response;
    }
	
	protected static void print(String content, String regex)
	{	// split the string according to one or more regex (separated by ; in the regex string) and print the resulting lines
		// each regex is a mandatory char followed by optional tokens
		// as the split method remove the regex from the resulting string, we add the mandatory char it before printing 
		String[] regexChar = regex.split(";");
		String[] contentLines = null;
		String contentForm = content;
		for (int i=0;i<regexChar.length;i++) {
			contentLines = contentForm.split(regexChar[i]);
			contentForm = "";
			for (int j=0;j<(contentLines.length - 1);j++)
				contentForm+= contentLines[j]+regexChar[i].charAt(0)+"\n";
			contentForm+= contentLines[(contentLines.length - 1)]+"\n";
		}
		System.out.println(contentForm);
	}
    

}
