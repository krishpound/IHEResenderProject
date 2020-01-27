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

public class IHESortAndMerge {

	private static ArrayList<IHEKeyObject> iheCombinedKeyList = new ArrayList<IHEKeyObject>();
	private static String fpattern = "yyyyMMddhhmm";
	private static String jpattern = "yyyy/MM/dd hh:mm:ss";
	private static SimpleDateFormat fileDatePattern = new SimpleDateFormat(fpattern);
	private static SimpleDateFormat jobDatePattern = new SimpleDateFormat(jpattern);
	//private static String devUrl = "http://dev-healthix:57772/csp/healthix/util/iheKeys";
	
	private static String stageAG4Url = "http://192.168.130.20:57772/csp/healthix/util/iheKeys";
	private static String stageAG5Url = "http://192.168.130.21:57772/csp/healthix/util/iheKeys";
	
	
	private static String instance1 = "AGIHE";
	private static String instance2 = "AGPUSH";
	private static String outputPath = "c:/home/en928/";
	private static String outputFile1 = outputPath + instance1 + "_sorted_IHE_keys.txt";
	private static String outputFile2 = outputPath + instance2 + "_sorted_IHE_keys.txt";
	private static String outputFile3 = outputPath + "MERGED_SORTED_IHE_KEYS.txt";
	private static String logFile = outputPath + "/logs/" + "IHESortAndMerge_log_";
	private static BufferedWriter fileWriter1,fileWriter2,fileWriter3,logWriter;
	private static int fw1Count=0;
	private static int fw2Count=0;
	private static int fw3Count=0;
	
    public static void main(String[] args) {
    	
    	try {
    	
    		String className = new Object() {}.getClass().getEnclosingClass().getName();
    		String fileDate = fileDatePattern.format(new Date());
    		logWriter = new BufferedWriter(new FileWriter(logFile + fileDate + ".txt"));
    		System.out.println(className + ": starting "+jobDatePattern.format(new Date()));
    		logWriter.write(className + ": starting "+jobDatePattern.format(new Date()));
    		fileWriter1 = new BufferedWriter(new FileWriter(outputFile1));
    		fileWriter2 = new BufferedWriter(new FileWriter(outputFile2));
    		fileWriter3 = new BufferedWriter(new FileWriter(outputFile3));
    		
    		//Instance 1 Fetch Processing
    		System.out.println("\n"+className+": Fetching IHE record keys on "+instance1 + " " + jobDatePattern.format(new Date()));
    		logWriter.write("\n"+ className+": Fetching IHE record keys on " + instance1 + " " + jobDatePattern.format(new Date()) +"\n");
    		JSONArray iheKeysArray1 = new JSONArray();
    		iheKeysArray1 = ApiHandler.Call(stageAG4Url,"Cache",className);
    		for(int i=0;i<iheKeysArray1.size();i++) {  
    			JSONObject iheObj1 = (JSONObject) iheKeysArray1.get(i);
    			IHEKeyObject kObj1 = new IHEKeyObject(iheObj1.get("instance").toString(),iheObj1.get("messageID").toString(),iheObj1.get("name").toString(),iheObj1.get("timeCreatedMs").toString(),iheObj1.get("status").toString());
    			fileWriter1.write(iheObj1.get("instance") + "\t" + iheObj1.get("messageID") + "\t" + iheObj1.get("name") + "\t" + iheObj1.get("timeCreatedMs") + "\t" + iheObj1.get("status") + "\n");
    			iheCombinedKeyList.add(kObj1);
    			fw1Count++;
    		}
    		
    		//Instance 2 Fetch Processing
    		System.out.println("\n"+className+": Fetching IHE record keys on "+instance2 + " " + jobDatePattern.format(new Date()));
    		logWriter.write("\n"+ className+": Fetching IHE record keys on " + instance2 + " " + jobDatePattern.format(new Date()) +"\n");
    		JSONArray iheKeysArray2 = new JSONArray();
    		iheKeysArray2 = ApiHandler.Call(stageAG5Url,"Cache",className);
    		for(int j=0;j<iheKeysArray2.size();j++) {  
    			JSONObject iheObj2 = (JSONObject) iheKeysArray2.get(j);
    			IHEKeyObject kObj2 = new IHEKeyObject(iheObj2.get("instance").toString(),iheObj2.get("messageID").toString(),iheObj2.get("name").toString(),iheObj2.get("timeCreatedMs").toString(),iheObj2.get("status").toString());
    			fileWriter2.write(iheObj2.get("instance") + "\t" + iheObj2.get("messageID") + "\t" + iheObj2.get("name") + "\t" + iheObj2.get("timeCreatedMs") + "\t" + iheObj2.get("status") + "\n");
    			iheCombinedKeyList.add(kObj2);
    			fw2Count++;
    		}
    	
    		System.out.println(className + ": merging and sorting the IHE record keys");
    		Collections.sort(iheCombinedKeyList, new IHEKeyObject());
    		for (IHEKeyObject key : iheCombinedKeyList) {
    			fileWriter3.write(key.getInstance() + "\t" + key.getMessageid() + "\t" + key.getName() + "\t" + key.getTimestamp() + "\t" + key.getStatus() + "\n");
    			fw3Count++;
    		}
    			
    		System.out.println("\noutput file: "+ outputFile1);
    		System.out.println("output file: "+ outputFile2);
    		System.out.println("output file: "+ outputFile3);
    		System.out.println("\nEXECUTION SUMMARY");
    		System.out.println("==============================================================");
    		System.out.println("Total " + instance1 + " IHE keys written to file   : " + fw1Count);
    		System.out.println("Total " + instance2 + " IHE keys written to file   : " + fw2Count);
    		System.out.println("Total Combined IHE keys written to file : " + fw3Count);
    		System.out.println("==============================================================");
    		System.out.println("\n"+className + ": completed "+jobDatePattern.format(new Date()));
    		
    		logWriter.write("\noutput file: " + outputFile1 + "\n");
    		logWriter.write("output file: " + outputFile2 + "\n");
    		logWriter.write("output file: " + outputFile3 + "\n");
    		logWriter.write("\nEXECUTION SUMMARY\n");
    		logWriter.write("==============================================================\n");
    		logWriter.write("Total " + instance1 + " IHE keys written to file   : " + fw1Count +"\n");
    		logWriter.write("Total " + instance2 + " IHE keys written to file   : " + fw2Count +"\n");
    		logWriter.write("Total Combined IHE keys written to file : " + fw3Count +"\n");
    		logWriter.write("==============================================================\n");
    		logWriter.write("\n"+className + ": completed "+jobDatePattern.format(new Date())+"\n");
    		
    		fileWriter1.close();
    		fileWriter2.close();
    		fileWriter3.close();
    		logWriter.close();
    		
    	
    	}
    	catch(IOException ioEx) {
    		ioEx.printStackTrace();
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    } 
}