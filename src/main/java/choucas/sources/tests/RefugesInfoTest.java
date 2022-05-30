package choucas.sources.tests;

import java.io.IOException;

import choucas.sources.algorithm.RefugesInfoAlgorithm;
import choucas.sources.algorithm.RefugesInfoAlgorithm.FormatEnum;
import choucas.sources.algorithm.RefugesInfoAlgorithm.TypeEnum;

public class RefugesInfoTest {
    public static void main( String[] args )
    {
        System.out.println( "Calling Refuge Info Class!" );
        RefugesInfoAlgorithm serviceReFinF = new RefugesInfoAlgorithm();
        serviceReFinF.setLiteralType(TypeEnum.refuge);
        serviceReFinF.setLiteralBbox("-13.00,44.05,17.00,49.79");
        serviceReFinF.setLiteralFormat(FormatEnum.geojson);        
        serviceReFinF.execute();
        System.out.println("\nRefugesInfoTest: Result of Refuge Info Services\n" + serviceReFinF.getOutput());
    }

}
