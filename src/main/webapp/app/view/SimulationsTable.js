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
		
		var simDetails = Ext.create('Presage2.view.SimulationsDetails', {
			region: 'south',
			animCollapse: true,
			collapsible: true,
			collapsed: true,
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
