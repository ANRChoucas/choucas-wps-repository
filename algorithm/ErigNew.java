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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlObject.Factory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.algorithm.annotation.LiteralDataOutput;
import org.n52.wps.algorithm.annotation.ComplexDataOutput;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Algorithm(version = "1.0.0", title = "Erig/Perdido", abstrakt = "Implementation of the Erig Web Service Chain PERDIDO")
public class ErigNew extends AbstractAnnotatedAlgorithm {
	
	private static final Logger log = LoggerFactory.getLogger(ErigNew.class);	
	
    private String textInput="", api_key = "demo", lang = "French";

    private XmlObject taggedText;
    private String topoList, topoUrl, textUrl;
    
    @LiteralDataInput(
    		identifier = "api_key", 
    		abstrakt = "Your API key",
    		defaultValue = "demo",
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setLiteralKey(String literalInput) {
        this.api_key = literalInput;
    }
    
    @LiteralDataInput(
    		identifier = "lang",
    		abstrakt = "Content language",
    		defaultValue = "French",
    		allowedValues = {"French", "Spanish", "English"},
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setLiteralLang(String literalInput) {
        this.lang = literalInput;
    }

    @LiteralDataInput(
    		identifier = "textInput",
    		abstrakt = "Textual content to annotate",
    		minOccurs = 1, 
    		maxOccurs = 1
    		)
    public void setTextInput(String textInput) {
        this.textInput = textInput;
    }
    
    @ComplexDataOutput(
    		identifier = "taggedText",
    		abstrakt = "XML/TEI NER tagged text",
    		binding = GenericXMLDataBinding.class
    		)
    public XmlObject getTaggedText() {
    	return taggedText;
    }
    
    @LiteralDataOutput(
  		  identifier = "textUrl",
  		  abstrakt = "Url of the tagged text file"
  		  )
    public String getTextUrl() {
    	return textUrl;
    }

  @LiteralDataOutput(
		  identifier = "topoList",
		  abstrakt = "List of toponyms found in the input text with their geo-location, using GeoJson standard output format"
		  )
  public String getTopoList() {
  	return topoList;
  }
  
  @LiteralDataOutput(
		  identifier = "topoUrl",
		  abstrakt = "Url of the topoList file"
		  )
  public String getTopoUrl() {
  	return topoUrl;
  }
	
    @Execute
    public void run() {
    	
        String url_base =  "http://erig.univ-pau.fr";
        String text = "Je visite les Pyrénées au sud de Toulouse.";
        String response = "";
        String separator = "****************";
        //TODO faire un fichier de config pour les paramètres serveur, temp...
        // Paramètres localhost
        //String hostUrl= "http://localhost:8080/";
        //String tempPath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\ROOT\\";
        // Paramètres serveur Choucas
        String hostUrl= "http://choucas.univ-pau.fr:8080/";
        String tempPath = "/opt/tomcat/webapps/ROOT/";
        String tempDir = "temp/";
        String timeFile;
        
        log.debug("Running erig process");

        if (textInput != null ) {
        	
            try {
                text = textInput;
                
                System.out.println("\n" + separator); 
                System.out.println("Calling POS...\nInput :\n" + text);
                response = callPOSUnitex(url_base, api_key, lang, text);
                System.out.println("\nREST Service Invoked Successfully...\nOutput :\n"+response);  
                
                System.out.println("\n" + separator);               
                System.out.println("Calling NER...\nInput :\n" + response);
                response = callNER(url_base, api_key, lang, response);
                System.out.println("\nREST Service Invoked Successfully...\nOutput :\n"+response);
                
                timeFile = java.time.LocalDateTime.now().toString().replace(":","-");
                writeFile(tempPath+tempDir+"taggedText"+timeFile+".xml", response);
                taggedText = Factory.parse(response);
                textUrl = hostUrl+tempDir+"taggedText"+timeFile+".xml";
                
                System.out.println("\n" + separator);                 
                System.out.println("Calling TOP...\nInput :\n" + response);
                response = callTOP(url_base, api_key, lang, stringCleaning(response));
                System.out.println("\nREST Service Invoked Successfully...\nOutput :");
                print(response,"};,");  
      
                System.out.println("\n" + separator);                 
                System.out.println("Calling GEO...\nInput :\n" + response);     
                response = callGEO(url_base,api_key, new JSONArray(response));
                System.out.println("\nREST Service Invoked Successfully:\nOutput :");
                print(response,"};,");
                
        		// Response from CallGEO is a String, so returned using literalOutput.
                // Despite  it represents structured data.
                timeFile = java.time.LocalDateTime.now().toString().replace(":","-");
                writeFile(tempPath+tempDir+"topoList"+timeFile+".json", response);
                topoList = response;
                topoUrl = hostUrl+tempDir+"topoList"+timeFile+".json";
                
            } catch (Exception e) {
                System.out.println("\nError while calling REST Service");
                e.printStackTrace();
            }

        }
        else {
        	System.out.println("No literal input");
            log.debug("No literal input");
        }

        log.debug("Finished erig process, literal output is : {}", topoList);
        log.debug("Finished erig process, complex output is : {}", taggedText);
    }
    
    // POS processing service, unitex output
    protected static String callPOSUnitex(String api_url, String api_key, String lang, String content) throws JSONException, IOException
    {
    		api_url += "/PERDIDO/api/pos/txt_unitex/";
    		
        	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\""+lang+"\"}";
   		
    		return callServiceREST(api_url, request);
 
    }
    
    // Expanded named entity recognition service
    protected static String callNER(String api_url, String api_key, String lang, String content) throws JSONException, IOException
    {
    		api_url += "/PERDIDO/api/ner/pos_xml/";
    		
        	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\""+lang+"\"}";
   		
    		return callServiceREST(api_url, request);
    }
    
    // GetToponyms service
    protected static String callTOP(String api_url, String api_key, String lang, String content) throws JSONException, IOException
    {
    		api_url += "/PERDIDO/api/toponyms/ner_json/";
    		
    		//String contentTest="<?xml version=\"1.0\" encoding=\"UTF-8\"?><TEI xmlns=\"http://www.tei-c.org/ns/1.0\"><teiHeader><fileDesc><titleStmt><title><!-- title of the resource --></title></titleStmt><publicationStmt><p><!-- Information about distribution of the resource --></p></publicationStmt><sourceDesc><p><!-- Information about source from which the resource derives --></p></sourceDesc></fileDesc></teiHeader><text><body><p><s><rs><name><w lemma=\"pau\" type=\"NPr\">Pau</w></name></rs><pc force=\"strong\">.</pc></s><s/></p></body></text></TEI>";
        	
    		content = URLEncoder.encode(content, "UTF-8");
    		
        	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\""+lang+"\"}";
  
    		return callServiceREST(api_url, request);
    }
 
    // GeoJson, KML or GPX service.
    // Returns a String containing a list of toponyms with their geo-location structured using standard output format (GeoJSON, KML, GPX). 
    // The expected output format is specified as a parameter value.
    protected static String callGEO(String api_url, String api_key, JSONArray content) throws JSONException, IOException
    {
    		api_url += "/PERDIDO/api/toponyms/json_gps/";
  
    		String outputFormat = "GeoJson";
        	String getURL = "false";
    		
    		String request = "{\"api_key\":\""+api_key+"\",\"content\":"+content+",\"outputFormat\":\""+outputFormat+"\",\"getURL\":\""+getURL+"\"}";
   		
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
			
			        // System.out.println("debug");
		 	 
		InputStreamReader input = new InputStreamReader(connection.getInputStream());
		BufferedReader in = new BufferedReader(input);
		
		String line = null;
			
		//System.out.println("debug");
			 
		while ((line = in.readLine()) != null) {
			response += line;
		}
			 
		in.close();
			 
		return response;
    }
	
	protected static void print(String content, String regex)
	{	// split the string according to one char regex (separated by ; in the regex string) and print the resulting lines
		// as the split method remove the char from the resulting string, we add it before printing 
		String[] regexChar = regex.split(";");
		String[] contentLines = null;
		String contentForm = content;
		for (int i=0;i<regexChar.length;i++) {
			contentLines = contentForm.split(regexChar[i]);
			contentForm = "";
			for (int j=0;j<contentLines.length;j++)
				contentForm+= (contentLines[j]+regexChar[i]+"\n");
		}
		System.out.println(contentForm);
	}
	
	protected static void writeFile(String filePath, String content) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		writer.println(content);
		writer.close();	
	}

}
