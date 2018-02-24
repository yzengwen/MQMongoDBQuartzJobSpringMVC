package com.sap.datahubmonitor.cronjob;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// test cron job
public class TestJobBean {
	private static final Logger LOG = LoggerFactory.getLogger(TestJobBean.class);

	private boolean enable;

	public void clearData() {
		if (BooleanUtils.isNotTrue(enable)) {
			System.out.println("test cronjob");
			LOG.debug("test cron job ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			return;
		}
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
