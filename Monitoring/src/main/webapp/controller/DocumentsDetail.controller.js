sap.ui.define([
	"datahub-monitor/controller/BaseController"
], function(baseController) {
	"use strict";
	return baseController.extend("datahub-monitor.controller.DocumentsDetail", {
		baseController: baseController,
		
		onInit: function() {
			baseController.prototype.onInit.call(this);
			sap.ui.core.UIComponent.getRouterFor(this).attachRouteMatched(this._onRouteMatched, this);
		},
		
		onAfterRendering: function(){
			//this._initCytoscape();
		},
		
		/**********************************************
         * Default execute when enter into this page
         * load idocs data
         * Return: None
         **********************************************/
		_onRouteMatched: function(oEvent) {
			if(oEvent.getParameter("name") == "documentsDetail"){
				if(this.getView().getModel("documentsDetailModel") 
				   && this.getView().getModel("documentsDetailModel").oData.Data.messageType 
				   		== oEvent.getParameter("arguments").docType
				   && this.getView().getModel("documentsDetailModel").oData.Data.batchPrimaryId 
						== oEvent.getParameter("arguments").docId){
					return;
				}
				this.docType = oEvent.getParameter("arguments").docType;
				var docId = oEvent.getParameter("arguments").docId;
				if(this.docType == 'null'){
					this.docType = "";
				}
				if(docId == 'null'){
					docId = "";
				}
				var self = this;
				var url = "/idocinfo?messageType=" + this.docType + "&batchPrimaryId="+ docId;
				this.service.get(url, this, function(data){
					var dataObj = {};
	        		dataObj.DataNum = data.itemList.length;
	        		dataObj.Data = data;
	        		var resultModel = new sap.ui.model.json.JSONModel(dataObj);
	        		self.getView().setModel(resultModel,"documentsDetailModel");
				});
			}
		},
		
		/**********************************************
         * Default execute when enter into this page
         * Return: None
         **********************************************/
		goToCanonicalsList: function(oEvent){
			var rowitem = oEvent.getParameter("rowBindingContext");
			if (undefined == rowitem) {
				return;
			}
			var oModelBatch = this.getView().getModel("documentsDetailModel");
			var data = oModelBatch.oData.Data;
			var transferData = this.getRowData(data, rowitem.sPath);
			if (transferData.docId != undefined && transferData.canonicalType == undefined) {
				return;
			} else if (transferData.docId == undefined && transferData.canonicalType == undefined) {
				sap.m.MessageToast.show("No data!");
				return;
			} else {
				transferData.canonical = "";
				this._oRouter.navTo("canonicalsList", transferData);
			}
		},
		
		/**********************************************
         * Table row navigation
         * Return: Object
         **********************************************/
		getRowData: function(tableData, spath) {
			var retObj = {};
			retObj['docType'] = tableData.messageType == undefined
								? null : tableData.messageType;
			retObj['docId'] = tableData.batchPrimaryId == undefined 
								? null : tableData.batchPrimaryId;
			var rowsplit = spath.split("/");
			var itemListIndex = rowsplit[3];
			if(itemListIndex == undefined){
				return retObj;
			}
			var itemDetail = tableData.itemList[itemListIndex];
			retObj['canonicalType'] = itemDetail.itemType == undefined
									? null : itemDetail.itemType;
			return retObj;
		},
		
		onOpenFlowDialog: function(){
			if (!this._flowDialog) {
				this._flowDialog = sap.ui.xmlfragment("datahub-monitor.view.fragment.dialog.IDocFlowDialog", this);
				this.getView().addDependent(this._flowDialog);
			}
			this._flowDialog.open();
			this._initCytoscape();
		},
		
		handleFlowDialogClose: function(){
			if(this._flowDialog){
				this._flowDialog.close();
			}
		},
		
		_initCytoscape: function(){
			var self = this;
			var cy = cytoscape({
			    container: $('#idIDocProcessFlow'),
			    boxSelectionEnabled: false,
			    autounselectify: true,

			    style: cytoscape.stylesheet()
			        .selector('node')
			        .css({
			            'content': 'data(id)'
			        })
			        .selector('edge')
			        .css({
			            'target-arrow-shape': 'triangle',
			            'width': 4,
			            'line-color': '#ddd',
			            'target-arrow-color': '#ddd',
			            'curve-style': 'bezier'
			        })
			        .selector('.highlighted')
			        .css({
			            'background-color': '#61bffc',
			            'line-color': '#61bffc',
			            'target-arrow-color': '#61bffc',
			            'transition-property': 'background-color, line-color, target-arrow-color',
			            'transition-duration': '0.5s'
			        }),

			    elements: {
			        nodes: [{
			                data: {
			                    id: 'a'
			                }
			            },
			            {
			                data: {
			                    id: 'b'
			                }
			            },
			            {
			                data: {
			                    id: 'c'
			                }
			            },
			            {
			                data: {
			                    id: 'd'
			                }
			            },
			            {
			                data: {
			                    id: 'e'
			                }
			            }
			        ],

			        edges: [{
			                data: {
			                    id: 'a"e',
			                    weight: 1,
			                    source: 'a',
			                    target: 'e'
			                }
			            },
			            {
			                data: {
			                    id: 'ab',
			                    weight: 3,
			                    source: 'a',
			                    target: 'b'
			                }
			            },
			            {
			                data: {
			                    id: 'be',
			                    weight: 4,
			                    source: 'b',
			                    target: 'e'
			                }
			            },
			            {
			                data: {
			                    id: 'bc',
			                    weight: 5,
			                    source: 'b',
			                    target: 'c'
			                }
			            },
			            {
			                data: {
			                    id: 'ce',
			                    weight: 6,
			                    source: 'c',
			                    target: 'e'
			                }
			            },
			            {
			                data: {
			                    id: 'cd',
			                    weight: 2,
			                    source: 'c',
			                    target: 'd'
			                }
			            },
			            {
			                data: {
			                    id: 'de',
			                    weight: 7,
			                    source: 'd',
			                    target: 'e'
			                }
			            }
			        ]
			    },

			    layout: {
			        name: 'breadthfirst',
			        directed: true,
			        roots: '#a',
			        padding: 10
			    }
			});

			var bfs = cy.elements().bfs('#a', function() {}, true);

			var i = 0;
			var highlightNextEle = function() {
			    if (i < bfs.path.length) {
			        bfs.path[i].addClass('highlighted');

			        i++;
			        setTimeout(highlightNextEle, 1000);
			    }
			};

			// kick off first highlight
			highlightNextEle();
		}
		
	});
});