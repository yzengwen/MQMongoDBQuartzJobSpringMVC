package com.sap.datahubmonitor.cronjobs;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.service.DatahubConsumerService;

import net.sf.json.JSONObject;

public class CanonicalItemStatusUpdateJob {
	private boolean enable;
	private static final Logger logger = LoggerFactory.getLogger(CanonicalItemStatusUpdateJob.class);

	@Resource(name = "datahubConsumerService")
	private DatahubConsumerService datahubConsumerService;

	public void run() {
		Instant startTime = Instant.now();
		logger.info("canonicalItemStatusUpdateJob start to process at {}.", startTime);
		if (!enable) {
			logger.info("UpdateStatusJob is disabled, if you want to run this cronjob, please set [enable] flag to true.");
			return;
		}
		JSONObject jobDetail = datahubConsumerService.getJobDetails(DatahubMonitorConstants.JOB_CANONICAL_ITEM_STATUS);
		Long timeRecord = 0L;
		if(jobDetail != null && !jobDetail.isNullObject() && !jobDetail.isEmpty()) {
			timeRecord = jobDetail.getLong(DatahubMonitorConstants.TIME_RECORD);
		}
		Map<String, JSONObject> map = datahubConsumerService.getFinalStatusEvents(timeRecord);
		datahubConsumerService.updateStatusForCanonicalItems(map);
		datahubConsumerService.updateErrorMessages(timeRecord);
		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		logger.info("UpdateStatusJob completed with last execution time:{}.", timeRecord);
		logger.info("UpdateStatusJob finished at {}, execution duration: {} seconds.", new Object[] { endTime, duration.getSeconds() });
	}
	
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
