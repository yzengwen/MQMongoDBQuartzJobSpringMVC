package com.sap.datahubmonitor.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configurations {

	private static final Logger logger = LoggerFactory.getLogger(Configurations.class);
	private static Properties properties = new Properties();

	static{
        try {
            InputStreamReader fileReader = new InputStreamReader(Configurations.class.getResourceAsStream("/config.properties"));
            properties.load(fileReader);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
}
