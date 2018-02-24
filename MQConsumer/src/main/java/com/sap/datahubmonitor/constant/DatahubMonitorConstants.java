package com.sap.datahubmonitor.constant;

public class DatahubMonitorConstants {

	//tables
	public final static String TABLE_IDOCINFO = "table.idocinfo.name";
	public final static String TABLE_RAWITEM = "table.rawitem.name";
	public final static String TABLE_CANONICALITEM = "table.canonicalitem.name";
	public final static String TABLE_TARGETITEM = "table.targetitem.name";
	public final static String TABLE_FINALSTATUS = "table.finalstatusevent.name";
	public final static String TABLE_CRONJOB = "table.cronjob.name";
	
	public final static String JOB_IDOC_GENERATION = "IdocGenerationUpdateJob";
	public final static String JOB_CANONICAL_ITEM_STATUS = "canonicalItemStatusUpdateJob";
	
	//item level events
	public final static String EVENT_RAW_ITEM = "RawItemEvent";
	public final static String EVENT_CANONICAL_ITEM = "CanonicalItemEvent";
	public final static String EVENT_TARGET_ITEM = "TargetItemEvent";
	
	//final status related events
	public final static String EVENT_ARCHIVED = "ArchivedCanonicalItemEvent";
	public final static String EVENT_NOT_PUBLISHED = "NoMorePublicationAttemptsEvent";
	public final static String EVENT_CANONICAL_PUBLICATION = "CanonicalItemPublicationStatusEvent";
	public final static String EVENT_BATCHES_RECEIVED = "BatchesReceivedEvent";
	
	public final static String VALUE_POOLNAME = "SAPCONFIGURATION_POOL";
	
	//fields for tables
	public final static String ID = "_id";
	public final static String MESSAGE_TYPE = "messageType";
	public final static String MESSAGE_SOURCE = "messageSource";
	public final static String BATCH_ID = "batchPrimaryId";
	public final static String EVENT_NAME = "eventName";
	public final static String ITEM_ID = "itemId";
	public final static String ITEM_TYPE = "itemType";
	public final static String STATUS = "status";
	public final static String CREATED_TIME = "createdTime";
	public final static String MODIFIED_TIME = "modifiedTime";
	public final static String DURATION = "duration";
	public final static String INTEGRATION_KEY = "integrationKey";
	public final static String TRACE_ID = "traceId";
	public final static String POOL_NAME = "poolName";
	public final static String EXCEPTION = "exception";
	public final static String SOURCE_IDS = "sourceIds";
	public final static String CANONICAL_ITEMID = "canonicalItemId";
	public final static String EXPORT_CODE = "exportCode";
	public final static String ITEM_NUM = "itemNum";
	public final static String JOB_NAME = "jobName";
	public final static String TIME_RECORD = "timeRecord";
	public final static String STATISTICAL_POINT = "statisticalPoint";
	public final static String AVERAGE_PROCESSING_TIME = "averageProcessingTime";
	public final static String IN_PROCESS_AMOUNT = "inProcessAmount";
	public final static String ERROR_MESSAGES = "errorMessages";
	
}
