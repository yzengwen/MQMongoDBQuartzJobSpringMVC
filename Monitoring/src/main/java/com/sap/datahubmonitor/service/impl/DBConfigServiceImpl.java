package com.sap.datahubmonitor.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.mongodb.CommandResult;
import com.sap.datahubmonitor.beans.CronjobConfigBean;
import com.sap.datahubmonitor.beans.SettingsBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.service.DBConfigService;

@Component("dbConfigService")
public class DBConfigServiceImpl implements DBConfigService {
	@Resource
	protected MongoTemplate mongoTemplate;
	
	int decayCronjobfreqHours;
	
	public int getDecayCronjobfreqHours(){
		Query query = new Query();
		query.addCriteria(
		    new Criteria().andOperator(
		        Criteria.where(DatahubMonitorConstants.CronjobConfigBean_JobName).is(DatahubMonitorConstants.JOB_DECAY_NAME)
		    )
		);
		mongoTemplate.getCollectionNames();
		boolean collectionExists = mongoTemplate.getCollectionNames().contains(CronjobConfigBean.class.getAnnotation(Document.class).collection());
		if(!collectionExists){
			generateDefaultCronjobConfigBean();
		}
		List<CronjobConfigBean> cronjobConfigBeans = mongoTemplate.find(query, CronjobConfigBean.class);
		if(CollectionUtils.isEmpty(cronjobConfigBeans)){
			return decayCronjobfreqHours;
		}
		else{
			return cronjobConfigBeans.get(0).getFrequencyHour();
		}
	}
	
	public void setDecayCronjobfreqHours(int decayCronjobfreqHours){
		this.decayCronjobfreqHours = decayCronjobfreqHours;
	}
	
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public SettingsBean getSettingsBean(){
		mongoTemplate.getCollectionNames();
		boolean collectionExists = mongoTemplate.getCollectionNames().contains(SettingsBean.class.getAnnotation(Document.class).collection());
		if(!collectionExists){
			generateDefaultSettingsBean();
		}
		SettingsBean settings = mongoTemplate.findOne(new Query(), SettingsBean.class);
		return settings;
	}
	
	public void saveSettingsBean(SettingsBean settings){
	    if (settings != null) {
	    	mongoTemplate.save(settings);
	    }
	}
	
	public double getStorageSize(){
		CommandResult cr = mongoTemplate.getDb().getStats();
		return (double)cr.get(DatahubMonitorConstants.DB_STORAGE_SIZE);
	}
	
	public double getDataSize(){
		CommandResult cr = mongoTemplate.getDb().getStats();
		return (double)cr.get(DatahubMonitorConstants.DB_DATA_SIZE);
	}
	
	private void generateDefaultCronjobConfigBean(){
		mongoTemplate.createCollection(CronjobConfigBean.class);
		CronjobConfigBean bean = new CronjobConfigBean();
		bean.setFrequencyHour(decayCronjobfreqHours);
		bean.setJobName(DatahubMonitorConstants.JOB_DECAY_NAME);
		mongoTemplate.insert(Sets.newHashSet(bean), CronjobConfigBean.class);
	}
	
	private void generateDefaultSettingsBean(){
		mongoTemplate.createCollection(SettingsBean.class);
		SettingsBean bean = new SettingsBean();
		bean.setErrorEventDays(10);
		bean.setErrorEventHours(10);
		bean.setSuccessEventDays(10);
		bean.setSuccessEventHours(10);
		bean.setCleanStrategy(true);
		bean.setThreshold(80.0);
		mongoTemplate.insert(Sets.newHashSet(bean), SettingsBean.class);
	}
}
