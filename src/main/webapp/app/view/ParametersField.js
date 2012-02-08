Ext.define('Presage2.view.ParametersField', {
	extend: 'Ext.grid.property.Grid',
	mixins: ['Ext.form.field.Field'],
	alias: 'widget.parameters-field',
	initComponent: function() {
		/*this.propertyGrid = Ext.create('Ext.grid.property.Grid', {
			title: 'Parameters',
			source: {}
		});*/
		Ext.apply(this, {
			name: 'parameters',
			title: 'Parameters',
			source: {}
		});
		this.callParent();
	},
	setValue: function(newValue) {
		this.setSource(newValue);
	}
});
