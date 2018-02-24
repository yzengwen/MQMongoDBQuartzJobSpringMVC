package com.sap.datahubmonitor.controller.statistic;

public enum TimeRangeType {
	HOUR(1), DAY(2), WEEK(3), MONTH(4);
    private final int value;

    private TimeRangeType(int value) {
        this.value = value;
    }
    
    public static TimeRangeType fromInteger(int x) {
        switch(x) {
        case 1:
            return HOUR;
        case 2:
            return DAY;
        case 3:
            return WEEK;
        case 4:
            return MONTH;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
    
    public int toInt() {
        return value;
    }

}
