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

import org.json.JSONObject;
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
 * This WPS process is an adapter for text to Ngram Web Service chain.
 *
 * @author Eric Gouardères
 * @date September 2021
 */

@Algorithm(version = "1.0.0", 
		title = "Text to vector Web Service chain process", 
		abstrakt = "Create vectors for a neural network input.. "
				+ "Provides JSON formatted text. "
				+ "Documentation : see  http://choucas.univ-pau.fr/docs for documentation on compound services : text_to_POS processing, create_ngram, create_vector."
				)

public class TextToVectorChain extends AbstractAnnotatedAlgorithm {
	
	// private static final Logger log = LoggerFactory.getLogger(TextToVector.class);	
	

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
			abstrakt = "Textual content to annotate", 
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
        String input=null, output=null;
        File inputFile=null, posFile=null, ngramFile=null;
    	
        // log.debug("Running choucas process");
        
        if (textInput != null) {
		  
		  try {
			  input = textInput;
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling text_to_POS...");
			  inputFile = File.createTempFile("inputFile", ".txt");
			  IoUtils.writeFile(inputFile.getPath(), input);
			  output = calltextPOS(url_base, url, inputFile, "text/plain");
			  dataOutput = output;
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n");
				  IoUtils.prettyPrintJson(dataOutput);
			  }
			  System.out.println("\nAdapter : Service Invoked Successfully !");
			  
			  input = WsUtils.jsonToPos(new JSONObject(output).getJSONArray("texte_sortie"));
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling create_ngram...");
			  posFile = File.createTempFile("posFile", ".tsv");
			  IoUtils.writeFile(posFile.getPath(), input);
			  output = callCreateNgram(url_base, url, posFile, "text/plain");
			  dataOutput = output;
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n");
				  IoUtils.prettyPrintJson(dataOutput);
			  }
			  System.out.println("\nAdapter : Service Invoked Successfully !");

			  input = WsUtils.jsonToNgram(new JSONObject(output).getJSONArray("texte_sortie_2"));
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling create_vector...");
			  ngramFile = File.createTempFile("ngramFile", ".tsv");
			  IoUtils.writeFile(ngramFile.getPath(), input);
			  //ngramFile = new File("C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\test_a_classer_MP.tsv");
			  output = callCreateVector(url_base, url, ngramFile, "text/plain");
			  dataOutput = output;
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n");
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
        //log.debug("Finished Choucas process, output is : {}", outputTSV);
    }
    
    
    // text_to_POS service call
    protected static String calltextPOS(String api_url, String url, File fileToLoad, String contentType) throws IOException {
    	api_url += "/api/text_to_POS/";
    	String response = null;
    	response =  WsUtils.callServicePostM(api_url, url, fileToLoad, contentType);
    	return response;
    }
    
    // cretae_ngram service call
    protected static String callCreateNgram(String api_url, String url, File fileToLoad, String contentType) throws IOException {
    	api_url += "/api/create_ngram/";
    	String response = null;
    	response =  WsUtils.callServicePostM(api_url, url, fileToLoad, contentType);
    	return response;
    }
    
    // cretae_vector service call
    protected static String callCreateVector(String api_url, String url, File fileToLoad, String contentType) throws IOException {
    	api_url += "/api/create_vector/";
    	String response = null;
    	response =  WsUtils.callServicePostM(api_url, url, fileToLoad, contentType);
    	return response;
    }
    
}
