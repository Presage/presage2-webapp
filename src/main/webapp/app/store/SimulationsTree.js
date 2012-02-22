Ext.define('Presage2.store.SimulationsTree', {
	extend: 'Ext.data.TreeStore',
	model: 'Presage2.model.Simulation',
	proxy: {
		type: 'rest',
		url: 'simulationstree',
		root: 'data'
	},
	defaultRootProperty: 'data',
	autoSync: true,
	clearOnLoad: false
});
