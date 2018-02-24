sap.ui.define([
	"datahub-monitor/controller/BaseController"
], function(baseController) {
	"use strict";
	return baseController.extend("datahub-monitor.controller.CanonicalsDetail", {
		baseController: baseController,
		
		onInit: function() {
			baseController.prototype.onInit.call(this);
			sap.ui.core.UIComponent.getRouterFor(this).attachRouteMatched(this._onRouteMatched, this);
		},
		
		/**********************************************
         * Default execute when enter into this page
         * load canonical detail data
         * Return: None
         **********************************************/
		_onRouteMatched: function(oEvent) {
			if(oEvent.getParameter("name") == "canonicalsDetail"){
				this.docType = oEvent.getParameter("arguments").docType;
				var docId = oEvent.getParameter("arguments").docId;
				var canonicalId = oEvent.getParameter("arguments").canonicalId;
				if(this.docType == 'null'){
					this.docType = "";
				}
				if(docId == 'null'){
					docId = "";
				}
				if(canonicalId == 'null'){
					canonicalId = "";
				}
				var self = this;
				var url = "/canonicalitemdetail?messageType=" + this.docType + "&batchPrimaryId="+ docId + "&canonicalItemId=" + canonicalId;
				this.service.get(url, this, function(data){
					var dataObj = {};
	        		dataObj.DataNum = data.itemList.length;
	        		dataObj.Data = data;
	        		var resultModel = new sap.ui.model.json.JSONModel(dataObj);
	        		self.getView().setModel(resultModel,"canonicalDetailModel");
				});
			}
		},
		
		/**********************************************
         * Table row navigation
         * Return: None
         **********************************************/
		goToTargetDetail: function(oEvent){
			var rowitem = oEvent.getParameter("rowBindingContext");
			if (undefined == rowitem) {
				return;
			}

			var oModelBatch = this.getView().getModel("canonicalDetailModel");
			var data = oModelBatch.oData.Data;
			var transferData = this.getRowData(data, rowitem.sPath);
			if (transferData.docId != undefined && transferData.itemId == undefined) {
				return;
			} else if (transferData.docId == undefined && transferData.itemId == undefined) {
				sap.m.MessageToast.show("No data!");
				return;
			} else {
				transferData.target = "";
				this._oRouter.navTo("targetDetail", transferData);
			}
		},
		
		/**********************************************
         * Get detail data for current row in table
         * Return: Object
         **********************************************/
		getRowData: function(tableData, spath) {
			var retObj = {};
			retObj['docId'] = tableData.batchPrimaryId == undefined 
								? null : tableData.batchPrimaryId;
			var rowsplit = spath.split("/");
			var itemListIndex = rowsplit[3];
			if(itemListIndex == undefined){
				return retObj;
			}
			var itemDetail = tableData.itemList[itemListIndex];
			retObj['docType'] = itemDetail.messageType == undefined
									? null : itemDetail.messageType;
			retObj['docId'] = itemDetail.batchPrimaryId == undefined
									? null : itemDetail.batchPrimaryId;
			retObj['itemId'] = itemDetail.itemId == undefined
									? null : itemDetail.itemId;
			return retObj;
		}
		
	});
});