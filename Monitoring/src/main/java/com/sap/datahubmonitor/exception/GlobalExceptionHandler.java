package com.sap.datahubmonitor.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(MonitoringException.class)
	public  ResponseEntity<ErrorResponseBean> handleMonitoringException(HttpServletRequest request, MonitoringException ex){
		logger.error("MonitoringException occured");
		
		return new ResponseEntity<ErrorResponseBean>(new ErrorResponseBean(ex.getMessage()), HttpStatus.EXPECTATION_FAILED);
	}

}