package com.sap.datahubmonitor.beans;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sap.datahubmonitor.constant.DatahubMonitorConstants;

@Document(collection="TargetItems")
public class TargetItemBean extends ItemBean {

	@Field(DatahubMonitorConstants.CANONICAL_ITEMID)
	private String canonicalItemId;
	@Field(DatahubMonitorConstants.EXPORT_CODE)
	private String exportCode;
	
	public String getCanonicalItemId() {
		return canonicalItemId;
	}
	public void setCanonicalItemId(String canonicalItemId) {
		this.canonicalItemId = canonicalItemId;
	}
	public String getExportCode() {
		return exportCode;
	}
	public void setExportCode(String exportCode) {
		this.exportCode = exportCode;
	}
	
}
