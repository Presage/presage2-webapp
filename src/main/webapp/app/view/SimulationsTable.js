Ext.require([
	'Presage2.store.SimulationsTree',
	'Presage2.view.NewSimulation'
]);

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
				{
					text: 'ID',
					dataIndex: 'id',
					flex: 0.4
				},
				{
					text: 'Name',
					dataIndex: 'name',
					felx: 1
				},
				{
					text: 'Class name',
					dataIndex: 'classname',
					flex: 3
				},
				{
					text: 'State',
					dataIndex: 'state',
					flex: 1
				},
				{
					text: 'Progress', 
					dataIndex: 'currentTime',
					flex: 0.8,
					renderer: function(value, cell, record) {
						return Ext.String.format('{0}/{1}', record.data.currentTime, record.data.finishTime);
					}
				}, 
				{
					text: 'Created',
					dataIndex: 'createdAt',
					flex: 1.5,
					renderer: formatDate
				},
				{
					text: 'Started',
					dataIndex: 'startedAt',
					flex: 1.5,
					renderer: formatDate
				},
				{
					text: 'Finished',
					dataIndex: 'finishedAt',
					flex: 1.5,
					renderer: formatDate
				},
				{
					text: 'Parameters',
					dataIndex: 'parameters',
					flex: 4,
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
					Ext.create('Presage2.view.SimulationsDetails', {
						simId: record.getId()
					});
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
						iconCls: Ext.baseCSSPrefix +'tree-icon-leaf',
						tooltop: 'Add a new simulation',
						handler: function() {
							Ext.create('Presage2.view.NewSimulation').show();
						}
					},
					'-',
					{
						text: 'New Group',
						iconCls: Ext.baseCSSPrefix +'tree-icon-parent',
						tooltop: 'Add a new simulation group',
						handler: function() {
							Ext.create('Presage2.view.NewSimulation', {group: true}).show();
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
