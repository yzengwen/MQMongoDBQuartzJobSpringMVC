package com.sap.datahubmonitor.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sap.datahubmonitor.constant.DatahubMonitorConstants;

public class ItemBean implements Comparable<ItemBean> {
	@Id
	private ObjectId id;
	@Field(DatahubMonitorConstants.MESSAGE_TYPE)
	private String messageType;
	@Field(DatahubMonitorConstants.MESSAGE_SOURCE)
	private String messageSource;
	@Field(DatahubMonitorConstants.BATCH_ID)
	private String batchPrimaryId;
	@Field(DatahubMonitorConstants.EVENT_NAME)
	private String eventName;
	@Field(DatahubMonitorConstants.ITEM_ID)
	private Long itemId;
	@Field(DatahubMonitorConstants.ITEM_TYPE)
	private String itemType;
	@Field(DatahubMonitorConstants.STATUS)
	private String status;
	@Field(DatahubMonitorConstants.CREATED_TIME)
	private Long createdTime;
	@Field(DatahubMonitorConstants.INTEGRATION_KEY)
	private String integrationKey;
	@Field(DatahubMonitorConstants.TRACE_ID)
	private String traceId;
	@Field(DatahubMonitorConstants.POOL_NAME)
	private String poolName;
	@Field(DatahubMonitorConstants.EXCEPTION)
	private String exception;
	@Field(DatahubMonitorConstants.MODIFIED_TIME)
	private Long modifiedTime;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessageSource() {
		return messageSource;
	}
	public void setMessageSource(String messageSource) {
		this.messageSource = messageSource;
	}
	public String getBatchPrimaryId() {
		return batchPrimaryId;
	}
	public void setBatchPrimaryId(String batchPrimaryId) {
		this.batchPrimaryId = batchPrimaryId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}
	public String getIntegrationKey() {
		return integrationKey;
	}
	public void setIntegrationKey(String integrationKey) {
		this.integrationKey = integrationKey;
	}
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public Long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	@Override
	public int compareTo(ItemBean o) {
		if(createdTime > o.getCreatedTime()){
			return 1;
		}
		else if(createdTime.equals(o.getCreatedTime())) {
			return 0;
		}
		else {
			return -1;
		}
	}
	
}
