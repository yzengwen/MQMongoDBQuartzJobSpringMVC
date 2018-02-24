package com.sap.datahubmonitor.cronjob.quartz;

import java.text.ParseException;
import java.time.LocalTime;

import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.service.DBConfigService;
import com.sap.datahubmonitor.utils.Configurations;

public class DecayCronTriggerManager {
	private static final Logger logger = LoggerFactory.getLogger(DecayCronTriggerManager.class);
	
	@Resource 
	private Scheduler schedulerFactoryBean;
	//private SchedulerFactoryBean schedulerFactoryBean;
	@Resource
	private DBConfigService dbConfigService;

	public DBConfigService getDbConfigService() {
		return dbConfigService;
	}
	public void setDbConfigService(DBConfigService dbConfigService) {
		this.dbConfigService = dbConfigService;
	}
	public Scheduler getSchedulerFactoryBean() {
		return schedulerFactoryBean;
	}
	public void setSchedulerFactoryBean(Scheduler schedulerFactoryBean) {
		this.schedulerFactoryBean = schedulerFactoryBean;
	}
	
	public String getDefaultDecayExpression(){
		return getCronExpression(dbConfigService.getDecayCronjobfreqHours());
	}
	
	public void rescheduleIntevalJob() {

	    //Scheduler scheduler = schedulerFactoryBean.getScheduler();
	    TriggerKey triggerKey = new TriggerKey(Configurations.getProperty(DatahubMonitorConstants.SETTINGS_TRIGGER_NAME));
	    try {
	    	SimpleTriggerImpl trigger = (SimpleTriggerImpl ) schedulerFactoryBean.getTrigger(triggerKey);
	    	int decayCronjobfreqHours = dbConfigService.getDecayCronjobfreqHours();
	    	trigger.setRepeatInterval(decayCronjobfreqHours * 60L * 60L * 1000L);//hourly
		    schedulerFactoryBean.triggerJob(trigger.getJobKey());
		} catch (SchedulerException e) {					
			logger.error(e.getMessage());
		} 
	}
	
	public void rescheduleCronJob() {
		int decayCronjobfreqHours = dbConfigService.getDecayCronjobfreqHours();
	    String newCronExpression = getCronExpression(decayCronjobfreqHours); 

	    //Scheduler scheduler = schedulerFactoryBean.getScheduler();
	    TriggerKey triggerKey = new TriggerKey(Configurations.getProperty(DatahubMonitorConstants.SETTINGS_TRIGGER_CRON_NAME));
	    try {
		    CronTriggerImpl trigger = (CronTriggerImpl) schedulerFactoryBean.getTrigger(triggerKey);
		    trigger.setCronExpression(newCronExpression );
		    schedulerFactoryBean.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {					
			logger.error(e.getMessage());
		} catch (ParseException e) {
			logger.error(e.getMessage());
		} 
	}
	
	private String getCronExpression(int intervalHour) {		
		String cronExpression = "";
		LocalTime now = LocalTime.now();
		int startTime = now.getHour();
		System.out.println(now.getHour());
		//cronExpression = "0 0 10/1 * * ?";
		cronExpression = "0 0 " + startTime + "/" + intervalHour + " * * ?";
			
		return cronExpression;
	}
	
}
