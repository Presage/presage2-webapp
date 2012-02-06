Ext.define('Presage2.model.Simulation', {
	extend: 'Ext.data.Model',
	fields: [
		'id', 
		'name', 
		'classname', 
		'state', 
		'currentTime', 
		'finishTime', 
		'createdAt', 
		'startedAt', 
		'finishedAt', 
		'parameters'
	]
});