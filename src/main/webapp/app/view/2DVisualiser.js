Ext.define('Presage2.view.2DVisualiser', {
	extend: 'Ext.draw.Component',
	store: 'Simulations',
	alias: 'widget.2dvisualiser',
	initComponent: function() {
		Ext.apply(this, {
			width: 520,
			height: 520,
			viewBox: false
		});
		this.callParent(arguments);
		this.sprites = {};
	},
	loadSimulation: function(simId) {
		var simulation = Ext.data.StoreManager.lookup('Simulations').getById(simId);
		this.timeline = simulation.timeline();
		// clear existing sprites
		Ext.Object.each(this.sprites, function(aid, sp) {
			sp.remove();
		});
		this.sprites = {};

		// configure scaling
		this.scale = 1.0;
		if("xSize" in simulation.data.parameters
				&& "ySize" in simulation.data.parameters) {
			var size = Math.max(simulation.data.parameters.xSize, simulation.data.parameters.ySize);
			this.scale = 250 / size;
		}

		// load time point 0
		this.timeline.getById(0).agents().each(function(ag) {
			if(ag.data.data.x != undefined && ag.data.data.y != undefined) {
				var sp = this.surface.add({
					type: 'circle',
					radius: 5,
					fill: '#111',
					x: 10 + (ag.data.data.x * this.scale),
					y: 10 + (ag.data.data.y * this.scale)
				});
				sp.show(true);
				this.sprites[ag.getId()] = sp
			}
		}, this);
		this.setTimeStep(0);
	},
	setTimeStep: function(time) {
		var step = this.timeline.getById(time);
		if(step != null) {
			step.agents().each(function(ag) {
				if(ag.getId() in this.sprites) {
					var sp = this.sprites[ag.getId()];
					sp.setAttributes({
						translate: {
							x: 10 + (ag.data.data.x * this.scale),
							y: 10 + (ag.data.data.y * this.scale)
						}
					}, true)
				}
			}, this);
		}
	}
});
