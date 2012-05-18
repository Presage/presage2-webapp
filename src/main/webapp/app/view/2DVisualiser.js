Ext.define('Presage2.view.2DVisualiser', {
	extend: 'Ext.draw.Component',
	store: 'Simulations',
	alias: 'widget.2dvisualiser',
	initComponent: function() {
		Ext.apply(this, {
			width: 520,
			height: 520,
			viewBox: true
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
		this.scale = this.getScale(simulation);

		// load time point 0
		this.createSprites(this.timeline.getById(0));
		this.setTimeStep(0);
	},
	setTimeStep: function(time) {
		var step = this.timeline.getById(time);
		if(step != null) {
			step.agents().each(function(ag) {
				if(ag.getId() in this.sprites) {
					var sp = this.sprites[ag.getId()];
					if("x" in ag.data.data && "y" in ag.data.data) {
						sp.setAttributes({
							x: 10 + (ag.data.data.x * this.scale),
							y: 10 + (ag.data.data.y * this.scale)
						}, true)
					} else {
						sp.remove();
						delete this.sprites[ag.getId()];
					}
				} else {
					var sp = this.surface.add(this.drawAgentSprite(ag));
					sp.show(true);
					this.sprites[ag.getId()] = sp;
				}
			}, this);
		}
	},
	createSprites: function(timeline) {
		timeline.agents().each(function(ag) {
			if(ag.data.data.x != undefined && ag.data.data.y != undefined) {
				var sp = this.surface.add(this.drawAgentSprite(ag));
				sp.show(true);
				this.sprites[ag.getId()] = sp
			}
		}, this);
	},
	drawAgentSprite: function(ag) {
		var agent = {
			type: 'circle',
			radius: 5,
			fill: '#111',
			x: 10 + (ag.data.data.x * this.scale),
			y: 10 + (ag.data.data.y * this.scale)
		};
		return agent;
	},
	getScale: function(simulation) {
		if("xSize" in simulation.data.parameters
				&& "ySize" in simulation.data.parameters) {
			var size = Math.max(simulation.data.parameters.xSize, simulation.data.parameters.ySize);
			return 500 / size;
		} else if("x" in simulation.data.parameters
				&& "y" in simulation.data.parameters) {
			var size = Math.max(simulation.data.parameters.x, simulation.data.parameters.y);
			return 500 / size;
		} else
			return 1.0;
	}
});
