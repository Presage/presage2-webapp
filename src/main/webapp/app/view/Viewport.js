Ext.define('Presage2.view.Viewport', {
	extend: 'Ext.container.Viewport',
	
	requires: ['Presage2.view.SimulationsTable'],
	
	initComponent: function() {
		Ext.apply(this, {
			id: 'app-viewport',
			layout: {
				type: 'border',
				padding: '0 5 5 5'
			},
			items: [{
				id: 'app-header',
				xtype: 'box',
				region: 'north',
				height: 40,
				html: 'Presage2'
			},{
				xtype: 'container',
				region: 'center',
				layout: 'border',
				items: [{
					id: 'app-options',
					title: 'Menu',
					region: 'west',
					animCollapse: true,
					width: 200,
					minWidth: 150,
					maxWidth: 400,
					split: true,
					collapsible: true,
					collapsed: true
				},{
					id: 'app-portal',
					region: 'center',
					xtype: 'tabpanel',
					items: [{
						title: 'Simulations',
						xtype: 'simulations-table'
					}]
				}]
			}]
		});
		this.callParent(arguments);
	}
});