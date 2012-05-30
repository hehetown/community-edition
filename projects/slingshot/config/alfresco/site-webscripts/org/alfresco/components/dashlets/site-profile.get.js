/**
 * Site Profile component GET method
 */
function main()
{
   // Call the repo for the sites profile
   var profile =
   {
      title: "",
      shortName: "", 
      description: ""
   }
   
   var profile = null;
   var json = remote.call("/api/sites/" + page.url.templateArgs.site);
   if (json.status == 200)
   {
      // Create javascript object from the repo response
      var obj = eval('(' + json + ')');
      if (obj)
      {
         profile = obj;
      }
   }
   
   // Find the manager for the site
   var sitemanagers = [];
   
   json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships?rf=SiteManager");
   if (json.status == 200)
   {
      var obj = eval('(' + json + ')');
      if (obj)
      {
         var managers = [];
         for (var x=0; x < obj.length; x++)
         {
            managers.push(obj[x]);
         }
         
         sitemanagers = managers;
      }
   }
   
   // Prepare the model
   model.profile = profile;
   model.sitemanagers = sitemanagers;
   
   // Widget instantiation metdata...
   model.webScriptWidgets = [];
   var dashletTitleBarActions = {};
   dashletTitleBarActions.name = "Alfresco.widget.DashletTitleBarActions";
   dashletTitleBarActions.provideOptions = true;
   dashletTitleBarActions.provideMessages = false;
   dashletTitleBarActions.options = {};
   dashletTitleBarActions.options.actions = [];
   dashletTitleBarActions.options.actions.push({
      cssClass: "help",
      bubbleOnClick:
      {
         message: msg.get("dashlet.help")
      },
      tooltip: msg.get("dashlet.help.tooltip")
   });

   model.webScriptWidgets.push(dashletTitleBarActions);
}

main();