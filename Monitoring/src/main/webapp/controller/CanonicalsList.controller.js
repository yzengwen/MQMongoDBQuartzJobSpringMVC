sap.ui.define([
	"datahub-monitor/controller/BaseController"
], function(baseController) {
	"use strict";
	return baseController.extend("datahub-monitor.controller.CanonicalsList", {
		baseController: baseController,
		
		onInit: function() {
			baseController.prototype.onInit.call(this);
			sap.ui.core.UIComponent.getRouterFor(this).attachRouteMatched(this._onRouteMatched, this);
		},
		
		/**********************************************
         * Default execute when enter into this page
         * load canonical items data
         * Return: None
         **********************************************/
		_onRouteMatched: function(oEvent) {
			if(oEvent.getParameter("name") == "canonicalsList"){
				this.docType = oEvent.getParameter("arguments").docType;
				var docId = oEvent.getParameter("arguments").docId;
				var canonicalType = oEvent.getParameter("arguments").canonicalType;
				if(this.docType == 'null'){
					this.docType = "";
				}
				if(docId == 'null'){
					docId = "";
				}
				if(canonicalType == 'null'){
					canonicalType = "";
				}
				var self = this;
				var url = "/canonicalitemsinfo?messageType=" + this.docType + "&batchPrimaryId="+ docId + "&canonicalItemType=" + canonicalType;
				this.service.get(url, this, function(data){
					var dataObj = {};
	        		dataObj.DataNum = data.length;
	        		dataObj.Data = data;
	        		var resultModel = new sap.ui.model.json.JSONModel(dataObj);
	        		self.getView().setModel(resultModel,"canonicalItemsModel");
				});
			}
		},
		
		/**********************************************
         * Table row navigation
         * Return: None
         **********************************************/
		goToCanonicalDetail: function(oEvent){
			var rowitem = oEvent.getParameter("rowBindingContext");
			if (undefined == rowitem) {
				return;
			}

			var oModelBatch = this.getView().getModel("canonicalItemsModel");
			var data = oModelBatch.oData.Data;
			var transferData = this.getRowData(data, rowitem.sPath);
			if (transferData.docId != undefined && transferData.canonicalId == undefined) {
				return;
			} else if (transferData.docId == undefined && transferData.canonicalId == undefined) {
				sap.m.MessageToast.show("No data!");
				return;
			} else {
				transferData.canonical = "";
				transferData.detail = "";
				this._oRouter.navTo("canonicalsDetail", transferData);
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
			var itemListIndex = rowsplit[2];
			if(itemListIndex == undefined){
				return retObj;
			}
			var itemDetail = tableData[itemListIndex];
			retObj['docType'] = itemDetail.messageType == undefined
									? null : itemDetail.messageType;
			retObj['docId'] = itemDetail.batchPrimaryId == undefined
									? null : itemDetail.batchPrimaryId;
			retObj['canonicalId'] = itemDetail.itemId == undefined
									? null : itemDetail.itemId;
			return retObj;
		}
		
	});
});