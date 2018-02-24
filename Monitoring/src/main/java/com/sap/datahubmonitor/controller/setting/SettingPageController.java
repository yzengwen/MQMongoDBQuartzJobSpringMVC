package com.sap.datahubmonitor.controller.setting;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.datahubmonitor.beans.SettingsBean;
import com.sap.datahubmonitor.cronjob.quartz.DecayCronTriggerManager;
import com.sap.datahubmonitor.exception.ErrorResponseBean;
import com.sap.datahubmonitor.exception.MonitoringException;
import com.sap.datahubmonitor.service.DBConfigService;

@Controller
@RequestMapping("/settings")
public class SettingPageController {
	private static final Logger logger = LoggerFactory.getLogger(SettingPageController.class);
	
	@Resource
	private DBConfigService dbConfigService;
	@Resource
	private DecayCronTriggerManager decayCronTriggerManager;
	
	@RequestMapping( method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String getAllMessage() throws MonitoringException{
		SettingsBean setting = dbConfigService.getSettingsBean();
		ObjectMapper mapper = new ObjectMapper();

		//Object to JSON in String
		String jsonInString = null;
		try {
			jsonInString = mapper.writeValueAsString(setting);
		} catch (JsonGenerationException e) {
			throw new MonitoringException(e.getMessage());
		} catch (JsonMappingException e) {
			throw new MonitoringException(e.getMessage());
		} catch (IOException e) {
			if(logger.isDebugEnabled()){
				logger.debug(e.getMessage());
			}
			throw new MonitoringException(e.getMessage());
		}
		return jsonInString;
	}
	
	@RequestMapping( method = RequestMethod.POST)
	public ResponseEntity<SettingsBean> update(@RequestBody SettingsBean settings) {
		dbConfigService.saveSettingsBean(settings);
	    decayCronTriggerManager.rescheduleIntevalJob();
	    return new ResponseEntity<SettingsBean>(settings, HttpStatus.OK);
	}

}
