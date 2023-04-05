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
   	if(typeof window.parent.DisplayMessage === "function")
   	{
       	window.parent.DisplayMessage("XiInterceptIFrameRQ." + strWhere,"ERROR", strMsg);
   	}
   	else
   	{
   		xiStatus.innerText = "XiInterceptIFrameRQ." + strWhere + " - ERROR:\n\t" + strMsg;
   	}
}

function clientListener(event)
{
    var strData = event.data.toString();

    if (strData.indexOf("SUBMIT") != -1)
    {
        submitform();
        return;
    }
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
//	DSData.addAdditional3DSField("AcquirerId", "542515");
//	DSData.addAdditional3DSField("AcquirerMerchantId", "38345215");

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
	                //intcp3DSecure: Custom3DSHandling,
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

function Custom3DSHandling(e)
{
    if (!e)
    {
        return;
    }
    
    if (!e.HasPassed)
    {
        return;
    }

    if($("#post_frame").length > 0)
    {
        return;
    }

	var xiFrame = document.getElementById('XIFrame');
	if(xiFrame)
	{
		xiFrame.style.display = 'none';
	}

    // Note that the iframe size here matches what was set for the ACSWindowSize field.
    $('#IFrameWrapper').append('<iframe id="post_frame" class="hostedIframe" scrolling="no" onload="onLoadDynamic();return false;"></iframe>');

    var framebody = $('#post_frame').contents().find('body');

    var context = e.UrlsAndPayloadsFor3Ds;

    var form = parseInt(context.ThreeDSVersion) > 1
                   ? build3DS2Form(context.StepUpUrl, context.JWT, context.PaymetricPayload)
                   : build3DS1Form(context.CentinelAcsUrl, context.CentinelPayload, context.CentinelTermUrl, context.PaymetricPayload);

    framebody.append(form);

    $("#post_frame").contents().find('form').submit();
}

function build3DS2Form(stepUpUrl, jwt, md)
{
    var form = $("<form method=\"post\">").attr("action", stepUpUrl);
    form.append($("<input name=\"JWT\" type=\"hidden\">").val(jwt));
    form.append($("<input name=\"MD\" type=\"hidden\">").val(md));

    return form;
}

function build3DS1Form(acsUrl, payload, termUrl, md)
{
    var form = $("<form method=\"post\">").attr("action", acsUrl);
    form.append($("<input name=\"PaReq\" type=\"hidden\">").val(payload));
    form.append($("<input name=\"TermUrl\" type=\"hidden\">").val(termUrl));
    form.append($("<input name=\"MD\" type=\"hidden\">").val(md));

    return form;
}

function onLoadDynamic()
{
    $('#ajaxLoader').hide();
}
