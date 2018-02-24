package com.sap.datahubmonitor.beans;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="StatisticInfo")
public class StatisticInfoBean {

	private ObjectId _id;
	private long statisticalPoint ;
	private long averageProcessingTime ;
	private long inProcessAmount ;
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public long getStatisticalPoint() {
		return statisticalPoint;
	}
	public void setStatisticalPoint(long statisticalPoint) {
		this.statisticalPoint = statisticalPoint;
	}
	public long getAverageProcessingTime() {
		return averageProcessingTime;
	}
	public void setAverageProcessingTime(long averageProcessingTime) {
		this.averageProcessingTime = averageProcessingTime;
	}
	public long getInProcessAmount() {
		return inProcessAmount;
	}
	public void setInProcessAmount(long inProcessAmount) {
		this.inProcessAmount = inProcessAmount;
	}
	
}
