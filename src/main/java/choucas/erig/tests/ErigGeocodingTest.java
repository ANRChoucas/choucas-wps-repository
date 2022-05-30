/**
 * Package choucas.erig
 * Provides WPS processes (services) and tools to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.erig.tests;

import choucas.erig.algorithm.ErigGeocoding;
import choucas.utils.IoUtils;

/**
 * Test application for Erig Geocoding adapter.
 *
 * @author Eric Gouardères
 * @date August 2021
 */

public class ErigGeocodingTest 
{
    public static void main( String[] args )
    {
    	ErigGeocoding service = new ErigGeocoding();
    	String text = "On est passé au col de la Mine de Fer. On descend vers le Lac de Crop.";
        //String text = "Je passe à Grenoble puis je pars en direction de Bourg-d'Oisans.";
        
        service.setLiteralContent(text);
        
        System.out.println( "-------------------------------------" );
        System.out.println( "Test : Calling Erig Geocoding Service");
        service.run();
        System.out.println("\n---------------------------------------" );
        System.out.println("\nTest: Result of Erig Geocoding Service");
        IoUtils.prettyPrintJson(service.getComplexOutput().toString());
        
    }
}
