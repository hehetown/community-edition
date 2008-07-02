<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = sitedata.getObject("component", componentId);

// process
if(component != null)
{
	var mediaType = wizard.getSafeProperty(component, "mediaType");
	var mediaUrl = wizard.getSafeProperty(component, "mediaUrl");
	var unsupportedText = wizard.getSafeProperty(component, "unsupportedText");
	var width = wizard.getSafeProperty(component, "width");
	var height = wizard.getSafeProperty(component, "height");
	
	// the controls
	wizard.addElement("mediaType", mediaType);
	wizard.addElement("mediaUrl", mediaUrl);
	wizard.addElement("unsupportedText", unsupportedText);
	wizard.addElement("width", width);
	wizard.addElement("height", height);
	
	wizard.addElementFormat("mediaType", "Media Type", "combo", 290);
	wizard.addElementFormat("mediaUrl", "URL", "textfield", 290);
	wizard.addElementFormat("unsupportedText", "Unsupported Text", "textarea", 290);
	wizard.addElementFormat("width", "Width", "textfield", 290);	
	wizard.addElementFormat("height", "Height", "textfield", 290);	
	
	//
	// media types
	//
	wizard.addElementFormatKeyPair("mediaType", "title", "Media Type");
	
	// video
	wizard.addElementSelectionValue("mediaType", "video", "Video");
	wizard.addElementSelectionValue("mediaType", "audio", "Audio");
	wizard.addElementSelectionValue("mediaType", "pdf", "PDF");
	wizard.addElementSelectionValue("mediaType", "flash", "Flash");
}

