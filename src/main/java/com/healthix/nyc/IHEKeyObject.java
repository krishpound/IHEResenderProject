package com.healthix.nyc;

import java.util.Comparator;

public class IHEKeyObject implements Comparator<IHEKeyObject>{

	private String instance;
	private String messageid;
	private String name;
	private String timestamp;
	private String resendtarget;
	
	public IHEKeyObject() {}
	public IHEKeyObject(String instance, String messageid, String name, String timestamp, String resendtarget) {
		this.instance=instance;
		this.messageid=messageid;
		this.name=name;
		this.timestamp=timestamp;
		this.resendtarget=resendtarget;
	}
	public int compare(IHEKeyObject obj1, IHEKeyObject obj2) {
        return obj1.getTimestamp().compareTo(obj2.getTimestamp());
    }
	public void setInstance(String s) {
		this.instance=s;
	}
	public String getInstance() {
		return this.instance;
	}
	public void setMessageid(String s) {
		this.messageid=s;
	}
	public String getMessageid() {
		return this.messageid;
	}
	public void setName(String s) {
		this.name=s;
	}
	public String getName() {
		return this.name;
	}
	public void setTimestamp(String s) {
		this.timestamp=s;
	}
	public String getTimestamp() {
		return this.timestamp;
	}
	public void setResendtarget(String s) {
		this.resendtarget=s;
	}
	public String getResendtarget() {
		return this.resendtarget;
	}
	public String toString() {
		return "instance = " + this.instance 
							+ " messageid = " + this.messageid
							+ " name = " + this.name
							+ " timestamp = " + this.timestamp
							+ " resendtarget = " + this.resendtarget;
							
	}
}
