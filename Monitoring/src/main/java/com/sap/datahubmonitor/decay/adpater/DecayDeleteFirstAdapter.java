package com.sap.datahubmonitor.decay.adpater;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.beans.ItemBean;
import com.sap.datahubmonitor.beans.RawItemBean;
import com.sap.datahubmonitor.beans.SettingsBean;
import com.sap.datahubmonitor.beans.TargetItemBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;

/**
 * @author I318050 principles: delete first, then loop to delete the left
 */
public class DecayDeleteFirstAdapter extends DecayBasicAdapter {
	private static final Logger logger = LoggerFactory.getLogger(DecayDeleteFirstAdapter.class);
	@Override
	public void handleDocuments(SettingsBean setting) {
		this.outputFilePath = this.getDumpFilesPath();
		Calendar calendar = Calendar.getInstance();
		handleSuccessData(setting, (Calendar) calendar.clone());
		handleErrorData(setting, (Calendar) calendar.clone());
		handleWeekPassedData(setting, (Calendar) calendar.clone());
	}

	private void handleSuccessData(SettingsBean setting, Calendar calendar) {
		Long timestamp = this.getSuccessPointTimestamp(setting, calendar);
		if (timestamp == null)
			return;
		deleteSuccessData(setting, timestamp);
	}

	// Error data are estimated as minority, use loop might be accepted.
	private void handleErrorData(SettingsBean setting, Calendar calendar) {
		Long timestamp = this.getErrorPointTimestamp(setting, calendar);
		if (timestamp == null)
			return;
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.CREATED_TIME).lt(timestamp),
				Criteria.where(DatahubMonitorConstants.STATUS).in(errorStatuses)));
		List<IdocBean> iDocs = mongoTemplate.find(query, IdocBean.class);
		if(logger.isDebugEnabled()){
			logger.debug("error iDocInfo to delete: count is "+ iDocs.size());
		}
		this.remove(setting, query, IdocBean.class);

		for (IdocBean iDoc : iDocs) {
			String docNumber = iDoc.getBatchPrimaryId();
			handleIDocNoRelated(setting, docNumber);
		}
	}

	private void handleWeekPassedData(SettingsBean setting, Calendar calendar) {
		// one week ago
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		Long timestamp = calendar.getTimeInMillis();
		deleteNoStatusData(setting, RawItemBean.class, timestamp);
		deleteNoStatusData(setting, TargetItemBean.class, timestamp);
	}

	private void deleteSuccessData(SettingsBean setting, Long timestamp) {
		// 1 delete iDocInfo
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.CREATED_TIME).lt(timestamp),
				Criteria.where(DatahubMonitorConstants.STATUS).in(sucessStatuses)));

		this.remove(setting, query, IdocBean.class);

		// 2 delete child beans
		for (Class<? extends ItemBean> itemBeanSubType : itemBeanSubTypes) {
			query = new Query();
			query.addCriteria(
					new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.CREATED_TIME).lt(timestamp),
							new Criteria().orOperator(Criteria.where(DatahubMonitorConstants.STATUS).exists(false),
									Criteria.where(DatahubMonitorConstants.STATUS).in(itemSuccessStatuses))));
			this.remove(setting, query, itemBeanSubType);
		}
	}

	private void deleteNoStatusData(SettingsBean setting, Class<? extends ItemBean> itembean, Long timestamp) {
		Query query = new Query();
		query.addCriteria(
				new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.CREATED_TIME).lt(timestamp)));
		this.remove(setting, query, itembean);
	}
}
