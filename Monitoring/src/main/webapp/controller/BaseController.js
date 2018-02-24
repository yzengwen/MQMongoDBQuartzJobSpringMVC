/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
sap.ui.define([ 
    "sap/ui/core/mvc/Controller",
    "sap/ui/core/routing/History",
    "../utils/Formatter",
    "../utils/Service",
    "../utils/ToggleFullScreenHandler"],
                
	function(Controller, History, Formatter, Service, ToggleFullScreenHandler) {
		"use strict";
		  
		var baseController = Controller.extend("datahub-monitor.controller.BaseController",{
            formatter: Formatter,
            service: Service,
            _oRouter: null,
            toggleFullScreenHandler: ToggleFullScreenHandler,
            
            onInit : function() {
            	this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            },
			
            getRouter : function(){
            	return this._oRouter;
            },
            
			onNavBack : function(oEvent) {
				var oHistory, sPreviousHash;
				oHistory = History.getInstance();
				sPreviousHash = oHistory.getPreviousHash();
				if (sPreviousHash !== undefined){
					window.history.go(-1);
				} else {
					this.getRouter().navTo("documents",{}, true );
				}
			},
			
			_openBusyDialog: function() {
	            if (!this._busyDialog) {
	            	this._busyDialog = sap.ui.xmlfragment("datahub-monitor.view.fragment.dialog.BusyDialog", this);
	            	this.getView().addDependent(this._busyDialog);
	            }
	            this._busyDialog.open();
	        },

	        _closeBusyDialog: function() {
	            if (this._busyDialog) {
	            	this._busyDialog.close();
	            }
	        },
	        
	        hideMaster: function(oEvent){
				this.toggleFullScreenHandler.updateMode(oEvent, this.getView());
			},
			
			homePage: function(){
				this.getRouter().navTo("detail",{}, true );
			}
			
		});
		
		return 	baseController;				
	});
