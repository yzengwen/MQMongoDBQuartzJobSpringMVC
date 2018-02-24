package com.sap.datahubmonitor.beans;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
@Document(collection="Message")
public class Message {

	@Field(DatahubMonitorConstants.MESSAGE_TYPE)
	private String type;
	@Field(DatahubMonitorConstants.BATCH_ID)
	private String batchPrimaryId;
	@Field(DatahubMonitorConstants.ITEM_ID)
	private String itemId;
	@Field(DatahubMonitorConstants.STATUS)
	private String status;
	private int total;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBatchPrimaryId() {
		return batchPrimaryId;
	}
	public void setBatchPrimaryId(String batchPrimaryId) {
		this.batchPrimaryId = batchPrimaryId;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
}
