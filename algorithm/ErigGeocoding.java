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
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
 
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

import choucas.erig.io.data.GenericJSONDataBinding;

@Algorithm(version = "1.0.0", title = "Erig/Perdido", abstrakt = "Implementation of the Erig Web Service Geocoding")
public class ErigGeocoding extends AbstractAnnotatedAlgorithm {
	
	//private static final Logger log = LoggerFactory.getLogger(ErigGeocoding.class);	
	
    private String content=null, api_key="choucas", lang="French", geocoding="true", POStagger="treetagger", version="Standard", 
    		mode="s", gazetier="bdnyme_ign", bbox=null;

    private JSONObject complexOutput;

    @LiteralDataInput(
    		identifier = "api_key", 
    		abstrakt = "Your API key",
    		defaultValue = "choucas",
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
    		identifier = "content",
    		abstrakt = "Textual content to annotate",
    		minOccurs = 1, 
    		maxOccurs = 1
    		)
    public void setLiteralContent(String literalInput) {
        this.content = literalInput;
    }
    
    @LiteralDataInput(
    		identifier = "geocoding", 
    		abstrakt = "Enable spatial classification",
    		defaultValue = "true",
    		allowedValues = {"true", "false"}, 
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setLiteralGeocoding(String literalInput) {
        this.geocoding = literalInput;
    }
 
    @LiteralDataInput(
    		identifier = "POStagger", 
    		abstrakt = "Postagging tool : treetagger (default), talismane (only for French), nominal for no pos-processing ",
    		allowedValues = {"treetagger", "talismane", "nominal"},
    		defaultValue = "treetagger",
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setLiteralPOStagger(String literalInput) {
        this.POStagger = literalInput;
    }
    
    @LiteralDataInput(
    		identifier = "version", 
    		abstrakt = "Determines the cascade of processing",
    		allowedValues = {"Standard", "Encyclopedie"},
    		defaultValue = "Standard",
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setLiteralVersion(String literalInput) {
        this.version = literalInput;
    }
    
    @LiteralDataInput(
    		identifier = "mode", 
    		abstrakt = "Method of querying gazeteers, s = strict name, a = alternate name",
    		allowedValues = {"s", "a"},
    		defaultValue = "s",
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setLiteralMode(String literalInput) {
        this.mode = literalInput;
    }
    
    @LiteralDataInput(
    		identifier = "gazetier",
    		abstrakt = "Gazeteer to use for classification, you can choice more than once separated by +", 
    		allowedValues = {"bdnyme_ign", "geonames", "osm", "wikipedia"},
    		defaultValue = "bdnyme_ign",
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setLiteralGazetier(String literalInput) {
        this.gazetier = literalInput;
    }
    
    @LiteralDataInput(
    		identifier = "bbox",
    		abstrakt = "A bounding box to restrict queries on gazeteers : lat1;lng1;lat2;lng2 (order to be checked)",
    		minOccurs = 0, 
    		maxOccurs = 1)
    public void setLiteralBbox(String literalInput) {
        this.bbox = literalInput;
    }    
    
  @ComplexDataOutput(identifier = "complexOutput", binding = GenericJSONDataBinding.class)
  public JSONObject getComplexOutput() {
      return complexOutput;
  }
  
  @Execute
  public void run() {
	  String url_base =  "http://erig.univ-pau.fr";
	  JSONObject response = null;
	  String separator = "****************";
	  
	  //log.debug("Running erig process");
	  
	  if (content != null) {
		  
		  try {
			  System.out.println("\n" + separator);               
			  System.out.println("Calling Geocoding..\nInput :\n" + content);
			  response = callGEO(url_base, api_key, lang, content, geocoding, POStagger, version, mode, gazetier, bbox);
			  System.out.println("\nREST Service Invoked Successfully...\nOutput :\n");
			  complexOutput = response;
			  System.out.println(complexOutput.toString());
			  
		  } catch (Exception e) {
			  System.out.println("\nError while calling REST Service");
			  e.printStackTrace();
			  }
		  
	  }
    else {
    	
    	System.out.println("No content");
    	//log.debug("No content");
    	
    }

    //log.debug("Finished erig process, complex output is : {}", complexOutput);
}
    
    
    // Expanded named entity recognition and classification service
//    protected static JsonObject callGEO(String api_url, String api_key, String lang, String content, String geocoding, String POStagger, String version, String mode, String gazetier, String bbox) throws JsonSyntaxException, JSONException, IOException
//    {	String response;
//    	JsonObject respJson;
//    	Gson respGson = new Gson();  
//    
//    	//api_url += "/PERDIDO/api/geocoding/";
//
////    	String request = "{\"api_key\":\""+api_key
////    			+"\",\"content\":\""+content
////    			+"\",\"lang\":\""+lang
////    			+"\",\"geocoding\":\""+geocoding
////    			+"\",\"POStagger\":\""+POStagger
////    			+"\",\"version\":\""+version
////    			+"\",\"mode\":\""+mode
////    			+"\",\"gazetier\":\""+gazetier
////    			+"\",\"bbox\":\""+bbox+"\"}";
//    	
//    	if (POStagger != "nominal") {
//    		api_url += "/PERDIDO/api/toponyms/txt_json/";
//    		}
//    	else {
//    		api_url += "/PERDIDO/api/toponyms/ner_json/";
//    		}
//    	
//    	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\""+lang+"\"}";
//    	System.out.println(request);
//    	response = callServiceREST(api_url, request);
//    	System.out.println(response);
//    	respJson = respGson.fromJson(response, JsonObject.class);
//    	
//    	return respJson; 	
//    }
//    
    protected static JSONObject callGEO(String url_base, String api_key, String lang, String content, String geocoding, String POStagger, String version, String mode, String gazetier, String bbox) throws JsonSyntaxException, JSONException, IOException
    {	String response;
    	Gson respGson = new Gson();
    	String api_url;
    
    	//api_url += "/PERDIDO/api/geocoding/";

//    	String request = "{\"api_key\":\""+api_key
//    			+"\",\"content\":\""+content
//    			+"\",\"lang\":\""+lang
//    			+"\",\"geocoding\":\""+geocoding
//    			+"\",\"POStagger\":\""+POStagger
//    			+"\",\"version\":\""+version
//    			+"\",\"mode\":\""+mode
//    			+"\",\"gazetier\":\""+gazetier
//    			+"\",\"bbox\":\""+bbox+"\"}";
    	
    	if (POStagger != "nominal") {
    		api_url = url_base + "/PERDIDO/api/toponyms/txt_json/";
    		}
    	else {
    		api_url = url_base + "/PERDIDO/api/toponyms/ner_json/";
    		content = URLEncoder.encode(content, "UTF-8");
    		}
    	
    	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\""+lang+"\"}";
    	System.out.println(request);
    	response = callServiceREST(api_url, request);
    	System.out.println(response);
    	//respJson = respGson.fromJson(response, JsonObject.class);
    	
		// converting to GeoJson format
    	api_url = url_base + "/PERDIDO/api/toponyms/json_gps/";	  
    	JSONArray respJson = new JSONArray(response);
		String outputFormat = "GeoJson";
    	String getURL = "false";
		request = "{\"api_key\":\""+api_key+"\",\"content\":"+respJson+",\"outputFormat\":\""+outputFormat+"\",\"getURL\":\""+getURL+"\"}";
		
		response = callServiceREST(api_url, request);
		
    	return new JSONObject(response); 	
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

    	InputStreamReader input = new InputStreamReader(connection.getInputStream());
    	BufferedReader in = new BufferedReader(input);

    	String line = null;
    	while ((line = in.readLine()) != null) {
    		response += line;
    	}

    	in.close();

    	return response;
    }
}
