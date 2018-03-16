package com.sap.datahubmonitor.utils;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;  

public class MongoDbTest {  

	public MongoDatabase getDataBase(String dbName) {
		MongoDatabase database = null;
		try {
			ServerAddress serverAddress = new ServerAddress("localhost", 27017);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            addrs.add(serverAddress);
            MongoClient mongoClient = new MongoClient(addrs);
            if(mongoClient != null){
                database = mongoClient.getDatabase(dbName);
                System.out.println("Connect to database successfully");
            }
		} catch(Exception e) {
			e.printStackTrace();
		}
		return database;
	}
	
	public DB getDB(String dbName) {
		DB db = null;
		try {
			ServerAddress serverAddress = new ServerAddress("localhost", 27017);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            addrs.add(serverAddress);
            MongoClient mongoClient = new MongoClient(addrs);
            if(mongoClient != null){
                db = mongoClient.getDB(dbName);
                System.out.println("Connect to database successfully");
            }
		} catch(Exception e) {
			e.printStackTrace();
		}
		return db;
	}
	
	private void insertOneDoc() {
		MongoDatabase dataBase = getDataBase("test");
		MongoCollection collection = dataBase.getCollection("mycol");
		System.out.println(collection.count());
		collection.insertOne(createDocument());
		System.out.println(collection.count());
	}
	
	
	private FindIterable<Document> findAll(String collectionName){
		MongoDatabase dataBase = getDataBase("test");
		FindIterable<Document> docs = dataBase.getCollection(collectionName).find();
		return docs;
	}
	
	private void printWithCursor() {
		DB db = getDB("test");
		System.out.println(db.getCollection("mycol").findOne().toString());
		DBCursor cursor = db.getCollection("mycol").find();
		while(cursor.hasNext()){  
			DBObject obj = cursor.next();  
	        System.out.println(obj.toString());  
	    }  
		
	}
	
	private Document createDocument() {
		Document doc = new Document("name", "MongoDB")
				.append("type", "database")
				.append("count", 1)
				.append("info",new Document("x", 203)
						.append("y", 102));
		return doc;
	}
	
	public static void main(String[] args) {
		MongoDbTest dbTest = new MongoDbTest();
		
		dbTest.insertOneDoc();
		FindIterable<Document> docs = dbTest.findAll("mycol");
		for (Document document : docs) {
			System.out.println(document.toJson());
		}
		
		// print with cursor
		dbTest.printWithCursor();
		
	}
}