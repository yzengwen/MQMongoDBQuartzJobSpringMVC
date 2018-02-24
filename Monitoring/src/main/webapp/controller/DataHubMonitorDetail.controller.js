sap.ui.define([
	"datahub-monitor/controller/BaseController",
	"../utils/Formatter"
], function(baseController, Formatter) {
	"use strict";

	return baseController.extend("datahub-monitor.controller.DataHubMonitorDetail", {
		baseController: baseController,
		Formatter: Formatter,
		
		onInit: function () {
			baseController.prototype.onInit.call(this);
			sap.ui.core.UIComponent.getRouterFor(this).attachRouteMatched(this._onRouteMatched, this);
		},
		
		_onRouteMatched: function(oEvent){
			if(oEvent.getParameter("name") == "documents" || oEvent.getParameter("name") == "detail"){
				var treeNodesModel = sap.ui.getCore().getModel("treeNodesModel");
				var searchedModelData = {};
				if(treeNodesModel){
					var searchData = treeNodesModel.oData.SearchData;
					searchedModelData = {
						"idocNoFrom": searchData.idocNoFrom,
						"idocNoTo": searchData.idocNoTo,
						"msgTypeList": searchData.msgTypeList.join(","),
						"rangeSelected": searchData.rangeSelected,
						"singleSelected": searchData.singleSelected,
						"statusList": searchData.statusList.join(","),
						"timeFrom": Formatter.formatDate(searchData.timeFrom),
						"timeTo": Formatter.formatDate(searchData.timeTo)
					}
				}
				var searchInputsModel = new sap.ui.model.json.JSONModel(searchedModelData);
				this.getView().setModel(searchInputsModel, "searchInputsModel");
			}
		}
	
	});
});