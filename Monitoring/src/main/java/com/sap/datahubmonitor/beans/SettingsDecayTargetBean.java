package com.sap.datahubmonitor.beans;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="SettingsDecayTarget")
public class SettingsDecayTargetBean {
	List<String> staticsTables;
	List<String> idocRelatedTables;
	
	public List<String> getStaticsTables() {
		return staticsTables;
	}
	public void setStaticsTables(List<String> staticsTables) {
		this.staticsTables = staticsTables;
	}
	public List<String> getIdocRelatedTables() {
		return idocRelatedTables;
	}
	public void setIdocRelatedTables(List<String> idocRelatedTables) {
		this.idocRelatedTables = idocRelatedTables;
	}
	
}
