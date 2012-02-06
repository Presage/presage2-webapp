Ext.define('Presage2.store.Simulations', {
	extend: 'Ext.data.Store',
	model: 'Presage2.model.Simulation',
	storeId: 'simulationStore',
	proxy: {
		type: 'ajax',
		url: 'simulations',
		reader: {
			type: 'json',
			root: 'simulations',
			totalProperty: 'totalCount'
		},
		pageParam: undefined,
		simpleSortMode: true
	},
	autoLoad: true,
	remoteSort: true,
	pageSize: 35
});
