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
				disabled: true,
				handler: function() {
					me.fireEvent('setTime', 0);
				}
			},{
				xtype: 'button',
				text: '<',
				itemId: 'prevbtn',
				disabled: true,
				handler: function() {
					me.fireEvent('setTime', me.currentTime - 1);
				}
			},{
				xtype: 'button',
				text: '>',
				itemId: 'nextbtn',
				disabled: true,
				handler: function() {
					me.fireEvent('setTime', me.currentTime + 1);
				}
			},{
				xtype: 'button',
				text: '>|',
				itemId: 'endbtn',
				disabled: true
			},{
				xtype: 'slider',
				width: 100,
				value: 0,
				margin: "5 5 5 15",
				disabled: true
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
					width: 520,
					items: [
						this.drawPanel,
						this.controls
					]
				}, this.sidemenu
				]
			}]
		});
		this.callParent(arguments);

		this.addListener('setTime', function(newTime) {
			if(this.sim != undefined) {
				var totalAvailable = this.sim.timeline().getTotalCount()
				if(newTime > totalAvailable) {
					newTime = totalAvailable;
				}
				this.currentTime = newTime;
				this.drawPanel.setTimeStep(newTime);
				this.controls.setProgress(newTime, totalAvailable);

				// dynamic data loading
				if(this.maxId - this.currentTime <= 25) {
					this.sim.timeline().guaranteeRange(this.currentTime, this.currentTime + 100);
					this.maxId = this.currentTime + 100;
				}
			}
		}, this);
	},
	loadSimulationById : function(id) {
		if(this.sim != null) {
			this.sim.timeline().un('guaranteedrange', this.onGuaranteedRange);
		}
		this.controls.disablePanel();
		this.controls.setLoading();
		this.sim = this.store.getById(id);
		if(this.sim != null) {
			// initialise controls & load sim data
			this.sim.timeline().on('guaranteedrange', this.onGuaranteedRange, this);
			this.currentTime = 0;
			this.sim.timeline().guaranteeRange(0, 25, this.onInitialLoad, this);
		}
	},
	onInitialLoad : function() {
		this.maxId = 25;
		console.log(this.sim.timeline().getTotalCount());
		this.controls.setProgress(0, this.sim.timeline().getTotalCount());
		this.controls.enablePanel();
		this.drawPanel.loadSimulation(this.sim.getId());
	},
	onGuaranteedRange : function(range, start, end) {
		this.sim.timeline().loadRecords(range);
	}
});
