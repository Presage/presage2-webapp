Ext.require([
	'Presage2.view.2DVisualiser'
]);

Ext.define('Presage2.view.VisualiserPlugin', {
	extend: 'Ext.Panel',
	alias: 'widget.visualiserplugin',
	initComponent: function() {
		var me = this;
		this.store = Ext.data.StoreManager.lookup('Simulations');
		this.drawPanel = Ext.create('Presage2.view.2DVisualiser', {
			layout: 'fit',
			height: 500,
			border: 5
		});
		this.controls = Ext.create('Ext.Panel', {
			layout: {
				type: 'table',
				columns: 5
			},
			margin: 5,
			padding: 5,
			width: 500,
			items: [{
				xtype: 'button',
				text: 'Play',
				colspan: 4,
				layout: 'fit',
				flex: 2,
				width: 80,
				margin: 'auto',
				itemId: 'playbtn',
				disabled: true
			},{
				xtype: 'progressbar',
				width: 300,
				animate: true,
				value: 0.5,
				margin: 5,
				itemId: 'progress',
				disabled: true
			},{
				xtype: 'button',
				text: '|<',
				itemId: 'startbtn',
				disabled: true
			},{
				xtype: 'button',
				text: '<',
				itemId: 'prevbtn',
				disabled: true
			},{
				xtype: 'button',
				text: '>',
				itemId: 'nextbtn',
				disabled: true
			},{
				xtype: 'button',
				text: '>|',
				itemId: 'endbtn',
				disabled: true
			},{
				xtype: 'slider',
				width: 100,
				value: 0,
				margin: "5 5 5 15"
			}],
			enablePanel: function() {
				this.getComponent('playbtn').enable();
				this.getComponent('progress').enable();
				this.getComponent('startbtn').enable();
				this.getComponent('prevbtn').enable();
				this.getComponent('nextbtn').enable();
				this.getComponent('endbtn').enable();
			},
			disablePanel: function() {
				this.getComponent('playbtn').disable();
				this.getComponent('progress').disable();
				this.getComponent('startbtn').disable();
				this.getComponent('prevbtn').disable();
				this.getComponent('nextbtn').disable();
				this.getComponent('endbtn').disable();
			},
			setLoading: function() {
				this.getComponent('progress').setLoading(true);
			},
			setProgress: function(current, max) {
				if(max == 0) {
					max = 1;
				}
				var p = this.getComponent('progress');
				p.setLoading(false);
				p.updateProgress(current / max, Ext.String.format("{0}/{1}", current, max));
			}
		});
		this.sidemenu = Ext.create('Ext.Panel', {
			//width: 200,
			layout: {
				type: 'vbox',
				align: 'top'
			},
			flex: 1,
			border: 5,
			items: [{
				xtype: 'fieldset',
				layout: 'hbox',
				title: 'Choose a simulation',
				margin: 10,
				width: 250,
				items: [{
					xtype: 'combo',
					store: 'Simulations',
					valueField: 'id',
					displayField: 'id',
					margin: 5
				},{
					xtype: 'button',
					text: 'Load',
					margin: 5,
					handler: function() {
						this.disable();
						var combo = this.ownerCt.down('combo');
						combo.disable();
						// check for existance
						if(me.store.getById(combo.getValue()) !== null) {
							me.loadSimulationById(combo.getValue());
						}
						combo.enable();
						this.enable();
					}
				}]
			}]
		});
		Ext.apply(this, {
			layout: 'fit',
			border: 5,
			padding: 5,
			items:[{
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items: [{
					layout: {
						type: 'vbox',
						align: 'top'
					},
					items: [
						this.drawPanel,
						this.controls
					]
				}, this.sidemenu
				]
			}]
		});
		this.callParent(arguments);
	},
	loadSimulationById : function(id) {
		if(this.sim != null) {
			//me.sim.timeline().un()
		}
		this.controls.disablePanel();
		this.controls.setLoading();
		this.sim = this.store.getById(id);
		if(this.sim != null) {
			// initialise controls & load sim data
			this.sim.timeline().on('load', this.onInitialLoad, this);
			this.sim.timeline().load({start: 0, limit: 25});
			this.currentTime = 0;
			this.maxId = 0;
		}
	},
	onInitialLoad : function() {
		this.sim.timeline().un(this.onInitialLoad);
		this.maxId = 25;
		console.log(this.sim.timeline().getTotalCount());
		this.controls.setProgress(0, this.sim.timeline().getTotalCount());
		this.controls.enablePanel();
	}
});