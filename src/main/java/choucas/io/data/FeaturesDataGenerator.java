/**
 * Package choucas.io.data
 * Provides classes and methods to handle data for WPS processes (services)
 * Data bindings are the internal representation of WPS in- and outputs
 * Data bindings are wrapping data objects used in computation 
 * They are returned by parsers (inputs) or provided by generators(outputs)  
 * For more details, see https://wiki.52north.org/Geoprocessing/CreateNewDataBinding
 */

package choucas.io.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;

/**
 * 
 * This Class is a generator for an Object Complex Data. 
 * 
 */

public class FeaturesDataGenerator extends AbstractGenerator {

	public FeaturesDataGenerator(){
		super();
		supportedIDataTypes.add(FeaturesDataBinding.class);
	}
	
	@Override
	public boolean isSupportedSchema(String schema) {
		//no schema checks
		return true;
	}
	
	@Override
	public InputStream generateStream(IData data, String mimeType, String schema)
			throws IOException {
		
		InputStream in = null;
		
		System.out.println(data instanceof FeaturesDataBinding);
		if(data instanceof FeaturesDataBinding){
			
			Object objData = ((FeaturesDataBinding)data).getPayload();
			in = new ByteArrayInputStream(objData.toString().getBytes());
			
		}
		
		return in;
	}

}
