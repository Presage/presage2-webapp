Ext.define('Presage2.view.SimulationsTree', {
	extend: 'Ext.Panel',
	alias: 'widget.simulations-tree',
	store: 'SimulationsTree',
	requires: ['Presage2.view.ParametersField'],
	initComponent: function() {
		var simulations = Ext.data.StoreManager.lookup('Simulations'),
			simulationsTree = Ext.data.StoreManager.lookup('SimulationsTree');

		// reload tree when something is changed in simulation store
		simulations.on('write', function() {
			simulationsTree.getRootNode().removeAll();
			simulationsTree.load();
		});
		simulations.on('update', function() {
			simulationsTree.getRootNode().removeAll();
			simulationsTree.load();
		});
		// auto sync changes back to rest proxy
		simulationsTree.on('move', function() {
			simulationsTree.sync();
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
					flex: 1
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
				},{
					text: 'Parameters',
					dataIndex: 'parameters',
					flex: 3.5,
					sortable: false,
					renderer: function(value) {
						var params = "";
						Ext.Object.each(value, function(key, value) {
							params += Ext.String.format('<span class="parameter">{0}:{1}</span>', key, value);
						});
						return params;
					}
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
						text: 'New Simulation',
						iconCls: Ext.baseCSSPrefix +'tree-icon-leaf',
						tooltop: 'Add a new simulation',
						handler: function() {
							Ext.create('Presage2.view.NewSimulation').show();
						}
					},
					'-',
					{
						text: 'New Group',
						iconCls: Ext.baseCSSPrefix +'tree-icon-parent',
						tooltop: 'Add a new simulation group',
						handler: function() {
							Ext.create('Presage2.view.NewSimulation', {group: true}).show();
						}
					},
					'->',
					{
						itemId: 'refresh',
						tooltip: 'Refresh',
						overflowText: 'Refresh',
						iconCls: Ext.baseCSSPrefix + 'tbar-loading',
						handler: function() {
							simulationsTree.getRootNode().removeAll();
							simulationsTree.load();
						}
					}]
				}]
			}]
		});
		this.callParent(arguments);
	}
});
