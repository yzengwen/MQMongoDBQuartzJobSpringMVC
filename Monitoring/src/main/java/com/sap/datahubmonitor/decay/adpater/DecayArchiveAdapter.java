package com.sap.datahubmonitor.decay.adpater;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.beans.SettingsBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;

public class DecayArchiveAdapter extends DecayBasicAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(DecayArchiveAdapter.class);

	public void handleDocuments(SettingsBean setting) {
		this.outputFilePath = this.getDumpFilesPath();

		handleSuccessData(setting);
		handleErrorData(setting);
		//runZipProcessCommand();
	}

	private void handleSuccessData(SettingsBean setting) {
		Calendar calendar = Calendar.getInstance();
		Long timestamp = this.getSuccessPointTimestamp(setting, calendar);
		if(timestamp == null)
			return;
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.CREATED_TIME).lt(timestamp),
				Criteria.where(DatahubMonitorConstants.STATUS).in(sucessStatuses)));
		// run for IdocBean
		runDumpProcessCommand(IdocBean.class.getAnnotation(Document.class).collection(), query.getQueryObject().toString());
		List<IdocBean> iDocs = mongoTemplate.findAllAndRemove(query, IdocBean.class);
		for (IdocBean iDoc : iDocs) {
			String docNumber = iDoc.getBatchPrimaryId();
			handleIDocNoRelated(setting, docNumber);
		}
	}
	private void handleErrorData(SettingsBean setting) {
		int days = setting.getErrorEventDays();
		int hours = setting.getErrorEventHours();
		int diffHours = days * 24 + hours;
		if (diffHours < 1) {
			return;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, diffHours * -1);
		Long timestamp = calendar.getTimeInMillis();
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.CREATED_TIME).lt(timestamp),
				Criteria.where(DatahubMonitorConstants.STATUS).in(errorStatuses)));
		// run for IdocBean
		runDumpProcessCommand(IdocBean.class.getAnnotation(Document.class).collection(), query.getQueryObject().toString());
		List<IdocBean> iDocs = mongoTemplate.findAllAndRemove(query, IdocBean.class);
		for (IdocBean iDoc : iDocs) {
			String docNumber = iDoc.getBatchPrimaryId();
			handleIDocNoRelated(setting, docNumber);
		}
	}
}
