package com.sap.datahubmonitor.beans;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="CronjobConfig")
public class CronjobConfigBean {
	private int frequencyHour;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private String jobName;
	public int getFrequencyHour() {
		return frequencyHour;
	}
	public void setFrequencyHour(int frequencyHour) {
		this.frequencyHour = frequencyHour;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
}
