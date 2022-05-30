/**
 * Package choucas.io.data
 * Provides classes and methods to handle data for WPS processes (services)
 * Data bindings are the internal representation of WPS in- and outputs
 * Data bindings are wrapping data objects used in computation 
 * They are returned by parsers (inputs) or provided by generators(outputs)  
 * For more details, see https://wiki.52north.org/Geoprocessing/CreateNewDataBinding
 */

package choucas.io.data;

import org.json.JSONObject;
import org.n52.wps.io.data.IComplexData;

/**
 * 
 * This class holds a JsonObject as payload. 
 *
 */
public class GenericJSONDataBinding implements IComplexData {

	private static final long serialVersionUID = -2528480446631098248L;

	private JSONObject payload;
	
	public GenericJSONDataBinding(JSONObject payload){
		this.payload = payload;
	}
	
	@Override
	public JSONObject getPayload() {
		return payload;
	}
	@Override
	public Class<JSONObject> getSupportedClass() {
		return JSONObject.class;
	}
	@Override
	public void dispose() {}
}
