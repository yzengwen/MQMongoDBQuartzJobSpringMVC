package com.sap.datahubmonitor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.service.MongoService;
import com.sap.datahubmonitor.utils.Configurations;


@Controller
@RequestMapping("/sample")
public class SampleController {
	
	@Resource(name="mongoService")
	private MongoService<String> mongoService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String test(@RequestParam(value = "city", required = false) final String cityCode, final Model model)
	{
		return "This is a sample";
	}
	
	@RequestMapping(value = "/sample", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getAllMessage() {
		
		List<String> results = mongoService.findAll(Configurations.getProperty(DatahubMonitorConstants.TABLE_IDOCINFO));
		return results.toString();
	}
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public ModelAndView getCECResults(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		results.put("result", "Hello world - page");
		return new ModelAndView("/hello", results);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		model.addAttribute("result", "login method");
		return "/hello";
	}

	@RequestMapping(value = "/{path}", method = RequestMethod.GET)
	public String allRequestHandler(@PathVariable String path, ModelMap model) {
		model.addAttribute("result", "come back from path variable method, path=" + path);
		return "/hello";
	}
	
	// need to add dependency jackson
	@RequestMapping(value = "/inbound", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getInboundListAsJSON() {
		MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase database = mongoClient.getDatabase("datahubMonitor");
		MongoCollection<Document> collection = database.getCollection("inbound");
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> cursor = findIterable.iterator();
		List<String> inboundList = new ArrayList<String>();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			inboundList.add(doc.toJson());
		}
		mongoClient.close();
		
		return inboundList.toString();
	}
	
	@RequestMapping(value = "/firstpage1", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getFirstPageAsJSON() {

		MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase database = mongoClient.getDatabase("dhm_db");
		BsonDocument paramBson = new BsonDocument("eval", new BsonString("getFirstPage()"));
		Document doc = database.runCommand(paramBson);
		mongoClient.close();
		

		
		return doc.toJson();
		
//		MongoClient mongoClient = new MongoClient("localhost", 27017);
//		DB db = mongoClient.getDB("Message");
//		db.to
//		CommandResult result= db.command("db.MyCollection.find()");
//		JSONObject resultJson = new JSONObject(result.toString());
//		System.out.println(resultJson.toString(4));
//		
//		final DBObject command = new BasicDBObject();
//	    command.put("eval", "function() { return db." + collectionName + ".find(); }");
//		//JSONObject resultJson = new JSONObject(result.toString());
//		//System.out.println(resultJson.toString(4));
//	    return resultJson.toString(4);
	}
	
	
}


