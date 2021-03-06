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
				}
			}],
			hideHeaders: true,
			width: 200,
				listeners: {
					'itemdblclick': function(dataview, record, item, index) {
						// fire openplugin event
						this.fireEvent('openplugin', record); 
					}
				}
		});
		Ext.data.StoreManager.lookup('Plugins').load({
			scope: this,
			callback: function(records, operation, success) {
				// preload all plugins
				var plugins = []
				Ext.Array.each(records, function(record) {
					plugins.push(record.data.class);
				});
				Ext.require(plugins);
			}
		});
		this.callParent(arguments);
	}
});
