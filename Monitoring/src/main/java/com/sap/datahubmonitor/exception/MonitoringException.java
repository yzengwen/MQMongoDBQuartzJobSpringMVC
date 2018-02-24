package com.sap.datahubmonitor.exception;

public class MonitoringException extends Exception  {
	private static final long serialVersionUID = -7561639085006395679L;
	public MonitoringException() {
        super();
    }
    public MonitoringException(String message, Throwable cause) {
        super(message, cause);
    }
    public MonitoringException(String message) {
        super(message);
    }
    public MonitoringException(Throwable cause) {
        super(cause);
    }
}
