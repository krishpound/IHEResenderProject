package com.healthix.nyc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

public class IHEResenderClient {

	
	
	private static String devUrl = "http://dev-healthix:57772/csp/healthix/util/iheKeys";
	private static String SourceConfigName = "HS.IHE.XDSb.Repository.Services";
	private static String TargetConfigName = "BHIX.IHE.XDSb.Repository.AutoReplace";
	private static String OID = "1.2.840.114350.1.13.382.2.7.3.688884.100";  //Brookdale University
	private static String StartDateTime = "201711010000";
	private static String EndDateTime = "201712010000";
	private static String queryString = "?SourceConfigName=" + SourceConfigName + "&TargetConfigName=" + TargetConfigName + "&OID=" + OID + "&StartDateTime=" + StartDateTime + "&EndDateTime=" + EndDateTime;
	private static String sqlQuery = "SELECT %nolock mh.ID, x.id from ens.messageheader mh join hs_message.xmlmessage x on (mh.messagebodyid = x.id) where mh.sourceconfigname = ? and mh.targetconfigname = ? and x.samldata_organizationoid = ? and mh.timecreated between ? and ? and mh.type = 'Request' order by mh.id asc";
	
    public static void main(String[] args) {
    	
    	String className = new Object() {}.getClass().getEnclosingClass().getName();
    	System.out.println(className + ": starting "+new Date());
    	System.out.println("\nExecution Parameters for fetching IHE record keys:");
    	System.out.println("===============================================================================");
    	System.out.println(sqlQuery);
    	System.out.println("SourceConfigName : " + SourceConfigName);
    	System.out.println("TargetConfigName : " + TargetConfigName);
    	System.out.println("OID              : " + OID);
    	System.out.println("StartDateTime    : " + StartDateTime);
    	System.out.println("EndDateTime      : " + EndDateTime);
    	System.out.println("Type             : Request");
    	System.out.println("===============================================================================");
    	System.out.println("NOTE: This query will run in HSBUS namespace on both AG4 and AG5.");
    	System.out.println("      The two result sets will be merged and sorted chronologically in this routine.");
    	System.out.println("      The output dataset will drive the resends to ensure the IHE transactions are processd in order.\n");
    	
    	JSONArray iheKeysArray = new JSONArray();
    	iheKeysArray = ApiHandler.Call(devUrl+queryString,"Cache",className); 
    	for(int i=0;i<iheKeysArray.size();i++) {  
			JSONObject iheObj = (JSONObject) iheKeysArray.get(i);
			System.out.println("MessageID: " + iheObj.get("MessageID") + " Name: " + iheObj.get("Name") + " TimeCreatedMillisecond: " + iheObj.get("TimeCreatedMs"));
    	}
		System.out.println("\nExecution Summary");
    	System.out.println("==============================================================");
    	//System.out.println("Records commited to Server Table: "+serverInsertCount);
    	//System.out.println("Records commited to Namespace Table: "+namespaceInsertCount);
    	//System.out.println("Records commited to Production Table: "+productionInsertCount);
    	//System.out.println("Records commited to Interface Table: "+interfaceInsertCount);
    	//System.out.println("Records commited to Task Table: "+taskInsertCount);
    	//System.out.println("Records commited to Certificate Table: "+certificateInsertCount);
    	System.out.println("==============================================================");
    	System.out.println("\n" + className + ": completed "+new Date());
    } 
}