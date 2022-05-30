/**
 * Package choucas.utils
 * Provides configuration and utility classes and methods 
 * Project : LMAP/IPRA/CHOUCAS, 2017-2022
 */

package choucas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class provides utility method for Web Services
 *
 * @author Eric Gouardères
 * @date August 2021
 */


public class WsUtils {
	protected static boolean stdoutFlag = false;
	
	public static void setStdoutFlag(boolean flag) {
		stdoutFlag = flag;
	}
	
	public static boolean getStdoutFlag() {
		return stdoutFlag;
	}
	
    public static String callServicePost(String api_url, String request) throws IOException, JSONException {
    	String response = "";

    	JSONObject jsonObject = new JSONObject("{\"request\":"+request+"}");

    	URL url = new URL(api_url);
    	URLConnection connection = url.openConnection();
    	connection.setDoOutput(true); // Indicates POST method
    	connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
    	OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
    	out.write(jsonObject.toString());
    	out.close();

    	InputStreamReader input = new InputStreamReader(connection.getInputStream(), "UTF-8");
    	BufferedReader in = new BufferedReader(input);
    	String line = null;
    	while ((line = in.readLine()) != null) {
    		response += line;
    	}

    	in.close();

    	return response;
    }
    
    /**
     * POST request using multipart/form-data content type
     * source : https://square.github.io/okhttp/recipes/
     */
    
	 public static String callServicePostM(String api_url, String url, File fileToLoad, String contentType) {
	
		 String fileName = fileToLoad.getName();
		 MediaType MEDIA_TYPE = MediaType.parse(contentType);	 

		 String respBody=null;
		 
		 OkHttpClient client = new OkHttpClient();
		 
		 RequestBody requestBody = new MultipartBody.Builder()
				 .setType(MultipartBody.FORM)
				 .addFormDataPart("Fichier_recu", fileName,  
						 RequestBody.create(fileToLoad,MEDIA_TYPE))
				  .addFormDataPart("url", url)
				  .build();
		 
		Request request = new Request.Builder()
				  .url(api_url)
				  .method("POST", requestBody)
				  .build();
		try {
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			
			respBody = response.body().string();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return respBody;
	 }
	 
	 public static String callServicePostUseM(String api_url, String url, File vectorFile, File mpFile, File textFile, String contentType) {
			
		 String vFileName = vectorFile.getName();
		 String mFileName = mpFile.getName();
		 String tFileName = textFile.getName();
		 
		 MediaType MEDIA_TYPE = MediaType.parse(contentType);	 

		 String respBody=null;
		 
		 //OkHttpClient client = new OkHttpClient();
		 OkHttpClient client = new OkHttpClient.Builder()
				 .readTimeout(60, TimeUnit.SECONDS)
				 .build();
		 
		 RequestBody requestBody = new MultipartBody.Builder()
				 .setType(MultipartBody.FORM)
				 .addFormDataPart("Fichier_recu", vFileName,  
						 RequestBody.create(vectorFile,MEDIA_TYPE))
				 .addFormDataPart("Fichier_mp", mFileName,  
						 RequestBody.create(mpFile,MEDIA_TYPE))
				 .addFormDataPart("Fichier_text", tFileName,  
						 RequestBody.create(textFile,MEDIA_TYPE))
				  .addFormDataPart("url", url)
				  .build();
		 
		Request request = new Request.Builder()
				  .url(api_url)
				  .method("POST", requestBody)
				  .build();
		try {
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			
			respBody = response.body().string();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return respBody;
	 }
	 
		public static String jsonToPos(JSONArray data) {
			String stringData="";
			int dataLength = data.length();
			
			for (int i=0;i<dataLength;i++) {
				for (int j=0;j<3;j++) {
					stringData+=data.getJSONArray(i).get(j).toString();
					if (j < 2)
						stringData+="\t";
					}
				if (i < dataLength - 1)
					stringData+="\n";
				}
			return(stringData);
			
		}
		
		public static String jsonToNgram(JSONArray data) {
			String stringData="";
			int dataLength = data.length();
			
			for (int i=0;i<dataLength;i++) {
				stringData+=data.getJSONArray(i).toString();
				if (i < dataLength - 1)
					stringData+="\n";
				}
			return(stringData.replaceAll(",", ", "));
			
		}
		
		public static String jsonToVector(JSONArray data) {
			String stringData="[";
			int dataLength = data.length();
			
			for (int i=0;i<dataLength;i++) {
				stringData+=data.getJSONArray(i).toString();
				if (i < dataLength - 1)
					stringData+="\n";
				}
			stringData+="]";
			return(stringData.replaceAll(",", " "));
			
		}
		
		public static String jsonToText(JSONArray data) {
			String stringData="";
			int dataLength = data.length();
			
			for (int i=0;i<dataLength - 1;i++) {
				stringData+=data.getString(i)+"],[";
			}
			stringData+=data.getString(dataLength - 1);
			return(stringData);
			
		}
		
		public static String jsonToUnitex(String data) {
			String stringData = JSONObject.quote(data);
			return(stringData.substring(1, stringData.length()-1));
			
		}
		
		public static String stringToUnitex(String data) {
			return data.replace("\\,", "\\\\,");
			
		}
}
