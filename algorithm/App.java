package choucas.erig.algorithm;

import com.google.gson.GsonBuilder;

/**
 * Package algorithm.erig
 * Provides WPS processes (services) to access services hosted on erig.univ-pau.fr HTTP server
 * WPS processes (services) are hosted on a 52°North WPS server
 * See http://erig.univ-pau.fr/PERDIDO/api.jsp
 * See https://52north.org/software/software-projects/wps/ 
 * Author : Eric Gouardères
 * Project : LMAP/IPRA/CHOUCAS, november 2020
 */



/**
 * Test application 
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Calling Erig Services using ErigNew Class!" );
        ErigNerc serviceNerc = new ErigNerc();
        ErigNer serviceNer = new ErigNer();
        ErigNew erigChain = new ErigNew();
        ErigGeoparsing serviceGeoNer = new ErigGeoparsing();
        ErigGeocoding serviceGeoCod = new ErigGeocoding();
        //String text = "On est passé au col de la Mine de Fer. On descend vers le Lac de Crop.";
        String text = "Je passe à Grenoble puis je pars en direction de Bourg-d'Oisans.";
        
//        System.out.println( "App : Calling Erig Ner Services\n" );
//        serviceNer.setLiteralKey("choucas");
//        serviceNer.setLiteralLang("French");
//        serviceNer.setLiteralContent(text);
//        serviceNer.run();
//        System.out.println("\nApp: Result of Erig Ner Services\n" + serviceNer.getComplexOutput());
        
//        System.out.println( "App : Calling Erig Geoparsing Service (without classification)\n" );
//        serviceGeoNer.setLiteralContent(text);
//        serviceGeoNer.setLiteralGeocoding("false");
//        serviceGeoNer.run();
//        System.out.println("\nApp: Result of Erig Geoparsing Service (without classification)\n" + serviceGeoNer.getComplexOutput());
        
//        System.out.println( "App : Calling Erig Geocoding Service\n" );
//        serviceGeoCod.setLiteralContent(text);
//        serviceGeoCod.setLiteralGeocoding("false");
//        serviceGeoCod.run();
//        String PrettyOutput = new GsonBuilder().setPrettyPrinting().create().toJson(serviceGeoCod.getComplexOutput());
//        System.out.println("\nApp: Result of Erig Geocoding Service\n" + PrettyOutput);
        
        System.out.println( "App : Calling Erig Nerc Services\n" );
        serviceNerc.setLiteralKey("choucas");
        serviceNerc.setLiteralLang("French");
        serviceNerc.setLiteralContent(text);
        serviceNerc.run();
        System.out.println("\nApp: Result of Erig Nerc Services\n" + serviceNerc.getComplexOutput());
//        
//        System.out.println( "Calling Erig Chain!" );
//        erigChain.setTextInput(text);
//        erigChain.run();
//        System.out.println("\nApp: Result of Erig Chain, toponyms list\n" + erigChain.getTopoList());
//        System.out.println("\nApp: Result of Erig Chain, toponyms Url\n" + erigChain.getTopoUrl());
//        System.out.println("\nApp: Result of Erig Chain, tagged Text\n" + erigChain.getTaggedText().toString());
//        System.out.println("\nApp: Result of Erig Chain, tagged Text Url \n" + erigChain.getTextUrl());
    }
}
