/**
 * Package choucas.erig
 * Provides WPS processes (services) and tools to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.erig.algorithm;

import java.io.IOException;
import org.json.JSONException;
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

import choucas.utils.WsUtils;

/**
 * This WPS process is an adapter for Erig Nerc Service.
 *
 * @author Eric Gouardères
 * @date August 2021
 */

@Algorithm(	
		version = "1.0.0", 
		title = "Erig Nerc Service Adapter", 
		abstrakt = "Extraction of spatial features and spatial relationships from text. "
				+ "Provides an XML document. "
				+ "API : http://erig.univ-pau.fr/PERDIDO/api/nerc/txt_xml/"
				+ "Documentation : http://erig.univ-pau.fr/PERDIDO/api.jsp#esne. "
				)

public class ErigNerc extends AbstractAnnotatedAlgorithm {
	
	// private static final Logger log = LoggerFactory.getLogger(ErigNerc.class);	
	
    private String content=null, api_key="choucas", lang="French";

    private XmlObject complexOutput;

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
    
    @ComplexDataOutput(identifier = "complexOutput", binding = GenericXMLDataBinding.class)
    public XmlObject getComplexOutput() {
        return complexOutput;
    }
	
    @Execute
    public void run() {
    	
        String url_base =  "http://erig.univ-pau.fr";
        String response = "";
        String separator = "****************";
    	
        // log.debug("Running erig process");
        
        if (content != null) {
		  
		  try {
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling Nerc...");
			  response = callNERC(url_base, api_key, lang, content);
			  complexOutput = Factory.parse(response);
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + content);
				  System.out.println("\nOutput :\n"+ complexOutput);
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
    
    
    // Expanded named entity recognition and classification service
    protected static String callNERC(String api_url, String api_key, String lang, String content) throws JSONException, IOException
    {
    		api_url += "/PERDIDO/api/nerc/txt_xml/";
    		
        	String request = "{\"api_key\":\""+api_key+"\",\"content\":\""+content+"\",\"lang\":\""+lang+"\"}";
   		
    		return WsUtils.callServicePost(api_url, request);
    }
    
}
