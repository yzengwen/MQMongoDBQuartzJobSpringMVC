package com.sap.datahubmonitor.beans;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
@Document(collection="CanonicalItems")
public class CanonicalItemBean extends ItemBean {

	@Field(DatahubMonitorConstants.SOURCE_IDS)
	private Long[] sourceIds;
	@Field(DatahubMonitorConstants.ERROR_MESSAGES)
	private List<String> errorMessages;

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(final List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	public Long[] getSourceIds() {
		return sourceIds;
	}

	public void setSourceIds(Long[] sourceIds) {
		this.sourceIds = sourceIds;
	}
	
}
