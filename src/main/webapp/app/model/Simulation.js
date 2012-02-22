Ext.require([
	'Presage2.model.TransientData',
	'Presage2.model.AgentState'
]);

Ext.define('Presage2.model.Simulation', {
	extend: 'Ext.data.Model',
	fields: [{
			name: 'id',
			type: 'long',
			useNull: true
		}, 
		'name', 
		'classname', 
		'state', 
		'currentTime', 
		'finishTime', 
		'createdAt', 
		'startedAt', 
		'finishedAt', 
		'parameters'
	],
	hasMany: {
		model: 'Presage2.model.TransientData',
		name: 'timeline',
		storeConfig: {
			pageSize: 50,
			buffered: true
		}
	},
	validations: [{
		type: 'length',
		field: 'name',
		min: 1
	}, {
		type: 'length',
		field: 'classname',
		min: 1
	}, {
		type: 'length',
		field: 'state',
		min: 1
	}],
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
		}
	}
});
