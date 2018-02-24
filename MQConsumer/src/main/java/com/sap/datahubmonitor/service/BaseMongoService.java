package com.sap.datahubmonitor.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.sap.datahubmonitor.utils.ReflectionUtils;

@Component("mongoService")
public class BaseMongoService<T> implements MongoService<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseMongoService.class);
	
	@Resource
	protected MongoTemplate mongoTemplate;
	
	@Override
	public List<T> find(Query query) {
		return mongoTemplate.find(query, this.getEntityClass());
	}

	@Override
	public T findOne(Query query) {
		return mongoTemplate.findOne(query, this.getEntityClass());
	}

	@Override
	public void update(Query query, Update update) {
		mongoTemplate.findAndModify(query, update, this.getEntityClass());
		
	}

	@Override
	public T save(T entity) {
		mongoTemplate.insert(entity);  
        return entity;
	}

	@Override
	public T findById(String id) {
		return mongoTemplate.findById(id, this.getEntityClass());
	}

	@Override
	public T findById(String id, String collectionName) {
		return mongoTemplate.findById(id, this.getEntityClass(), collectionName);
	}

	@Override
	public long count(Query query) {
		return mongoTemplate.count(query, this.getEntityClass());
	}

	/** 
     * get the class of persist object 
     *  
     * @return 
     */  
    private Class<T> getEntityClass(){  
        return ReflectionUtils.getSuperClassGenricType(getClass());
    }

	@Override
	public T save(T entity, String collectionName) {
		mongoTemplate.insert(entity, collectionName);
		if(logger.isDebugEnabled()){
			logger.debug("Entity insert completed {collectionName, entity}", new Object[] {collectionName, entity});
		}
        return entity;
	}

	@Override
	public void remove(Query query, String collectionName) {
		mongoTemplate.remove(query, collectionName);
	}

	@Override
	public long count(Query query, String collectionName) {
		return mongoTemplate.count(query, collectionName);
	}

    
}
