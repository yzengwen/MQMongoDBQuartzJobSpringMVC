sap.ui.define([
	"datahub-monitor/controller/BaseController",
	"datahub-monitor/vendor/chartist-plugin-axistitle",
	"../utils/DHMCharts"
], function(baseController, chartistPluginAxistitle, DHMCharts) {
	"use strict";
	return baseController.extend("datahub-monitor.controller.Index", {
		baseController: baseController,
		
		onInit: function() {
			baseController.prototype.onInit.call(this);
			this.getView().addStyleClass("sapUiSizeCompact");
			var dataModel = new sap.ui.model.json.JSONModel({
				"dateRange": [{
						"name": "Hour",
			            "value": 1
			        },
			        {
						"name": "Day",
			            "value": 2
			        },
			        {
			        	"name": "Week",
			            "value": 3
			        },
			        {
			        	"name": "Month",
			            "value": 4
			    }]
			});
			this.getView().setModel(dataModel,"selectRangeModel");
			DHMCharts.init(this);
			
			this._loadSearchPageData();
			this._initSearchInputs();
		},
		
		handleIconTabBarSelect: function(oEvent){
			var filter = oEvent.mParameters.selectedKey;
			if(filter == "__filter0"){
				this._loadCharts(this);
			}else if(filter == "__filter2"){
				this._initSettingData();
			}
		},
		
		_initSearchInputs: function(){
			var today = new Date();
			var timeFromStr =  today.getFullYear() + '-' + (today.getMonth()+1) + '-' + today.getDate()
						+ " 00:00:00";
			var timeFrom = new Date(timeFromStr);
			var searchInputsModel = new sap.ui.model.json.JSONModel({
				"idocNoFrom": "",
				"idocNoTo": "",
				"singleSelected": true,
				"rangeSelected": false,
				"msgTypeList": [" "],
				"statusList": [" "],
				"timeFrom": timeFrom,
				"timeTo": new Date()
			})
			this.getView().byId("idSearchConditions").setModel(searchInputsModel,"searchInputsModel");
		},
		
		_loadSearchPageData: function(){
			var self = this;
			var url = "/getAvailableMessageTypes";
			this.service.get(url, this, function(data){
				var searchModel = new sap.ui.model.json.JSONModel({
					"statusList": [
				    	{
				    		"status": " "
				    	},
				    	{
				            "status": "SUCCESS"
				        },
				        {
				            "status": "PENDING_PUBLICATION"
				        },
				        {
				            "status": "SUPERCEDED"
				        },
				        {
				            "status": "COMPLETE_FAILURE"
				        },
				        {
				            "status": "PARTIAL_ERROR"
				        }
				    ]
				});
				data.push(" ");
				searchModel.setProperty("/messageType", 
					data.map(function(item){
						return {type: item};
				  	})
				 );
				self.getView().setModel(searchModel,"searchModel");
			});
		},
		
		/**********************************************
         * Open Inbound & Outbound popup
         * Return: None
         **********************************************/
        openMenuItemsPop: function(oEvent) {
            var oButton = oEvent.getSource();
            if (!this._menu) {
                this._menu = sap.ui.xmlfragment("datahub-monitor.view.fragment.MenuItems", this);
                this.getView().addDependent(this._menu);
            }
            this._menu.openBy(oButton);
        },
        
        _loadCharts: function(self){
        	DHMCharts.getAVGData(1);
        	DHMCharts.getStatusData();
        	DHMCharts.getProcessData(1);
        },
		
		onAfterRendering: function(){
			window.setTimeout(this._loadCharts, 600, this);
			this._initSetting();
			//this._initSettingData();
		},
		
		onTimeRangeSelectChange:function(item){
			DHMCharts.onTimeRangeSelectChange(item);
		},
		
		navToMasterDetail: function(){
			this._oRouter.navTo("documents");
		},
		
		/**********************************************
         * Initial default model for setting view
         * Return: None
         **********************************************/
        _initSetting: function() {
            var settingModel = new sap.ui.model.json.JSONModel({
                "_id": {},
                "success": {
                    "days": {
                        value: 0,
                        min: 0,
                        max: 999,
                        step: 1,
                        valueState: "None"
                    },
                    "hours": {
                        value: 0,
                        min: 0,
                        max: 24,
                        step: 1,
                        valueState: "None"
                    }
                },
                "error": {
                    "days": {
                        value: 0,
                        min: 0,
                        max: 999,
                        step: 1,
                        valueState: "None"
                    },
                    "hours": {
                        value: 0,
                        min: 0,
                        max: 24,
                        step: 1,
                        valueState: "None"
                    }
                },
                "cleanup": [{
                        "name": "Archive",
                        "selected": true
                    },
                    {
                        "name": "Delete",
                        "selected": false
                    }
                ],
                "threshold": {
                    "value": 80
                }
            });
            this.getView().setModel(settingModel, "settingModel");
        },
        
        /**********************************************
         * Initial data in setting page
         * Return: None
         **********************************************/
        _initSettingData: function() {
            var oMs = sap.ui.getCore().byId("idSettingMsgStrip");
            if (oMs) {
                oMs.destroy();
            }
            var self = this;
            this.service.get("/settings", this, function(data) {
                var resultModel = new sap.ui.model.json.JSONModel(data);
                var settingModel = self.getView().getModel("settingModel");
                settingModel.setProperty("/success/days/value", data.successEventDays);
                settingModel.setProperty("/success/hours/value", data.successEventHours);
                settingModel.setProperty("/success/days/valueState", "None");
                settingModel.setProperty("/success/hours/valueState", "None");
                settingModel.setProperty("/error/days/value", data.errorEventDays);
                settingModel.setProperty("/error/hours/value", data.errorEventHours);
                settingModel.setProperty("/error/days/valueState", "None");
                settingModel.setProperty("/error/hours/valueState", "None");
                settingModel.setProperty("/cleanup/0/selected", data.cleanStrategy == false); //Archive
                settingModel.setProperty("/cleanup/1/selected", data.cleanStrategy == true); //Delete
                settingModel.setProperty("/threshold/value", data.threshold);
                settingModel.setProperty("/_id", data._id);
                self.getView().setModel(settingModel, "settingModel");
            });
        },
        
        /**********************************************
         * Save setting data, send a request
         * Return: None
         **********************************************/
        handleSettingSave: function(oEvent) {
            if (!this.validateThresholdInput() || !this.handleSettingEventInputCheck()) {
            	sap.m.MessageToast.show("Invalid values !");
                return;
            }
            var self = this;
            this.getView().getModel("settingModel").refresh();
            var settingModel = this.getView().getModel("settingModel");
            var data = {};
            data.successEventDays = settingModel.getProperty("/success/days/value");
            data.successEventHours = settingModel.getProperty("/success/hours/value");
            data.errorEventDays = settingModel.getProperty("/error/days/value");
            data.errorEventHours = settingModel.getProperty("/error/hours/value");
            data.cleanStrategy = settingModel.getProperty("/cleanup/1/selected");
            data.threshold = settingModel.getProperty("/threshold/value");
            data._id = settingModel.getProperty("/_id");
            this.service.post("/settings", this, JSON.stringify(data), function(data) {
                self._showMessageStrip("success", "Save successful");
            }, function(data){
            	self._showMessageStrip("error", data.errorMessage);
            });
        },
        
        /**********************************************
         * Show message when save data in setting
         * Return: None
         **********************************************/
        _showMessageStrip: function(type, message) {
            var oMs = sap.ui.getCore().byId("idSettingMsgStrip");
            if (oMs) {
                oMs.destroy();
            }
            var msgType = "Success";
            if (type == "error" && message != undefined) {
                msgType = "Error";
            }
            var msgLayout = this.getView().byId("idSettingMsgStripLayout");
            var oMsgStrip = new sap.m.MessageStrip("idSettingMsgStrip", {
                text: message,
                showCloseButton: true,
                showIcon: true,
                type: msgType
            });
            msgLayout.addFixContent(oMsgStrip);
        },
        
        /**********************************************
         * Validate threshold input in setting
         * Return: Boolean
         **********************************************/
        validateThresholdInput: function() {
            var thresholdInput = this.getView().byId("idSettingThresholdInput");
            var value = thresholdInput.getValue();
            var regex = /^\d+(\.\d{0,2})?$/g;
            regex.lastIndex = 0;
            if (isNaN(value) || !regex.test(value) || value < 0 || value > 100) {
                thresholdInput.setValueState(sap.ui.core.ValueState.Error);
                thresholdInput.setValueStateText("Please enter a valid number");
                return false;
            } else {
                thresholdInput.setValueState(sap.ui.core.ValueState.None);
                return true;
            }
        },
        
        /**********************************************
         * Validate events input in setting
         * Return: Boolean
         **********************************************/
        handleSettingEventInputCheck: function(){
        	var dayRegex =  /[0-9]{1,3}/;
        	var hourRegex = /[0-1]?\d|2[0-4]/;
        	var settingModel = this.getView().getModel("settingModel");
        	
        	//Success event days and hours
        	var successEventFlag = true;
        	var successEventDays = settingModel.getProperty("/success/days/value");
            var successEventHours = settingModel.getProperty("/success/hours/value");
            if(!isNaN(successEventDays) && dayRegex.test(successEventDays)){
        		settingModel.setProperty("/success/days/valueState","None");
        	}else{
        		successEventFlag = false;
        		settingModel.setProperty("/success/days/valueState","Error");
        	}
            if(!isNaN(successEventHours) && hourRegex.test(successEventHours)){
        		settingModel.setProperty("/success/hours/valueState","None");
        	}else{
        		successEventFlag = false;
        		settingModel.setProperty("/success/hours/valueState","Error");
        	}
            // One of day and hour is larger than 0 at least.
            if (successEventFlag && (successEventDays + successEventHours) == 0){
            	successEventFlag = false;
            	settingModel.setProperty("/success/days/valueState","Error");
        		settingModel.setProperty("/success/hours/valueState","Error");
            }
            
            //Error event days and hours
            var errorEventFlag = true;
            var errorEventDays = settingModel.getProperty("/error/days/value");
            var errorEventHours = settingModel.getProperty("/error/hours/value");
            if(!isNaN(errorEventDays) && dayRegex.test(errorEventDays)){
        		settingModel.setProperty("/error/days/valueState","None");
        	}else{
        		errorEventFlag = false;
        		settingModel.setProperty("/error/days/valueState","Error");
        	}
            if(!isNaN(errorEventHours) && hourRegex.test(errorEventHours)){
        		settingModel.setProperty("/error/hours/valueState","None");
        	}else{
        		errorEventFlag = false;
        		settingModel.setProperty("/error/hours/valueState","Error");
        	}
            // One of day and hour is larger than 0 at least.
            if (errorEventFlag && (errorEventDays + errorEventHours) == 0){
            	errorEventFlag = false;
            	settingModel.setProperty("/error/days/valueState","Error");
        		settingModel.setProperty("/error/hours/valueState","Error");
            }
            
            return successEventFlag && errorEventFlag;
        },
        
        /**********************************************
         * Add new message type select input
         * Return: None
         **********************************************/
        addNewMsgTypeSelect: function(){
        	var vBox = this.getView().byId("idMsgTypeVBox");
        	var typeIndex = vBox.getItems().length;
        	var oSelect = new sap.m.Select({
        		width: "15rem",
        		forceSelection: false,
        		selectedKey: "{searchInputsModel>/msgTypeList/" + typeIndex + "}",
        		change: this.handleSearchSelectCheck,
        		items: {
        			path: "searchModel>/messageType",
        			template: new sap.ui.core.Item({
        				key: "{searchModel>type}",
        				text: "{searchModel>type}"
        			})
        		}
        	});
        	var delIcon = new sap.m.Button({
        		type: "Transparent",
        		icon: "sap-icon://less",
        		press: this.removeNewSelect,
        		ariaLabelledBy: "msg-"+typeIndex
        	}).addStyleClass("sapUiMediumMarginBegin sapUiMediumMarginEnd");
        	
        	var hBox = new sap.m.HBox({
        		alignItems: "Center",
        		items: [oSelect, delIcon]
        	});
        	vBox.addItem(hBox);
        },
        
        /**********************************************
         * Add new status select input
         * Return: None
         **********************************************/
        addNewStatusSelect: function(){
        	var vBox = this.getView().byId("idStatusVBox");
        	var statusIndex = vBox.getItems().length;
        	if(statusIndex === 6){
        		return;
        	}
        	var oSelect = new sap.m.Select({
        		width: "15rem",
        		forceSelection: false,
        		change: this.handleSearchSelectCheck,
        		selectedKey: "{searchInputsModel>/statusList/" + statusIndex + "}",
        		items: {
        			path: "searchModel>/statusList",
        			template: new sap.ui.core.Item({
        				key: "{searchModel>status}",
        				text: "{searchModel>status}"
        			})
        		}
        	});
        	var delIcon = new sap.m.Button({
        		type: "Transparent",
        		icon: "sap-icon://less",
        		press: this.removeNewSelect,
        		ariaLabelledBy: "status-" + statusIndex
        	}).addStyleClass("sapUiMediumMarginBegin sapUiMediumMarginEnd");
        	
        	var hBox = new sap.m.HBox({
        		alignItems: "Center",
        		items: [oSelect, delIcon]
        	});
        	vBox.addItem(hBox);
        },
        
        /**********************************************
         * Remove new added select input
         * Return: None
         **********************************************/
        removeNewSelect: function(oEvent){
        	var labelStr = oEvent.getSource().getAriaLabelledBy()[0].split("-");
        	var type = labelStr[0];
        	var index = labelStr[1];
        	var searchInputsModel = sap.ui.getCore().byId("__xmlview1--idSearchConditions").getModel("searchInputsModel");
        	if(type == "status"){
        		searchInputsModel.getProperty("/statusList/").splice(index,1);
        	}else{
        		searchInputsModel.getProperty("/msgTypeList/").splice(index,1);
        	}
        	oEvent.getSource().getParent().destroy();
        },
        
        /**********************************************
         * Select input value check, make sure no 
         * duplicate value
         * Return: None
         **********************************************/
        handleSearchSelectCheck: function(oEvent){
        	var searchInputsModel = sap.ui.getCore().byId("__xmlview1--idSearchConditions").getModel("searchInputsModel");
        	var selectedStatusArr = searchInputsModel.getProperty("/statusList");
        	var statusArr = [];
        	for(var i=0; i<selectedStatusArr.length - 1; i++){
        		statusArr.push(selectedStatusArr[i]);
        	}
        	var selectedMsgTypeArr = searchInputsModel.getProperty("/msgTypeList");
        	var msgTypeArr = [];
        	for(var i=0; i<selectedMsgTypeArr.length - 1; i++){
        		msgTypeArr.push(selectedMsgTypeArr[i]);
        	}
        	if($.inArray(oEvent.getSource().getSelectedKey(), statusArr) > -1 || 
        			$.inArray(oEvent.getSource().getSelectedKey(), msgTypeArr) > -1){
        		oEvent.getSource().setValueState(sap.ui.core.ValueState.Error);
        		oEvent.getSource().setValueStateText("Duplicate status");
        	}else{
        		oEvent.getSource().setValueState(sap.ui.core.ValueState.None);
        	}
        	
        },
        
        /**********************************************
         * Check box select check for Single and Range
         * Return: None
         **********************************************/
        handleSearchIdocCheckbox: function(oEvent){
        	var checkboxType = oEvent.getSource().getText();
        	var searchInputsModel = this.getView().byId("idSearchConditions").getModel("searchInputsModel");
        	if(oEvent.getParameter("selected")){
        		if(checkboxType == "Single"){
            		searchInputsModel.setProperty("/rangeSelected",false);
            		searchInputsModel.setProperty("/idocNoTo","");
            	}else{
            		searchInputsModel.setProperty("/singleSelected",false);
            	}
        	}else{
        		if(checkboxType == "Single"){
            		searchInputsModel.setProperty("/rangeSelected",true);
            	}else{
            		searchInputsModel.setProperty("/singleSelected",true);
            	}
        	}
        },
        
        /**********************************************
         * Search button function, submit search conditions
         * Return: None
         **********************************************/
        handleSubmitSearch: function(){
        	var searchInputsModel = this.getView().byId("idSearchConditions").getModel("searchInputsModel");
        	var url = "/advancedSearch";
        	var timeFrom = new Date(searchInputsModel.getProperty("/timeFrom")).getTime();
        	var timeTo = new Date(searchInputsModel.getProperty("/timeTo")).getTime();
        	
        	var sendData = {
    			"idocNoFrom": searchInputsModel.getProperty("/idocNoFrom"),
				"idocNoTo": searchInputsModel.getProperty("/idocNoTo"),
				"singleSelected": searchInputsModel.getProperty("/singleSelected"),
				"rangeSelected": searchInputsModel.getProperty("/rangeSelected"),
				"msgTypeList": searchInputsModel.getProperty("/msgTypeList"),
				"statusList": searchInputsModel.getProperty("/statusList"),
				"timeFrom": timeFrom,
				"timeTo": timeTo
        	}
        	var self = this;
        	var regex = /^\d+$/g;
        	regex.lastIndex = 0;
        	if(isNaN(sendData.idocNoFrom) || !regex.test(sendData.idocNoFrom)){
        		if(sendData.idocNoFrom.length > 0){
        			sap.m.MessageToast.show("Invalid values !");
            		return;
        		}
        	}
        	regex.lastIndex = 0;
        	if(isNaN(sendData.idocNoTo) || !regex.test(sendData.idocNoTo)){
        		if(sendData.idocNoTo.length > 0){
        			sap.m.MessageToast.show("Invalid values !");
            		return;
        		}
        	}
        	if(!this.handleRangeCheck(timeFrom,timeTo, 
        			"Incorrect date range. From date should be less than To date") 
        	||!this.handleRangeCheck(sendData.idocNoFrom, sendData.idocNoTo, 
        			"Incorrect Idoc range. From IDoc number should be less than To IDoc number")) {
        		return;
        	}
        	this.service.post(url, this, JSON.stringify(sendData), function(data){
        		if(data.length == 0){
        			sap.m.MessageToast.show("No data, please change your search condition!");
        			return;
        		}
        		var treeNodesModel = new sap.ui.model.json.JSONModel({
                    Title: "Inbound",
                    Data: data,
                    DataNum: data.length,
                    SearchData: sendData
                });
        		sap.ui.getCore().setModel(treeNodesModel, "treeNodesModel");
        		//self._oRouter.navTo("documents");
        		self._oRouter.navTo("detail");
        	});
        },
        
        /**********************************************
         * Reset button function, reset search conditions
         * Return: None
         **********************************************/
        handleResetSearch: function(){
        	this._initSearchInputs();
        	this._removeVBoxItems(this.getView().byId("idMsgTypeVBox"));
        	this._removeVBoxItems(this.getView().byId("idStatusVBox"));
        },
        
        _removeVBoxItems: function(VBox){
        	var num = VBox.getItems().length;
        	if(num > 1){
        		for(var i=1; i<num; i++){
        			VBox.removeItem(VBox.getItems().length-1);
        		}
        	}
        },
        
        handleRangeCheck: function(from, to, message){
        	if(from.length == 0 || to.length == 0 || parseInt(from) < parseInt(to) ) {
    			return true;
        	}else{
        		sap.m.MessageToast.show(message);
        		return false;
        	}
        },
        
        /**********************************************
         * Search time date validation
         * Return: None
         **********************************************/
        handleDateTimeCheck: function(oEvent){
        	var searchInputsModel = this.getView().byId("idSearchConditions").getModel("searchInputsModel");
        	var valid = oEvent.getParameter("valid");
        	var dateValue = new Date(oEvent.getSource().getValue()).getTime();
        	if(!valid || isNaN(dateValue)){
        		oEvent.getSource().setValueState(sap.ui.core.ValueState.Error);
        		oEvent.getSource().setValueStateText(oEvent.getSource().getValue() + " invalid date");
        		if(oEvent.getParameter("id").indexOf("timeFrom") > -1){
        			searchInputsModel.setProperty("/timeFrom","");
        		}else{
        			searchInputsModel.setProperty("/timeTo","");
        		}
        	}else{
        		oEvent.getSource().setValueState(sap.ui.core.ValueState.None);
        	}
        },
        
        /**********************************************
         * Search IDoc Number validation
         * Return: None
         **********************************************/
        handleSearchIDocCheck: function(oEvent){
        	if(oEvent.getSource().getValue() == ""){
        		return;
        	}
        	var searchInputsModel = this.getView().byId("idSearchConditions").getModel("searchInputsModel");
        	var idocNum = oEvent.getSource().getValue();
        	var regex = /^\d+$/g;
        	regex.lastIndex = 0;
        	if(isNaN(idocNum) || idocNum < 0 || !regex.test(idocNum)){
        		oEvent.getSource().setValueState(sap.ui.core.ValueState.Error);
        		oEvent.getSource().setValueStateText(oEvent.getSource().getValue() + " invalid number");
        		return;
        	}else{
        		oEvent.getSource().setValueState(sap.ui.core.ValueState.None);
        	}
        	var formatNum = "/idocNoFrom";
        	if(oEvent.getParameter("id").indexOf("idIdocNumTo") > -1){
        		formatNum = "/idocNoTo";
        	}
        	var len = (idocNum+"").length;
        	idocNum = Array(16>len ? 16-len+1||0 : 0).join(0) + idocNum;
        	searchInputsModel.setProperty(formatNum,idocNum);
        }
	});
});