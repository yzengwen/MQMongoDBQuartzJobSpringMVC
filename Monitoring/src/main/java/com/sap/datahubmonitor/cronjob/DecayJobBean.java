package com.sap.datahubmonitor.cronjob;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.datahubmonitor.service.DecayService;

public class DecayJobBean {
	@Resource
	private DecayService decayService;
	private boolean enable;
	
	private static final Logger LOG = LoggerFactory.getLogger(DecayJobBean.class);

	public void clearData() {
		if (BooleanUtils.isTrue(enable)) {
			if(LOG.isDebugEnabled()){
				LocalDateTime startTime = LocalDateTime.now();
				LOG.debug("DecayJobBean start running at "+ startTime);
			}
			decayService.archiveDBData();
			if(LOG.isDebugEnabled()){
				LocalDateTime endTime = LocalDateTime.now();
				LOG.debug("DecayJobBean end running at "+ endTime);
			}
		}
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public DecayService getDecayService() {
		return decayService;
	}

	public void setDecayService(DecayService decayService) {
		this.decayService = decayService;
	}

}
