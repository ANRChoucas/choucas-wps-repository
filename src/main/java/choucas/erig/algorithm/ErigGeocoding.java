/**
 * Package choucas.erig
 * Provides WPS processes (services) and tools to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.erig.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlObject.Factory;

import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.ComplexDataOutput;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.datahandler.generator.GenericXMLDataGenerator;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import choucas.io.data.GenericJSONDataBinding;
import choucas.utils.WsUtils;

/**
 * This WPS process is an adapter for Erig Geoparsing Service.
 *
 * @author Eric Gouardères
 * @date August 2021
 */

@Algorithm(	
		version = "1.0.0", 
		title = "Erig Geocoding Service Adapter", 
		abstrakt = "Extraction of spatial features from text. Returns features localisation."
				+ "Provides a Geojson document. "
				+ "API : http://erig.univ-pau.fr/PERDIDO/api/geocoding/"
				+ "Documentation : not available yet. "
				)

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
	  JSONObject response;
	  String separator = "****************";
	  
	  //log.debug("Running erig process");
  
      if (content != null) {
		  
		  try {
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling Geocoding...");
			  response = callGEO(url_base, api_key, lang, content, geocoding, POStagger, version, mode, gazetier, bbox);
			  complexOutput = response;
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + content);
				  System.out.println("\nOutput :\n"+ complexOutput.toString());
			  }
			  System.out.println("\nAdapter : Service Invoked Successfully !");

		  } catch (Exception e) {
			  System.out.println("\n Adapter: Error while calling REST Service");
			  e.printStackTrace();
			  }
		  }
      else {
      	System.out.println("No content");
      	//log.debug("No content");
      	}
      System.out.println("\n" + separator);  
      //log.debug("Finished erig process, complex output is : {}", complexOutput);
  }
  
  protected static JSONObject callGEO(String api_url, String api_key, String lang, String content, String geocoding, String POStagger, String version, String mode, String gazetier, String bbox) throws JSONException, IOException
  {
	  String response;
	  
	  String request = "{\"api_key\":\""+api_key
			  +"\",\"content\":\""+content
			  +"\",\"lang\":\""+lang
			  +"\",\"geocoding\":\""+geocoding
			  +"\",\"POStagger\":\""+POStagger
			  +"\",\"version\":\""+version
			  +"\",\"mode\":\""+mode
			  +"\",\"gazetier\":\""+gazetier
			  +"\",\"bbox\":\""+bbox+"\"}";
	  
	  api_url += "/PERDIDO/api/geocoding/";

  	response = WsUtils.callServicePost(api_url, request);
		
  	return (new JSONObject(response)); 	
  } 
	
}
