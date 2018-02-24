package com.sap.datahubmonitor.beans;

public class User {
	
	private String name;
	private int age;
	private String sex;
	private String[] book;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	public String[] getBook() {
		return book;
	}

	public void setBook(String[] book) {
		this.book = book;
	}
	
}
