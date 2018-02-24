sap.ui.define(function() {
    "use strict";

    var Service = {
    	
    	rootUrl: "/Monitoring",

        get: function(urlPath, oController, successCallback, failCallback) {
        	this.ajax(urlPath, oController, successCallback, failCallback, 'GET');
        },
        
        post: function(urlPath, oController, data, successCallback, failCallback) {
        	this.ajax(urlPath, oController, successCallback, failCallback, 'POST', data);
        },
        
        //httpMethod:GET POST
        ajax: function(urlPath, oController, successCallback, failCallback, httpMethod, data) {
        	var self = this;
        	var request = {
                    url : self.rootUrl + urlPath,
                    type: httpMethod,
                    dataType: 'json',
    	            async: false,
    	            cache: false,
    	            beforeSend: function(xhr) {
    	            	oController._openBusyDialog();
                        xhr.setRequestHeader("Content-Type", "application/json");
                        xhr.setRequestHeader("Accept", "application/json");
                    },
                    
    	            success : function(data){
    	            	oController._closeBusyDialog();
    	            	successCallback(data);
                    },
    	            error: function(jqXHR, textStatus, err) {
    	            	oController._closeBusyDialog();
    	            	var oErrorResponse = jqXHR.responseJSON || err;
                    	if (failCallback) {
                    		failCallback(jqXHR.responseJSON);
                    	}
                    	else if (jqXHR.responseJSON && jqXHR.responseJSON.errorMessage) {
    						sap.m.MessageToast.show(jqXHR.responseJSON.errorMessage, {
    							duration: 6000
    						});
    					}
                    	else if (err !== undefined) {
    						sap.m.MessageToast.show(err, {
    							duration: 6000
    						});
    					} else {
    						sap.m.MessageToast.show("Unknown error!");
    					}
    				},
                    fail: function(err){
                    	oController._closeBusyDialog();
                    	if (failCallback) {
                    		failCallback();
                    	}
                        if (err !== undefined) {
    						var oErrorResponse = $.parseJSON(err.responseText);
    						sap.m.MessageToast.show(oErrorResponse.message, {
    							duration: 6000
    						});
    					} else {
    						sap.m.MessageToast.show("Unknown error!");
    					}
                    }
    		    };
        	if(httpMethod=='POST'){
        		request.data = data;
        		request.contentType= 'application/json';
        	}
        	$.ajax(request);
        },
        
        setModelInView: function(oController, relativeUrl, modelName){
        	var oModel = new sap.ui.model.json.JSONModel(this.rootUrl + relativeUrl);
        	oModel.attachRequestCompleted(function(){
        		var dataObj = {};
        		dataObj.DataNum = oModel.getData().itemList.length;
        		dataObj.Data = oModel.getData();
        		var resultModel = new sap.ui.model.json.JSONModel(dataObj);
        		oController.getView().setModel(resultModel,modelName);
        	});
        }
    };

    return Service;

});