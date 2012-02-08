Ext.define('Presage2.store.Simulations', {
	extend: 'Ext.data.Store',
	model: 'Presage2.model.Simulation',
	storeId: 'simulationStore',
	proxy: {
		type: 'rest',
		url: 'simulations',
		reader: {
			type: 'json',
			root: 'data',
			totalProperty: 'totalCount'
		},
		writer: {
			type: 'json'
		},
		pageParam: undefined,
		simpleSortMode: true
	},
	autoLoad: true,
	autoSync: true,
	remoteSort: true,
	pageSize: 25,
	buffered: true
});
