sap.ui.define(function() {
    "use strict";

    var Formatter = {
        formatTimestampToDate: function(now) {
        	var newDate = new Date(parseInt(now));
        	return newDate.toUTCString();
        },
    
	    formatDate: function (dateInMilliseconds) {
	        if (dateInMilliseconds) {
	            return moment(parseInt(dateInMilliseconds)).format("MMM Do YYYY, hh:mm:ss A");
	        }
	    },
	
		formatStatusToObjectState:function(sStatus) {
			switch (sStatus) {
			case "SUCCESS":
				return "Success";
			case "PUBLISHED":
				return "Success";
			case "ARCHIVED":
				return "Success";
			case "PENDING_PUBLICATION":
				return "Warning";
			case "SUPERCEDED":
				return "Warning";
			case "NOT_PUBLISHED":
				return "Error";
			case "COMPLETE_FAILURE":
				return "Error";
			case "ERROR":
				return "Error";
			default:
				return "None";
		    }
		},
		
		formatTreeTotal: function(num, ref){
			if(num != 0 && num != undefined && ref == "sap-icon://tree"){
				return true;
			}else{
				return false;
			}
		}
    };

    return Formatter;

});