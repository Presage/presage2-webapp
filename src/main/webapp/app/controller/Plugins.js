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
		Ext.ComponentManager.get('app-tabs').add(
			Ext.create(plugin.data.class, {
				title: plugin.data.name,
				closable: true
			})
		);
	}
});
