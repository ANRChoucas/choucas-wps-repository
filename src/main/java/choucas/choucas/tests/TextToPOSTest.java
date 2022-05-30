/**
 * Package choucas.choucas
 * Provides WPS processes (services) and tools to access services hosted on choucas.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://choucas.univ-pau.fr/docs
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.choucas.tests;

import java.io.File;
import java.io.IOException;

import choucas.choucas.algorithm.TextToPOS;
import choucas.utils.IoUtils;
import choucas.utils.WsUtils;

/**
 * Test application for Choucas text_to_POS adapter.
 *
 * @author Eric Gouardères
 * @date September 2021
 */

public class TextToPOSTest 
{
    public static void main( String[] args )
    {
    	TextToPOS service = new TextToPOS();

    	String url = "";
    	String textFilePath = "C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\test_nettoye.txt";
        String textInput=null;
        
        try {
			textInput = IoUtils.readFile(textFilePath);
	        service.setDataUrl(url);
	        service.setTextInput(textInput);
	        
	        System.out.println( "--------------------------------" );
	        System.out.println( "Test : Calling Choucas Text_to_POS Service" );
	        WsUtils.setStdoutFlag(false);
	        service.run();
	        System.out.println( "\n--------------------------------------");
	        System.out.println( "Test: Result of Choucas Text_to_POS Service\n");
	        IoUtils.prettyPrintJson(service.getDataOutput());  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println( "Test: Error in calling Choucas Text_to_POS Service\n");
			e.printStackTrace();
		}
            
        
    }
}
