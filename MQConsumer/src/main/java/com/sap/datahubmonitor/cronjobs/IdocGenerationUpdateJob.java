package com.sap.datahubmonitor.cronjobs;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.datahubmonitor.beans.CanonicalItemBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.service.DatahubConsumerService;

import net.sf.json.JSONObject;

public class IdocGenerationUpdateJob {
	
	private static final Logger logger = LoggerFactory.getLogger(IdocGenerationUpdateJob.class);

	private boolean enable;

	@Resource
	private DatahubConsumerService datahubConsumerService;
	
	public void run() {
		Instant startTime = Instant.now();
		logger.info("IdocGenerationUpdateJob start to process at {}.", startTime);
		if (!enable) {
			logger.info("IdocGenerationUpdateJob is disabled, if you want to run this cronjob, please set [enable] flag to true.");
			return;
		}
		JSONObject jobInfo = datahubConsumerService.getJobDetails(DatahubMonitorConstants.JOB_IDOC_GENERATION);
		Long timeRecord = 0l;
		//first time the job running, set a default time record
		if(jobInfo!=null && !jobInfo.isNullObject() && jobInfo.get(DatahubMonitorConstants.TIME_RECORD)!=null) {
			timeRecord = jobInfo.getLong(DatahubMonitorConstants.TIME_RECORD);
		}
		//log the time before retrieve data
		Long nextTimeRecord = Instant.now().toEpochMilli();
		//get all canonical items which after the time record.
		List<CanonicalItemBean> list = datahubConsumerService.getCanonicalItems(timeRecord);
		if(CollectionUtils.isNotEmpty(list)) {
			//group canonical items by batchId
			Map<String, List<CanonicalItemBean>> map = datahubConsumerService.getGroupedCanonicalItems(list);
			//generate records for IdocInfo table and calculate the statuses for them
			datahubConsumerService.generateIdocInfos(map);
		}
		//persist the job status for next time
		jobInfo = new JSONObject();
		jobInfo.element(DatahubMonitorConstants.JOB_NAME, DatahubMonitorConstants.JOB_IDOC_GENERATION);
		jobInfo.element(DatahubMonitorConstants.TIME_RECORD, nextTimeRecord);
		datahubConsumerService.saveJobDetails(jobInfo);
		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		logger.info("IdocGenerationUpdateJob completed with last execution time:{}.", timeRecord);
		logger.info("IdocGenerationUpdateJob finished at {}, execution duration: {} seconds.", new Object[]{endTime, duration.getSeconds()});
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
