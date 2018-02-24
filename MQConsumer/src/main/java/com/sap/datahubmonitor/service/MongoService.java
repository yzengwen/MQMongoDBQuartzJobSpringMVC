package com.sap.datahubmonitor.service;

import java.util.List;  

import org.springframework.data.mongodb.core.query.Query;  
import org.springframework.data.mongodb.core.query.Update;  
  
public interface MongoService<T> {

	/** 
     * find by query
     *  
     * @param query 
     * @return List
     */  
    public List<T> find(Query query) ;  
  
    /** 
     * find by query
     *  
     * @param query 
     * @return T
     */  
    public T findOne(Query query) ;  
  
    /** 
     * update
     *  
     * @param query 
     * @param update 
     * @return 
     */  
    public void update(Query query, Update update) ;  
  
    /** 
     * save entity
     *  
     * @param entity 
     * @return 
     */  
    public T save(T entity) ; 
    
    /** 
     * save entity
     *  
     * @param entity 
     * @param entity collectionName 
     * @return 
     */  
    public T save(T entity, String collectionName) ; 
  
    /** 
     * get by id
     *  
     * @param id 
     * @return 
     */  
    public T findById(String id) ;  
  
    /** 
     * get by ID with collection name
     *  
     * @param id 
     * @param collectionName 
     * @return 
     */  
    public T findById(String id, String collectionName) ;  
      
    /** 
     * get total amount
     * @param query 
     * @return 
     */  
    public long count(Query query);
    
    /** 
     * get total amount
     * @param query 
     * @return 
     */ 
    public long count(Query query, String collectionName);
    
    /** 
     * remove data by query
     * @param query 
     * @param collectionName
     * @return 
     */  
    public void remove(Query query, String collectionName);
}
