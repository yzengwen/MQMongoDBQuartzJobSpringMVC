sap.ui.define([
	"datahub-monitor/controller/BaseController"
], function(baseController) {
	"use strict";
	return baseController.extend("datahub-monitor.controller.DocumentsList", {
		baseController: baseController,
		statusInput: '',
		
		onInit: function() {
			baseController.prototype.onInit.call(this);
			var self = this;
			sap.ui.core.UIComponent.getRouterFor(this).attachRouteMatched(this._onRouteMatched, this);
		},
		
		/**********************************************
         * Default execute when enter into this page
         * load idocs detail info
         * Return: None
         **********************************************/
		_onRouteMatched: function(oEvent) {
			if(oEvent.getParameter("name") == "documentsList"){
				this.docType = oEvent.getParameter("arguments").docType;
				if(this.docType == 'null'){
					this.docType = "";
				}
				var self = this;
				var url = "/idocsinfo?messageType=" + this.docType;
				this.service.get(url, this, function(data){
					var dataObj = {};
	        		dataObj.DataNum = data.length;
	        		dataObj.Data = data;
	        		var allDataModel = new sap.ui.model.json.JSONModel(dataObj);
	        		self.getView().setModel(allDataModel,"allDataModel");
	        		var resultModel = new sap.ui.model.json.JSONModel(dataObj);
	        		self.getView().setModel(resultModel,"batchList");
	        		self._initPaginator("allDataModel","batchList");
	        		self.getView().byId("idDocumentListTable").sort(self.getView().byId("idTableHeaderBatchPrimaryId"), 
	        				sap.ui.table.SortOrder.Ascending);
				});
			}
		},
		
//		onAfterRendering: function(){
//			this._initPaginator("allDataModel","batchList");
//		},
		
		/**********************************************
         * Table row navigation
         * Return: None
         **********************************************/
		goToDocumentDetail: function(oEvent){
			var rowitem = oEvent.getParameter("rowBindingContext");
			if (undefined == rowitem) {
				return;
			}
			var oModelBatch = this.getView().getModel("batchList");
			var data = oModelBatch.oData.Data;
			var transferData = this.getRowData(data, rowitem.sPath);

			if (this.docType != undefined && transferData.docId == undefined) {
				return
			} else if(this.docType == undefined && transferData.docId == undefined){
				sap.m.MessageToast.show("No data!");
				return;
			} else {
				transferData.docType = this.docType;
				this._oRouter.navTo("documentsDetail", transferData);
			}
		},
		
		/**********************************************
         * Get detail data for current row in table
         * Return: Object
         **********************************************/
		getRowData: function(tableData, spath) {
			var retObj = {};
			var rowsplit = spath.split("/");
			var rowIndex = rowsplit[2];
			var itemRowIndex = rowsplit[4];
			var batch = tableData[rowIndex];
			retObj['docId'] = batch.batchPrimaryId == undefined 
								? null : batch.batchPrimaryId;

			if (itemRowIndex) {
				var document = batch.itemList[itemRowIndex];
				retObj['itemId'] = document.itemId == undefined ? null : document.itemId;
			}
			return retObj;
		},
		
		/**********************************************
         * Initial pagination for idocs data
         * Return: None
         **********************************************/
		_initPaginator: function(dataModelName, resultModelName){
			var self = this;
			var numOnePage = 10;
			var dataModel = this.getView().getModel(dataModelName);
			var oPaginator = this.getView().byId("idPaginator");
			var totalNum = dataModel.oData.Data.length;
			var endNum = numOnePage;
			if(endNum > totalNum){
				endNum = totalNum;
			}
			var tempObj = {};
			var tempData = [];
			for(var i=0; i < endNum; i++){
				tempData.push(dataModel.getData().Data[i]);
			}
			tempObj.Data = tempData;
			tempObj.DataNum = totalNum;
			this.getView().getModel(resultModelName).setData(tempObj);
			this.getView().getModel(resultModelName).refresh();
			
			oPaginator.setNumberOfPages(Math.ceil(totalNum / numOnePage));
			oPaginator.setCurrentPage(1);
			oPaginator.attachPage(function(oEvent) {
				var nextPage = oEvent.getParameter("targetPage");
				oPaginator.setCurrentPage(nextPage);
				var tempObj = {};
				var tempData = [];
				var endNum = numOnePage*nextPage;
				if(endNum > totalNum){
					endNum = totalNum;
				}
				for(var i=numOnePage*(nextPage-1); i<endNum; i++){
					tempData.push(dataModel.getData().Data[i]);
				}
				tempObj.Data = tempData;
				tempObj.DataNum = totalNum;
				self.getView().getModel(resultModelName).setData(tempObj);
				self.getView().getModel(resultModelName).refresh();
			});
		},
		
		/**********************************************
         * Search Idocs
         * Return: None
         **********************************************/
		handleDataSearch: function(){
			var aFilters = [];
			var docId = this.getView().byId("idDocumentIdInput").getValue();
			var allDocs = this.getView().getModel("allDataModel").getData().Data;
			var filterData = [];
			for(var i=0, len=allDocs.length; i<len; i++){
				if(allDocs[i].batchPrimaryId.search(new RegExp(docId, "i")) > -1){
					filterData.push(allDocs[i]);
				}
			}
			var filterModel = new sap.ui.model.json.JSONModel({
				Data:filterData,
				DataNum: filterData.length
			})
			this.getView().setModel(filterModel, "filterModel");
			this._initPaginator("filterModel", "batchList");
			this.getView().byId("idDocumentIdInput").setValue("");
		}

	});
});