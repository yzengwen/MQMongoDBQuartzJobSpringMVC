package com.sap.datahubmonitor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx  = new ClassPathXmlApplicationContext("/applicationContext.xml");
	}

}
