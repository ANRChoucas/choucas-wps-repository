/**
 * Package choucas.erig
 * Provides WPS processes (services) and tools to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.erig.tests;

import choucas.erig.algorithm.ErigGeoparsing;

/**
 * Test application for Erig Geoparsing adapter.
 *
 * @author Eric Gouardères
 * @date August 2021
 */

public class ErigGeoparsingTest 
{
    public static void main( String[] args )
    {
    	ErigGeoparsing service = new ErigGeoparsing();

        //String text = "On est passé au col de la Mine de Fer. On descend vers le Lac de Crop.";
        String text = "Je passe à Grenoble puis je pars en direction de Bourg-d'Oisans.";
        service.setLiteralContent(text);
        
        System.out.println( "---------------------------------------------------------------" );
        System.out.println( "Test : Calling Erig Geoparsing Service (without classification)" );
        service.setLiteralGeocoding("false");
        service.run();
        System.out.println("\n--------------------------------------------------------------" );
        System.out.println("Test: Result of Erig Geoparsing Service (without classification)\n\n" + service.getComplexOutput());
        
        System.out.println("----------------------------------------------------------" );
        System.out.println("Test : Calling Erig Geoparsing Service with classification" );
        service.setLiteralGeocoding("true");
        service.run();
        System.out.println("\n----------------------------------------------------------------------------------------" );
        System.out.println("Test: Result of Erig Geoparsing Service Calling Erig Geoparsing Service with classification\n\n" + service.getComplexOutput());
        
        
    }
}
