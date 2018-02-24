package com.sap.datahubmonitor.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.sap.datahubmonitor.beans.CanonicalItemBean;
import com.sap.datahubmonitor.beans.FinalStatusEventBean;
import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.beans.StatisticInfoBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.utils.Configurations;
import com.sap.datahubmonitor.utils.StatusCalculationUtils;

import net.sf.json.JSONObject;
@Component("datahubConsumerService")
public class DefaultDatahubConsumerService implements DatahubConsumerService{
	@Resource
	protected MongoTemplate mongoTemplate;
	private final static String SEPARATOR = "@@@";
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultDatahubConsumerService.class);

	@Override
	public Map<String, JSONObject> getFinalStatusEvents(Long timestamp) {
		Query query = new Query(Criteria.where(DatahubMonitorConstants.MODIFIED_TIME).gte(timestamp));
		// query.with(new Sort(new Order(Direction.DESC,"timestamp")));
		List<FinalStatusEventBean> result = mongoTemplate.find(query, FinalStatusEventBean.class);
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		for (FinalStatusEventBean temp : result) {
			JSONObject key = new JSONObject();
			key.element(DatahubMonitorConstants.MESSAGE_TYPE, temp.getMessageType() == null ? "null" : temp.getMessageType());
			key.element(DatahubMonitorConstants.BATCH_ID,
					temp.getBatchPrimaryId() == null ? "null" : temp.getBatchPrimaryId());
			key.element(DatahubMonitorConstants.ITEM_ID, temp.getItemId() == null ? "null" : temp.getItemId());
			key.element(DatahubMonitorConstants.ITEM_TYPE, temp.getItemType() == null ? "null" : temp.getItemType());
			String keyString = key.toString();
			if (map.get(keyString) != null) {
				if (temp.getCreatedTime() >= map.get(keyString).getLong(DatahubMonitorConstants.CREATED_TIME)) {
					JSONObject value = new JSONObject();
					value.element(DatahubMonitorConstants.MODIFIED_TIME, temp.getModifiedTime());
					value.element(DatahubMonitorConstants.CREATED_TIME, temp.getCreatedTime());
					value.element(DatahubMonitorConstants.STATUS, temp.getStatus());
					map.put(key.toString(), value);
				}
			} else {
				JSONObject value = new JSONObject();
				value.element(DatahubMonitorConstants.MODIFIED_TIME, temp.getModifiedTime());
				value.element(DatahubMonitorConstants.CREATED_TIME, temp.getCreatedTime());
				value.element(DatahubMonitorConstants.STATUS, temp.getStatus());
				map.put(key.toString(), value);
			}
		}
		return map;
	}

	@Override
	public void updateErrorMessages(Long timestamp) {
		Query query = new Query(Criteria.where(DatahubMonitorConstants.MODIFIED_TIME).gte(timestamp));
		List<FinalStatusEventBean> result = mongoTemplate.find(query, FinalStatusEventBean.class);
		for (FinalStatusEventBean temp : result) {
			if (DatahubMonitorConstants.EVENT_CANONICAL_PUBLICATION.equals(temp.getEventName())) {
				this.updateStatusForCanonicalItem(temp.getMessageType(), temp.getBatchPrimaryId(), temp.getItemId(),
						temp.getItemType(), temp.getStatus(), temp.getErrorMessages());
			}
		}
	}

	@Override
	public void updateStatusForCanonicalItems(Map<String, JSONObject> map) {
		JSONObject jobDetail = this.getJobDetails(DatahubMonitorConstants.JOB_CANONICAL_ITEM_STATUS);
		Long timeRecord = 0L;
		if(jobDetail != null && !jobDetail.isNullObject() && !jobDetail.isEmpty()) {
			timeRecord = jobDetail.getLong(DatahubMonitorConstants.TIME_RECORD);
		}
		for(String temp : map.keySet()){
			JSONObject key = JSONObject.fromObject(temp);
			String messageType = key.getString(DatahubMonitorConstants.MESSAGE_TYPE);
			String batchPrimaryId = key.getString(DatahubMonitorConstants.BATCH_ID);
			Long itemId = key.getLong(DatahubMonitorConstants.ITEM_ID);
			String itemType = key.getString(DatahubMonitorConstants.ITEM_TYPE);
			JSONObject value = map.get(temp);
			if(value.get(DatahubMonitorConstants.STATUS) == null){
				logger.error("status is null, continue to next item.");
				continue;
			}
			String status = value.getString(DatahubMonitorConstants.STATUS);
			//record the time stamp before changed
			Long timestamp = value.getLong(DatahubMonitorConstants.MODIFIED_TIME);
			if(timestamp > timeRecord){
				timeRecord = timestamp;
			}
			this.updateStatusForCanonicalItem(messageType, batchPrimaryId, itemId, itemType, status, null);
		}
		JSONObject jobUpdated = new JSONObject();
		jobUpdated.element(DatahubMonitorConstants.JOB_NAME, DatahubMonitorConstants.JOB_CANONICAL_ITEM_STATUS);
		jobUpdated.element(DatahubMonitorConstants.TIME_RECORD, timeRecord);
		this.saveJobDetails(jobUpdated);
	}
	
	private void updateStatusForCanonicalItem(String messageType, String batchPrimaryId, Long itemId, String itemType, String status, List<String> errorMessages) {
		logger.info("messageType= " + messageType + " ; batchPrimaryId= " + batchPrimaryId + " ; itemId= " + itemId + " ; itemType= " + itemType);
		if(status == null || "null".equals(status)){
			logger.error("status is null, continue to next item.");
			return;
		}
		Criteria criteria = new Criteria();
		if(!"null".equals(messageType)){
			criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		}
		if(!"null".equals(batchPrimaryId)){
			criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		}
		if(!"null".equals(itemId)){
			criteria.and(DatahubMonitorConstants.ITEM_ID).is(itemId);
		}
		if(!"null".equals(itemType)){
			criteria.and(DatahubMonitorConstants.ITEM_TYPE).is(itemType);
		}
		Query query = new Query(criteria);
		List<CanonicalItemBean> result = mongoTemplate.find(query, CanonicalItemBean.class);
		if(result.size() != 1){
			logger.error("DATA issues, can not locate to one Canonical Item according messageType, batchPrimaryId, itemId, itemType");
			return;
		}
		CanonicalItemBean canonicalItem = result.get(0);
		canonicalItem.setStatus(status);
		canonicalItem.setModifiedTime(Instant.now().toEpochMilli());
		canonicalItem.setErrorMessages(errorMessages);
		mongoTemplate.save(canonicalItem);
	}

	@Override
	public List<CanonicalItemBean> getCanonicalItems(final Long timestamp) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MODIFIED_TIME).gte(timestamp);
		Query query = new Query(criteria); 
		//query.with(new Sort(new Order(Direction.DESC, DatahubMonitorConstants.MODIFIED_TIME)));
		List<CanonicalItemBean> list = mongoTemplate.find(query, CanonicalItemBean.class, Configurations.getProperty(DatahubMonitorConstants.TABLE_CANONICALITEM));
		return list;
	}

	@Override
	public List<CanonicalItemBean> getIdocCanonicalItems(final String messageType, final String batchPrimaryId) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		Query query = new Query(criteria); 
		query.with(new Sort(new Order(Direction.DESC, DatahubMonitorConstants.CREATED_TIME)));
		List<CanonicalItemBean> list = mongoTemplate.find(query, CanonicalItemBean.class, Configurations.getProperty(DatahubMonitorConstants.TABLE_CANONICALITEM));
		return list;
	}

	@Override
	public Map<String, List<CanonicalItemBean>> getGroupedCanonicalItems(final List<CanonicalItemBean> list) {
		Map<String, List<CanonicalItemBean>> map = new HashMap<String, List<CanonicalItemBean>>();
		if(CollectionUtils.isNotEmpty(list)) {
			String batchId = null;
			String messageType = null;
			List<CanonicalItemBean> itemList = null;
			for(CanonicalItemBean canonicalItem : list) {
				batchId = canonicalItem.getBatchPrimaryId();
				messageType = canonicalItem.getMessageType();
				String key = messageType + SEPARATOR + batchId;
				if(map.containsKey(key)) {
					itemList = map.get(key);
					itemList.add(canonicalItem);
				}
				else {
					itemList = new ArrayList<CanonicalItemBean>();
					itemList.add(canonicalItem);
					map.put(key, itemList);
				}
			}
		}
		return map;
	}

	@Override
	public IdocBean getIdocInfo(final String messageType, final String batchPrimaryId) {
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
		criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
		Query query = new Query(criteria); 
		//query.with(new Sort(new Order(Direction.ASC, DatahubMonitorConstants.CREATED_TIME)));
		List<IdocBean> list = mongoTemplate.find(query, IdocBean.class, Configurations.getProperty(DatahubMonitorConstants.TABLE_IDOCINFO));
		if(logger.isDebugEnabled()) {
			logger.debug("Find {} Idocs by criterias messageType:{}, batchPrimaryId:{}", new Object[]{list.size(), messageType, batchPrimaryId});
			if(list.size()==0) {
				logger.debug("Will create IdocInfo with messageType:{}, batchPrimaryId:{}", new Object[]{messageType, batchPrimaryId});
			}
		}
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public List<IdocBean> getIdocInfos(final Long startTime, final Long endTime) {
		Query query = new Query();
		query.fields().include(DatahubMonitorConstants.STATUS);
		query.fields().include(DatahubMonitorConstants.DURATION);
		
		final Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.MODIFIED_TIME).lte(endTime).gte(startTime);
		query.addCriteria(criteria);
		
		
		final List<IdocBean> list = mongoTemplate.find(query, IdocBean.class, Configurations.getProperty(DatahubMonitorConstants.TABLE_IDOCINFO));
		logger.debug("Find {} Idocs by criterias startTime:{}, endTime:{}", new Object[]{list.size(), startTime, endTime});
		
		return list;
	}

	@Override
	public void generateIdocInfos(final Map<String, List<CanonicalItemBean>> map) {
		if(map!=null && !map.isEmpty()) {
			for(Entry<String, List<CanonicalItemBean>> entry :map.entrySet()) {
				List<CanonicalItemBean> itemList = entry.getValue();
				Collections.sort(itemList);
				String key = entry.getKey();
				String messageType = key.split(SEPARATOR)[0];
				if(StringUtils.isBlank(messageType)) {
					messageType = null;
				}
				String batchPrimaryId = key.split(SEPARATOR)[1];
				if(StringUtils.isBlank(batchPrimaryId)) {
					batchPrimaryId = null;
				}
				IdocBean idoc = getIdocInfo(messageType, batchPrimaryId);
				//create new scenario
				if(idoc == null) {
					idoc = new IdocBean();
					idoc.setMessageType(messageType);
					idoc.setBatchPrimaryId(batchPrimaryId);
					idoc.setItemNum(itemList.size());
					//use the createTime of the first element
					idoc.setCreatedTime(itemList.get(0).getCreatedTime());
					idoc.setPoolName(itemList.get(0).getPoolName());
					idoc.setException(itemList.get(0).getException());
					idoc.setStatus(StatusCalculationUtils.calculateStatusForIdoc(itemList).getName());
					idoc.setModifiedTime(Instant.now().toEpochMilli());
					idoc.setDuration(Duration.between(Instant.ofEpochMilli(itemList.get(0).getCreatedTime()), Instant.now()).getSeconds());
					mongoTemplate.save(idoc);
					if(logger.isDebugEnabled()) {
						logger.debug("Idoc created with messageType:{}, batchPrimaryId:{}", new Object[]{messageType, batchPrimaryId});
					}
				}
				//existing scenario
				else {
					itemList = getIdocCanonicalItems(messageType, batchPrimaryId);
					Criteria criteria = new Criteria();
					criteria.and(DatahubMonitorConstants.MESSAGE_TYPE).is(messageType);
					criteria.and(DatahubMonitorConstants.BATCH_ID).is(batchPrimaryId);
					
					final Update update = new Update();
					update.set(DatahubMonitorConstants.ITEM_NUM, itemList.size());
					update.set(DatahubMonitorConstants.STATUS, StatusCalculationUtils.calculateStatusForIdoc(itemList).getName());
					update.set(DatahubMonitorConstants.MODIFIED_TIME, Instant.now().toEpochMilli());
					update.set(DatahubMonitorConstants.DURATION, Duration.between(Instant.ofEpochMilli(idoc.getCreatedTime()), Instant.now()).getSeconds());
					
					mongoTemplate.upsert(new Query(criteria), update, IdocBean.class);
					if(logger.isDebugEnabled()) {
						logger.debug("Idoc with messageType:{}, batchPrimaryId:{} updated.", new Object[]{messageType, batchPrimaryId});
					}
				}
			}
		}
	}
	

	@Override
	public JSONObject getJobDetails(String jobName) {
		Query query = new Query(Criteria.where("jobName").is(jobName));
		Object result = mongoTemplate.findOne(query, Object.class, Configurations.getProperty(DatahubMonitorConstants.TABLE_CRONJOB));
		return JSONObject.fromObject(result);
	}
	@Override
	public void saveJobDetails(JSONObject jobDetails) {
		Query query = new Query(Criteria.where("jobName").is(jobDetails.getString("jobName")));
		mongoTemplate.remove(query, Configurations.getProperty(DatahubMonitorConstants.TABLE_CRONJOB));
		mongoTemplate.insert(jobDetails, Configurations.getProperty(DatahubMonitorConstants.TABLE_CRONJOB));
	}
	

	@Override
	public void upsertStatisticInfo(final StatisticInfoBean statisticInfo) {
		if (statisticInfo == null) {
			logger.debug("The given statistic info is NULL");
		} else {
			final Query query = new Query(Criteria.where(DatahubMonitorConstants.STATISTICAL_POINT).is(statisticInfo.getStatisticalPoint()));
			
			final Update update = new Update();
			update.set(DatahubMonitorConstants.AVERAGE_PROCESSING_TIME, statisticInfo.getAverageProcessingTime());
			update.set(DatahubMonitorConstants.IN_PROCESS_AMOUNT, statisticInfo.getInProcessAmount());
			
			mongoTemplate.upsert(query, update, StatisticInfoBean.class);
		}
	}
	
	/*
	public static void main(String[] args) {
		ApplicationContext ctx  = new ClassPathXmlApplicationContext("/applicationContext.xml");
		MongoTemplate mongoTemplate = (MongoTemplate) ctx.getBean("mongoTemplate");
		Criteria criteria = new Criteria();
		criteria.and(DatahubMonitorConstants.CREATED_TIME).gte(Long.valueOf("1491898111883"));
		Query query = new Query(criteria); 
		query.with(new Sort(new Order(Direction.ASC, DatahubMonitorConstants.CREATED_TIME)));
		List<CanonicalItemBean> list = mongoTemplate.find(query, CanonicalItemBean.class, Configurations.getProperty(DatahubMonitorConstants.TABLE_CANONICALITEM));
	}
	*/
}
