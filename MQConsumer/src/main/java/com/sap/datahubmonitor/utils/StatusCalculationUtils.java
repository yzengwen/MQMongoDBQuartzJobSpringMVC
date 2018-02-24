package com.sap.datahubmonitor.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sap.datahubmonitor.beans.CanonicalItemBean;
import com.sap.datahubmonitor.beans.StatusEnum;

public class StatusCalculationUtils {
	
	public static StatusEnum calculateStatusForIdoc(List<CanonicalItemBean> itemList) {
		Set<StatusEnum> publishedSet = new HashSet<StatusEnum>();
		Set<StatusEnum> errorSet = new HashSet<StatusEnum>();
		Set<StatusEnum> notPublishedSet = new HashSet<StatusEnum>();
		Set<StatusEnum> archivedSet = new HashSet<StatusEnum>();
		Set<StatusEnum> pendingPublicationSet = new HashSet<StatusEnum>();
		for(CanonicalItemBean canonicalItem : itemList) {
			String status = canonicalItem.getStatus();
			if(StatusEnum.PUBLISHED.toString().equals(status)) {
				publishedSet.add(StatusEnum.valueOf(status));
			} else if (StatusEnum.ERROR.toString().equalsIgnoreCase(status)) {
				errorSet.add(StatusEnum.valueOf(status));
			} else if (StatusEnum.NOT_PUBLISHED.toString().equalsIgnoreCase(status)) {
				notPublishedSet.add(StatusEnum.valueOf(status));
			} else if (StatusEnum.ARCHIVED.toString().equalsIgnoreCase(status)) {
				archivedSet.add(StatusEnum.valueOf(status));
			} else if (StatusEnum.PENDING_PUBLICATION.toString().equalsIgnoreCase(status)) {
				pendingPublicationSet.add(StatusEnum.valueOf(status));
			}
		}
		if (notPublishedSet.size() + errorSet.size() == 0 && pendingPublicationSet.size() > 0) {
			return StatusEnum.PENDING_PUBLICATION;
		} else if ((errorSet.size() + notPublishedSet.size() > 0) && (publishedSet.size() + archivedSet.size() + pendingPublicationSet.size() == 0)) {
			return StatusEnum.COMPLETE_FAILURE;
		} else if (errorSet.size() + notPublishedSet.size() > 0) {
			return StatusEnum.PARTIAL_ERROR;
		} else if (publishedSet.size() + errorSet.size() + notPublishedSet.size() + pendingPublicationSet.size() == 0 && archivedSet.size() > 0) {
			return StatusEnum.SUPERCEDED;
		} else if (notPublishedSet.size() + errorSet.size() + pendingPublicationSet.size() == 0 && publishedSet.size() > 0) {
			return StatusEnum.SUCCESS;
		} else {
			return StatusEnum.STATUS_NULL;
		}
	}

}
