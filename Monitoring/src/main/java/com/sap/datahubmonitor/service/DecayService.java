package com.sap.datahubmonitor.service;

public interface DecayService {
	//void deleteDBData(SettingsBean setting);
	void archiveDBData();
	boolean checkThreshold();
}
