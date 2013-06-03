dojo.provide("arcgis.soe.takepicture.soe.TakePictureSOE.Config");

dojo.require("dijit._Templated");

dojo.require("esri.discovery.dijit.services._CustomSoeConfigurationPane");

dojo.declare("arcgis.soe.takepicture.soe.TakePictureSOE.Config", [ esri.discovery.dijit.services._CustomSoeConfigurationPane, dijit._Templated ], {
  
  templatePath: dojo.moduleUrl("arcgis.soe.takepicture.soe.TakePictureSOE", "templates/TakePictureSOE.html"),
  widgetsInTemplate: true,
  typeName: TakePictureSOE,
  _capabilities: null,

  // some UI element references...
  _setProperties: function(extension) {
	this.inherited(arguments); 
    this.set({
      	serverPhpUrl:extension.properties.serverPhpUrl

    });
  },
  
  getProperties: function() {
	  var myCustomSoeProps = {
      properties: {
    		serverPhpUrl:this.get("serverPhpUrl")
      }
    };

    return dojo.mixin(this.inherited(arguments), myCustomSoeProps);
  }
  
  //Create setters and getters for all properties
});