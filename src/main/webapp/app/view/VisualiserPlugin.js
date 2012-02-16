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
				disabled: true,
				handler: function() {
					// invert playing
					if(me.controls.playing == undefined) {
						me.controls.playing = true;
					} else {
						me.controls.playing = !me.controls.playing;
					}

					if(me.controls.playing) {
						// start
						this.setText('Pause');
						me.controls.play();
					} else {
						// stop
						this.setText('Play');
						clearTimeout(me.controls.timeout);
					}
				}
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
				itemId: 'speed',
				width: 100,
				value: 0,
				margin: "5 5 5 15",
				disabled: true,
				minValue: 100,
				maxValue: 1000,
				increment: 100,
				value: 1000
			}],
			enablePanel: function() {
				this.getComponent('playbtn').enable();
				this.getComponent('progress').enable();
				this.getComponent('startbtn').enable();
				this.getComponent('prevbtn').enable();
				this.getComponent('nextbtn').enable();
				this.getComponent('endbtn').enable();
				this.getComponent('speed').enable();
			},
			disablePanel: function() {
				this.getComponent('playbtn').disable();
				this.getComponent('progress').disable();
				this.getComponent('startbtn').disable();
				this.getComponent('prevbtn').disable();
				this.getComponent('nextbtn').disable();
				this.getComponent('endbtn').disable();
				this.getComponent('speed').disable();
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
			},
			play: function() {
				if(!me.controls.playing) {
					clearTimeout(me.controls.timeout);
					return;
				}
				if(me.currentTime == me.sim.timeline().getTotalCount()) {
					me.controls.pause();
				}
				me.fireEvent('setTime', me.currentTime + 1);
				me.controls.timeout = setTimeout(me.controls.play, me.controls.getPlayPeriod());
			},
			pause: function() {
				var playBtn = this.getComponent('playbtn');
				playBtn.handler.call(playBtn, playBtn, Ext.EventObject);
			},
			getPlayPeriod: function() {
				return this.getComponent('speed').getValue();
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
				var timeline = this.sim.timeline(),
					totalAvailable = timeline.getTotalCount();
				if(newTime > totalAvailable) {
					newTime = totalAvailable;
				} else if(newTime < 0) {
					newTime = 0;
				}
				// check for availability of this data
				if(timeline.getById(newTime) == null) {
					var start = newTime;
					if(start < 50) {
						start = 0;
					} else if(totalAvailable - start < 100) {
						start = Math.max(totalAvailable - 100, 0);
					}
					timeline.guaranteeRange(start, start + 100, function() {
						this.fireEvent('setTime', newTime);
					}, this);
				} else {
					this.currentTime = newTime;
					this.drawPanel.setTimeStep(newTime);
					this.controls.setProgress(newTime, totalAvailable);

					// dynamic data loading
					if(this.maxId - this.currentTime <= 25) {
						this.sim.timeline().guaranteeRange(this.currentTime, this.currentTime + 100);
						this.maxId = this.currentTime + 100;
					}
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
