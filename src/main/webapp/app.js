Ext.Loader.setConfig({enabled:true});
Ext.Loader.setPath('Presage2', 'app');
Ext.require([
	'Presage2.view.Viewport',
	'Presage2.store.Simulations'
]);

Ext.onReady(function() {
	Ext.application({
		name: 'Presage2',
		autoCreateViewport: true,
		models: ['Simulation', 'Plugin'],
		stores: ['Simulations', 'Plugins'],
		launch: function() {
		}
	});
});
