package com.healthix.nyc;

import java.util.Base64;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
 
public class ApiHandler {
 
    public static JSONArray Call(String api, String type, String caller){
    	
    	String className = new Object() {}.getClass().getEnclosingClass().getName();
    	System.out.println(className + ": request received from " + caller);
    	JSONParser jsonParser = new JSONParser();
    	JSONArray jsonArray = new JSONArray();	 
    	JSONObject jObj;	
    	  
    	try {
    		ClientResponse resp=null;
    		String name = "CPound";
    		String password = "J9h4c81997!";
    		String authString = name + ":" + password;
    		String base64encodedString = Base64.getEncoder().encodeToString(authString.getBytes("utf-8"));
    		//System.out.println("Base64 Encoded String (Basic) :" + base64encodedString+"\n");
    		Client restClient = Client.create();
    		WebResource webResource = restClient.resource(api);
    		System.out.println(className + ": calling this api: "+api);
    		if(type.equalsIgnoreCase("Cache")) {resp = webResource.accept("application/json").get(ClientResponse.class);}
    		if(type.equalsIgnoreCase("LogicMonitor")) {resp = webResource.accept("application/json").header("Authorization", "Basic " + base64encodedString).get(ClientResponse.class);}
    		if(resp.getStatus() != 200){System.err.println("Unable to connect to the server "+resp.getStatus());}
    		System.out.println(className + ": response from server - "+resp.getStatus());
    		jObj = (JSONObject) jsonParser.parse(resp.getEntity(String.class));
    		if (api.contains("/iheKeys")) {
    			jsonArray = (JSONArray) jObj.get("IHEKeys");
    			System.out.println(className + ": api returned "+jsonArray.size()+" IHE record keys from instance "+jObj.get("Instance") + "\n");
    		}
    	}
    	catch(IOException ioe) {
    		System.out.println("IOException");
    		ioe.getMessage();
    	}
    	catch(ParseException pe) {
    		System.out.println("ParseException");
    		pe.printStackTrace();}
    	catch(Exception e) {
    		System.out.println("Exception");
    		e.printStackTrace();}
    	return jsonArray;
    }
}
