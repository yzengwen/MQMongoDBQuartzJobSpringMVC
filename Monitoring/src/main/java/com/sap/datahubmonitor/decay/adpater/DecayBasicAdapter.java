package com.sap.datahubmonitor.decay.adpater;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.sap.datahubmonitor.beans.ItemBean;
import com.sap.datahubmonitor.beans.SettingsBean;
import com.sap.datahubmonitor.beans.StatusEnum;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.decay.DecayHandler;
import com.sap.datahubmonitor.utils.Configurations;

/**
 * @author I318050
 * This class do not do 
 */
public abstract class DecayBasicAdapter implements DecayHandler {
	private static final Logger logger = LoggerFactory.getLogger(DecayBasicAdapter.class);
	
	@Resource
	protected MongoTemplate mongoTemplate;
	protected String mongoDbName;
	protected String mongoHost;
	protected String mongoPort;
	protected String mongoUser;
	protected String mongoPass;
	protected int bulkLimit;
	protected String outputFilePath;
	
	public String getOutputFilePath() {
		return outputFilePath;
	}
	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	protected static Set<String> sucessStatuses = Stream.of(StatusEnum.SUCCESS.getName(), StatusEnum.SUPERCEDED.getName())
			.collect(Collectors.toSet());
	protected static Set<String> errorStatuses = Stream
			.of(StatusEnum.PARTIAL_ERROR.getName(), StatusEnum.COMPLETE_FAILURE.getName()).collect(Collectors.toSet());
	protected static Set<String> itemSuccessStatuses = Stream.of(StatusEnum.PUBLISHED.getName(), StatusEnum.ARCHIVED.getName(),StatusEnum.NOT_PUBLISHED.getName())
			.collect(Collectors.toSet());
	protected static Set<String> itemFailStatuses = Stream.of(StatusEnum.ERROR.getName())
			.collect(Collectors.toSet());
	
	protected static Set<Class<? extends ItemBean>> itemBeanSubTypes = new Reflections("com.sap.datahubmonitor.beans")
			.getSubTypesOf(ItemBean.class);
	
	@Override
	public void handleDocuments(SettingsBean setting) {
		// TODO Auto-generated method stub

	}
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public String getMongoDbName() {
		return mongoDbName;
	}
	@Required
	public void setMongoDbName(String mongoDbName) {
		this.mongoDbName = mongoDbName;
	}

	public String getMongoHost() {
		return mongoHost;
	}
	@Required
	public void setMongoHost(String mongoHost) {
		this.mongoHost = mongoHost;
	}

	public String getMongoPort() {
		return mongoPort;
	}
	@Required
	public void setMongoPort(String mongoPort) {
		this.mongoPort = mongoPort;
	}

	public String getMongoUser() {
		return mongoUser;
	}
	@Required
	public void setMongoUser(String mongoUser) {
		this.mongoUser = mongoUser;
	}

	public String getMongoPass() {
		return mongoPass;
	}
	@Required
	public void setMongoPass(String mongoPass) {
		this.mongoPass = mongoPass;
	}
	
	public int getBulkLimit() {
		return bulkLimit;
	}
	@Required
	public void setBulkLimit(int bulkLimit) {
		this.bulkLimit = bulkLimit;
	}
	
	protected Long getSuccessPointTimestamp(SettingsBean setting, Calendar calendar){
		int days = setting.getSuccessEventDays();
		int hours = setting.getSuccessEventHours();
		int diffHours = days * 24 + hours;
		if (diffHours < 1) {
			return null;
		}
		calendar.add(Calendar.HOUR_OF_DAY, diffHours * -1);
		return calendar.getTimeInMillis();
	}
	
	protected Long getErrorPointTimestamp(SettingsBean setting, Calendar calendar){
		int days = setting.getErrorEventDays();
		int hours = setting.getErrorEventHours();
		int diffHours = days * 24 + hours;
		if (diffHours < 1) {
			return null;
		}
		calendar.add(Calendar.HOUR_OF_DAY, diffHours * -1);
		return calendar.getTimeInMillis();
	}
	
	// handle the beans which inherit ItemBean
	protected void handleIDocNoRelated(SettingsBean setting, String docNumber) {
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.BATCH_ID).is(docNumber)));
		for (Class<? extends ItemBean> itemBeanSubType : itemBeanSubTypes) {
			if (!setting.isCleanStrategy()) {
				String collectionName = itemBeanSubType.getAnnotation(Document.class).collection();
				runDumpProcessCommand(collectionName, query.getQueryObject().toString());
			}
			this.remove(setting, query, itemBeanSubType);
		}
	}
	
	protected void remove(SettingsBean setting, Query query, Class<?> entityClass){
		Instant start = Instant.now();
		if(!setting.isCleanStrategy()){
			runDumpProcessCommand(entityClass.getAnnotation(Document.class).collection(), query.getQueryObject().toString());
		}
		if(logger.isDebugEnabled()){
			logger.debug("Dump " + entityClass.toString() +" by query "+ query.toString() + System.lineSeparator() 
			+ "Duration:"+ Duration.between(start, Instant.now()).toString());
		}
		start = Instant.now();
		mongoTemplate.remove(query, entityClass);
		if(logger.isDebugEnabled()){
			logger.debug("Remove " + entityClass.toString() +" by query "+ query.toString() + System.lineSeparator() 
			+ "Duration:"+ Duration.between(start, Instant.now()).toString());
		}
	}
	
	protected void runDumpProcessCommand(String collectionName, String query) {
		query = "\""+ query.replace("\"", "'")+ "\"";
		List<String> cmdArgs;
		cmdArgs = Arrays.asList(// mongodumpCmd,
				 Configurations.getProperty(DatahubMonitorConstants.SETTINGS_MONGO_BIN_PATH), 
				"--db", mongoDbName, 
				"--collection", collectionName, 
				"--host", mongoHost, 
				"--port", mongoPort, 
				"--query", query,
				"--username", "\"" + mongoUser + "\"", 
				"--password", "\"" + mongoPass + "\"", 
				"--out", "\"" + outputFilePath + "\"",
				"--gzip"				
				);
		try {
			runProcessCommand("mongodump", cmdArgs, this.outputFilePath);
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
	
	private int runProcessCommand(String processName, List<String> cmdArgs, String outFileName)
			throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(cmdArgs);
		logger.debug("{} : {}", processName, formatToParameterString(cmdArgs));
		Process process = pb.start();
		if ("mongodump".equals(processName)) {
			logger.info("please notice that mongodump reports all dump action into stderr (not only errors)");
		}
		// TODO: test the redirect effect, if not work, process the stream
		// again.
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		// not wait when zip may cause integrity problem
		process.waitFor();
		int exitCode = process.exitValue();
		if(logger.isDebugEnabled()){
			logger.debug("Run process using "+processName+" with exit code: " + exitCode);
		}
		return exitCode;
	}
	
	private String formatToParameterString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s);
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private String getDumpPath() {
		String path = Configurations.getProperty(DatahubMonitorConstants.SETTINGS_DUMP_PATH);
		File dir = new File(path);
		// make sure dir exist // TODO: try not use
		dir.mkdirs();
		return path;
	}

	protected String getDumpFilesPath() {
		LocalDateTime now = LocalDateTime.now();
		String toSecondFormat = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ENGLISH));
		return getDumpPath() + File.separator + toSecondFormat;
	}
	
}