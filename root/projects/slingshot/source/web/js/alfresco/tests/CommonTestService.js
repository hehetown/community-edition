define(["dojo/_base/declare",
        "alfresco/core/Core",
        "doh/runner",
        "dijit/registry",
        "dojo/_base/array",
        "dojo/domReady!"],
        function(declare, AlfCore, doh, registry, array) {
   

   return declare(AlfCore, {
      
      /**
       * This will be initialised as an object containing references to all the objects
       * that will be used in the tests. This is setup in the "testSetup" test.
       */
      testObjects: null,
      
      /**
       * 
       */
      constructor: function() {
         
         // Create a reference to this Service for capturing and reading tests events...
         var _this = this;
         
         // Capture navigation requests...
         this.pageRequests = [];
         this.alfSubscribe("ALF_NAVIGATE_TO_PAGE", function(payload) {
            _this.alfLog("log", "Received page request", payload);
            _this.pageRequests.push(payload.url);
         });
         
         // Capture keyboard menu item selections...
         // Each time the keyboard selects a menu item in the first drop down menu the item
         // clicked will be pushed into an array. The array will then be inspected to check
         // that each item was clicked in the correct order. This will prove that keyboard
         // navigation is working.
         this.keyboardNavResults = [];
         this.alfSubscribe("KEYBOARD_CLICK", function(payload) {
            _this.alfLog("log", "Received keyboard selection", payload);
            _this.keyboardNavResults.push(payload.item);
         });
      },
      
      /**
       * This function is used to create references to all the objects that are used throughout the tests.
       * It works by accepting an array of Strings where each String is a widget id and it creates a new
       * attribute in the "testObjects" object of the same name that maps to referenced object.
       * 
       * At the same time it performs a DOH assertion to ensure that the object has been found.
       * 
       * @method findTestObjects
       * @param {array} targetObjects An array of Strings of the objects to find.
       */
      findTestObjects: function(targetObjects) {
         this.testObjects = {};
         var _this = this;
         array.forEach(targetObjects, function(objectId, index) {
            _this.testObjects[objectId] = registry.byId(objectId);
            doh.assertNotEqual(null, _this.testObjects[objectId], "Could not find '" + objectId + "'");
         })
      }
   });
});
