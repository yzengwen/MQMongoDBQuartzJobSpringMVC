package com.sap.datahubmonitor.service;

import java.util.List;  

import org.springframework.data.mongodb.core.query.Query;  
import org.springframework.data.mongodb.core.query.Update;  
  
public interface MongoService<T> {

    public List<T> find(Query query);  
    public List<T> find(Query query, String collectionName);  
    public T findOne(Query query);
    public List<T> findAll(String collectionName);
    public T findById(String id);  
    public T findById(String id, String collectionName); 
    public void update(Query query, Update update);  
    public T save(T entity); 
    public T save(T entity, String collectionName); 
    public long count(Query query);
}
