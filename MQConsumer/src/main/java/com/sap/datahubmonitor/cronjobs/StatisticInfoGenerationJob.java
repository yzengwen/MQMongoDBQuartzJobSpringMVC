package com.sap.datahubmonitor.cronjobs;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.datahubmonitor.beans.IdocBean;
import com.sap.datahubmonitor.beans.StatisticInfoBean;
import com.sap.datahubmonitor.beans.StatusEnum;
import com.sap.datahubmonitor.service.DatahubConsumerService;

public class StatisticInfoGenerationJob {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticInfoGenerationJob.class);

	private boolean enable;

	private int amountToSubtract;

	@Resource
	private DatahubConsumerService datahubConsumerService;

	public void run() {
		LOG.info("StatisticInfoGenerationJob start to process at {}.", Instant.now());
		if (!enable) {
			LOG.info(
					"StatisticInfoGenerationJob is disabled, if you want to run this cronjob, please set [enable] flag to true.");
			return;
		}

		final Instant statisticalPoint = calculateStatisticalPoint();
		LOG.debug("The statistical point is {}", statisticalPoint.toEpochMilli());
		final List<IdocBean> idocInfos = datahubConsumerService.getIdocInfos(
				statisticalPoint.minus(amountToSubtract, ChronoUnit.MINUTES).toEpochMilli(),
				statisticalPoint.toEpochMilli());

		if (CollectionUtils.isNotEmpty(idocInfos)) {
			final StatisticInfoBean statisticInfoBean = new StatisticInfoBean();
			statisticInfoBean.setStatisticalPoint(statisticalPoint.toEpochMilli());
			fillStatisticInfoBean(statisticInfoBean, idocInfos);

			datahubConsumerService.upsertStatisticInfo(statisticInfoBean);
		} else {
			LOG.info("Can not found any IDoc, so do not insert statistic info.");
		}
	}

	/**
	 * The statistical point must be 1:00, 1:10, 1:20, 1:30, ....
	 * 
	 * @param now
	 * @return
	 */
	private Instant calculateStatisticalPoint() {
		final ZonedDateTime zonedDateTime = ZonedDateTime.now(Clock.systemUTC());
		return zonedDateTime.minus(zonedDateTime.getMinute() % 10, ChronoUnit.MINUTES).withSecond(0).withNano(0)
				.toInstant();
	}

	private void fillStatisticInfoBean(final StatisticInfoBean statisticInfoBean, final List<IdocBean> idocInfos) {
		final AtomicLong totalProcessingTime = new AtomicLong(0);
		final AtomicLong inProcessAmount = new AtomicLong(0);

		idocInfos.parallelStream().forEach(idoc -> {
			totalProcessingTime.addAndGet(idoc.getDuration() == null ? 0 : idoc.getDuration());
			if (StringUtils.equals(StatusEnum.PENDING_PUBLICATION.toString(), idoc.getStatus())) {
				inProcessAmount.incrementAndGet();
			}
		});

		statisticInfoBean.setInProcessAmount(inProcessAmount.get());
		statisticInfoBean.setAverageProcessingTime(totalProcessingTime.get() / idocInfos.size());
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setAmountToSubtract(int amountToSubtract) {
		this.amountToSubtract = amountToSubtract;
	}
}
