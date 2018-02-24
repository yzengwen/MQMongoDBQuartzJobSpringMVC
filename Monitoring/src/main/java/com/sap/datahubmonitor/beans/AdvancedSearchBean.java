package com.sap.datahubmonitor.beans;

import java.util.List;

public class AdvancedSearchBean {
	private String idocNoFrom ;
	private String idocNoTo ;
	private List<String> msgTypeList ;
	private List<String> statusList ;
	private Long timeFrom;
	private Long timeTo;
	private Boolean rangeSelected;
	private Boolean singleSelected;
	public String getIdocNoFrom() {
		return idocNoFrom;
	}
	public void setIdocNoFrom(String idocNoFrom) {
		this.idocNoFrom = idocNoFrom;
	}
	public String getIdocNoTo() {
		return idocNoTo;
	}
	public void setIdocNoTo(String idocNoTo) {
		this.idocNoTo = idocNoTo;
	}
	public List<String> getMsgTypeList() {
		return msgTypeList;
	}
	public void setMsgTypeList(List<String> msgTypeList) {
		this.msgTypeList = msgTypeList;
	}
	public List<String> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}
	public Long getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(Long timeFrom) {
		this.timeFrom = timeFrom;
	}
	public Long getTimeTo() {
		return timeTo;
	}
	public void setTimeTo(Long timeTo) {
		this.timeTo = timeTo;
	}
	public Boolean getRangeSelected() {
		return rangeSelected;
	}
	public void setRangeSelected(Boolean rangeSelected) {
		this.rangeSelected = rangeSelected;
	}
	public Boolean getSingleSelected() {
		return singleSelected;
	}
	public void setSingleSelected(Boolean singleSelected) {
		this.singleSelected = singleSelected;
	}
}
