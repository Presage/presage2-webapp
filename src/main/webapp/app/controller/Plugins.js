Ext.define('Presage2.controller.Plugins', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'plugin-selector': {
				openplugin: this.openPlugin
			}
		});
		this.application.on({
			openplugin: this.openPlugin
		});
	},
	openPlugin: function(plugin) {
		var plugin = Ext.create(plugin.data.class, {
			title: plugin.data.name,
			closable: true
		}), tabbar = Ext.ComponentManager.get('app-tabs');
		tabbar.add(plugin);
		tabbar.setActiveTab(plugin);
	}
});
