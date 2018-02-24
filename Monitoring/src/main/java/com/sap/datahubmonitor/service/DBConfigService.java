package com.sap.datahubmonitor.service;

import com.sap.datahubmonitor.beans.SettingsBean;

public interface DBConfigService {

	int getDecayCronjobfreqHours();
	SettingsBean getSettingsBean();
	void saveSettingsBean(SettingsBean settings);
	double getStorageSize();
	double getDataSize();
}
