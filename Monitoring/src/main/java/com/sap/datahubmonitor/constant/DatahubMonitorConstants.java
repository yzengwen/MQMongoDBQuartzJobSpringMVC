package com.sap.datahubmonitor.constant;

public class DatahubMonitorConstants {

	//**************Settings page part**************
	public final static String SETTINGS_TRIGGER_NAME = "setting.decay.tringger";
	public final static String SETTINGS_TRIGGER_CRON_NAME = "setting.decay.cron.tringger";
	public final static String SETTINGS_DUMP_PATH = "setting.mongo.dump.path";
	public final static String SETTINGS_MONGO_BIN_PATH = "setting.mongo.binpath";
	public final static String SETTINGS_MONGO_ROOT_PATH = "setting.mongo.diskrootpath";
	
	public final static String JOB_IDOC_GENERATION = "IdocGenerationUpdateJob";
	public final static String JOB_CANONICAL_ITEM_STATUS = "updateStatus";
	public final static String JOB_DECAY_NAME= "decay";
	
	//**************Event Analyze part**************
	
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
	
	//**************Frontend part**************
	//search page fields
	public final static String IDOCNO_FROM = "idocNoFrom";
	public final static String IDOCNO_TO = "idocNoTo";
	public final static String MESSAGE_TYPES = "messageTypes";
	public final static String MESSAGE_STATUSES = "messageStatuses";
	public final static String POSTING_TIME_FROM = "postingTimeFrom";
	public final static String POSTING_TIME_TO = "postingTimeTo";
	//text presentation for tree
	public final static String TEXT = "text";
	//icons for the nav tree
	public final static String TREE_REF_KEY = "ref";
	public final static String TREE_REF_NODES = "nodes";
	public final static String TREE_REF_TYPE = "sap-icon://tree";
	public final static String TREE_REF_IDOC = "sap-icon://documents";
	public final static String TREE_REF_CANONICALITEM = "sap-icon://document";
	public final static String TREE_REF_TARGETITEM = "sap-icon://activity-assigned-to-goal";
	//colors for idocs
	public final static String TREE_COLOR_GREEN = "#2b7d2b";
	public final static String TREE_COLOR_YELLOW = "#e78c07";
	public final static String TREE_COLOR_RED = "#bb0000";
	
	//**************MongoDb part**************
	//tables
	public final static String TABLE_IDOCINFO = "table.idocinfo.name";
	public final static String TABLE_RAWITEM = "table.rawitem.name";
	public final static String TABLE_CANONICALITEM = "table.canonicalitem.name";
	public final static String TABLE_TARGETITEM = "table.targetitem.name";
	public final static String TABLE_FINALSTATUS = "table.finalstatusevent.name";
	public final static String TABLE_CRONJOB = "table.cronjob.name";
	
	//fields for tables
	public final static String ID = "_id";
	public final static String MESSAGE_TYPE = "messageType";
	public final static String MESSAGE_SOURCE = "messageSource";
	public final static String BATCH_ID = "batchPrimaryId";
	public final static String EVENT_NAME = "eventName";
	public final static String ITEM_ID = "itemId";
	public final static String ITEM_TYPE = "itemType";
	public final static String STATUS = "status";
	public final static String STATUS_COLOR = "statusColor";
	public final static String CREATED_TIME = "createdTime";
	public final static String MODIFIED_TIME = "modifiedTime";
	public final static String DURATION = "duration";
	public final static String START_TIME = "startTime";
	public final static String STATISTICAL_POINT = "statisticalPoint";
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
	public final static String ERROR_MESSAGES = "errorMessages";
	//table_field
	public final static String CronjobConfigBean_JobName = "jobName";
	public final static String DB_STORAGE_SIZE = "storageSize";
	public final static String DB_DATA_SIZE = "dataSize";
	
	//fields for presentation
	//public final static String TOTAL_NUM = "total";
	public final static String ITEM_LIST = "itemList";
	public final static String CANONICAL_ITEMTYPE = "canonicalItemType";
	
	//**************Session instants part**************
	//session field
	public final static String ADVANCED_SEARCH_BEAN = "AdvancedSearchBean";
	
}
