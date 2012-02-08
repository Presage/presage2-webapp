Ext.define('Presage2.view.PluginSelector', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.plugin-selector',
	store: 'Plugins',
	initComponent: function() {
		Ext.apply(this, {
			columns: [{ 
				dataIndex: 'name',
				flex: 1,
				renderer: function(value, p, record) {
					return Ext.String.format("<b>{0}</b><p>{1}", value, record.data.description);
				},
				listeners: {
					dblclick: function() {
						// TODO open plugin in new tab.
						console.log('Dbl click');
					}
				}
			}],
			hideHeaders: true,
			width: 200
		});
		Ext.data.StoreManager.lookup('Plugins').load();
		this.callParent(arguments);
	}
});
