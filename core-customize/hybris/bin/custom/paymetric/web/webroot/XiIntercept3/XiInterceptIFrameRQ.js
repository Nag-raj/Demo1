var strError = "";
var merchantGuid = "";
var accessToken = "";
var signature = "";
var amount = "";
var orderNo = "999999999";
var currencyCode = "USD";
var threeDSVersion = "2.2.0";
var threeDS = false;
var autoSizeHeight = true;
var autoSizeWidth = true;
var responseURL = "";
var xiTokenize = null;
var xiStatus = null;


function displayError(strWhere, strMsg)
{
	var errorMsg = "XiInterceptIFrameRQ." + strWhere;
	
   	if(typeof window.parent.DisplayMessage === "function")
   	{
       	window.parent.DisplayMessage(errorMsg, "ERROR", strMsg);
   	}

   	errorMsg += " - ERROR:\n\t" + strMsg;
   	xiStatus.innerText = errorMsg;
   	
   	console.log(errorMsg);
}

function clientListener(event)
{
	var strWhere = "clientListener()";
    var strData = event.data.toString();

	//console.log(strWhere + " - " + strData);
    if (strData.indexOf("SUBMIT") != -1)
    {
        submitform();
        return;
    }
    window.parent.postMessage(strData, "*");
}

function submitform()
{
    var iframe = document.getElementsByName('xieCommFrame');

    if(iframe)
    {
        if(threeDS)
        {
        	submitForm3DS(iframe);
        }
        else
        {
            $XIFrame.submit({iFrameId: 'xieCommFrame', targetUrl: iframe[0].getAttribute("src"), onSuccess: onSubmitSuccess, onError: onSubmitError});
        }
    }
}

function submitForm3DS(iframe)
{
	var DSData = $XIPlugin.createJSRequestPacket(merchantGuid, accessToken);
	var json3DSData = null;
	var iField = 0;
	var fields = null;
	var field = null;
	var obj = null;

	DSData.addField($XIPlugin.createField('Amount', false, amount.replace('.', '')));
	DSData.addAdditional3DSField("OrderNumber", orderNo);
	DSData.addAdditional3DSField("CurrencyCode", currencyCode);

	// ACSWindowSize field: 01=250x400, 02=390x400, 03=500x600, 04=600x400, 05=Full Page
	DSData.addAdditional3DSField("ACSWindowSize", "03");  
 
	if(typeof window.parent.get3DSData == "function")
	{
		json3DSData = window.parent.get3DSData();
		obj = JSON.parse(json3DSData);
		fields = obj.Fields.Field;
	    for(iField = 0; iField < fields.length; iField++)
	    {
			field = fields[iField];
			if (field.Value !== undefined && field.Value !== "")
			{
                 DSData.addAdditional3DSField(field.Name, field.Value);
            }
		}
	}

    $XIFrame.submit({
        iFrameId: 'xieCommFrame',
        targetUrl: iframe[0].getAttribute("src"), 
        data: DSData, 
        threeDSVersion: threeDSVersion, 
        onSuccess: onSubmitSuccess,
        onError: onSubmitError
	});
}

function onSubmitSuccess(msg)
{
	var message = JSON.parse(msg);
    if (message && message.data.HasPassed)
    {
        window.location = responseURL + "?id=" + accessToken + "&s=" + signature;
    }
    else
    {
		displayError("onSubmitSuccess()", message.data.Message);
    }
}

function onSubmitError(msg)
{
	var message = JSON.parse(msg);
    if (message && message.data)
	{
    	displayError("onSubmitError()", message.data.Message);
	}
}

function onLoadIFrame()
{
	try
	{
		var iframe = document.getElementsByName('xieCommFrame');
		xiTokenize = document.getElementById('xiTokenize');
		xiStatus = document.getElementById('Status');

		if (xiTokenize)
		{
			if(typeof window.parent.InitForTokenization == 'undefined')
			{
				xiTokenize.style.display = 'block';
				xiStatus.style.display = 'block';
			}
			else
			{
				xiTokenize.style.display = 'none';
				xiStatus.style.display = 'none';
			}
		}

		if(strError.length != 0)
		{
			displayError("IFrame_OnLoad()", strError);
		}

	    if(iframe)
	    {
	        if(threeDS)
	        {
				$XIFrame.onload({
					iFrameId: 'xieCommFrame',
					targetUrl: iframe[0].getAttribute("src"),
					autosizeheight: autoSizeHeight,
					autosizewidth: autoSizeWidth,
					autosizedelay: autoSizeDelay,
	                threeDSVersion: threeDSVersion, 
					onSuccess: onLoadSuccess,
					onValidate: onLoadValidate,
					onInvalidHandler: onLoadInvalidHandler,
					onError: onLoadError
				});
	        }
	        else
	        {
				$XIFrame.onload({
					iFrameId: 'xieCommFrame',
					targetUrl: iframe[0].getAttribute("src"),
					autosizeheight: autoSizeHeight,
					autosizewidth: autoSizeWidth,
					autosizedelay: autoSizeDelay,
					onSuccess: onLoadSuccess,
					onValidate: onLoadValidate,
					onInvalidHandler: onLoadInvalidHandler,
					onError: onLoadError
				});
	        }
	    }
	}
	catch (err)
	{
	}
}

function onLoadSuccess(msg)
{
	displayError("onLoadSuccess()", msg);
}

function onLoadValidate(e)
{
	var index = 0;
	var msg = "";
	
	for(index = 0; index < e.length; index++)
	{
		msg += "\t" + e[index].definedName + ": " + e[index].message + "\n";
	}

	displayError("onLoadValidate()", msg);
}

function onLoadInvalidHandler(e)
{
	var index = 0;
	var msg = "";

	for(index = 0; index < e.length; index++)
	{
		msg += "\t" + e[index].definedName + ": " + e[index].message + "\n";
	}

	displayError("onLoadInvalidHandler()", msg);
}

function onLoadError(e)
{
	var index = 0;
	var msg = "";

	for(index = 0; index < e.length; index++)
	{
		msg += "\t" + e[index].definedName + ": " + e[index].message + "\n";
	}

	displayError("onLoadError()", msg);
}
