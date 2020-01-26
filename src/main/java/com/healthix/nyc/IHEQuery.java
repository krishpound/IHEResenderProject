package com.healthix.nyc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IHEQuery {

	private static ArrayList<IHEKeyObject> iheCombinedKeyList = new ArrayList<IHEKeyObject>();
	private static String fpattern = "yyyyMMddhhmm";
	private static String jpattern = "yyyy/MM/dd hh:mm:ss";
	private static SimpleDateFormat fileDatePattern = new SimpleDateFormat(fpattern);
	private static SimpleDateFormat jobDatePattern = new SimpleDateFormat(jpattern);
	private static String devUrl = "http://dev-healthix:57772/csp/healthix/util/iheKeys";
	private static String SourceConfigName = "HS.IHE.XDSb.Repository.Services";
	private static String TargetConfigName = "BHIX.IHE.XDSb.Repository.AutoReplace";
	private static String OID = "1.2.840.114350.1.13.382.2.7.3.688884.100";  //Brookdale University
	private static String StartDateTime = "201711010000";
	private static String EndDateTime = "201712010000";
	private static String queryString = "?SourceConfigName=" + SourceConfigName + "&TargetConfigName=" + TargetConfigName + "&OID=" + OID + "&StartDateTime=" + StartDateTime + "&EndDateTime=" + EndDateTime;
	private static String sqlQuery = "SELECT %nolock mh.ID, x.id from ens.messageheader mh join hs_message.xmlmessage x on (mh.messagebodyid = x.id) where mh.sourceconfigname = ? and mh.targetconfigname = ? and x.samldata_organizationoid = ? and mh.timecreated between ? and ? and mh.type = 'Request' order by mh.id asc";
	private static String instance1 = "HXDEV1";
	private static String instance2 = "HXDEV2";
	private static String outputPath = "c:/home/en928/";
	private static String outputFile1 = outputPath + instance1 + "_sorted_IHE_keys_";
	private static String outputFile2 = outputPath + instance2 + "_sorted_IHE_keys_";
	private static String outputFile3 = outputPath + "MERGED_SORTED_IHE_KEYS_";
	private static String logFile = outputPath + "/logs/" + "IHEQuery_log_";
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
    		fileWriter1 = new BufferedWriter(new FileWriter(outputFile1 + fileDate + ".txt"));
    		fileWriter2 = new BufferedWriter(new FileWriter(outputFile2 + fileDate + ".txt"));
    		fileWriter3 = new BufferedWriter(new FileWriter(outputFile3 + fileDate + ".txt"));
    		
    		logWriter.write("\n"+ className + ": Execution Parameters for fetching IHE record keys:\n");
    		logWriter.write("===============================================================================\n");
    		logWriter.write(sqlQuery+"\n");
    		logWriter.write("SourceConfigName : " + SourceConfigName+"\n");
    		logWriter.write("TargetConfigName : " + TargetConfigName+"\n");
    		logWriter.write("OID              : " + OID+"\n");
    		logWriter.write("StartDateTime    : " + StartDateTime+"\n");
    		logWriter.write("EndDateTime      : " + EndDateTime+"\n");
    		logWriter.write("Type             : Request"+"\n");
    		logWriter.write("===============================================================================\n");
    		
    		System.out.println("\n" + className + ": Execution Parameters for fetching IHE record keys:");
    		System.out.println("===============================================================================");
    		System.out.println(sqlQuery);
    		System.out.println("SourceConfigName : " + SourceConfigName);
    		System.out.println("TargetConfigName : " + TargetConfigName);
    		System.out.println("OID              : " + OID);
    		System.out.println("StartDateTime    : " + StartDateTime);
    		System.out.println("EndDateTime      : " + EndDateTime);
    		System.out.println("Type             : Request");
    		System.out.println("===============================================================================");
    		System.out.println("\n"+ className + ": Executing query on "+instance1 +jobDatePattern.format(new Date()));
    		
    		//Instance 1 Query Processing
    		System.out.println("\n"+className+": Executing query on "+instance1 + " " + jobDatePattern.format(new Date()));
    		logWriter.write("\n"+ className+": Executing query on " + instance1 + " " + jobDatePattern.format(new Date()) +"\n");
    		JSONArray iheKeysArray1 = new JSONArray();
    		iheKeysArray1 = ApiHandler.Call(devUrl+queryString,"Cache",className);
    		for(int i=0;i<iheKeysArray1.size();i++) {  
    			JSONObject iheObj = (JSONObject) iheKeysArray1.get(i);
    			IHEKeyObject kObj = new IHEKeyObject(instance1,iheObj.get("MessageID").toString(),iheObj.get("Name").toString(),iheObj.get("TimeCreatedMs").toString(),TargetConfigName);
    			fileWriter1.write(instance1 + "\t" + iheObj.get("MessageID") + "\t" + iheObj.get("Name") + "\t" + iheObj.get("TimeCreatedMs") + "\n");
    			iheCombinedKeyList.add(kObj);
    			fw1Count++;
    		}
    		
    		//Instance 2 Query Processing
    		System.out.println("\n"+className+": Executing query on "+instance2 + " " + jobDatePattern.format(new Date()));
    		logWriter.write("\n"+className+": Executing query on "+instance2 + " " + jobDatePattern.format(new Date()) + "\n");
    		JSONArray iheKeysArray2 = new JSONArray();
    		iheKeysArray2 = ApiHandler.Call(devUrl+queryString,"Cache",className);
    		for(int j=0;j<iheKeysArray2.size();j++) {  
    			JSONObject iheObj = (JSONObject) iheKeysArray2.get(j);
    			IHEKeyObject kObj = new IHEKeyObject(instance2,iheObj.get("MessageID").toString(),iheObj.get("Name").toString(),iheObj.get("TimeCreatedMs").toString(),TargetConfigName);
    			fileWriter2.write(instance2 + "\t" + iheObj.get("MessageID") + "\t" + iheObj.get("Name") + "\t" + iheObj.get("TimeCreatedMs") + "\n");
    			iheCombinedKeyList.add(kObj);
    			fw2Count++;
    		}
    	
    		System.out.println(className + ": merging and sorting the IHE record keys");
    		Collections.sort(iheCombinedKeyList, new IHEKeyObject());
    		for (IHEKeyObject key : iheCombinedKeyList) {
    			fileWriter3.write(key.getInstance() + "\t" + key.getMessageid() + "\t" + key.getName() + "\t" + key.getTimestamp() + "\n");
    			fw3Count++;
    		}
    			
    		System.out.println("\noutput file: "+ outputFile1 + fileDate + ".txt");
    		System.out.println("output file: "+ outputFile2 + fileDate + ".txt");
    		System.out.println("output file: "+ outputFile3 + fileDate + ".txt");
    		System.out.println("\nEXECUTION SUMMARY");
    		System.out.println("==============================================================");
    		System.out.println("Total " + instance1 + " IHE keys written to file   : " + fw1Count);
    		System.out.println("Total " + instance2 + " IHE keys written to file   : " + fw2Count);
    		System.out.println("Total Combined IHE keys written to file : " + fw3Count);
    		System.out.println("==============================================================");
    		System.out.println("\n"+className + ": completed "+jobDatePattern.format(new Date()));
    		
    		logWriter.write("\noutput file: " + outputFile1 + fileDate + ".txt\n");
    		logWriter.write("output file: " + outputFile2 + fileDate + ".txt\n");
    		logWriter.write("output file: " + outputFile3 + fileDate + ".txt\n");
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