// Controller for the view hosting the SplitApp.
sap.ui.define([
	"datahub-monitor/controller/BaseController"
], function(baseController) {
	"use strict";
	return baseController.extend("datahub-monitor.controller.App", {
		baseController: baseController,
		
		onInit: function() {
			baseController.prototype.onInit.call(this);
			this.getView().addStyleClass("sapUiSizeCompact");
		},
		
		onAfterRendering: function(){
			//sap.ui.getCore().byId('datahub-monitor-shell').setAppWidthLimited(false);
		}

	});
});