/**
 * Package choucas.utils
 * Provides configuration and utility classes and methods 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class provides utility method for input/output processings
 *
 * @author Eric Gouardères
 * @date August 2021
 */

public class IoUtils {
	
	public static void writeFile(String filePath, String content) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		writer.print(content);
		writer.close();	
	}
	
	public static String readFile(String filePath) throws IOException {
	
    	String response="";
		InputStreamReader input = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
    	BufferedReader in = new BufferedReader(input);
    	int c;
    	while ((c = in.read()) != -1) {
    		response += (char)c;
    	}
    	in.close();

    	return(response);
	}
	
	public static String readDataString(String data ) throws IOException {
		
    	String response="";
		InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(data.getBytes()), "UTF-8");
    	BufferedReader in = new BufferedReader(input);
    	int c;
    	while ((c = in.read()) != -1) {
    		response += (char)c;
    	}
    	in.close();

    	return(response);
	}
	
	public static void print(String content, String regex) {	
		// split the string according to one char regex (separated by ; in the regex string) and print the resulting lines
		// as the split method remove the char from the resulting string, we add it before printing 
		String[] regexChar = regex.split(";");
		String[] contentLines = null;
		String contentForm = content;
		for (int i=0;i<regexChar.length;i++) {
			contentLines = contentForm.split(regexChar[i]);
			contentForm = "";
			for (int j=0;j<contentLines.length;j++)
				contentForm+= (contentLines[j]+regexChar[i]+"\n");
		}
		System.out.println(contentForm);
	}
	
	public static void prettyPrintJson(String jsonString) {
		int tab = 2;
		if (jsonString.charAt(0) == '{') {
			System.out.println(new JSONObject(jsonString).toString(tab));
		}
		if (jsonString.charAt(0) == '[') {
			System.out.println(new JSONArray(jsonString).toString(tab));
		}
		
	}
	
	public static String stringCleaning(String content) {
		return content.replaceAll("[\\n\\r]", "").replaceAll(">\\s*<","><");
	}

}
