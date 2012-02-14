Ext.define('Presage2.model.TransientData', {
	extend: 'Ext.data.Model',
	fields: ['time'],
	idProperty: 'time',
	belongsTo: 'Simulations',
	hasMany: {
		model: 'Presage2.model.AgentState',
		name: 'agents'
	},
	proxy: {
		type: 'ajax',
		url: '/simdata',
		reader: {
			type: 'json',
			root: 'data',
			totalProperty: 'totalCount'
		},
		pageParam: undefined,
		batchActions: false
	}
});
