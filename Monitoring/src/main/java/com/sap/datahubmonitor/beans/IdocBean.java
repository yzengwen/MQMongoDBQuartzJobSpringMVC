package com.sap.datahubmonitor.beans;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
@Document(collection="IdocInfo")
public class IdocBean implements Comparable<IdocBean> {
	@Id
	private ObjectId id;
	@Field(DatahubMonitorConstants.MESSAGE_TYPE)
	private String messageType;
	@Field(DatahubMonitorConstants.BATCH_ID)
	private String batchPrimaryId;
	@Field(DatahubMonitorConstants.ITEM_NUM)
	private int itemNum;
	@Field(DatahubMonitorConstants.STATUS)
	private String status;
	@Field(DatahubMonitorConstants.CREATED_TIME)
	private Long createdTime;
	@Field(DatahubMonitorConstants.MODIFIED_TIME)
	private Long modifiedTime;
	@Field(DatahubMonitorConstants.DURATION)
	private Long duration;
	@Field(DatahubMonitorConstants.POOL_NAME)
	private String poolName;
	@Field(DatahubMonitorConstants.EXCEPTION)
	private String exception;
	
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
	public String getBatchPrimaryId() {
		return batchPrimaryId;
	}
	public void setBatchPrimaryId(String batchPrimaryId) {
		this.batchPrimaryId = batchPrimaryId;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
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
	public Long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
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

	@Override
	public int compareTo(IdocBean o) {
		if(StringUtils.isNotBlank(batchPrimaryId) && StringUtils.isNotBlank(o.getBatchPrimaryId())) {
			try {
				long batchId1 = Long.parseLong(batchPrimaryId);
				long batchId2 = Long.parseLong(o.getBatchPrimaryId());
				if(batchId1 > batchId2){
					return -1;
				}
				else if(batchPrimaryId.equals(o.getBatchPrimaryId())) {
					return 0;
				}
				else {
					return 1;
				}
			}
			catch(Exception e) {
				return 0;
			}
		}
		return 0;
	}
	
}
