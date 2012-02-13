Ext.define('Presage2.model.AgentState', {
	extend: 'Ext.data.Model',
	fields: ['aid', 'data'],
	idProperty: 'aid',
	belongsTo: 'Presage2.model.TransientData'
});