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
		var grid = Ext.create('Ext.grid.Panel', {
			store: 'Simulations',
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
			minHeight: 500,
			bbar: Ext.create('Ext.PagingToolbar', {
				store: 'Simulations',
				displayInfo: true,
				displayMsg: 'Displaying simulations {0} - {1} of {2}',
				emptyMsg: 'No simulations to display'
			})
		})
		Ext.apply(this, {
			id: "sims-table",
			layout: 'border',
			frame: true,
			items: [
				grid
			]
		});
		this.callParent(arguments);

	}
});
