sap.ui.define([
	"sap/ui/core/UIComponent",
	"sap/ui/Device",
	"datahub-monitor/model/models"
], function(UIComponent, Device, models) {
	"use strict";
	
	var mRoutenames = {
		MASTER: "DataHubMonitorMaster",
		DETAIL: "DataHubMonitorDetail"
	};
	
	return UIComponent.extend("datahub-monitor.Component", {
		metadata: {
			name: "DataHubMonitor",
			dependencies: {
				libs: ["sap.m", "sap.ushell"],
				components: []
			},
			rootView: "datahub-monitor.view.IndexApp",
			config: {
				fullWidth: true,
				resourceBundle: "i18n/i18n.properties"
			},
			routing: {
				config: {
					routerClass: "sap.m.routing.Router",
					viewType: "XML",
					viewPath: "datahub-monitor.view",
					targetAggregation: "pages",
					clearTarget: false
				},
				routes: [
					{
						pattern: "",
						name: "Index",
						view: "Index",
						targetControl: "datahub-monitor-indexapp"
					},
					{
						pattern: "Inbound",
						name: "Inbound",
						view: "App",
						targetControl: "datahub-monitor-indexapp",
						subroutes: [{
							pattern:"documents",
							name: "documents",
							view: "DataHubMonitorMaster",
							targetAggregation: "masterPages",
							targetControl: "datahub-monitor-splitapp",
							subroutes: [{
								pattern: "detail",
								name: "detail",
								view: "DataHubMonitorDetail",
								targetAggregation: "detailPages"
							},
							{
								pattern: "documents/{docType}",
								name: "documentsList",
								view: "DocumentsList",
								targetAggregation: "detailPages"
							},
							{
								pattern: "documents/{docType}/{docId}",
								name: "documentsDetail",
								view: "DocumentsDetail",
								targetAggregation: "detailPages"
							},
							{
								pattern: "documents/{docType}/{docId}/canonical/{canonicalType}",
								name: "canonicalsList",
								view: "CanonicalsList",
								targetAggregation: "detailPages"
							},
							{
								pattern: "documents/{docType}/{docId}/canonical/detail/{canonicalId}",
								name: "canonicalsDetail",
								view: "CanonicalsDetail",
								targetAggregation: "detailPages"
							},
							{
								pattern: "documents/{docType}/{docId}/target/{itemId}",
								name: "targetDetail",
								view: "TargetDetail",
								targetAggregation: "detailPages"
							}]
						}]
					}
				]
			}
		},
		/**
		 * The component is initialized by UI5 automatically during the startup of the app and calls the init method once.
		 * @public
		 * @override
		 */
		init: function() {
			var sRootPath = jQuery.sap.getModulePath("datahub-monitor");
			var mConfig = this.getMetadata().getConfig();
			this.setModel(new sap.ui.model.resource.ResourceModel({
				bundleUrl: [sRootPath, mConfig.resourceBundle].join("/")
			}), "i18n");
			
			// call the base component's init function
			UIComponent.prototype.init.apply(this, arguments);
			// set the device model
			//this.setModel(models.createDeviceModel(), "device");
			this.getRouter().initialize();
		},
		
		/**
		 * The component is destroyed by UI5 automatically.
		 * In this method, the ErrorHandler are destroyed.
		 * @public
		 * @override
		 */
		destroy: function() {
			this.getModel().destroy();
			this.getModel("i18n").destroy();
			this.getModel("device").destroy();
			// call the base component's destroy function
			UIComponent.prototype.destroy.apply(this, arguments);
		}
	});
});