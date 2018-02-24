package com.sap.datahubmonitor.beans;

public enum StatusEnum {

	//to handle null status
	STATUS_NULL("",10),
	//every level has this status
	PENDING_PUBLICATION("PENDING_PUBLICATION", 1),
	//Item Statuses
	ERROR("ERROR",2), 
	NOT_PUBLISHED("NOT_PUBLISHED",3),
	PUBLISHED("PUBLISHED",4),
	ARCHIVED("ARCHIVED",5),
	
	//Document Statuses & Batch Statuses
	PARTIAL_ERROR("PARTIAL_ERROR",6),
	COMPLETE_FAILURE("COMPLETE_FAILURE",7),
	SUPERCEDED("SUPERCEDED",8),
	SUCCESS("SUCCESS",9);
	
	private StatusEnum(String name , int index){
        this.name = name ;
        this.index = index ;
    }
	
	private String name ;
    private int index ;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
	
}
