package com.sap.datahubmonitor.beans;

import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
@Document(collection="Settings")
public class SettingsBean {
	private ObjectId _id;
	private int successEventDays ;
	private int successEventHours ;
	private int errorEventDays ;
	private int errorEventHours ;
	private int statisticalDays;
	private int statisticalHours;
	private boolean cleanStrategy;
	private double threshold;
	
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public int getSuccessEventDays() {
		return successEventDays;
	}
	public void setSuccessEventDays(int successEventDays) {
		this.successEventDays = successEventDays;
	}
	public int getSuccessEventHours() {
		return successEventHours;
	}
	public void setSuccessEventHours(int successEventHours) {
		this.successEventHours = successEventHours;
	}
	public int getErrorEventDays() {
		return errorEventDays;
	}
	public void setErrorEventDays(int errorEventDays) {
		this.errorEventDays = errorEventDays;
	}
	public int getErrorEventHours() {
		return errorEventHours;
	}
	public void setErrorEventHours(int errorEventHours) {
		this.errorEventHours = errorEventHours;
	}
	public int getStatisticalDays() {
		return statisticalDays;
	}
	public void setStatisticalDays(int statisticalDays) {
		this.statisticalDays = statisticalDays;
	}
	public int getStatisticalHours() {
		return statisticalHours;
	}
	public void setStatisticalHours(int statisticalHours) {
		this.statisticalHours = statisticalHours;
	}
	public boolean isCleanStrategy() {
		return cleanStrategy;
	}
	public void setCleanStrategy(boolean cleanStrategy) {
		this.cleanStrategy = cleanStrategy;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	
}
