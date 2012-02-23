Ext.define('Presage2.view.NewSimulation', {
	extend: 'Ext.window.Window',
	title: 'New Simulation',
	alias: 'widget.new-simulation',
	layout: 'fit',
	y: 200,
	initComponent: function() {
		var me = this;
		me.store = Ext.data.StoreManager.lookup('Simulations');
		me.formPanel = Ext.create('Ext.form.Panel', {
			bodyStyle: 'padding:5px 5px 0',
			frame: true,
			items: [],
			buttons: [{
				text: 'Save',
				handler: function() {
					var form = this.up('form').getValues();
					form.id = null;
					form.currentTime = 0;
					form.createdAt = 0;
					form.startedAt = 0;
					form.finishedAt = 0;
					if(me.group) {
						form.state = 'GROUP';
						form.finishTime = 0;
					}
					me.store.add(form);
					this.up('window').close();
				}
			},{
				text: 'Cancel',
				handler: function() {
					this.up('window').close();
				}
			}]
		});
		// name & classname fields
		me.formPanel.add([{
			xtype: 'textfield',
			fieldLabel: 'Name',
			name: 'name',
			minLength: 1
		},{
			xtype: 'textfield',
			fieldLabel: 'Class name',
			name: 'classname',
			minLength: 1,
			value: me.group ? 'group' : ''
		}]);
		if(!me.group) {
			me.formPanel.add({
				fieldLabel: 'Initial State',
				name: 'state',
				xtype: 'combo',
				allowBlank: false,
				store: [
					'NOT STARTED',
					'AUTO START',
					'GROUP'
				]
			},{
				xtype: 'numberfield',
				fieldLabel: 'Finish time',
				name: 'finishTime',
				allowBlank: false
			});
		}
		me.formPanel.add({
			xtype: 'parameters-field',
			name: 'parameters'
		});
		Ext.apply(this, {
			items: [me.formPanel]
		});
		this.callParent(arguments);
	}
});