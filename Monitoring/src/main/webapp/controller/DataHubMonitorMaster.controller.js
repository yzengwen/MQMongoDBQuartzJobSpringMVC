sap.ui.define([
    "datahub-monitor/controller/BaseController"
], function(baseController) {
    "use strict";

    return baseController.extend("datahub-monitor.controller.DataHubMonitorMaster", {
        baseController: baseController,

        /**********************************************
         * Initial function for this controller
         * Return: None
         **********************************************/
        onInit: function(evt) {
            baseController.prototype.onInit.call(this);
            sap.ui.core.UIComponent.getRouterFor(this).attachRouteMatched(this._onRouteMatched, this);
        },
        
        _onRouteMatched: function(oEvent){
        	if(oEvent.getParameter("name") == "documents"){
        		if(sap.ui.getCore().getModel("treeNodesModel")){
        			this.getView().setModel(sap.ui.getCore().getModel("treeNodesModel"), "treeNodesModel");
				}else{
					this._refreshData();
				}
        	}
        },
        
        /**********************************************
         * Load new data when click refresh button in 
         * search field
         * Return: None
         **********************************************/
        _refreshData: function(text) {
        	var self = this;
        	if(text != undefined && text != ""){
        		var text = text.toUpperCase();
        		var treeModelData = sap.ui.getCore().getModel("treeNodesModel").getData().Data;
        		var filterData = [];
        		for(var i=0; i<treeModelData.length; i++){
        			if(treeModelData[i].text != undefined 
        					&& treeModelData[i].text.toUpperCase().indexOf(text) > -1){
        				filterData.push(treeModelData[i]);
        			}else if(treeModelData[i].nodes != undefined && treeModelData[i].nodes.length > 0){
        				var nodeData = {};
    					$.extend(nodeData,treeModelData[i]);
						nodeData.nodes = [];
						var searchFlag = false;
        				for(var j=0; j<treeModelData[i].nodes.length; j++){
        					if(treeModelData[i].nodes[j].text != undefined 
        							&& treeModelData[i].nodes[j].text.toUpperCase().indexOf(text) > -1){
        						nodeData.nodes.push(treeModelData[i].nodes[j]);
        						searchFlag = true;
        					}
        				}
        				if(searchFlag){
        					filterData.push(nodeData);
        				}
        			}
        		}
        		var filterTreeModel = new sap.ui.model.json.JSONModel({
        			Title: "Inbound",
                    Data: filterData,
                    DataNum: filterData.length,
                    SearchData: sap.ui.getCore().getModel("treeNodesModel").getData().SearchData
        		});
        		sap.ui.getCore().setModel(filterTreeModel,"treeNodesModel");
        		this.getView().setModel(filterTreeModel, "treeNodesModel");
        	}else{
        		var url = "/refreshPage";
                this.service.post(url, this, "", function(data){
                	var treeNodesModel = new sap.ui.model.json.JSONModel({
                        Title: "Inbound",
                        Data: data.treeData,
                        DataNum: data.treeData.length,
                        SearchData: data.searchData
                    });
                	sap.ui.getCore().setModel(treeNodesModel,"treeNodesModel");
                	self.getView().setModel(treeNodesModel, "treeNodesModel");
                });
        	}
        },
        
        /**********************************************
         * back button press, navigate to index page
         * Return: None
         **********************************************/
        navToIndexPage: function(){
        	this._oRouter.navTo("Index");
        },
        
        /**********************************************
         * Press event for tree nodes, and navigate to 
         * different detail page
         * Return: None
         **********************************************/
        treeNodePress: function(oEvent) {
            if (oEvent.getParameter("rowBindingContext") == undefined) {
                return;
            }
            var nodePathArr = oEvent.getParameter("rowBindingContext").sPath.split("/");
            var treeNodes = {};
            treeNodes.nodes = this.getView().getModel("treeNodesModel").oData.Data;
            var nodePath = "treeNodes";
            var nodeArr = [];
            for (var i = 0; i < nodePathArr.length; i++) {
                if (!isNaN(nodePathArr[i]) && nodePathArr[i] != "") {
                    nodeArr.push(nodePathArr[i]);
                }
            }
            var transferData = {};
            for (var i = 0; i < nodeArr.length; i++) {
                if (i == 0) {
                    transferData.docType = treeNodes.nodes[nodeArr[i]].text;
                } else if (i == 1) {
                    transferData.docId = treeNodes.nodes[nodeArr[0]].nodes[nodeArr[i]].text;
                } else if (i == 2) {
                    transferData.canonicalType = treeNodes.nodes[nodeArr[0]].nodes[nodeArr[1]].nodes[nodeArr[i]].text;
                } else if (i == 3) {
                    transferData.itemId = treeNodes.nodes[nodeArr[0]].nodes[nodeArr[1]].nodes[nodeArr[2]].nodes[nodeArr[i]].itemId;
                }
            }
            this.oHasher = sap.ui.core.routing.HashChanger.getInstance();
            if (transferData.docType != undefined &&
                transferData.docId != undefined &&
                transferData.itemId != undefined) {
            	this._oRouter.navTo("targetDetail", transferData);
            } else if (transferData.docType != undefined &&
                transferData.docId != undefined &&
                transferData.canonicalType != undefined) {
            	transferData.canonical = "";
            	this._oRouter.navTo("canonicalsList", transferData);
            } else if (transferData.docType != undefined &&
                transferData.docId != undefined) {
            	this._oRouter.navTo("documentsDetail", transferData);
            } else if (transferData.docType != undefined) {
            	this._oRouter.navTo("documentsList", transferData);
            }
        },

        /**********************************************
         * Handle search field for tree nodes
         * Return: None
         **********************************************/
        handleTreeNodeSearch: function(oEvent) {
        	var text = "";
            var refreshPress = oEvent.getParameters().refreshButtonPressed;
            var clearPress = oEvent.getParameters().clearButtonPressed;
            if(!refreshPress || !clearPress){
            	text = oEvent.getSource().getValue();
            }
            this._refreshData(text);
        },

        /**********************************************
         * Collapse all tree nodes
         * Return: None
         **********************************************/
        handleCollapseAllTreeNodes: function() {
            var oTree = this.getView().byId("idNodeTree");
            oTree.collapseAll();
        },

        /**********************************************
         * Ajax request for loading sub-node tree
         * Return: None
         **********************************************/
        handleExpandTreeNodes: function(oEvent) {
            var self = this;
            if (oEvent.getParameter("rowContext") == undefined) {
                return;
            }
            var rPath = oEvent.getParameter("rowContext").sPath;
            var expanded = oEvent.getParameter("expanded");
            var treeNodesModel = this.getView().getModel("treeNodesModel");
            var treeNodes = {};
            treeNodes.nodes = treeNodesModel.oData.Data;
            if (expanded) {
                var nodePathArr = rPath.split("/");
                var nodeArr = [];
                for (var i = 0; i < nodePathArr.length; i++) {
                    if (!isNaN(nodePathArr[i]) && nodePathArr[i] != "") {
                        nodeArr.push(nodePathArr[i]);
                    }
                }
                var messageType, docId, canonicalItemType, url;
                var nodeSearched = false;
                var nodeLevel = nodeArr.length;
                if (nodeLevel == 1) {
                    messageType = treeNodes.nodes[nodeArr[0]].text;
                    url = "/idocstree?messageType=" + messageType;
                    if(treeNodes.nodes[nodeArr[0]].nodes[0].status != undefined){
                    	nodeSearched = true;
                    }
                } else if (nodeLevel == 2) {
                	messageType = treeNodes.nodes[nodeArr[0]].text;
                    docId = treeNodes.nodes[nodeArr[0]].nodes[nodeArr[1]].text;
                    url = "/canonicalstree?messageType=" + messageType + "&batchPrimaryId=" + docId;
                } else if (nodeLevel == 3) {
                	messageType = treeNodes.nodes[nodeArr[0]].text;
                	docId = treeNodes.nodes[nodeArr[0]].nodes[nodeArr[1]].text;
                	canonicalItemType = treeNodes.nodes[nodeArr[0]].nodes[nodeArr[1]].nodes[nodeArr[2]].text;
                    url = "/targetstree?messageType=" + messageType + "&batchPrimaryId=" + docId + "&canonicalItemType=" + canonicalItemType;
                } else if (nodeLevel == 4) {
                    url = "";
                }
                if (url != "" && !nodeSearched) {
                    this.service.get(url, this, function(data) {
                        treeNodesModel.getProperty(rPath).nodes = data;
                        treeNodesModel.refresh();
                    }, function() {
                        sap.m.MessageToast.show("No data !");
                    });
                }
            }
        },
        
        /**********************************************
         * Status icon popup for detail information
         * Return: None
         **********************************************/
        handeStatusIconPress: function(oEvent){
        	var oMessagePopover = new sap.m.Popover({
        		showHeader: false,
        		content: new sap.m.Text({
        			text: oEvent.getSource().getAlt()
        		}).addStyleClass("sapUiTinyMargin")
        	});
        	oMessagePopover.openBy(oEvent.getSource());
        }
        
    });
});