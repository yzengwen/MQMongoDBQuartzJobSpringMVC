package com.sap.datahubmonitor.decay;

import com.sap.datahubmonitor.beans.SettingsBean;

public interface DecayHandler {
	void handleDocuments(SettingsBean setting);
}
