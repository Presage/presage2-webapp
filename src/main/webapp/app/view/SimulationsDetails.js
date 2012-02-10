Ext.define('Presage2.view.SimulationsDetails', {
	extend: 'Ext.window.Window',
	alias: 'widget.simulations-details',
	store: 'Simulations',
	requires: ['Presage2.view.ParametersField'],
	parameterGrid: Ext.create('Ext.grid.property.Grid', {
		title: 'Parameters',
		source: {}
	}),
	initComponent: function() {
		var me = this;
		
		Ext.define('Presage2.view.TimeField', {
			extend: 'Ext.form.field.Base',
			alias: 'widget.unixtimefield',
			disabled: true,
			valueToRaw: function(value) {
				if(value > 0) {
					return Ext.Date.format(new Date(value), "Y-m-d H:i:s");
				} else {
					return "--"
				}
			},
			rawToValue: function(raw) {
				if(raw == "--") {
					return 0;
				} else {
					return new Date(raw).getTime();
				}
			}
		});

		Ext.apply(this, {
			title: 'Simulation Details',
			layout: 'fit',
			y: 200,
			items: [{
				xtype: 'form',
				fieldDefaults: {
					labelAlign: 'left',
					msgTarget: 'side'
				},
				bodyStyle: 'padding:5px 5px 0',
				frame: true,
				defaultType: 'textfield',
				items: [{
					xtype: 'displayfield',
					fieldLabel: 'ID',
					name: 'id',
				},{
					xtype: 'displayfield',
					fieldLabel: 'Name',
					name: 'name',
					width: 450
				},{
					xtype: 'displayfield',
					fieldLabel: 'Class name',
					name: 'classname',
					width: 450
				},{
					fieldLabel: 'State',
					name: 'state',
					xtype: 'combo',
					allowBlank: false,
					store: [
						'NOT STARTED',
						'AUTO START',
						'PAUSED',
						'STOPPED',
						'COMPLETE'
					]
				},{
					fieldLabel: 'Current Time',
					name: 'currentTime',
					xtype: 'numberfield',
					value: ''
				},{
					fieldLabel: 'Finish Time',
					name: 'finishTime',
					xtype: 'numberfield',
					value: ''
				},{
					fieldLabel: 'Created',
					name: 'createdAt',
					xtype: 'unixtimefield',
					value: '',
					width: 300
				},{
					fieldLabel: 'Started',
					name: 'startedAt',
					xtype: 'unixtimefield',
					value: '',
					width: 300
				},{
					fieldLabel: 'Finished',
					name: 'finishedAt',
					xtype: 'unixtimefield',
					value: '',
					width: 300
				},{
					xtype: 'parameters-field',
					name: 'parameters'
				}],
				buttons: [{
					text: 'Save',
					handler: function() {
						me.down('form').getForm().updateRecord(me.sim);
						me.close();
					}
				},{
					text: 'Cancel',
					handler: function() {
						me.close();
					}
				}]
			}]
		});
		this.callParent(arguments);
		if(this.sim != undefined) {
			this.down().getForm().loadRecord(this.sim);
		}
	}
});
