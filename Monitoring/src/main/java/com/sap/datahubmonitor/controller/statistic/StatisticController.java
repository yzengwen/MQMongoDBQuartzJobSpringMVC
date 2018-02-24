package com.sap.datahubmonitor.controller.statistic;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.datahubmonitor.beans.StatisticInfoBean;
import com.sap.datahubmonitor.constant.DatahubMonitorConstants;
import com.sap.datahubmonitor.exception.MonitoringException;

@Controller
@RequestMapping("/statistic")
public class StatisticController {
	private static final Logger logger = LoggerFactory.getLogger(StatisticController.class);
	@Resource
	private MongoTemplate mongoTemplate;
	private static long tenMinutes = 10 * 60 * 1000L;
	private static long hour = 60 * 60 * 1000L;
	private static long day = 24 * 60 * 60 * 1000L;
	// get the data in XX minutes
	@RequestMapping(value = "/AVG", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getAVGData(@RequestParam("timemode") int timemode) throws MonitoringException {
		return getInProcessData(timemode);
	}

	// get the data in XX minutes
	@RequestMapping(value = "/inProcess", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getInProcessData(@RequestParam("timemode") int timemode) throws MonitoringException{
		TimeRangeType trt = TimeRangeType.fromInteger(timemode);
		if(trt == null){
			throw new MonitoringException("Illegal Argument with timemode:" + timemode);
		}
		Calendar calendar = Calendar.getInstance();
		long nowTimestamp = calendar.getTimeInMillis();
		long startTimestamp = getStartTimeStamp(trt, calendar);
		int numberX = 6+1;
		if(trt == TimeRangeType.DAY){
			numberX = 24*6+1;
		}else if(trt == TimeRangeType.WEEK){
			numberX = 7*24*6+1;
		}else if(trt == TimeRangeType.MONTH){
			numberX= 30*24*6+1;
		}
		
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(DatahubMonitorConstants.STATISTICAL_POINT).gte(startTimestamp)))
				.with(new Sort(new Order(Direction.ASC, DatahubMonitorConstants.STATISTICAL_POINT)));
		List<StatisticInfoBean> statisticInfos = (List<StatisticInfoBean>) mongoTemplate.find(query,
				StatisticInfoBean.class);
		replenishXCoordinate(statisticInfos, startTimestamp, numberX);
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		try {
			jsonInString = mapper.writeValueAsString(statisticInfos);
		} catch (JsonGenerationException e) {
			logger.error(e.getMessage());
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return jsonInString;
	}

	// note: statisticInfos must be sorted as Asc!
	private void replenishXCoordinate(List<StatisticInfoBean> statisticInfos, long startXTimeStamp, int xNumber) {
		// first X
		long targetX = startXTimeStamp;
		ListIterator<StatisticInfoBean> listIterator = statisticInfos.listIterator();

		while (listIterator.hasNext()) {
			StatisticInfoBean statisticInfo = listIterator.next();
			long currentX = statisticInfo.getStatisticalPoint();
			if (targetX < currentX) {
				int count = (int)((currentX - targetX) / tenMinutes);
				for (int i = 0; i < count; i++) {
					StatisticInfoBean bean = new StatisticInfoBean();
					bean.setAverageProcessingTime(0);
					bean.setInProcessAmount(0);
					bean.setStatisticalPoint(targetX + i * tenMinutes);
					listIterator.add(bean);
				}
				// next check point
				targetX = currentX + tenMinutes*(count +1);
			}
			else if(targetX == currentX){
				targetX = targetX+tenMinutes;
			}

		}
		// append at end if not enough yet
		if(statisticInfos.size()<xNumber){
			int count = xNumber - statisticInfos.size();
			for (int i = 0; i < count; i++) {
				StatisticInfoBean bean = new StatisticInfoBean();
				bean.setAverageProcessingTime(0);
				bean.setInProcessAmount(0);
				bean.setStatisticalPoint(targetX + i * tenMinutes);
				listIterator.add(bean);
			}
		}

	}
	private long getStartTimeStamp(TimeRangeType type, Calendar calendar) {
		if(type ==TimeRangeType.HOUR ){
			calendar.add(Calendar.MINUTE, (-1 * 60));
			long timestamp = calendar.getTimeInMillis();
			long startTimeStamp = timestamp - (timestamp % tenMinutes) ;
			return startTimeStamp;
		}
		else if(type ==TimeRangeType.DAY ){
			calendar.add(Calendar.HOUR, (-1 * 24));
			long timestamp = calendar.getTimeInMillis();
			long startTimeStamp = timestamp - (timestamp % hour) + hour;
			return startTimeStamp;
		}
		else if(type ==TimeRangeType.WEEK ){
			calendar.add(Calendar.MINUTE, (-1 * 60 * 24 * 7));
			long timestamp = calendar.getTimeInMillis();
			long startTimeStamp = timestamp - (timestamp % day) + day;
			return startTimeStamp;
		}
		else if(type ==TimeRangeType.MONTH){
			calendar.add(Calendar.MINUTE, (-1 * 60 * 24 * 30));
			long timestamp = calendar.getTimeInMillis();
			long startTimeStamp = timestamp - (timestamp % day) + day;
			return startTimeStamp;
		}
		else
			return 0;
	}
}
