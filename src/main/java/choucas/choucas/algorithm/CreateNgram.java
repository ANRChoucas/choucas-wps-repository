/**
 * Package choucas.choucas
 * Provides WPS processes (services) and tools to access services hosted on choucas.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://choucas.univ-pau.fr/docs
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.choucas.algorithm;

import java.io.File;
import java.io.IOException;

import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.algorithm.annotation.LiteralDataOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import choucas.utils.IoUtils;
import choucas.utils.WsUtils;


/**
 * This WPS process is an adapter for create_ngram service.
 *
 * @author Eric Gouardères
 * @date September 2021
 */

@Algorithm(	
		version = "1.0.0", 
		title = "Choucas create_ngram Service Adapter", 
		abstrakt = "Create ngram for a neural network processing. "
				+ "Provides JSON formatted text. "
				+ "Documentation and API : http://choucas.univ-pau.fr/api/create_ngram/."
				)

public class CreateNgram extends AbstractAnnotatedAlgorithm {
	
	// private static final Logger log = LoggerFactory.getLogger(CreateNgram.class);	
	

    private String url=null, textInput=null, dataOutput=null;

    @LiteralDataInput(
    		identifier = "url", 
    		abstrakt = "Source url of the text",
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setDataUrl(String data) {
        this.url = data;
    }
    
	@LiteralDataInput(
			identifier = "textInput", 
			abstrakt = "A POS tagging formatted text", 
			minOccurs = 1, 
			maxOccurs = 1)
	public void setTextInput(String textInput) {
		this.textInput = textInput;
	}
    
       
    @LiteralDataOutput(identifier = "dataOutput")
    public String getDataOutput() {
        return dataOutput;
    }
	
    @Execute
    public void run() {
    	
        String url_base =  "http://choucas.univ-pau.fr";
        String response = "";
        String separator = "****************";
    	
        // log.debug("Running choucas process");
        
        if (textInput != null) {
		  
		  try {
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling create_ngram...");
			  response = callCreateNgram(url_base, url, textInput, "text/plain");
			  dataOutput = response;
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + textInput);
				  System.out.println("Output :\n");
				  IoUtils.prettyPrintJson(dataOutput);
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
        //log.debug("Finished Choucas process, output is : {}", dataOutput);
    }
    
    
    protected static String callCreateNgram(String api_url, String url, String textInput, String contentType) throws IOException {
    	api_url += "/api/create_ngram/";
    	String response = null;
    	File fileToLoad = File.createTempFile("ChoucasFileToLoad", "tsv");    
    	IoUtils.writeFile(fileToLoad.getPath(), textInput);
    	//fileToLoad = new File("C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\1_tag_Perdido.tsv");
    	response =  WsUtils.callServicePostM(api_url, url, fileToLoad, contentType);
    	//fileToLoad.delete();
    	return response;
    }
    
}
