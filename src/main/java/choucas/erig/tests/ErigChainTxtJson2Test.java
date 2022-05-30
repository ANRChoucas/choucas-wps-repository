/**
 * Package choucas.erig
 * Provides WPS processes (services) and tools to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.erig.tests;

import choucas.erig.algorithm.ErigChainTxtJson2;
import choucas.utils.IoUtils;

/**
 * Test application for Erig Chain process.
 *
 * @author Eric Gouardères
 * @date August 2021
 */

public class ErigChainTxtJson2Test 
{
    public static void main( String[] args )
    {
    	ErigChainTxtJson2 service = new ErigChainTxtJson2();

        //String text = "On est passé au col de la Mine de Fer. On descend vers le Lac de Crop.";
    	String text = "Je vais à  Bourg-d'Oisans, en passant près de Grenoble. Puis je pars en direction du col de Bellefont.";

        service.setTextInput(text);
        
        System.out.println( "--------------------------------" );
        System.out.println( "Test : Calling Erig Chain Txt to GeoJson Service" );
        service.run();
        System.out.println( "\n--------------------------------------");
        System.out.println("Test: Result of Erig Chain, toponyms list\n");
        IoUtils.prettyPrintJson(service.getTopoList());
        System.out.println( "\n--------------------------------------");
        System.out.println("Test: Result of Erig Chain, toponyms Url\n\n" + service.getTopoUrl());
        System.out.println( "\n--------------------------------------");
        System.out.println("Test: Result of Erig Chain, tagged Text\n\n" + service.getTaggedText().toString());
        System.out.println( "\n--------------------------------------");
        System.out.println("Test: Result of Erig Chain, tagged Text Url\n\n" + service.getTextUrl());
        
    }
}
