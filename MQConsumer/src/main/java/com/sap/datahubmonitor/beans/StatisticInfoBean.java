package com.sap.datahubmonitor.beans;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sap.datahubmonitor.constant.DatahubMonitorConstants;

@Document(collection = "StatisticInfo")
public class StatisticInfoBean {
	@Field(DatahubMonitorConstants.ID)
	private ObjectId id;
	@Field(DatahubMonitorConstants.STATISTICAL_POINT)
	private Long statisticalPoint;
	@Field(DatahubMonitorConstants.AVERAGE_PROCESSING_TIME)
	private Long averageProcessingTime;
	@Field(DatahubMonitorConstants.IN_PROCESS_AMOUNT)
	private Long inProcessAmount;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Long getStatisticalPoint() {
		return statisticalPoint;
	}

	public void setStatisticalPoint(Long statisticalPoint) {
		this.statisticalPoint = statisticalPoint;
	}

	public Long getAverageProcessingTime() {
		return averageProcessingTime;
	}

	public void setAverageProcessingTime(Long averageProcessingTime) {
		this.averageProcessingTime = averageProcessingTime;
	}

	public Long getInProcessAmount() {
		return inProcessAmount;
	}

	public void setInProcessAmount(Long inProcessAmount) {
		this.inProcessAmount = inProcessAmount;
	}

}
