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
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;

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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import choucas.utils.ChoucasConfig;
import choucas.utils.IoUtils;
import choucas.utils.WsUtils;
import choucas.utils.XmlUtils;

/**
 * This WPS process is an implementation of an Erig Web Service chain .
 *
 * @author Eric Gouardères
 * @date August 2021
 */


@Algorithm(version = "1.0.0", title = "Erig Chain Txt to Json process", abstrakt = "Extraction of spatial features and spatial relationships from text and toponyms location "
		+ "Provides a GeoJson document."
		+ "Documentation : see  http://erig.univ-pau.fr/PERDIDO/api.jsp for documentation on compound services : POS processing, GetToponyms and GeoJson."
		+ "Documentation of Geoparsing service not yet available."
		+ "Documentation of Geoconding service not yet available."		
		+ "API of Geoparsing service : http://erig.univ-pau.fr/PERDIDO/api/geoparsing/"
		+ "API of Geocoding service : http://erig.univ-pau.fr/PERDIDO/api/geocoding/")
public class ErigChainTxtJson2 extends AbstractAnnotatedAlgorithm {

	// private static final Logger log =
	// LoggerFactory.getLogger(ErigChainTxtJson2.class);

	private String textInput = "", api_key = "demo", lang = "French";

	private XmlObject taggedText;
	private String topoList, topoUrl, textUrl;

	@LiteralDataInput(identifier = "api_key", abstrakt = "Your API key", defaultValue = "demo", minOccurs = 0, maxOccurs = 1)
	public void setLiteralKey(String literalInput) {
		this.api_key = literalInput;
	}

	@LiteralDataInput(identifier = "lang", abstrakt = "Content language", defaultValue = "French", allowedValues = {
			"French", "Spanish", "English" }, minOccurs = 0, maxOccurs = 1)
	public void setLiteralLang(String literalInput) {
		this.lang = literalInput;
	}

	@LiteralDataInput(identifier = "textInput", abstrakt = "Textual content to annotate", minOccurs = 1, maxOccurs = 1)
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

	@LiteralDataOutput(identifier = "topoList", abstrakt = "List of toponyms found in the input text with their geo-location, using GeoJson standard output format")
	public String getTopoList() {
		return topoList;
	}

	@LiteralDataOutput(identifier = "topoUrl", abstrakt = "Url of the topoList file")
	public String getTopoUrl() {
		return topoUrl;
	}

	@Execute
	public void run() {

		String url_base = "http://erig.univ-pau.fr";
		String text = "Je visite les Pyrénées au sud de Toulouse.";
		String input = "";
		String output = "";
		String separator = "****************";

		// begin application settings
		ChoucasConfig.setup();
		String hostUrl = ChoucasConfig.getHostUrl();
		String tempPath = ChoucasConfig.getTempPath();
		String tempDir = ChoucasConfig.getTempDir();
		String timeFile; // The time a file is saved

		System.out.println("HostUrl : " + hostUrl);
		System.out.println("TempPath : " + tempPath);		
		System.out.println("TempDir :" + tempDir);
		WsUtils.setStdoutFlag(true);
		// End application settings
		
		if (textInput != null) {

			try {
				input = textInput;
				input = IoUtils.readDataString(textInput);
				System.out.println("\n" + separator);
				System.out.println("Calling POS...");
				output = callPOSUnitex(url_base, api_key, lang, input);
				if (WsUtils.getStdoutFlag()) {
					System.out.println("\nInput :\n" + input);
					System.out.println("\nOutput :\n" + output);
				}
				System.out.println("\nService Invoked Successfully !");

				input = WsUtils.stringToUnitex(output);
				System.out.println("\n" + separator);
				System.out.println("Calling Geoparsing...");
				output = callGEOparsing(url_base, api_key, lang, input, "false", "nominal", "Standard", "s", "bdnyme_ign", null);
				if (WsUtils.getStdoutFlag()) {
					System.out.println("\nInput :\n" + input);
					System.out.println("\nOutput :\n" + output);
				}
				System.out.println("\nService Invoked Successfully !");

				timeFile = java.time.LocalDateTime.now().toString().replace(":", "-");
				IoUtils.writeFile(tempPath + tempDir + "taggedText" + timeFile + ".xml", output);
				taggedText = Factory.parse(output);
				textUrl = hostUrl + tempDir + "taggedText" + timeFile + ".xml";

				System.out.println("\n" + separator);
				System.out.println("Calling Geocoding...");
				output = callGEOcoding(url_base, api_key, lang, input, "true", "nominal", "Standard", "s", "bdnyme_ign", null);
				if (WsUtils.getStdoutFlag()) {
					System.out.println("\nInput :\n" + input);
					System.out.println("\nOutput :\n" + output);
				}
				System.out.println("\nService Invoked Successfully !");

//				input = IoUtils.stringCleaning(output);
//				System.out.println("\n" + separator);
//				System.out.println("Calling TOP...");
//				output = callTOP(url_base, api_key, lang, input);
//				output = output.toString();
//				if (WsUtils.getStdoutFlag()) {
//					System.out.println("\nInput :\n" + input);
//					System.out.println("\nOutput :\n");
//					IoUtils.prettyPrintJson(output);
//				}
//				System.out.println("\nService Invoked Successfully !");
//
//				input = output;
//				System.out.println("\n" + separator);
//				System.out.println("Calling GEO...");
//				output = callGEO(url_base, api_key, new JSONArray(input));
//				if (WsUtils.getStdoutFlag()) {
//					System.out.println("\nInput :\n");
//					IoUtils.prettyPrintJson(input);
//					System.out.println("\nOutput :\n");
//					IoUtils.prettyPrintJson(output);
//				}
//				System.out.println("\nService Invoked Successfully !");
//
				// Response from CallGEOcoding is a String, so returned using literalOutput.
				// Despite it represents structured data.
				timeFile = java.time.LocalDateTime.now().toString().replace(":", "-");
				IoUtils.writeFile(tempPath + tempDir + "topoList" + timeFile + ".json", output);
				topoList = output;
				topoUrl = hostUrl + tempDir + "topoList" + timeFile + ".json";

			} catch (Exception e) {
				System.out.println("\nError while calling REST Service");
				e.printStackTrace();
			}

		} else {
			System.out.println("No literal input");
			// log.debug("No literal input");
		}
		System.out.println("\n" + separator);
		// log.debug("Finished erig process, literal output is : {}", topoList);
		// log.debug("Finished erig process, complex output is : {}", taggedText);
	}

