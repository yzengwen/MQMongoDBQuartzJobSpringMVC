package com.sap.datahubmonitor.service;

import java.util.List;
import java.util.Map;

import com.sap.datahubmonitor.beans.CanonicalItemBean;
import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.beans.StatisticInfoBean;

import net.sf.json.JSONObject;

public interface DatahubConsumerService {
	
	public JSONObject getJobDetails(String jobName);
	
	public void saveJobDetails(JSONObject jobDetails);
	
	public Map<String, JSONObject> getFinalStatusEvents(Long timestamp);
	
	//group by messageType, batchPrimaryId, itemId, itemType as the key of the map
	public void updateErrorMessages(Long timestamp);
	
	public void updateStatusForCanonicalItems(final Map<String, JSONObject> map);
	
	public List<CanonicalItemBean> getCanonicalItems(final Long timestamp);
	
	public List<CanonicalItemBean> getIdocCanonicalItems(final String messageType, final String batchPrimaryId);
	
	//group by batchPrimaryId
	public Map<String, List<CanonicalItemBean>> getGroupedCanonicalItems(final List<CanonicalItemBean> list);
	
	public IdocBean getIdocInfo(final String messageType, final String batchPrimaryId);
	
	public List<IdocBean> getIdocInfos(final Long startTime, final Long endTime);
	
	//loop and generate idoc info
	public void generateIdocInfos(final Map<String, List<CanonicalItemBean>> map);
	
	public void upsertStatisticInfo(final StatisticInfoBean statisticInfo);
		
}
