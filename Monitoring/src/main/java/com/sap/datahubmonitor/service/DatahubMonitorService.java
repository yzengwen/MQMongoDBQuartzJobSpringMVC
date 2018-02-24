package com.sap.datahubmonitor.service;

import java.util.List;

import com.sap.datahubmonitor.beans.AdvancedSearchBean;
import com.sap.datahubmonitor.beans.CanonicalItemBean;
import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.beans.TargetItemBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface DatahubMonitorService {
	
	public JSONArray getLandingPageContent();

	/*
	 * below methods for navigation tree
	 */
	/*
	public JSONArray getMessageTypeTreeJSON();
	
	public JSONArray getIdocTreeJSONByType(String messageType);
	*/
	public JSONArray getCanonicalItemTreeJSONByIdoc(String messageType, String batchPrimaryId);
	
	public JSONArray getTargetItemTreeJSONByCanonical(String messageType, String batchPrimaryId, String canonicalItemType);
	
	/*
	 * retrieve by criteria
	 */
	public List<IdocBean> getIdocsByType(String messageType);
	
	public List<CanonicalItemBean> getCanonicalItemsByIdoc(String messageType, String batchPrimaryId);
	
	public List<CanonicalItemBean> getCanonicalItemsByItemType(String messageType, String batchPrimaryId, String canonicalItemType);
	
	public List<TargetItemBean> getTargetItemsByCanonicalItemType(String messageType, String batchPrimaryId, String canonicalItemType);
	
	public List<TargetItemBean> getTargetItemsByCanonicalItemId(String messageType, String batchPrimaryId, Long canonicalItemId);
	
	public JSONObject getIdocDetails(String messageType, String batchPrimaryId);
	
	public JSONObject getCanonicalItemDetails(String messageType, String batchPrimaryId, Long itemId);
	
	public JSONObject getTargetItemDetails(String messageType, String batchPrimaryId, Long itemId);
	
	//for text search usage
//	public JSONArray search(String text, AdvancedSearchBean advancedSearchBean);
	
	//for advanced search
	public JSONArray advancedSearch(AdvancedSearchBean advancedSearchBean);
	//get the drop down list of message types
	public List getAvailableMessageTypes();
		
	//for statistic diagram
	public List<IdocBean> getIdocStatusNum();
	
}
