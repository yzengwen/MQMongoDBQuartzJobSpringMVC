package com.sap.datahubmonitor.service;

import java.time.Instant;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.utils.Configurations;

public class DefaultMessageHandler implements MessageHandler{

	private static final Logger logger = LoggerFactory.getLogger(DefaultMessageHandler.class);
	
	@Resource(name="mongoService")
	private MongoService<DBObject> mongoService;
	
	private List<String> finalStatusEvents;
	
	@Override
	public void handle(String text) {
		
		DBObject dbObject = (DBObject) JSON.parse(text);
		if(logger.isDebugEnabled()){
			logger.debug("DBObject parsing completed.");
		}
		//filter out events with poolName="SAPCONFIGURATION_POOL"
		if(dbObject.containsField(DatahubMonitorConstants.POOL_NAME)) {
			String poolName = (String) dbObject.get(DatahubMonitorConstants.POOL_NAME);
			if(!DatahubMonitorConstants.VALUE_POOLNAME.equalsIgnoreCase(poolName)) {
				String eventName = null;
				//messages insert into different tables by event names
				if(dbObject.containsField(DatahubMonitorConstants.EVENT_NAME)) {
					dbObject.put(DatahubMonitorConstants.MODIFIED_TIME, Instant.now().toEpochMilli());
					eventName = (String) dbObject.get(DatahubMonitorConstants.EVENT_NAME);
					if(DatahubMonitorConstants.EVENT_RAW_ITEM.equalsIgnoreCase(eventName)) {
						mongoService.save(dbObject, Configurations.getProperty(DatahubMonitorConstants.TABLE_RAWITEM));
					}
					else if(DatahubMonitorConstants.EVENT_CANONICAL_ITEM.equalsIgnoreCase(eventName)) {
						mongoService.save(dbObject, Configurations.getProperty(DatahubMonitorConstants.TABLE_CANONICALITEM));
					}
					else if(DatahubMonitorConstants.EVENT_TARGET_ITEM.equalsIgnoreCase(eventName)) {
						mongoService.save(dbObject, Configurations.getProperty(DatahubMonitorConstants.TABLE_TARGETITEM));
					}
					else if(finalStatusEvents.contains(eventName)) {
						mongoService.save(dbObject, Configurations.getProperty(DatahubMonitorConstants.TABLE_FINALSTATUS));
					}
					else {
						mongoService.save(dbObject, "Test-collection");
						if(logger.isDebugEnabled()){
//							logger.debug("Message ignored: " + dbObject.toString());
							logger.debug("--------------------------------------------------------------------one test record");
						}
					}
				}
			}
			else {
				if(logger.isDebugEnabled()){
					logger.debug("Message ignored with poolName='SAPCONFIGURATION_POOL': " + dbObject.toString());
				}
			}
		}
		else {
			mongoService.save(dbObject, "Test-collection");
			if(logger.isDebugEnabled()){
				logger.debug("--------------------------------------------------------------------one test record");
//				logger.debug("Message ignored, no poolName field found: " + dbObject.toString());
			}
		}
		
	}

	public List<String> getFinalStatusEvents() {
		return finalStatusEvents;
	}

	public void setFinalStatusEvents(List<String> finalStatusEvents) {
		this.finalStatusEvents = finalStatusEvents;
	}
	
}
