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

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlObject.Factory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.ComplexDataOutput;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.algorithm.annotation.LiteralDataOutput;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import choucas.utils.ChoucasConfig;
import choucas.utils.IoUtils;
import choucas.utils.WsUtils;


/**
 * This WPS process is an adapter for text to geoparsing Web Service chain.
 *
 * @author Eric Gouardères
 * @date September 2021
 */

@Algorithm(version = "1.0.0", 
		title = "Text to geoparsing Web Service chain process", 
		abstrakt = "Extraction of spatial features and spatial relationships from text."
				+ "Provides an XML document. "
				+ "Documentation : see  http://choucas.univ-pau.fr/docs for documentation on compound services : text_to_POS processing, create_ngram, create_vector and use_model."
				+ "Documentation of Geoparsing service not yet available."
				+ "API of Geoparsing service : http://erig.univ-pau.fr/PERDIDO/api/geoparsing/"
				)

public class TextToGeoparsingChain extends AbstractAnnotatedAlgorithm {
	
	// private static final Logger log = LoggerFactory.getLogger(TextToGeoparsingChain);	
	

    private String url=null, textInput=null;
	private String api_key = "demo", lang = "French";

	private XmlObject taggedText;
	private String textUrl;

    @LiteralDataInput(
    		identifier = "url", 
    		abstrakt = "Source url of the text",
    		minOccurs = 0, 
    		maxOccurs = 1
    		)
    public void setDataUrl(String data) {
        this.url = data;
    }
    
	@LiteralDataInput(identifier = "api_key", abstrakt = "Your API key", defaultValue = "demo", minOccurs = 0, maxOccurs = 1)
	public void setLiteralKey(String literalInput) {
		this.api_key = literalInput;
	}

	@LiteralDataInput(identifier = "lang", abstrakt = "Content language", defaultValue = "French", allowedValues = {
			"French", "Spanish", "English" }, minOccurs = 0, maxOccurs = 1)
	public void setLiteralLang(String literalInput) {
		this.lang = literalInput;
	}
    
	@LiteralDataInput(
			identifier = "textInput", 
			abstrakt = "Textual content to annotate", 
			minOccurs = 1, 
			maxOccurs = 1)
	public void setTextInput(String textInput) {
		this.textInput = textInput;
	}
    
       
	@ComplexDataOutput(identifier = "taggedText", abstrakt = "XML/TEI NER tagged text", binding = GenericXMLDataBinding.class)
	public XmlObject getTaggedText() {
		return taggedText;
	}

	@LiteralDataOutput(identifier = "textUrl", abstrakt = "Url of the tagged text file")
	public String getTextUrl() {
		return textUrl;
	}
	
    @Execute
    public void run() {
    	
        String url_base =  "http://choucas.univ-pau.fr";
        String response = "";
        String separator = "****************";
        String input=null, output=null;
        JSONObject outputJson = null;
		String mpFileContent = null;
		String textFileContent = null;
        File inputFile=null, posFile=null, mpFile=null, vectorFile=null, textFile=null, unitexFile=null;
    	
        // log.debug("Running choucas process");
        
		// begin application settings
		ChoucasConfig.setup();
		String hostUrl = ChoucasConfig.getHostUrl();
		String tempPath = ChoucasConfig.getTempPath();
		String tempDir = ChoucasConfig.getTempDir();
		String timeFile; // The time a file is saved

		System.out.println("HostUrl : " + hostUrl);
		System.out.println("TempPath : " + tempPath);		
		System.out.println("TempDir :" + tempDir);
		// End application settings
        
        if (textInput != null) {
		  
		  try {
			  // POS tagging
			  
			  input = textInput;
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling text_to_POS...");
			  inputFile = File.createTempFile("inputFile", ".txt");
			  IoUtils.writeFile(inputFile.getPath(), input);
			  output = calltextPOS(url_base, url, inputFile, "text/plain");
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n");
				  IoUtils.prettyPrintJson(output);
			  }
			  System.out.println("\nAdapter : Service Invoked Successfully !");
			  
			  // text_to_POS
			  input = WsUtils.jsonToPos(new JSONObject(output).getJSONArray("texte_sortie"));
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling create_ngram...");
			  posFile = File.createTempFile("posFile", ".tsv");
			  IoUtils.writeFile(posFile.getPath(), input);
			  output = callCreateNgram(url_base, url, posFile, "text/plain");
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n");
				  IoUtils.prettyPrintJson(output);
			  }
			  System.out.println("\nAdapter : Service Invoked Successfully !");
			  outputJson = new JSONObject(output);
			  mpFileContent = WsUtils.jsonToNgram(outputJson.getJSONArray("texte_sortie_2"));
			  textFileContent = WsUtils.jsonToText(outputJson.getJSONArray("texte_sortie_1"));
			  textFile = File.createTempFile("textFile", ".tsv");
			  IoUtils.writeFile(textFile.getPath(), textFileContent);
			  
			  // create_ngram
			  input = mpFileContent;
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling create_vector...");
			  mpFile = File.createTempFile("mpFile", ".tsv");
			  IoUtils.writeFile(mpFile.getPath(), input);
			  output = callCreateVector(url_base, url, mpFile, "text/plain");
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n");
				  IoUtils.prettyPrintJson(output);
			  }
			  System.out.println("\nAdapter : Service Invoked Successfully !");
			  outputJson = new JSONObject(output);
			  
