


/**
 * Package choucas.io.data
 * Provides classes and methods to handle data for WPS processes (services)
 * Data bindings are the internal representation of WPS in- and outputs
 * Data bindings are wrapping data objects used in computation 
 * They are returned by parsers (inputs) or provided by generators(outputs)  
 * For more details, see https://wiki.52north.org/Geoprocessing/CreateNewDataBinding
 */
package choucas.io.data;

import org.n52.wps.io.data.IComplexData;

/**
 * 
 * This class holds an Object as payload. 
 *
 */

public class FeaturesDataBinding implements IComplexData {

	private static final long serialVersionUID = 8586931812896959156L;
    
	private final Object objet;

    public FeaturesDataBinding(Object o) {
        this.objet = o;
    }

    @Override
    public Object getPayload() {
        return this.objet;
    }

    @Override
    public Class<?> getSupportedClass() {
        return Object.class;
    }
	@Override
	public void dispose() {}
}
