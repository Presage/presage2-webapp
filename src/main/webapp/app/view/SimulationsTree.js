Ext.define('Presage2.view.SimulationsTree', {
	extend: 'Ext.Panel',
	alias: 'widget.simulations-tree',
	store: 'SimulationsTree',
	requires: ['Presage2.view.ParametersField'],
	initComponent: function() {
		var simulations = Ext.data.StoreManager.lookup('Simulations'),
			simulationsTree = Ext.data.StoreManager.lookup('SimulationsTree');

		simulations.on('write', function() {
			simulationsTree.getRootNode().removeAll();
			simulationsTree.load();
		});

		Ext.apply(this, {
			layout: 'fit',
			items: [{
				xtype: 'treepanel',
				store: simulationsTree,
				singleExpand: true,
				rootVisible: false,
				useArrows: true,
				viewConfig: {
					plugins: {
						ptype: 'treeviewdragdrop'
					}
				},
				columns: [{
					xtype: 'treecolumn',
					text: 'ID', 
					dataIndex: 'id',
					flex: 0.5
				},{
					text: 'Name',
					flex: 2,
					dataIndex: 'name'
				},{
					text: 'Class name',
					dataIndex: 'classname',
					flex: 3
				},{
					text: 'State',
					dataIndex: 'state',
					flex: 1
				},{
					text: 'Progress', 
					dataIndex: 'currentTime',
					renderer: function(value, cell, record) {
						return Ext.String.format('{0}/{1}', record.data.currentTime, record.data.finishTime);
					},
					flex: 1
				}],
				listeners: {
					itemdblclick: function(model, record) {
						Presage2.model.Simulation.load(record.getId(), {
							success: function(r, op) {
								var simDetails = Ext.create('Presage2.view.SimulationsDetails', {
									sim: r
								});
								simDetails.show();
							}
						});
					},
					update: function() {
						simulationsTree.sync();
					}
				},
				dockedItems: [{
					xtype: 'toolbar',
					dock: 'top',
					items: [{
						text: 'New Group',
						iconCls: 'folder',
						tooltop: 'Add a new group',
						handler: function() {
							Ext.create('Ext.window.Window', {
								title: 'New Simulation',
								layout: 'fit',
								y: 200,
								items: [{
									xtype: 'form',
									bodyStyle: 'padding:5px 5px 0',
									frame: true,
									items: [{
										xtype: 'textfield',
										fieldLabel: 'Name',
										name: 'name',
										minLength: 1
									},{
										xtype: 'textfield',
										fieldLabel: 'Class name',
										name: 'classname',
										minLength: 1,
										value: 'folder'
									}],
									buttons: [{
										text: 'Save',
										handler: function() {
											var form = this.up('form').getValues();
											form.id = null;
											form.state = "GROUP";
											form.finishTime = 0;
											form.currentTime = 0;
											form.createdAt = 0;
											form.startedAt = 0;
											form.finishedAt = 0;
											form.parameters = {};
											simulations.add(form);
											this.up('window').close();
										}
									},{
										text: 'Cancel',
										handler: function() {
											this.up('window').close();
										}
									}]
								}]
							}).show();
						}
					}]
				}]
			}]
		});
		this.callParent(arguments);
	}
});
