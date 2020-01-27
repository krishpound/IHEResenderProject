package com.healthix.nyc;

/********************************************
Chris. 20200126. EN-928.

These jobs must be run in this sequence:
	1. IHEQuery
	2. IHESortAndMerge
	3. IHEResender
*********************************************/

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;  
import java.io.FileNotFoundException;  
import java.util.Scanner; 

public class IHEResender {

	private static ArrayList<IHEKeyObject> iheCombinedKeyList = new ArrayList<IHEKeyObject>();
	private static String fpattern = "yyyyMMddhhmm";
	private static String jpattern = "yyyy/MM/dd hh:mm:ss";
	private static SimpleDateFormat fileDatePattern = new SimpleDateFormat(fpattern);
	private static SimpleDateFormat jobDatePattern = new SimpleDateFormat(jpattern);
	private static String path = "c:/home/en928/";
	private static String logFile = path + "/logs/" + "IHEResender_log_";
	private static BufferedWriter logWriter;
	private static ArrayList<String> resendList = new ArrayList<String>();
	private static int instance1ResendCount=0;
	private static int instance1DuplicateCount=0;
	private static int instance2ResendCount=0;
	private static int instance2DuplicateCount=0;
	private static int totalResendCount=0;
	private static int badMessageCount=0;
	
	
	/*
	 *  VERY IMPORTANT:  THE FILE BELOW CONTAINS THE ENS.MESSAGEHEADER KEYS THAT YOU WILL BE RESENDING
	 *  PLEASE CONFIGURE THE PROPERTIES BELOW VERY CAREFULLY FOR PRODUCTION!!!!
	 */
	private static String ResendFile = path + "MERGED_SORTED_IHE_KEYS.txt";
	private static String instance1Url = "http://dev-healthix:57772/csp/healthix/util/iheResender";
	private static String instance2Url = "http://dev2-healthix:57772/csp/healthix/util/iheResender";
	private static String resendTarget = "BHIX.IHE.XDSb.Repository.AutoReplace";
	
    public static void main(String[] args) {
    	
    	try {
    	
    		String className = new Object() {}.getClass().getEnclosingClass().getName();
    		String fileDate = fileDatePattern.format(new Date());
    		logWriter = new BufferedWriter(new FileWriter(logFile + fileDate + ".txt"));
    		System.out.println(className + ": starting "+jobDatePattern.format(new Date()));
    		logWriter.write(className + ": starting "+jobDatePattern.format(new Date()));
    		resendList.clear();
    		String resendToInstance=null;
    		String resendMessageID=null;
    		String resendUrl=null;
    		String queryString=null;
    		
    		 File resendKeysFile = new File(ResendFile);
    		 Scanner scanner = new Scanner(resendKeysFile);
    	      while (scanner.hasNextLine()) {
    	        String data = scanner.nextLine();
    	        String parms[] = data.split("\t");
    	        
    	        if(parms[4].equalsIgnoreCase("OK")) {
    	        	resendToInstance = parms[0];
    	        	resendMessageID = parms[1];
    	        	if(resendToInstance.equalsIgnoreCase("HXDEV")) resendUrl = instance1Url;
    	        	if(resendToInstance.equalsIgnoreCase("HXDEV2")) resendUrl = instance2Url;
    	        	queryString = "?MessageID="+resendMessageID+"&ResendTarget="+resendTarget;
    	        	if (resendList.contains(resendToInstance+resendMessageID)){
    	        		System.out.println(resendToInstance + ": Skipping messageID="+resendMessageID+" because it is a duplicate");
    	        		System.out.println(resendToInstance + ": Skipping messageID="+resendMessageID+" because it is a duplicate\n");
    	        		if(resendToInstance.equalsIgnoreCase("HXDEV")) instance1DuplicateCount++;
    	        		if(resendToInstance.equalsIgnoreCase("HXDEV2")) instance2DuplicateCount++;
    	        	}
    	        	else {
    	        		
    	        		JSONArray iheResendArray = new JSONArray();
    	        		iheResendArray = ApiHandler.Call(resendUrl+queryString,"Cache",className);
    	        		for(int i=0;i<iheResendArray.size();i++) {  
    	        			JSONObject rObj = (JSONObject) iheResendArray.get(i);
    	        			System.out.println(resendToInstance + ": Resend for " + rObj.get("MessageID") + " to Target: " + rObj.get("ResendTarget") + " Status = " + rObj.get("Status"));
    	        			logWriter.write(resendToInstance + ": Resend for " + rObj.get("MessageID") + " to Target: " + rObj.get("ResendTarget") + " Status = " + rObj.get("Status") + "\n");
    	        		}
    	        		if(resendToInstance.equalsIgnoreCase("HXDEV")) instance1ResendCount++;
    	        		if(resendToInstance.equalsIgnoreCase("HXDEV2")) instance2ResendCount++;
    	        		totalResendCount++;
    	        		resendList.add(resendToInstance+resendMessageID);
    	        		Thread.sleep(1000);
    	        	}
    	        }
    	        else {
    	        	badMessageCount++;
    	        	System.out.println("BAD MESSAGE: " + data);
    	        	logWriter.write("BAD MESSAGE: " + data);
    	        }
    	      }
    	     
     		System.out.println("\nEXECUTION SUMMARY");
     		System.out.println("==============================================================");
     		System.out.println("Total IHE Messages Replayed to Instance1: " + instance1ResendCount);
     		System.out.println("Total Duplicate IHE Messages Suppressed for Instance1: " + instance1DuplicateCount);
     		System.out.println("Total IHE Messages Replayed to Instance2: " + instance2ResendCount);
     		System.out.println("Total Duplicate IHE Messages Suppressed for Instance2: " + instance2DuplicateCount);
     		System.out.println("Total Bad Message Count: "+badMessageCount);
     		System.out.println("Total IHE Messages Replayed to Both Servers: " + totalResendCount);
     		System.out.println("==============================================================");
     		System.out.println("\n"+className + ": completed "+jobDatePattern.format(new Date()));
     		
     		logWriter.write("\nEXECUTION SUMMARY\n");
     		logWriter.write("==============================================================\n");
     		logWriter.write("Total IHE Messages Replayed to Instance1: " + instance1ResendCount+"\n");
     		logWriter.write("Total Duplicate IHE Messages Suppressed for Instance1: " + instance1DuplicateCount+"\n");
     		logWriter.write("Total IHE Messages Replayed to Instance2: " + instance2ResendCount+"\n");
     		logWriter.write("Total Duplicate IHE Messages Suppressed for Instance2: " + instance2DuplicateCount+"\n");
     		logWriter.write("Total Bad Message Count: "+badMessageCount+"\n");
     		logWriter.write("Total IHE Messages Replayed to Both Servers: " + totalResendCount+"\n");
     		logWriter.write("==============================================================\n");
     		logWriter.write("\n"+className + ": completed "+jobDatePattern.format(new Date())+"\n");
     		
     		scanner.close();
    		logWriter.close();
     		
    	}
    	catch(FileNotFoundException fnfe) {
    		fnfe.printStackTrace();
    	}
    	catch(IOException ioEx) {
    		ioEx.printStackTrace();
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    } 
}