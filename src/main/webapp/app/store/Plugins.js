Ext.define('Presage2.store.Plugins', {
	extend: 'Ext.data.Store',
	model: 'Presage2.model.Plugin',
	proxy: {
		type: 'ajax',
		url: 'data/plugins.json',
		reader: {
			type: 'json',
			root: 'plugins'
		}
	}
});
