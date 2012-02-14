Ext.define('Presage2.view.2DVisualiser', {
	extend: 'Ext.draw.Component',
	store: 'Simulations',
	alias: 'widget.2dvisualiser',
	initComponent: function() {
		Ext.apply(this, {
			width: 500,
			height: 500
		});
		this.callParent(arguments);
		
		this.on('show', function() {
			/*var sprites = [];
			for(var i=0; i<10; i++) {
				var sp = this.surface.add({
					type: 'circle',
					x: Math.random() * 500,
					y: Math.random() * 500,
					radius: 5,
					fill: '#111'
				});
				sp.show(true);
				sprites.push(sp);
			}*/
			/*setInterval( function() {
				Ext.Array.each(sprites, function(sp) {
					var bbox = sp.getBBox();
					sp.animate({
						to: {
							x: bbox.x + (Math.random() * 10) - 5,
							y: bbox.y + (Math.random() * 10) - 5
						},
						duration: 1000
					});
				});
			}, 1000);
			console.log(sprites);*/
		});
	}
});