	// POS processing service, unitex output
	protected static String callPOSUnitex(String api_url, String api_key, String lang, String content)
			throws JSONException, IOException {
		api_url += "/PERDIDO/api/pos/txt_unitex/";

		String request = "{\"api_key\":\"" + api_key + "\",\"content\":\"" + content + "\",\"lang\":\"" + lang + "\"}";

		return WsUtils.callServicePost(api_url, request);

	}

	// Expanded named entity recognition service
	protected static String callNER(String api_url, String api_key, String lang, String content)
			throws JSONException, IOException {
		api_url += "/PERDIDO/api/ner/pos_xml/";

		String request = "{\"api_key\":\"" + api_key + "\",\"content\":\"" + content + "\",\"lang\":\"" + lang + "\"}";

		return WsUtils.callServicePost(api_url, request);
	}

	// GetToponyms service
	protected static String callTOP(String api_url, String api_key, String lang, String content)
			throws JSONException, IOException {
		api_url += "/PERDIDO/api/toponyms/ner_json/";

		// String contentTest="<?xml version=\"1.0\" encoding=\"UTF-8\"?><TEI
		// xmlns=\"http://www.tei-c.org/ns/1.0\"><teiHeader><fileDesc><titleStmt><title><!--
		// title of the resource --></title></titleStmt><publicationStmt><p><!--
		// Information about distribution of the resource
		// --></p></publicationStmt><sourceDesc><p><!-- Information about source from
		// which the resource derives
		// --></p></sourceDesc></fileDesc></teiHeader><text><body><p><s><rs><name><w
		// lemma=\"pau\" type=\"NPr\">Pau</w></name></rs><pc
		// force=\"strong\">.</pc></s><s/></p></body></text></TEI>";

		content = URLEncoder.encode(content, "UTF-8");

		String request = "{\"api_key\":\"" + api_key + "\",\"content\":\"" + content + "\",\"lang\":\"" + lang + "\"}";

		return WsUtils.callServicePost(api_url, request);
	}

	// GeoJson, KML or GPX service.
	// Returns a String containing a list of toponyms with their geo-location
	// structured using standard output format (GeoJSON, KML, GPX).
	// The expected output format is specified as a parameter value.
	protected static String callGEO(String api_url, String api_key, JSONArray content)
			throws JSONException, IOException {
		api_url += "/PERDIDO/api/toponyms/json_gps/";

		String outputFormat = "GeoJson";
		String getURL = "false";

		String request = "{\"api_key\":\"" + api_key + "\",\"content\":" + content + ",\"outputFormat\":\""
				+ outputFormat + "\",\"getURL\":\"" + getURL + "\"}";

		return WsUtils.callServicePost(api_url, request);

	}
	
    // Expanded named entity recognition and classification service
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
    
    protected static String callGEOcoding(String api_url, String api_key, String lang, String content, String geocoding, String POStagger, String version, String mode, String gazetier, String bbox) throws JSONException, IOException
    { 
    	api_url += "/PERDIDO/api/geocoding/";
    	
    	String request = "{\"api_key\":\""+api_key
  			  +"\",\"content\":\""+content
  			  +"\",\"lang\":\""+lang
  			  +"\",\"geocoding\":\""+geocoding
  			  +"\",\"POStagger\":\""+POStagger
  			  +"\",\"version\":\""+version
  			  +"\",\"mode\":\""+mode
  			  +"\",\"gazetier\":\""+gazetier
  			  +"\",\"bbox\":\""+bbox+"\"}";
  		
    	return (WsUtils.callServicePost(api_url, request)); 	
    } 

}
