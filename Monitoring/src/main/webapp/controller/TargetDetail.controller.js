sap.ui.define([
	"datahub-monitor/controller/BaseController"
], function(baseController) {
	"use strict";
	return baseController.extend("datahub-monitor.controller.TargetDetail", {
		baseController: baseController,
		
		onInit: function() {
			baseController.prototype.onInit.call(this);
			sap.ui.core.UIComponent.getRouterFor(this).attachRouteMatched(this._onRouteMatched, this);
		},
		
		/**********************************************
         * Default execute when enter into this page
         * load target detail data
         * Return: None
         **********************************************/
		_onRouteMatched: function(oEvent) {
			if(oEvent.getParameter("name") == "targetDetail"){
				var docType = oEvent.getParameter("arguments").docType;
				var docId = oEvent.getParameter("arguments").docId;
				var itemId = oEvent.getParameter("arguments").itemId;
				
				if(this.getView().getModel("itemsDetailModel") 
				  && this.getView().getModel("itemsDetailModel").oData.Data.itemId 
				  		== oEvent.getParameter("arguments").itemId){
					return;
				}
				if(docType == 'null'){
					docType = "";
				}
				if(docId == 'null'){
					docId = "";
				}
				if(itemId == 'null'){
					itemId = "";
				}
				var url = encodeURI("/targetiteminfo?messageType=" + docType + "&batchPrimaryId=" + docId + 
						"&itemId=" + itemId);
				var self = this;
				this.service.get(url, this, function(data){
	        		var resultModel = new sap.ui.model.json.JSONModel(data);
	        		self.getView().setModel(resultModel,"targetDetailModel");
				});
			}
		}
		
	});
});