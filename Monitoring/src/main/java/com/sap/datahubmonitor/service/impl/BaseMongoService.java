package com.sap.datahubmonitor.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.CommandResult;
import com.sap.datahubmonitor.service.MongoService;
import com.sap.datahubmonitor.utils.ReflectionUtils;

@Component("mongoService")
public class BaseMongoService<T> implements MongoService<T> {
	
	@Resource
	protected MongoTemplate mongoTemplate;
	
	@Override
	public List<T> find(Query query) {
		return mongoTemplate.find(query, this.getEntityClass());
	}
	@Override
	public List<T> find(Query query, String collectionName) {
		return mongoTemplate.find(query, this.getEntityClass(), collectionName);
	}
	
	@Override
	public List<T> findAll(String collectionName) {
		return mongoTemplate.findAll(this.getEntityClass(), collectionName);
	}

	@Override
	public T findOne(Query query) {
		return mongoTemplate.findOne(query, this.getEntityClass());
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
	public void update(Query query, Update update) {
		mongoTemplate.findAndModify(query, update, this.getEntityClass());
		
	}

	@Override
	public T save(T entity) {
		mongoTemplate.insert(entity);  
        return entity;
	}
	
	@Override
	public T save(T entity, String collectionName) {
		mongoTemplate.insert(entity, collectionName);  
        return entity;
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

    /*
     * only for local test
     */
	public static void main(String[] args)
	{
		ApplicationContext ctx  = new ClassPathXmlApplicationContext("/spring.xml");
		MongoTemplate mongoTemplate = (MongoTemplate) ctx.getBean("mongoTemplate");
		CommandResult cr = mongoTemplate.getDb().getStats();
		
		double size = (double)cr.get("storageSize");
		List list = mongoTemplate.findAll(new BaseMongoService().getEntityClass(), "Message");
	}
    
}
