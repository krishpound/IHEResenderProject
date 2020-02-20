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
import java.util.HashMap;
import java.util.Date;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IHEQuery {

	private static String fpattern = "yyyyMMddhhmm";
	private static String jpattern = "yyyy/MM/dd hh:mm:ss";
	private static SimpleDateFormat fileDatePattern = new SimpleDateFormat(fpattern);
	private static SimpleDateFormat jobDatePattern = new SimpleDateFormat(jpattern);
	//private static String devUrl = "http://dev-healthix:57772/csp/healthix/util/iheQuery";
	//private static String stageAG4Url = "http://192.168.130.20:57772/csp/healthix/util/iheQuery";
	//private static String stageAG5Url = "http://192.168.130.21:57772/csp/healthix/util/iheQuery";
	
	/*
	 * PRODUCTION CONFIGURATION BELOW
	 */
	private static String prodAG4Url = "http://192.168.170.224:57772/csp/healthix/util/iheQuery";
	private static String prodAG5Url = "http://192.168.170.225:57772/csp/healthix/util/iheQuery";
	private static String SourceConfigName = "HS.IHE.XDR.Recipient.Services";   			//confirmed
	private static String TargetConfigName = "Healthix.IHE.XDR.Recipient.ProvideRouter";	//confirmed
	private static String OID = "urn:oid:1.2.840.114350.1.13.382.2.7.3.688884.100";  		//confirmed
	private static String StartDateTime = "202001171200";									//confirmed
	private static String EndDateTime = "202001271100";										//confirmed
	private static String queryString = "?SourceConfigName=" + SourceConfigName + "&TargetConfigName=" + TargetConfigName + "&OID=" + OID + "&StartDateTime=" + StartDateTime + "&EndDateTime=" + EndDateTime;
	private static String sqlQuery = "SELECT %nolock mh.ID, x.id from ens.messageheader mh join hs_message.xmlmessage x on (mh.messagebodyid = x.id) where mh.sourceconfigname = ? and mh.targetconfigname = ? and x.samldata_organizationoid = ? and mh.timecreated between ? and ? and mh.type = 'Request' order by mh.id asc";
	private static String instance1 = "ISSR04";
	private static String instance2 = "ISSR05";
	private static String outputPath = "c:/home/en928/";
	private static String logFile = outputPath + "/logs/" + "IHEQuery_log_";
	private static BufferedWriter logWriter;
	
	
    public static void main(String[] args) {
    	
    	try {
    	
    		String className = new Object() {}.getClass().getEnclosingClass().getName();
    		String fileDate = fileDatePattern.format(new Date());
    		logWriter = new BufferedWriter(new FileWriter(logFile + fileDate + ".txt"));
    		System.out.println(className + ": starting "+jobDatePattern.format(new Date()));
    		logWriter.write(className + ": starting "+jobDatePattern.format(new Date()));
    		
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
    		JSONArray iheJobObj1 = new JSONArray();
    		iheJobObj1 = ApiHandler.Call(prodAG4Url+queryString,"Cache",className);
    		for(int i=0;i<iheJobObj1.size();i++) {  
    			JSONObject jObj1= (JSONObject) iheJobObj1.get(i);
    			System.out.println("TaskId " + jObj1.get("TaskId") + " started on " + jObj1.get("Instance"));
    			logWriter.write("TaskId " + jObj1.get("TaskId") + " started on " + jObj1.get("Instance") + "\n");
    		}
    		
    		//Instance 2 Query Processing
    		System.out.println("\n"+className+": Executing query on "+instance2 + " " + jobDatePattern.format(new Date()));
    		logWriter.write("\n"+ className+": Executing query on " + instance2 + " " + jobDatePattern.format(new Date()) +"\n");
    		JSONArray iheJobObj2 = new JSONArray();
    		iheJobObj2 = ApiHandler.Call(prodAG5Url+queryString,"Cache",className);
    		for(int j=0;j<iheJobObj2.size();j++) {  
    			JSONObject jObj2 = (JSONObject) iheJobObj2.get(j);
    			System.out.println("TaskId " + jObj2.get("TaskId") + " started on " + jObj2.get("Instance"));
    			logWriter.write("TaskId " + jObj2.get("TaskId") + " started on " + jObj2.get("Instance") + "\n");
    		}
    		
    		System.out.println("\n"+className + ": completed "+jobDatePattern.format(new Date()));
    		logWriter.write("\n"+className + ": completed "+jobDatePattern.format(new Date())+"\n");
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