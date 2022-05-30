/**
 * Package choucas.erig
 * Provides WPS processes (services) and tools to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.choucas.tests;

import java.io.IOException;

import choucas.choucas.algorithm.TextToGeoJsonChain;
import choucas.utils.IoUtils;
import choucas.utils.WsUtils;

/**
 * Test application for Choucas text to geoparsing service chain adapter.
 *
 * @author Eric Gouardères
 * @date September 2021
 */

public class TextToGeoJsonTest 
{
    public static void main( String[] args )
    {
    	TextToGeoJsonChain service = new TextToGeoJsonChain();

    	String url = "";
    	String textFilePath = "C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\2-2_nettoye.txt";
        String textInput=null;
        
        try {
			textInput = IoUtils.readFile(textFilePath);
	        service.setDataUrl(url);
	        service.setTextInput(textInput);
	        
	        System.out.println( "--------------------------------" );
	        System.out.println( "Test : Calling Choucas text to geojson service chain" );
	        WsUtils.setStdoutFlag(false);
	        service.run();
	        System.out.println( "\n--------------------------------------");
	        System.out.println("Test: Result of Choucas Chain, toponyms list\n");
	        IoUtils.prettyPrintJson(service.getTopoList());
	        System.out.println( "\n--------------------------------------");
	        System.out.println("Test: Result of Choucas Chain, toponyms Url\n\n" + service.getTopoUrl());
	        System.out.println( "\n--------------------------------------");
	        System.out.println("Test: Result of Choucas Chain, tagged Text\n\n" + service.getTaggedText().toString());
	        System.out.println( "\n--------------------------------------");
	        System.out.println("Test: Result of Choucas Chain, tagged Text Url\n\n" + service.getTextUrl());
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println( "Test: Error in calling Choucas text to geojson service chain\n");
			e.printStackTrace();
		}
        
    }
}
