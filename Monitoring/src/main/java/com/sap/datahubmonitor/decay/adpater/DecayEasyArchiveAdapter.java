package com.sap.datahubmonitor.decay.adpater;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.sap.datahubmonitor.beans.SettingsBean;
import com.sap.datahubmonitor.decay.DecayHandler;

//TODO: just archive by time stamp
public class DecayEasyArchiveAdapter implements DecayHandler {
	@Resource
	protected MongoTemplate mongoTemplate;

	public void handleDocuments(SettingsBean setting) {
		//SettingsBean settingmongoTemplate.findAllAndRemove(query, entityClass);
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

}
