package com.sap.datahubmonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class QueueMessageErrorHandler implements ErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(QueueMessageErrorHandler.class);

	@Override
	public void handleError(Throwable arg0) {
		logger.error(arg0.getMessage(), arg0);
	}

}
