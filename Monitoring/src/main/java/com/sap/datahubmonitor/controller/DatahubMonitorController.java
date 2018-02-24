package com.sap.datahubmonitor.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.datahubmonitor.beans.AdvancedSearchBean;
import com.sap.datahubmonitor.beans.CanonicalItemBean;
import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.service.DatahubMonitorService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class DatahubMonitorController {

	@Resource(name="datahubMonitorService")
	private DatahubMonitorService datahubMonitorService;
	
	@RequestMapping(value = "/advancedSearch", method = RequestMethod.POST)
	public @ResponseBody String advancedSearch(HttpServletRequest request, @RequestBody AdvancedSearchBean advancedSearchBean) {
		//store the advanced search conditions into session
		request.getSession().setAttribute(DatahubMonitorConstants.ADVANCED_SEARCH_BEAN, advancedSearchBean);
		JSONArray jsonArray = datahubMonitorService.advancedSearch(advancedSearchBean);
		if (jsonArray != null) {
			return jsonArray.toString();
		} else {
			return new JSONArray().toString();
		}
	}
	
	@RequestMapping(value = "/getAvailableMessageTypes", method = RequestMethod.GET)
	public @ResponseBody String getAvailableMessageTypes(){
		return JSONArray.fromObject(datahubMonitorService.getAvailableMessageTypes()).toString();
	}
	
	@RequestMapping(value = "/refreshPage", method = RequestMethod.POST)
	public @ResponseBody String refreshPage(HttpServletRequest request) {
		AdvancedSearchBean advancedSearchBean = (AdvancedSearchBean) request.getSession().getAttribute(DatahubMonitorConstants.ADVANCED_SEARCH_BEAN);
		JSONArray searchResult = datahubMonitorService.advancedSearch(advancedSearchBean);
		JSONObject jsonObj = new JSONObject();
		jsonObj.element("treeData", searchResult);
		jsonObj.element("searchData", advancedSearchBean);
		return jsonObj.toString();
	}
	
	/*
	@RequestMapping(value = "/tree", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getNavigationTree(HttpServletRequest request, @RequestParam(value = DatahubMonitorConstants.TEXT, required = false) final String text) {
		JSONArray treeArray = null;
		if(StringUtils.isNotBlank(text)) {
			// search by conditions and group by batch id
			treeArray = datahubMonitorService.search(text, (AdvancedSearchBean) request.getSession().getAttribute(DatahubMonitorConstants.ADVANCED_SEARCH_BEAN));
			if(treeArray != null) {
				return treeArray.toString();
			}
			else {
				return new JSONArray().toString();
			}
		}
		else {
			return this.refreshPage(request);
		}
	}
	*/
	
	/*
	 * Ajax retrieve for tree presentation start
	 */
	/*
	@RequestMapping(value = "/idocstree", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getIdocsForTree(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType) {
		JSONArray treeArray = datahubMonitorService.getIdocTreeJSONByType(messageType);
		if(treeArray != null) {
			return treeArray.toString();
		}
		else {
			return new JSONArray().toString();
		}
	}
	*/
	@RequestMapping(value = "/content", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String landingPageContent() {
		JSONArray jsonArray = datahubMonitorService.getLandingPageContent();
		if(jsonArray != null) {
			return jsonArray.toString();
		}
		else {
			return new JSONArray().toString();
		}
	}
	
	
	@RequestMapping(value = "/canonicalstree", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getCanonicalsForTree(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType, 
			@RequestParam(value = DatahubMonitorConstants.BATCH_ID, required = true) final String batchPrimaryId) {
		String batchId = null;
		if(StringUtils.isNotBlank(batchPrimaryId)) {
			batchId = batchPrimaryId;
		}
		return datahubMonitorService.getCanonicalItemTreeJSONByIdoc(messageType, batchId).toString();
	}
	
	@RequestMapping(value = "/targetstree", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getTargetsForTree(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType, 
			@RequestParam(value = DatahubMonitorConstants.BATCH_ID, required = true) final String batchPrimaryId, 
			@RequestParam(value = DatahubMonitorConstants.CANONICAL_ITEMTYPE, required = true) final String canonicalItemType) {
		String batchId = null;
		if(StringUtils.isNotBlank(batchPrimaryId)) {
			batchId = batchPrimaryId;
		}
		return datahubMonitorService.getTargetItemTreeJSONByCanonical(messageType, batchId, canonicalItemType).toString();
	}
	/*
	 * Ajax retrieve for tree presentation end
	 */
	
	@RequestMapping(value = "/idocsinfo", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getIdocsInfo(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType) {
		List<IdocBean> list = datahubMonitorService.getIdocsByType(messageType);
		
		return JSONArray.fromObject(list).toString();
	}
	
	@RequestMapping(value = "/idocinfo", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getIdocInfo(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType, 
			@RequestParam(value = DatahubMonitorConstants.BATCH_ID, required = true) final String batchPrimaryId) {
		String batchId = null;
		if(StringUtils.isNotBlank(batchPrimaryId)) {
			batchId = batchPrimaryId;
		}
		return datahubMonitorService.getIdocDetails(messageType, batchId).toString();
	}
	
	
	@RequestMapping(value = "/canonicalitemsinfo", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getCanonicalItemsInfo(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType, 
			@RequestParam(value = DatahubMonitorConstants.BATCH_ID, required = true) final String batchPrimaryId, 
			@RequestParam(value = DatahubMonitorConstants.CANONICAL_ITEMTYPE, required = true) final String canonicalItemType) {
		String batchId = null;
		if(StringUtils.isNotBlank(batchPrimaryId)) {
			batchId = batchPrimaryId;
		}
		List<CanonicalItemBean> list = datahubMonitorService.getCanonicalItemsByItemType(messageType, batchId, canonicalItemType);
		if(CollectionUtils.isNotEmpty(list)) {
			return JSONArray.fromObject(list).toString();
		}
		else {
			return new JSONArray().toString();
		}
	}
	
	@RequestMapping(value = "/canonicalitemdetail", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getCanonicalItemDetail(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType, 
			@RequestParam(value = DatahubMonitorConstants.BATCH_ID, required = true) final String batchPrimaryId, 
			@RequestParam(value = DatahubMonitorConstants.CANONICAL_ITEMID, required = true) final String itemId) {
		String batchId = null;
		Long canonicalItemId = null;
		if(StringUtils.isNotBlank(batchPrimaryId)) {
			batchId = batchPrimaryId;
		}
		if(StringUtils.isNotBlank(itemId)) {
			canonicalItemId = Long.valueOf(itemId);
		}
		return datahubMonitorService.getCanonicalItemDetails(messageType, batchId, canonicalItemId).toString();
	}
	
	@RequestMapping(value = "/targetiteminfo", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getTargetItemInfo(@RequestParam(value = DatahubMonitorConstants.MESSAGE_TYPE, required = true) final String messageType, 
			@RequestParam(value = DatahubMonitorConstants.BATCH_ID, required = true) final String batchPrimaryId, 
			@RequestParam(value = DatahubMonitorConstants.ITEM_ID, required = true) final String itemId) {
		String batchId = null;
		Long itemID = null;
		if(StringUtils.isNotBlank(batchPrimaryId)) {
			batchId = batchPrimaryId;
		}
		if(StringUtils.isNotBlank(itemId)) {
			itemID = Long.valueOf(itemId);
		}
		return datahubMonitorService.getTargetItemDetails(messageType, batchId, itemID).toString();
	}
	
	
	@RequestMapping(value = "/getIdocStatusNum", method = RequestMethod.GET)
	public @ResponseBody String getIdocStatusNum() {
		// search by conditions and group by batch id
		JSONArray jsonArray = JSONArray.fromObject(datahubMonitorService.getIdocStatusNum());
		if(jsonArray!=null) {
			return jsonArray.toString();
		}
		else {
			return new JSONArray().toString();
		}
	}
	
}
