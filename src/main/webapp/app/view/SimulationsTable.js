Ext.define('Presage2.view.SimulationsTable', {
	extend: 'Ext.Panel',
	alias: 'widget.simulations-table',
	store: 'Simulations',
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
				selectionchange: function(model, records) {
					if (records[0]) {
						simDetails.getForm().loadRecord(records[0]);
						simDetails.expand();
					}
				}
			},
			dockedItems: [
				Ext.create('Ext.PagingToolbar', {
					dock: 'bottom',
					store: simulations,
					displayInfo: true,
					displayMsg: 'Displaying simulations {0} - {1} of {2}',
					emptyMsg: 'No simulations to display'
				})
			]
		});
		
		var simDetails = Ext.create('Ext.form.Panel', {
			store: simulations,
			xtype: 'form',
			title: 'Simulation Details',
			region: 'south',
			split: true,
			frame: true,
			animCollapse: true,
			collapsible: true,
			collapsed: true,
			height: 200,
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
					fieldLabel: 'ID',
					name: 'id',
					disabled: true
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
				}]
			},{
				xtype: 'fieldset',
				defaultType: 'textfield',
				maxHeight: 200,
				flex: 1,
				bodyPadding: 5,
				border: 0,
				items: [{
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
			}]
		});

		Ext.apply(this, {
			id: "sims-table",
			layout: 'border',
			frame: true,
			items: [
				grid, simDetails
			]
		});
		this.callParent(arguments);
		simulations.loadPage(1);
	}
});
