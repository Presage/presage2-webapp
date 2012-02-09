Ext.define('Presage2.view.ParametersField', {
	extend: 'Ext.grid.property.Grid',
	mixins: ['Ext.form.field.Field'],
	alias: 'widget.parameters-field',
	initComponent: function() {
		var me = this;
		Ext.apply(this, {
			name: 'parameters',
			title: 'Parameters',
			source: {},
			dockedItems: [{
				xtype: 'toolbar',
				dock: 'top',
				items: [{
					text: 'Add',
					//iconCls: 'add',
					tooltop: 'Add a new parameter',
					handler: function() {
						Ext.create('Ext.window.Window', {
							title: 'Add Parameter',
							layout: 'fit',
							constrain: true,
							renderTo: me.getComponent(),
							items: [{
								xtype: 'form',
								bodyStyle: 'padding:5px 5px 0',
								frame: true,
								items: [{
									xtype: 'textfield',
									fieldLabel: 'Parameter',
									name: 'key',
									minLength: 1
								},{
									xtype: 'textfield',
									fieldLabel: 'Value',
									name: 'value',
									minLength: 1
								}],
								buttons: [{
									text: 'Save',
									handler: function() {
										var param = this.up('form').getValues();
										me.setProperty(param.key, param.value, true);
									}
								}]
							}]
						}).show();
					}
				}]
			}]
		});
		this.callParent();
	},
	setValue: function(newValue) {
		this.setSource(newValue);
	},
	getValue: function() {
		return this.getSource();
	}
});
