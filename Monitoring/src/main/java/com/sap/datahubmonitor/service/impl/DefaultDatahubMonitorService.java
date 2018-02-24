package com.sap.datahubmonitor.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.sap.datahubmonitor.beans.AdvancedSearchBean;
import com.sap.datahubmonitor.beans.CanonicalItemBean;
import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.beans.StatusEnum;
import com.sap.datahubmonitor.beans.TargetItemBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.service.DatahubMonitorService;
import com.sap.datahubmonitor.utils.Configurations;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DefaultDatahubMonitorService implements DatahubMonitorService {

	@Resource
	protected MongoTemplate mongoTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultDatahubMonitorService.class);

	private List<StatusEnum> idocStatuses;
	
	private final static String NODES_PLACEHOLDER = "[{\"text\":\"\"}]";
	
	@Override
	public JSONArray getLandingPageContent() {
		JSONArray typeArray = new JSONArray();
		JSONObject typeObj = null;
		List<IdocBean> messageTypes = this.getGroupedMessageTypes();
		if(CollectionUtils.isNotEmpty(messageTypes)) {
			for(IdocBean idoc : messageTypes) {
				typeObj = JSONObject.fromObject(idoc);
				for(StatusEnum status : idocStatuses) {
					typeObj.element(status.toString(), this.getIdocsByTypeAndStatus(idoc.getMessageType(), status.toString()));
				}
				typeArray.add(typeObj);
			}
		}
		return typeArray;
	}
	
	/*
	@Override
	public JSONArray getMessageTypeTreeJSON() {
		List<IdocBean> list = this.getGroupedMessageTypes();
		JSONArray typeArray = new JSONArray();
		JSONObject typeObj = null;
		if(CollectionUtils.isNotEmpty(list)) {
			for(IdocBean type : list) {
				if(StringUtils.isNotBlank(type.getMessageType()) && type.getMessageType().length() >= 5) {
					typeObj = this.generateTreeNode(type.getMessageType(), type.getItemNum(), DatahubMonitorConstants.TREE_REF_TYPE, JSONArray.fromObject(NODES_PLACEHOLDER), null, null);
					typeArray.add(typeObj);
				}
			}
		}
		return typeArray;
	}
	*/
	
	private List<IdocBean> getGroupedMessageTypes() {
		Aggregation aggregation = Aggregation.newAggregation(IdocBean.class, 
				Aggregation.project(DatahubMonitorConstants.MESSAGE_TYPE), 
				Aggregation.group(DatahubMonitorConstants.MESSAGE_TYPE).count().as(DatahubMonitorConstants.ITEM_NUM), 
				Aggregation.project(DatahubMonitorConstants.MESSAGE_TYPE,DatahubMonitorConstants.ITEM_NUM).and(DatahubMonitorConstants.MESSAGE_TYPE).previousOperation());
		AggregationResults<IdocBean> results = mongoTemplate.aggregate(aggregation, Configurations.getProperty(DatahubMonitorConstants.TABLE_IDOCINFO), IdocBean.class);
		List<IdocBean> list = results.getMappedResults();
		return list;
	}

	/*
	@Override
	public JSONArray getIdocTreeJSONByType(String messageType) {
		List<IdocBean> idocs = getIdocsByType(messageType);
		return getIdocsTree(idocs);
	}
	*/
	
	private JSONArray getIdocsTree(List<IdocBean> idocs) {
		JSONArray idocArray = new JSONArray();
		if(CollectionUtils.isNotEmpty(idocs)) {
			JSONObject idocObj = null;
			JSONArray nodes = null;
			String statusColor = null;
			for(IdocBean idoc : idocs) {
				if(StatusEnum.PENDING_PUBLICATION.toString().equals(idoc.getStatus())) {
					statusColor = DatahubMonitorConstants.TREE_COLOR_YELLOW;
				}
				else if(StatusEnum.SUCCESS.toString().equals(idoc.getStatus()) || StatusEnum.SUPERCEDED.toString().equals(idoc.getStatus())) {
					statusColor = DatahubMonitorConstants.TREE_COLOR_GREEN;
				}
				else if(StatusEnum.PARTIAL_ERROR.toString().equals(idoc.getStatus()) || StatusEnum.COMPLETE_FAILURE.toString().equals(idoc.getStatus())) {
					statusColor = DatahubMonitorConstants.TREE_COLOR_RED;
				}
				if(idoc.getItemNum() > 0) {
					nodes = JSONArray.fromObject(NODES_PLACEHOLDER);
				}
				else {
					nodes = new JSONArray();
				}
				idocObj = this.generateTreeNode(idoc.getBatchPrimaryId(), idoc.getItemNum(), DatahubMonitorConstants.TREE_REF_IDOC, nodes, statusColor, null, idoc.getStatus());
				idocArray.add(idocObj);
			}
		}
		return idocArray;
	}

	@Override
	public JSONArray getCanonicalItemTreeJSONByIdoc(String messageType, String batchPrimaryId) {
		List<CanonicalItemBean> canonicalItems = this.getGroupedCanonicalItems(messageType, batchPrimaryId);
		JSONArray canonicalItemArray = new JSONArray();
		if(CollectionUtils.isNotEmpty(canonicalItems)) {
			JSONObject canonicalObj = null;
			for(CanonicalItemBean canonicalItem : canonicalItems) {
				//the total number of target items belongs to the grouped canonical item
				JSONArray nodes = new JSONArray();
				int itemNum = this.getTargetItemsByCanonicalItemType(messageType, batchPrimaryId, canonicalItem.getItemType()).size();
				if(itemNum > 0) {
					nodes = JSONArray.fromObject(NODES_PLACEHOLDER);
				}
				canonicalObj = this.generateTreeNode(canonicalItem.getItemType(), itemNum, DatahubMonitorConstants.TREE_REF_CANONICALITEM, nodes, null, null, canonicalItem.getStatus());
				canonicalItemArray.add(canonicalObj);
			}
		}
		return canonicalItemArray;
	}

	@Override
	public JSONArray getTargetItemTreeJSONByCanonical(String messageType, String batchPrimaryId, String canonicalItemType) {
		List<TargetItemBean> targetItems = getTargetItemsByCanonicalItemType(messageType, batchPrimaryId, canonicalItemType);
		JSONArray targetItemArray = new JSONArray();
		if(CollectionUtils.isNotEmpty(targetItems)) {
			JSONObject targetObj = null;
			for(TargetItemBean targetItem : targetItems) {
				targetObj = this.generateTreeNode(targetItem.getItemType(), 0, DatahubMonitorConstants.TREE_REF_TARGETITEM, null, null, targetItem.getItemId(), targetItem.getStatus());
				targetItemArray.add(targetObj);
			}
		}
		return targetItemArray;
	}

	@Override
	public List<IdocBean> getIdocsByType(String messageType) {
		Query query = new Query(Criteria.where(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType)); 
		List<IdocBean> list = mongoTemplate.find(query, IdocBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, result total:{}", new Object[] {messageType, list.size()});
		}
		return list;
	}
	
	
	public List<IdocBean> getIdocsByTypeAndStatus(String messageType, String status) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.STATUS).is(status);
		Query query = new Query(criteria); 
		List<IdocBean> list = mongoTemplate.find(query, IdocBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, status:{}, result total:{}", new Object[] {messageType, status, list.size()});
		}
		return list;
	}
	

	@Override
	public List<CanonicalItemBean> getCanonicalItemsByIdoc(String messageType, String batchPrimaryId) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		Query query = new Query(criteria); 
		List<CanonicalItemBean> list = mongoTemplate.find(query, CanonicalItemBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, batchPrimaryId{}, result total:{}", new Object[] {messageType, batchPrimaryId, list.size()});
		}
		return list;
	}
	
	
	public List<CanonicalItemBean> getCanonicalItemsByItemType(String messageType, String batchPrimaryId, String canonicalItemType) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		criteria.and(DatahubMonitorConstants.ITEM_TYPE).is(canonicalItemType);
		Query query = new Query(criteria); 
		List<CanonicalItemBean> list = mongoTemplate.find(query, CanonicalItemBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, batchPrimaryId:{}, canonicalItemType:{}, result total:{}", new Object[] {messageType, batchPrimaryId, canonicalItemType, list.size()});
		}
		return list;
	}
	
	private List<CanonicalItemBean> getGroupedCanonicalItems(String messageType, String batchPrimaryId) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		Aggregation aggregation = Aggregation.newAggregation(CanonicalItemBean.class, 
				Aggregation.match(criteria),
				Aggregation.project(DatahubMonitorConstants.ITEM_TYPE), 
				Aggregation.group(DatahubMonitorConstants.ITEM_TYPE).count().as(DatahubMonitorConstants.ITEM_NUM), 
				Aggregation.project(DatahubMonitorConstants.ITEM_TYPE, DatahubMonitorConstants.ITEM_NUM).and(DatahubMonitorConstants.ITEM_TYPE).previousOperation());
		AggregationResults<CanonicalItemBean> results = mongoTemplate.aggregate(aggregation, Configurations.getProperty(DatahubMonitorConstants.TABLE_CANONICALITEM), CanonicalItemBean.class);
		List<CanonicalItemBean> list = results.getMappedResults();
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, batchPrimaryId{}, result total:{}", new Object[] {messageType, batchPrimaryId, list.size()});
		}
		return list;
	}

	@Override
	public List<TargetItemBean> getTargetItemsByCanonicalItemType(String messageType, String batchPrimaryId,
			String canonicalItemType) {
		List<TargetItemBean> results = null;
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		criteria.and(DatahubMonitorConstants.ITEM_TYPE).is(canonicalItemType);
		Query query = new Query(criteria); 
		//get the canonical item ids first
		List<CanonicalItemBean> canonicals = mongoTemplate.find(query, CanonicalItemBean.class);
		List<Long> canonicalItemIds = new ArrayList<Long>();
		if(CollectionUtils.isNotEmpty(canonicals)) {
			for(CanonicalItemBean item : canonicals) {
				canonicalItemIds.add(item.getItemId());
			}
			Criteria criteria1 = new Criteria();
			criteria1.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
			criteria1.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
			criteria1.and(DatahubMonitorConstants.CANONICAL_ITEMID).in(canonicalItemIds);
			query = new Query(criteria1); 
			//query by canonical item ids
			results = mongoTemplate.find(query, TargetItemBean.class);
			if(logger.isDebugEnabled()) {
				logger.debug("messageType:{}, batchPrimaryId:{}, canonicalItemType:{}, result total:{}", new Object[] {messageType, batchPrimaryId, canonicalItemType, results.size()});
			}
		}
		return results;
	}
	
	@Override
	public List<TargetItemBean> getTargetItemsByCanonicalItemId(String messageType, String batchPrimaryId, Long canonicalItemId) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		criteria.and(DatahubMonitorConstants.CANONICAL_ITEMID).is(canonicalItemId);
		Query query = new Query(criteria); 
		List<TargetItemBean> results = mongoTemplate.find(query, TargetItemBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, batchPrimaryId:{}, canonicalItemId:{}, result total:{}", new Object[] {messageType, batchPrimaryId, canonicalItemId, results.size()});
		}
		return results;
	}

	@Override
	public JSONObject getIdocDetails(String messageType, String batchPrimaryId) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		Query query = new Query(criteria); 
		List<IdocBean> list = mongoTemplate.find(query, IdocBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, batchPrimaryId:{}, result total:{}", new Object[] {messageType, batchPrimaryId, list.size()});
		}
		
		if(CollectionUtils.isNotEmpty(list)) {
			JSONObject json = JSONObject.fromObject(list.get(0));
			List<CanonicalItemBean> itemList = getCanonicalItemsByIdoc(messageType, batchPrimaryId);
			json.element(DatahubMonitorConstants.ITEM_LIST, JSONArray.fromObject(itemList));
			return json;
		}
		return null;
	}

	@Override
	public JSONObject getCanonicalItemDetails(String messageType, String batchPrimaryId, Long itemId) {
		
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		criteria.and(DatahubMonitorConstants.ITEM_ID).is(itemId);
		Query query = new Query(criteria); 
		List<CanonicalItemBean> list = mongoTemplate.find(query, CanonicalItemBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, batchPrimaryId:{}, canonicalItemId:{}, result total:{}", new Object[] {messageType, batchPrimaryId, itemId, list.size()});
		}
		if(CollectionUtils.isNotEmpty(list)) {
			JSONObject json = JSONObject.fromObject(list.get(0));
			List<TargetItemBean> itemList = this.getTargetItemsByCanonicalItemId(messageType, batchPrimaryId, itemId);
			json.element(DatahubMonitorConstants.ITEM_LIST, JSONArray.fromObject(itemList));
			return json;
		}
		return null;
	}

	@Override
	public JSONObject getTargetItemDetails(String messageType, String batchPrimaryId, Long itemId) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		criteria.and(DatahubMonitorConstants.ITEM_ID).is(itemId);
		Query query = new Query(criteria); 
		List<TargetItemBean> list = mongoTemplate.find(query, TargetItemBean.class);
		if(logger.isDebugEnabled()) {
			logger.debug("messageType:{}, batchPrimaryId:{}, targetItemId:{}, result total:{}", new Object[] {messageType, batchPrimaryId, itemId, list.size()});
		}
		if(CollectionUtils.isNotEmpty(list)) {
			return JSONObject.fromObject(list.get(0));
		}
		return null;
	}

	/*
	@Override
	public JSONArray search(String text, AdvancedSearchBean advancedSearchBean) {
		List<IdocBean> list = null;
		Criteria criteria = new Criteria();
		JSONArray typeArray = new JSONArray();
		if(StringUtils.isNotBlank(text)) {
			if(StringUtils.isNotBlank(advancedSearchBean.getIdocNoFrom()) && StringUtils.isNotBlank(advancedSearchBean.getIdocNoTo())) {
				//compare range
				try {
					long idocNo = Long.parseLong(text.trim());
					long idocFrom = Long.parseLong(advancedSearchBean.getIdocNoFrom());
					long idocTo = Long.parseLong(advancedSearchBean.getIdocNoTo());
					if(idocFrom<idocNo && idocNo<idocTo) {
						criteria.and(DatahubMonitorConstants.BATCH_ID).gte(idocFrom).lte(idocTo);
						criteria.and(DatahubMonitorConstants.BATCH_ID).regex(text.trim());
					}
				}
				catch(Exception e) {
					logger.error("The format of input:{} is incorrect", text);
					return typeArray;
				}
			}
			else if(StringUtils.isNotBlank(advancedSearchBean.getIdocNoFrom())) {
				criteria.and(DatahubMonitorConstants.BATCH_ID).regex(text.trim());
			}
			criteria.and(DatahubMonitorConstants.BATCH_ID).regex(text.trim());
		}
		if(advancedSearchBean.getTimeFrom() != null) {
			criteria.and(DatahubMonitorConstants.CREATED_TIME).gte(advancedSearchBean.getTimeFrom());
		}
		if(advancedSearchBean.getTimeTo() != null) {
			criteria.and(DatahubMonitorConstants.CREATED_TIME).lte(advancedSearchBean.getTimeTo());
		}
		if (CollectionUtils.isNotEmpty(advancedSearchBean.getMsgTypeList())) {
			if(advancedSearchBean.getMsgTypeList().size() == 1) {
				criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(advancedSearchBean.getMsgTypeList().get(0));
			}
			else {
				criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).in(advancedSearchBean.getMsgTypeList());
			}
		}
		if (CollectionUtils.isNotEmpty(advancedSearchBean.getStatusList())) {
			if(advancedSearchBean.getStatusList().size() == 1) {
				criteria.and(DatahubMonitorConstants.STATUS).is(advancedSearchBean.getStatusList().get(0));
			}
			else {
				criteria.and(DatahubMonitorConstants.STATUS).in(advancedSearchBean.getStatusList());
			}
		}
		Query query = new Query(criteria); 
		list = mongoTemplate.find(query, IdocBean.class);
		if(CollectionUtils.isNotEmpty(list)) {
			Map<String, List<IdocBean>> map = getGroupedIdocsByType(list);
			typeArray = generateNavigationTree(map);
		}
		return typeArray;
	}
	*/

	private JSONArray generateNavigationTree(Map<String, List<IdocBean>> map) {
		JSONArray jsonArray = new JSONArray();
		if(map!=null && !map.isEmpty()) {
			JSONObject typeObj = null;
			List<IdocBean> idocs = null;
			for(Entry<String, List<IdocBean>> entry : map.entrySet()) {
				idocs = entry.getValue();
				Collections.sort(idocs);
				typeObj = this.generateTreeNode(entry.getKey(), entry.getValue().size(), DatahubMonitorConstants.TREE_REF_TYPE, getIdocsTree(idocs), null, null, null);
				jsonArray.add(typeObj);
			}
		}
		return jsonArray;
	}
	
	private JSONObject generateTreeNode(String text, int itemNum, String treeRef, JSONArray refNodes, String statusColor, Long itemId, String status) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.element(DatahubMonitorConstants.TEXT, text);
		jsonObj.element(DatahubMonitorConstants.ITEM_NUM, itemNum);
		jsonObj.element(DatahubMonitorConstants.TREE_REF_KEY, treeRef);
		if(refNodes != null) {
			jsonObj.element(DatahubMonitorConstants.TREE_REF_NODES, refNodes);
		}
		if(StringUtils.isNotBlank(statusColor)) {
			jsonObj.element(DatahubMonitorConstants.STATUS_COLOR, statusColor);
		}
		if(StringUtils.isNotBlank(status)) {
			jsonObj.element(DatahubMonitorConstants.STATUS, status);
		}
		if(itemId != null) {
			jsonObj.element(DatahubMonitorConstants.ITEM_ID, itemId);
		}
		return jsonObj;
	}

	private Map<String, List<IdocBean>> getGroupedIdocsByType(List<IdocBean> list) {
		Map<String, List<IdocBean>> map = null;
		if(CollectionUtils.isNotEmpty(list)) {
			map = new HashMap <String, List<IdocBean>>();
			for(IdocBean idoc : list) {
				List<IdocBean> values = map.get(idoc.getMessageType());
				if(CollectionUtils.isNotEmpty(values)) {
					values.add(idoc);
				}
				else {
					values = new ArrayList<IdocBean>();
					values.add(idoc);
					map.put(idoc.getMessageType(), values);
				}
			}
		}
		return map;
	}
	
	
	@Override
	public List<IdocBean> getIdocStatusNum() {
		Aggregation aggregation = Aggregation.newAggregation(IdocBean.class, 
				Aggregation.project(DatahubMonitorConstants.STATUS), 
				Aggregation.group(DatahubMonitorConstants.STATUS).count().as(DatahubMonitorConstants.ITEM_NUM), 
				Aggregation.project(DatahubMonitorConstants.STATUS,DatahubMonitorConstants.ITEM_NUM).and(DatahubMonitorConstants.STATUS).previousOperation());
		AggregationResults<IdocBean> results = mongoTemplate.aggregate(aggregation, Configurations.getProperty(DatahubMonitorConstants.TABLE_IDOCINFO), IdocBean.class);
		List<IdocBean> list = results.getMappedResults();
		return list;
	}

	public List<StatusEnum> getIdocStatuses() {
		return idocStatuses;
	}

	public void setIdocStatuses(List<StatusEnum> idocStatuses) {
		this.idocStatuses = idocStatuses;
	}


	@Override
	public JSONArray advancedSearch(AdvancedSearchBean advancedSearchBean) {
		Criteria criteria = new Criteria();
		List<String> tempMessageTypes = new ArrayList<String>();
		List<String> tempMessageStatuses = new ArrayList<String>();
		for(String messageType : advancedSearchBean.getMsgTypeList()){
			if(StringUtils.isNotBlank(messageType)){
				tempMessageTypes.add(messageType);
			}
		}
		for(String status : advancedSearchBean.getStatusList()){
			if(StringUtils.isNotBlank(status)){
				tempMessageStatuses.add(status);
			}
		}
		if (advancedSearchBean.getRangeSelected()) {
			if (StringUtils.isNotBlank(advancedSearchBean.getIdocNoFrom()) && StringUtils.isNotBlank(advancedSearchBean.getIdocNoTo())) {
				criteria.and(DatahubMonitorConstants.BATCH_ID).gte(advancedSearchBean.getIdocNoFrom().trim()).lte(advancedSearchBean.getIdocNoTo().trim());
			} else if (StringUtils.isNotBlank(advancedSearchBean.getIdocNoFrom())) {
				criteria.and(DatahubMonitorConstants.BATCH_ID).gte(advancedSearchBean.getIdocNoFrom().trim());
			} else if (StringUtils.isNotBlank(advancedSearchBean.getIdocNoTo())) {
				criteria.and(DatahubMonitorConstants.BATCH_ID).lte(advancedSearchBean.getIdocNoTo().trim());
			}
		} else {
			if (StringUtils.isNotBlank(advancedSearchBean.getIdocNoFrom())) {
				criteria.and(DatahubMonitorConstants.BATCH_ID).regex(advancedSearchBean.getIdocNoFrom().trim());
			}
		}
		if (CollectionUtils.isNotEmpty(tempMessageTypes)) {
			if(tempMessageTypes.size() == 1){
				criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(tempMessageTypes.get(0));
			}else{
				criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).in(tempMessageTypes);
			}
		}
		if (CollectionUtils.isNotEmpty(tempMessageStatuses)) {
			if(tempMessageStatuses.size() == 1){
				criteria.and(DatahubMonitorConstants.STATUS).is(tempMessageStatuses.get(0));
			}else{
				criteria.and(DatahubMonitorConstants.STATUS).in(tempMessageStatuses);
			}
		}
		if(advancedSearchBean.getTimeFrom() != null && advancedSearchBean.getTimeTo() != null) {
			criteria.and(DatahubMonitorConstants.CREATED_TIME).gte(advancedSearchBean.getTimeFrom()).lte(advancedSearchBean.getTimeTo());
		}
		else if(advancedSearchBean.getTimeFrom() != null) {
			criteria.and(DatahubMonitorConstants.CREATED_TIME).gte(advancedSearchBean.getTimeFrom());
		}
		else if(advancedSearchBean.getTimeTo() != null) {
			criteria.and(DatahubMonitorConstants.CREATED_TIME).lte(advancedSearchBean.getTimeTo());
		}
		Query query = new Query(criteria);
		List<IdocBean> list = mongoTemplate.find(query, IdocBean.class);
		Map<String, List<IdocBean>> map = getGroupedIdocsByType(list);
		return generateNavigationTree(map);
	}


	@SuppressWarnings("rawtypes")
	@Override
	public List getAvailableMessageTypes() {
		final List availableMessageTypes = mongoTemplate
				.getCollection(Configurations.getProperty(DatahubMonitorConstants.TABLE_CANONICALITEM))
				.distinct(DatahubMonitorConstants.MESSAGE_TYPE);

		return CollectionUtils.isNotEmpty(availableMessageTypes) ? availableMessageTypes : ListUtils.EMPTY_LIST;
	}

}