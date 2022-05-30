/**
 * Package choucas.utils
 * Provides configuration and utility classes and methods 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * This class provides configuration settings
 * TODO : should be revised to ensure genericity
 * @author Eric Gouardères
 * @date August 2021
 */

public class ChoucasConfig {
	
	public static int SERVER_MODE = 0; // distant host settings
	public static int LOCAL_MODE = 1; // local host settings
	
	protected static String hostUrl = "http://choucas.univ-pau.fr:8080/";
	protected static String tomcatBase = System.getProperty("catalina.base");
	protected static String tempPath = tomcatBase + "/webapps/ROOT/";
	protected static String tempDir = "temp/";
	
	
	public static String getHostUrl() {
		return hostUrl;
	}
	
	public static String getTempPath() {
		return tempPath;
	}
	
	public static String getTempDir() {
		return tempDir;
	}
	
	public static void setMode(int mode) {
		if (mode == SERVER_MODE) {
			hostUrl = "http://choucas.univ-pau.fr:8080/";
			tempPath = "/opt/tomcat/webapps/ROOT/";
		}
		
		if (mode == LOCAL_MODE) {
			hostUrl = "http://localhost:8080/";
			//tempPath = "C:\\Program Files\\Apache Software Foundation\\Tomcat9.0\\webapps\\ROOT\\";
			tempPath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\";
		}
		
		File d = new File(tempPath + tempDir);
		if(!d.exists())
		{
			new File(tempPath + tempDir).mkdir();

		}
	}	
	
	public static void setup() {
		// TODO revoir comment déterminer le mode sans bricoler... 
		
		if ((tomcatBase == null) || (tomcatBase.charAt(1) == ':')) {
			setMode(LOCAL_MODE);
			System.out.println("Local Mode");
			}
		else {
			setMode(SERVER_MODE);
			System.out.println("Server Mode");
			}
	
	}

}
