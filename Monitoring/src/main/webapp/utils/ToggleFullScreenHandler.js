/*!
 * ${copyright}
 */

// Provides a simple search feature
sap.ui.define(['jquery.sap.global'],
	function(jQuery) {
	"use strict";


	var ToggleFullScreenHandler = {
			
		updateMode : function(oEvt, oView) {
			if (!this._oShell) {
				this._oShell = sap.ui.getCore().byId('dataHub-monitor-shell');
			}
			var bSwitchToFullScreen = (this._getSplitApp().getMode() === "ShowHideMode");
			if (bSwitchToFullScreen) {
				this._getSplitApp().setMode('HideMode');
			} else {
				this._getSplitApp().setMode('ShowHideMode');
			}
			this.updateControl(oEvt.getSource(), oView, bSwitchToFullScreen);
		},


		_getSplitApp : function () {
			if (!this._oSplitApp) {
				this._oSplitApp = sap.ui.getCore().byId("__xmlview2--datahub-monitor-splitapp");
			}
			return this._oSplitApp;
		},

		updateControl : function (oButton, oView, bFullScreen) {
			if (arguments.length === 2) {
				bFullScreen = !(this._getSplitApp().getMode() === "ShowHideMode");
			}
			if (!bFullScreen) {
				oButton.setIcon('sap-icon://full-screen');
			} else {
				oButton.setIcon('sap-icon://exit-full-screen');
			}
		},

		cleanUp : function() {
			this._oSplitApp = null;
			this._oShell = null;
		}
	};

	return ToggleFullScreenHandler;

}, /* bExport= */ true);