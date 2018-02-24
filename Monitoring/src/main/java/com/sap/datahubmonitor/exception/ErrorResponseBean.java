package com.sap.datahubmonitor.exception;

public class ErrorResponseBean {
    String errorMessage;
    int errorCode;
    
	public ErrorResponseBean(Exception ex) {
		errorMessage = ex.getMessage();
	}
	
	public ErrorResponseBean(String msg) {
		errorMessage = msg;
	}

	public ErrorResponseBean(String msg, int errorCode) {
		errorMessage = msg;
		this.errorCode = errorCode;
	}
    
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

    
}
