Ext.require('Presage2.store.SimulationsTree');

Ext.define('Presage2.view.SimulationsTable', {
	extend: 'Ext.Panel',
	alias: 'widget.simulations-table',
	store: 'Simulations',
	requires: ['Presage2.view.ParametersField'],
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

		var simulations = Ext.data.StoreManager.lookup('Simulations');

		var grid = Ext.create('Ext.grid.Panel', {
			store: simulations,
			columns: [
				{text: 'ID', dataIndex: 'id', width: 40},
				{text: 'Name', dataIndex: 'name', width: 160},
				{text: 'Class name', dataIndex: 'classname', width: 300},
				{text: 'State', dataIndex: 'state'},
				{
					text: 'Progress', 
					dataIndex: 'currentTime',
					renderer: function(value, cell, record) {
						return Ext.String.format('{0}/{1}', record.data.currentTime, record.data.finishTime);
					}
				}, 
				{
					text: 'Created',
					dataIndex: 'createdAt',
					width: 120,
					renderer: formatDate
				},
				{
					text: 'Started',
					dataIndex: 'startedAt',
					width: 120,
					renderer: formatDate
				},
				{
					text: 'Finished',
					dataIndex: 'finishedAt',
					width: 120,
					renderer: formatDate
				},
				{
					text: 'Parameters',
					dataIndex: 'parameters',
					flex: true,
					minWidth: 200,
					sortable: false,
					renderer: function(value) {
						var params = "";
						Ext.Object.each(value, function(key, value) {
							params += Ext.String.format('<span class="parameter">{0}:{1}</span>', key, value);
						});
						return params;
					}
				}
			],
			region: 'center',
			split: true,
			listeners: {
				itemdblclick: function(model, record) {
					var simDetails = Ext.create('Presage2.view.SimulationsDetails', {
						sim: record
					});
					simDetails.show();
				}
			},
			dockedItems: [
				Ext.create('Ext.PagingToolbar', {
					dock: 'bottom',
					store: simulations,
					displayInfo: true,
					displayMsg: 'Displaying simulations {0} - {1} of {2}',
					emptyMsg: 'No simulations to display'
				}),{
					xtype: 'toolbar',
					dock: 'top',
					items: [{
						text: 'New Simulation',
						//iconCls: 'add',
						tooltop: 'Add a new simulation',
						handler: function() {
							Ext.create('Ext.window.Window', {
								title: 'New Simulation',
								layout: 'fit',
								y: 200,
								items: [{
									xtype: 'form',
									bodyStyle: 'padding:5px 5px 0',
									frame: true,
									items: [{
										xtype: 'textfield',
										fieldLabel: 'Name',
										name: 'name',
										minLength: 1
									},{
										xtype: 'textfield',
										fieldLabel: 'Class name',
										name: 'classname',
										minLength: 1
									},{
										fieldLabel: 'Initial State',
										name: 'state',
										xtype: 'combo',
										allowBlank: false,
										store: [
											'NOT STARTED',
											'AUTO START'
										]
									},{
										xtype: 'numberfield',
										fieldLabel: 'Finish time',
										name: 'finishTime',
										allowBlank: false
									},{
										xtype: 'parameters-field',
										name: 'parameters'
									}],
									buttons: [{
										text: 'Save',
										handler: function() {
											var form = this.up('form').getValues();
											form.id = null;
											form.currentTime = 0;
											form.createdAt = 0;
											form.startedAt = 0;
											form.finishedAt = 0;
											simulations.add(form);
											this.up('window').close();
										}
									},{
										text: 'Cancel',
										handler: function() {
											this.up('window').close();
										}
									}]
								}]
							}).show();
						}
					}]
				}
			]
		});

		Ext.apply(this, {
			layout: 'border',
			frame: true,
			items: [
				grid
			]
		});
		this.callParent(arguments);
	}
});
