package choucas.sources.algorithm;

/**
 * Package choucas.sources.algorithm
 * Provides custom WPS processes to access services providing data for Choucas project
 * see : http://choucas.ign.fr/
 * 
 * @author Eric Gouardères
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.algorithm.annotation.LiteralDataOutput;
import org.n52.wps.algorithm.annotation.ComplexDataOutput;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import choucas.io.data.FeaturesDataBinding;
/**
 * 
 * Process to access Refuges.info API
 *    example: https://www.refuges.info/api/bbox?type_points=refuge
 *    documentation: https://www.refuges.info/api/doc/#/api/bbox
 * 	  see also :  https://github.com/ANRChoucas/MetadataRandoApiChoucas 
 * 
 * @author Eric Gouardères
 */

@Algorithm(title = "API refuges.info par BBOX",
	abstrakt = "This service returns different types of POI in mountain areas for planning sport activities from refuge.info API",
	version = "1.0.0")
public class RefugesInfoAlgorithm extends AbstractAnnotatedAlgorithm  {
	
	private static final Logger log = LoggerFactory.getLogger(RefugesInfoAlgorithm.class);	
	
	
	// ------------------------------------------------------------------------
	//     INPUT
	
	@LiteralDataInput(identifier = "type_points",
            abstrakt = "Les types de point à exporter, parmi la liste suivante : cabane, refuge, gite, pt_eau, sommet, pt_passage, bivouac et lac. La valeur all sélectionne tous les types.",
            title = "Les types de point",
//            minOccurs = 0,
//            maxOccurs = 1,
            defaultValue="all")
    public void setLiteralType(TypeEnum literalInput) {
        this.type_points = literalInput;
        }	
    
	
	private TypeEnum type_points;
	
	public static enum TypeEnum {
		all,
		cabane,
		refuge,
		gite,
		pt_eau,
		sommet,
		pt_passage
	}
	
	@LiteralDataInput(identifier = "bbox",
			title = "Les dimensions de la bbox à exporter",
			abstrakt = "Les dimensions de la bbox à exporter dans la projection 4326: ouest, sud, est, nord. Il est possible de sélectionner la planète entière via la valeur world.",
			minOccurs = 1,
		    maxOccurs = 1,
			defaultValue="-13.00,44.05,17.00,49.79"		)
    public void setLiteralBbox(String literalInput) {
        this.bbox = literalInput;
        }	
	private String bbox;
	
	
	@LiteralDataInput(identifier = "format",
            abstrakt = "Le format de l'export:geojson, kml, gml, gpx, csv, xml. C'est le format geojson par défaut.",
            title = "Le format de l'export",
            minOccurs = 0,
            maxOccurs = 1,
            defaultValue = "geojson")
    public void setLiteralFormat(FormatEnum literalInput) {
        this.format = literalInput;
        }	
	private FormatEnum format;
	
	public static enum FormatEnum {
		geojson,
		kml,
		gml,
		gpx,
		csv,
		xml
	}
	
	
	
	
	
	
	// ------------------------------------------------------------------------
	//      EXECUTE
	@Execute
	public void execute() {
    	
        String url_base =  "https://www.refuges.info/api/bbox";
        String default_bbox = "-13.00,44.05,17.00,49.79";
        FormatEnum default_format = FormatEnum.geojson;
        TypeEnum default_points = TypeEnum.all;
        String response = "";
        String separator = "****************";

    	
        log.debug("Running refuge Info process");
        
        if (type_points == null) {
        	type_points = default_points;
        }
        
        if (bbox == null) {
        	bbox = default_bbox;
        }

        if (format != null) {
        	format = default_format;
        }
        
        try {
        	System.out.println("\n" + separator);               
        	System.out.println("Calling Refuge Info...\nInput :\n");
                output = callRefInf(url_base, type_points, bbox, format);
                System.out.println("\nREST Service Invoked Successfully...\nOutput :\n");
                
                //output = Factory.parse(response);
                //System.out.println(output);
                
                
            } catch (Exception e) {
                System.out.println("\nError while calling REST Service");
                e.printStackTrace();
            }

 //       log.debug("Finished erig process, literal output is : {}", literalOutput);
        log.debug("Finished process, complex output is : {}", output);
    }

	protected static JsonObject callRefInf(String api_url, TypeEnum type_points, String bbox, FormatEnum format) throws IOException,  JsonSyntaxException {
		
    	String request = "{\"type_points\":\""+type_points+"\",\"bbox\":\""+bbox+"\",\"format\":\""+format+"\"}";

    	String response;
    	JsonObject respJson;
    	Gson respGson = new Gson(); 
    	
    	//String payload = "bbox="+bbox+"&type_points="+type_points+"&format="+format;
    	String payload = "?bbox="+bbox+"&type_points="+type_points+"&nb_points=2";
    	//api_url = "https://www.refuges.info/api/bbox?bbox=5.5,45.1,6.5,45.6&type_points=sommet&nb_points=2";
    	response = callServiceREST(api_url, payload);
    	respJson = respGson.fromJson(response, JsonObject.class);
    	
    	return respJson;
	}
	
	protected static String callServiceREST(String api_url, String payload) throws IOException
    {
    	String response = "";
    		 		
    	URL url = new URL(api_url+payload);
		URLConnection connection = url.openConnection();
		
		InputStreamReader input = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
		BufferedReader in = new BufferedReader(input);
		
		String line = null;
			 
		while ((line = in.readLine()) != null) {
			response+=line;
		}
			 
		in.close();
			 
		return response;
    }
	
	
	
	// ------------------------------------------------------------------------
	// 		OUTPUT
	
	@LiteralDataOutput(identifier = "timestamp",
            title = "Timestamp de la requête",
            abstrakt = "Timestamp de la requête")
	private String timestamp;
	
	
	@ComplexDataOutput(identifier = "output",
             abstrakt = "Liste des poi contenus dans une bbox",
             title = "Liste des poi",
             binding = FeaturesDataBinding.class)
	private Object output;
	public Object getOutput() {
		return output;
	}

	
}


