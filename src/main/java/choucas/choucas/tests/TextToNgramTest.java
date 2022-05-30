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

import choucas.choucas.algorithm.TextToNgramChain;
import choucas.utils.IoUtils;
import choucas.utils.WsUtils;

/**
 * Test application for Choucas text to Ngram adapter.
 *
 * @author Eric Gouardères
 * @date September 2021
 */

public class TextToNgramTest 
{
    public static void main( String[] args )
    {
    	TextToNgramChain service = new TextToNgramChain();

    	String url = "";
    	String textFilePath = "C:\\Users\\gouarder\\Nuage\\Stage-CMI-BD\\Lot2\\Data\\temp\\2_nettoye.txt";
        String textInput=null;
        
        try {
			textInput = IoUtils.readFile(textFilePath);
	        service.setDataUrl(url);
	        service.setTextInput(textInput);
	        
	        System.out.println( "--------------------------------" );
	        System.out.println( "Test : Calling Choucas text to Ngram service chain" );
	        WsUtils.setStdoutFlag(false);
	        service.run();
	        System.out.println( "\n--------------------------------------");
	        System.out.println( "Test: Result of Choucas text to Ngram service chain\n");
	        IoUtils.prettyPrintJson(service.getDataOutput());  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println( "Test: Error in calling Choucas text to Ngram service chain\n");
			e.printStackTrace();
		}
            
        
    }
}
