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
	}]
});