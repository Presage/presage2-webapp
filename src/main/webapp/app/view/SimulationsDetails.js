Ext.define('Presage2.view.SimulationsDetails', {
	extend: 'Ext.form.Panel',
	alias: 'widget.simulations-details',
	store: 'Simulations',
	requires: ['Presage2.view.ParametersField'],
	parameterGrid: Ext.create('Ext.grid.property.Grid', {
		title: 'Parameters',
		source: {}
	}),
	initComponent: function() {
		var formatDate = function(value) {
			if(value > 0) {
				return Ext.Date.format(new Date(value), "Y-m-d H:i:s");
			} else {
				return "--"
			}
		};
		var formatDateField = function(field, newValue, oldValue) {
			var timestamp = parseFloat(newValue);
			if(!isNaN(timestamp) && isFinite(newValue)) {
				field.setValue(formatDate(timestamp));
			}
		}

		Ext.apply(this, {
			xtype: 'form',
			title: 'Simulation Details',
			split: true,
			frame: true,
			height: 260,
			layout: {
				type: 'hbox',
				align: 'stretch'
			},
			fieldDefaults: {
				labelAlign: 'left',
				msgTarget: 'side'
			},
			items: [{
				xtype: 'fieldset',
				defaultType: 'textfield',
				flex: 1,
				bodyPadding: 5,
				border: 0,
				items: [{
					xtype: 'displayfield',
					fieldLabel: 'ID',
					name: 'id',
				},{
					fieldLabel: 'Name',
					name: 'name',
					width: 400
				},{
					fieldLabel: 'Class name',
					name: 'classname',
					width: 500
				},{
					fieldLabel: 'State',
					name: 'state',
					xtype: 'combo',
					allowBlank: false,
					store: [
						'LOADING',
						'READY',
						'INITIALISING',
						'RUNNING',
						'PAUSED',
						'STOPPED',
						'FINISHING',
						'COMPLETE'
					]
				},{
					fieldLabel: 'Current Time',
					name: 'currentTime',
					xtype: 'displayfield',
					value: ''
				},{
					fieldLabel: 'Finish Time',
					name: 'finishTime',
					xtype: 'displayfield',
					value: ''
				},{
					fieldLabel: 'Created',
					name: 'createdAt',
					xtype: 'displayfield',
					value: '',
					width: 300,
					listeners: {
						change: formatDateField
					}
				},{
					fieldLabel: 'Started',
					name: 'startedAt',
					xtype: 'displayfield',
					value: '',
					width: 300,
					listeners: {
						change: formatDateField
					}
				},{
					fieldLabel: 'Finished',
					name: 'finishedAt',
					xtype: 'displayfield',
					value: '',
					width: 300,
					listeners: {
						change: formatDateField
					}
				}]
			},{
				xtype: 'fieldset',
				defaultType: 'textfield',
				flex: 1,
				bodyPadding: 5,
				border: 0,
				items: [{
					xtype: 'parameters-field',
					name: 'parameters'
				}]
			}],
			listeners: {
				
			}
		});
		this.callParent(arguments);
	}
});
