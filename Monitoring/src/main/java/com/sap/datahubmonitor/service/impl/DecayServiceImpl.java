package com.sap.datahubmonitor.service.impl;
import java.io.File;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.CommandResult;
import com.sap.datahubmonitor.beans.SettingsBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.decay.DecayHandler;
import com.sap.datahubmonitor.service.DBConfigService;
import com.sap.datahubmonitor.service.DecayService;
import com.sap.datahubmonitor.utils.Configurations;

public class DecayServiceImpl implements DecayService {
	private static final Logger LOG = LoggerFactory.getLogger(DecayServiceImpl.class);
	@Resource
	protected DBConfigService dbConfigService;
	protected DecayHandler decayHandler;
	
	@Override
	public void archiveDBData() {
		SettingsBean setting = dbConfigService.getSettingsBean();
		
		if(!checkThreshold()){
			setting.setCleanStrategy(true);
		}
		decayHandler.handleDocuments(setting);
	}

	@Override
	public boolean checkThreshold() {
		SettingsBean setting = dbConfigService.getSettingsBean();
		double threshold = setting.getThreshold()/100.0;
		double dataSize = dbConfigService.getDataSize();
		File f = new File( Configurations.getProperty(DatahubMonitorConstants.SETTINGS_MONGO_ROOT_PATH));
		long freespace = f.getFreeSpace();//byte
		double usePercent = dataSize/(dataSize+freespace);
		if(usePercent > threshold){
			if(LOG.isDebugEnabled()){
				LOG.debug(String.format("Threshold occurs:dataSize%s usePercent%s threshold%s",dataSize,usePercent,threshold));
			}
			return false;
		}
		return true;
	}

	public DecayHandler getDecayHandler() {
		return decayHandler;
	}
	@Required
	public void setDecayHandler(DecayHandler decayHandler) {
		this.decayHandler = decayHandler;
	}

	public DBConfigService getDbConfigService() {
		return dbConfigService;
	}

	public void setDbConfigService(DBConfigService dbConfigService) {
		this.dbConfigService = dbConfigService;
	}


}