			  // Use_model
			  input = WsUtils.jsonToVector(outputJson.getJSONArray("texte_sortie"));
			  System.out.println("\n" + separator);               
			  System.out.println("Adapter : Calling use_model...");
			  vectorFile = File.createTempFile("vectorFile", ".tsv");
			  IoUtils.writeFile(vectorFile.getPath(), input);
			  //vectorFile = new File ("C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\2_vecteurs.tsv");
			  //mpFile = new File ("C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\2_a_classer_MP.tsv");
			  //textFile = new File ("C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\2_text.tsv");
			  output = callUseModel(url_base, url, vectorFile, mpFile, textFile, "text/plain");
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n");
				  IoUtils.prettyPrintJson(output);
			  }
			  System.out.println("\nAdapter : Service Invoked Successfully !");
			  outputJson = new JSONObject(output);
			  
			  // Geoparsing
			  input = WsUtils.jsonToUnitex(outputJson.getString("texte_sortie"));
			  //input = IoUtils.readFile("C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\test_Unitex.txt");
			  unitexFile = File.createTempFile("unitexFile", ".txt");
			  IoUtils.writeFile(unitexFile.getPath(), input);
			  System.out.println("\n" + separator);
			  System.out.println("Calling Geoparsing...");
			  output = callGEOparsing("http://erig.univ-pau.fr", api_key, lang, input, "false", "nominal", "Standard", "s", "bdnyme_ign", null);
			  if (WsUtils.getStdoutFlag()) {
				  System.out.println("\nInput :\n" + input);
				  System.out.println("\nOutput :\n" );
				  System.out.println(Factory.parse(output).toString());
			  }
			  System.out.println("\nService Invoked Successfully !");

			  timeFile = java.time.LocalDateTime.now().toString().replace(":", "-");
			  IoUtils.writeFile(tempPath + tempDir + "taggedText" + timeFile + ".xml", output);
			  taggedText = Factory.parse(output);
			  textUrl = hostUrl + tempDir + "taggedText" + timeFile + ".xml";
			  
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
    
    // create_ngram service call
    protected static String callCreateNgram(String api_url, String url, File fileToLoad, String contentType) throws IOException {
    	api_url += "/api/create_ngram/";
    	String response = null;
    	response =  WsUtils.callServicePostM(api_url, url, fileToLoad, contentType);
    	return response;
    }
    
    // create_vector service call
    protected static String callCreateVector(String api_url, String url, File fileToLoad, String contentType) throws IOException {
    	api_url += "/api/create_vector/";
    	String response = null;
    	response =  WsUtils.callServicePostM(api_url, url, fileToLoad, contentType);
    	return response;
    }
    
    // use_model service call
    protected static String callUseModel(String api_url, String url, File vectorFile, File mpFile ,File textFile, String contentType) throws IOException {
    	api_url += "/api/use_model/";
    	String response = null;
    	response =  WsUtils.callServicePostUseM(api_url, url, vectorFile, mpFile, textFile, contentType);
    	return response;
    }
    
    // Geoparsing service call
    protected static String callGEOparsing(String api_url, String api_key, String lang, String content, String geocoding, String POStagger, String version, String mode, String gazetier, String bbox) throws JSONException, IOException
    {
    	api_url += "/PERDIDO/api/geoparsing/";

    	String request = "{\"api_key\":\""+api_key
    			+"\",\"content\":\""+content
    			+"\",\"lang\":\""+lang
    			+"\",\"geocoding\":\""+geocoding
    			+"\",\"POStagger\":\""+POStagger
    			+"\",\"version\":\""+version
    			+"\",\"mode\":\""+mode
    			+"\",\"gazetier\":\""+gazetier
    			+"\",\"bbox\":\""+bbox+"\"}";

    	//System.out.println(content);
    	//System.out.println(request);
    	return WsUtils.callServicePost(api_url, request);
    }
}
