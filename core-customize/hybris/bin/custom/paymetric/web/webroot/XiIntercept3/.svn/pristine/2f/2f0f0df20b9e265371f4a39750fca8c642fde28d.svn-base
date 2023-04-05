
var gv_Reload = true;
var gv_Mask = false;
var gv_eChecks = false;
var gv_XIFrameURL = "";
var gv_strToken = "";
var gv_strLast4 = "";
var gv_strMask = "************";
var gv_ctlCCNum = null;
var gv_ctlSubmit = null;
var gv_xiFrame = null; 
var gv_xiFrameDoc = null; 
var gv_xiFrameWin = null;
var gv_divFrame = "";
var gv_divFade = "";
var gv_xmlDoc = null;
var gv_xmlRS = null;
var gv_jsonRS = null;
var gv_jsonObj = null;
var gv_tokenizingMsg = "*-- PLEASE WAIT --*";


function blinker(ctlObject)
{
	if(gv_strToken.length == 0)
	{
		gv_ctlCCNum.style.textAlign="center";
		gv_ctlCCNum.style.backgroundColor="#ddd";
		setTimeout("gv_ctlCCNum.style.backgroundColor=''", 500);
		setTimeout("blinker()", 1000);
	}
	else
	{
		gv_ctlCCNum.style.textAlign="";
	}
}

function ShowIFrame()
{
    document.getElementById(gv_divFrame).style.display = 'block';
    document.getElementById(gv_divFade).style.display = 'none';
}

function DisplayIFrame()
{
    document.getElementById(gv_divFrame).style.display = 'block';
    document.getElementById(gv_divFade).style.display = 'block';
}

function HideIFrame()
{
    document.getElementById(gv_divFrame).style.display = 'none';
    document.getElementById(gv_divFade).style.display = 'none';
}

function DisplayException(strWhere, err)
{
    DisplayMessage(strWhere, "ERROR", err.message);
}

function DisplayMessage(strWhere, strType, strMsg)
{
	var objMessaging = null;

	if(gv_ctlSubmit != null)
		gv_ctlSubmit.disabled = false;
	
	if(typeof handleDisplayMessage === "function")
	{
		handleDisplayMessage(strWhere, strType, strMsg);
	}
	else if((objMessaging = document.getElementById("xiStatus")) != null)
	{
		objMessaging.innerText = strMsg;
	}
	else
	{
	    var strDesc = "    Function:  " + strWhere + "\n";
	    strDesc += "Message Type:  " + strType + "\n";
	    strDesc += " Description:  " + strMsg + "\n\n";
	    strDesc += "Click OK to continue.\n\n";
	    alert(strDesc);
	}
}

function pad(num, size)
{
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return s;
}

function XiInterceptResponse(strXml, id)
{
	gv_xmlRS = strXml;
	if (window.DOMParser)
	{
		parser = new DOMParser();
		gv_xmlDoc = parser.parseFromString(gv_xmlRS, "text/xml");
		if(!(gv_xmlDoc.evaluate))
		{
			gv_xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
			gv_xmlDoc.async = false;
			gv_xmlDoc.loadXML(strXml);
		} 
	}
	else // Internet Explorer
	{
		gv_xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		gv_xmlDoc.async = false;
		gv_xmlDoc.loadXML(gv_xmlRS);
	}

	gv_jsonRS = xml2json(gv_xmlDoc, "  ");
	gv_jsonObj = JSON.parse(gv_jsonRS);

	if(typeof handleXiInterceptResponse === "function")
	{
		handleXiInterceptResponse(id);
	}
}

function getFieldValue(strXPath)
{
	var nodes = null;
	var strResult = "";
    var txt = "";

    if(gv_xmlDoc.evaluate)
    {
    	nodes = gv_xmlDoc.evaluate(strXPath, gv_xmlDoc, null, XPathResult.ANY_TYPE, null);
        var result = nodes.iterateNext();
        while (result)
        {
        	if(result.childNodes.length > 0)
        		txt += result.childNodes[0].nodeValue + "<br>";
            result = nodes.iterateNext();
        }
    }
    else // Internet Explorer
    {
    	gv_xmlDoc.setProperty("SelectionLanguage", "XPath");
        nodes = gv_xmlDoc.selectNodes(strXPath);
        for (i = 0; i < nodes.length; i++)
        {
        	if(nodes[i].childNodes.length > 0)
        		txt += nodes[i].childNodes[0].nodeValue + "<br>";
        }
    }
    
    if(txt != "")
    {
        strResult = txt.split("<br>");
        if(strResult.length == 2)
        	strResult = strResult[0];
    }

    return strResult;
}

function InitForTokenization(bMask, ccNumber, ctlSubmit, xiFrame, divFrame, divFade)
{
    var strWhere = "InitForTokenization()";
    
    try
    {
        gv_Mask = bMask;									// Enable Token Masking?
        gv_xiFrame = xiFrame;								// Save the IFRAME HTML ID
        gv_divFrame = divFrame;								// Save the IFRAME DIV HTML ID
        gv_divFade = divFade;								// Save the WINDOW DIV HTML ID
        HideIFrame();										// Hide the IFRAME
        GetIFrame();										// Get the IFRAME's Document and Window references
        gv_ctlCCNum = document.getElementById(ccNumber);	// Save the HTML ID for CreditCard/BankAccount number
        gv_ctlSubmit = document.getElementById(ctlSubmit);	// Save the HTML ID for SUBMIT button
        reloadIFrame(false);								// Save the IFRAME URL for future use
        
    	window.addEventListener("message", SetWindowListener);
    	gv_xiFrameWin.addEventListener("message", SetIFrameListener);
        gv_ctlCCNum.onblur = function () {
        	SendIFrameMessage();
        };
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }
    finally
    {
    }

    return true;
}

function SetWindowListener(event)
{
    var strWhere = "SetWindowListener()";
	var strData = event.data.toString();
	var strType = null;
	var strMsg = "";


	if (strData.indexOf("TOKEN: ") != -1)
	{
		gv_ctlCCNum.value = strData.substring(7);
		return;
	}
	else if (strData.indexOf("ERROR:") != -1)
	{
		strType = "ERROR";
		strMsg = strData.substring(7);
	}
	else if (strData.indexOf("INFO:") != -1)
	{
		strType = "INFO";
		strMsg = strData.substring(6);
	}
	
    if(strType != null)
		DisplayMessage(strWhere, strType, strMsg);
}

function SendWindowMessage(strMsg)
{
    gv_xiFrameWin.parent.postMessage(strMsg, "*");
}

function SendIFrameMessage()
{
    var strWhere = "SendIFrameMessage()";
	var strMsg = gv_ctlCCNum.value;

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Make sure the credit card field is in a good state to tokenize
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    if (strMsg.length == 0)
    {
        return false;
    }
    else if (IsToken(strMsg))
    {
		return false;
    }
    else if (gv_eChecks == false && !luhnCheck(strMsg))
    {
    	gv_ctlCCNum.value = "";
		SendWindowMessage("ERROR: Invalid Credit card number", "*");
		return false;
    }
	
	gv_ctlCCNum.value= gv_tokenizingMsg;
	blinker();
	if(gv_ctlSubmit != null)
		gv_ctlSubmit.disabled = true;
    gv_xiFrameWin.postMessage("CARDNO:" + strMsg, "*");
}

function SetIFrameListener(event)
{
	var strWhere = "SetIFrameListener()";
	var objInput = event.data.toString();


	if(objInput.indexOf("CARDNO:") != -1)
	{
		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Tokenization has not taken place, copy the data entered on the required field
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			gv_xiFrameDoc.getElementById("CardNo").value = objInput.substring(7).trim();

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Trigger Tokenization 
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			gv_xiFrameDoc.getElementById("xiTokenize").click();
		 }
		 catch (err)
		 {
			DisplayException(strWhere, err);
		 }
		 finally
		 {
		 }

		 return true;
	}
}

function GetIFrame()
{
    var xiFrame = document.getElementById(gv_xiFrame);

    gv_xiFrameWin = xiFrame.contentWindow;
    gv_xiFrameDoc = (xiFrame.contentWindow || xiFrame.contentDocument);
    if (gv_xiFrameDoc.document)
    	gv_xiFrameDoc = gv_xiFrameDoc.document;

    return gv_xiFrameDoc;
}

function IsToken(strCardNo)
{
    var bResult = false;

    if (strCardNo != null && strCardNo.length > 0)
    {
        strCardNo = strCardNo.toUpperCase();
        if (strCardNo.length == 15 && strCardNo.indexOf(gv_strMask) == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 16 && strCardNo.indexOf(gv_strMask) == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 25 && strCardNo.indexOf("-E") == 0)
        {
            gv_strLast4 = strCardNo.substr(6, 4);
            bResult = true;
        }
        else if (strCardNo.length == 14 && strCardNo.indexOf("7") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 16 && strCardNo.indexOf("8") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 16 && strCardNo.indexOf("11") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 17 && strCardNo.indexOf("T") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 19 && strCardNo.indexOf("8") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 19 && strCardNo.indexOf("11") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 19 && strCardNo.indexOf("T") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 20 && strCardNo.indexOf("8") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 20 && strCardNo.indexOf("T") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 24 && strCardNo.indexOf("804424") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
        else if (strCardNo.length == 25 && strCardNo.indexOf("T") == 0)
        {
            gv_strLast4 = strCardNo.substr(strCardNo.length - 4);
            bResult = true;
        }
    }

    return bResult;
}

function reloadIFrame(bAction)
{
    var strWhere = "reloadIFrame()";
	GetIFrame();
    try
    {
        if (gv_Reload)
        {
            if (gv_xiFrameDoc != null)
            {
                if (bAction == true)
                {
                	var objLoc = (gv_xiFrameDoc.location != null ? gv_xiFrameDoc.location : gv_xiFrameWin.location);
                	objLoc.href = gv_XIFrameURL;
                }                   
                else
            	{
                	gv_XIFrameURL = gv_xiFrameDoc.location.href;
            	}
            }
        }
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }

    return false;
}

function onLoadHostedIFrame()
{
    var strWhere = "onLoadHostedIFrame()";
    var iFrame = document.getElementById("xiFrameHosted");

    try
    {
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // The statement below will trigger an exception when a 3DS redirect occurs
        // This will use a different styling class to resize based on content rendered
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        var doc = iFrame.contentWindow.document;
				if(doc.location.pathname.indexOf('RS.jsp') != -1)
				{
					iFrame.style.display='none';
				}
				else
				{
		            iFrame.className = "xiFrameFormat";
		            try
		            {
//		           	iFrame.style.height = (iFrame.contentWindow.document.body.scrollHeight + 5) + 'px';
		            	iFrame.style.width = (iFrame.contentWindow.document.body.scrollWidth + 5) + 'px';
		            }
		            catch(ex)
		            {
		                DisplayException(strWhere, err);
		            }
				}
    }
    catch (ex)
    {
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 3DS page is being rendered, hide the fields below
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        iFrame.className = "hostedIframe";
        displayHybrisFields(false);
    }
}

function submitHostedIFrame()
{
    var strWhere = "submitHostedIFrame()";
    var hosted = null;

    try
    {
        hosted = (!document.getElementsByClassName ? getElementsByClassName(document.body, "xiFrameFormat") : document.getElementsByClassName("xiFrameFormat"));
    	if(hosted != null && hosted.length > 0)
    	{
    		for (var iNo = 0; iNo < hosted.length; iNo++)
    		{
                hosted[iNo].contentWindow.postMessage("SUBMIT", "*");
			}
    	}
    	else if((hosted = document.getElementById("xiFrameHosted")) != null)
        {
            hosted.contentWindow.postMessage("SUBMIT", "*");
    	}
    	else
    	{
    		DisplayMessage(strWhere, "ERROR", "Hosted IFRAME not defined!!")
    	}
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }
}

function onLoadHostedIFrameToken()
{
    var strWhere = "onLoadHostedIFrameToken()";
    var iFrame = document.getElementById("xiFrameTokenHosted");
    var iFrame2 = document.getElementById("xiFrameHosted");


    try
    {
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // The statement below will trigger an exception when a 3DS redirect occurs
        // This will use a different styling class to resize based on content rendered
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        var doc = iFrame.contentWindow.document;
		if(doc.location.pathname.indexOf('RS.jsp') != -1)
		{
			iFrame.style.display='none';
		}
		else
		{
            try
            {
//	            iFrame.style.height = (iFrame.contentWindow.document.body.scrollHeight + 5) + 'px';
            	iFrame.style.width = (iFrame.contentWindow.document.body.scrollWidth + 5) + 'px';
            }
            catch(ex)
            {
                DisplayException(strWhere, err);
            }
            iFrame.className = "xiFrameFormat";
            iFrame.contentWindow.postMessage("SUBMIT_TOKEN", "*");
		}
    }
    catch (ex)
    {
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 3DS page is being rendered, hide the fields below
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	
        iFrame.className = "hostedIframe";
        displayHybrisFields(false);
    }
}

function submitHostedIFrameToken(tabIndex)
{
    var strWhere = "submitHostedIFrameToken()";
    var xiUrl = "/paymetric/XiIntercept3/XiInterceptIFrameTokenRQ.jsp?";
    var iFrame = document.getElementById("xiFrameTokenHosted");
    var iFrame2 = document.getElementById("xiFrameHosted");
    var dlgTokens = document.getElementById("cboxClose");


    try
    {
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Close the "Saved Cards" dialog and hide the hosted IFrame
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	dlgTokens.click();
        iFrame2.style.display = 'none';

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Copy data needed when using "Saved Cards"
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        document.getElementById("card_nameOnCard").value = document.getElementById("xiTokenName_" + tabIndex).value;
	    document.getElementById("card_cardType").value = hybrisCardType("" + document.getElementById("xiTokenType_" + tabIndex).value);
     	document.getElementById("card_cvNumber").value = "null";


		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Construct the URL with the data needed when using "Saved Cards"
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	xiUrl += "store=" + document.getElementById("xiStore").value + "&";
    	xiUrl += "Amount=" + document.getElementById("xiAmount").value + "&";
    	xiUrl += "Currency=" + document.getElementById("xiCurrency").value + "&";
    	xiUrl += "OrderNo=" + document.getElementById("xiOrderNo").value + "&";
     	xiUrl += "token=" + document.getElementById("xiToken_" + tabIndex).value + "&";
    	xiUrl += "expMonth=" + document.getElementById("xiExpMonth_" + tabIndex).value + "&";
    	xiUrl += "expYear=" + document.getElementById("xiExpYear_" + tabIndex).value;

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// Load the IFrame used when using "Saved Cards"
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	iFrame.onload = function () { onLoadHostedIFrameToken(); };
        iFrame.style.display = 'block';
    	iFrame.src = xiUrl;
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }
}

function displayHybrisFields(bDisplay)
{
    var strWhere = "displayHybrisFields()";
    var strDisplay = '';

    try
    {
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// Determine if we need to hide or display these Hybris fields
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	strDisplay = (bDisplay ? 'block' : 'none');
        document.getElementById("useSavedCard").style.display = strDisplay;
        document.getElementById("savePaymentInfo").style.display = strDisplay;
        document.getElementById("billingAddress").style.display = strDisplay;
        document.getElementById("cmdSubmit2").style.display = strDisplay;
//	    document.getElementById("card_cardTypeDIV").style.display = strDisplay;
//		document.getElementById("card_nameOnCardDIV").style.display = strDisplay;
//		document.getElementById("card_accountNumberDIV").style.display = strDisplay;
//	    document.getElementById("card_cvNumberDIV").style.display = strDisplay;
//		document.getElementById("cardDate").style.display = strDisplay;
//		document.getElementById("startDate").style.display = strDisplay;
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }
}

function hybrisCardType(cardType)
{
    var strWhere = "hybrisCardType()";

    try
    {
	    if(cardType.startsWith("vi"))
	    	cardType = "001";
	    else if(cardType.startsWith("mas") || cardType == "mc")
	    	cardType = "002";
	    else if(cardType.startsWith("ame") || cardType == "ax") 
	    	cardType = "003";
	    else if(cardType.startsWith("dis") || cardType == "di")
	    	cardType = "004";
	    else if(cardType.startsWith("din") || cardType == "dc")
	    	cardType = "005";
	    else if(cardType.startsWith("JCB") || cardType == "jc")
	    	cardType = "007";
	    else if(cardType.startsWith("mae") || cardType == "sw")
	    	cardType = "024";
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }
    
    return cardType;
}

function getElementsByClassName(node, classname)
{
    var a = [];
    var re = new RegExp('(^| )'+classname+'( |$)');
    var els = node.getElementsByTagName("*");
    
    for(var i=0,j=els.length; i<j; i++)
    {
    	if(re.test(els[i].className))
    	{
    		a.push(els[i]);
    	}
    }

    return a;
}

function swapToken()
{
    var strWhere = "swapToken()";

    try
    {
    	if (IsToken(gv_strToken))
    	{
    		gv_ctlCCNum.value = gv_strToken;
    		return gv_strToken;
    	}
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }
}

function showToken(strToken)
{
    var strWhere = "showToken()";
   

    try
    {
    	if(strToken != null)
    	{
    		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    		// Send the token back to the parent page
    		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    		gv_strToken = strToken;
    		strToken = (gv_Mask ? maskToken(strToken) : strToken);
    		if(gv_ctlSubmit != null)
    			gv_ctlSubmit.disabled = false;
    		window.parent.postMessage("TOKEN: " + strToken, "*");
    	}
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }
    finally
    {
        reloadIFrame(true);
    }

    return false;
}

function maskToken(strCardNo)
{
    var strWhere = "maskToken()";
    var strMasked = "";


    try
    {
        if (IsToken(strCardNo))
        {
            strMasked = gv_strMask + gv_strLast4;
        }
        else
        {
            strMasked = strCardNo;
        }
    }
    catch (err)
    {
        DisplayException(strWhere, err);
    }

    return strMasked;
}

function luhnCheck(strCard)
{
	var nCheck = 0;
	var nDigit = 0;
	var bEven = false; 
	strCard = strCard.replace(/\D/g, ""); 
 
	if(strCard.length < 12 || strCard.length > 19)
		return false;
	
	for (var n = strCard.length - 1; n >= 0; n--)
	{ 
		var cDigit = strCard.charAt(n); 
		var nDigit = parseInt(cDigit, 10); 
 
 		if (bEven)
 		{
			if ((nDigit *= 2) > 9) 
				nDigit -= 9; 
 		} 
 
 		nCheck += nDigit; 
 		bEven = !bEven; 
	}
	  
	return((nCheck % 10) == 0);
}

function jsonField(name, value, def)
{
	def = (def == null ? '' : def);
	value = (value == null ? '' : value);
	value = (value.length < 1 ? def : value);

	return '{"Name":"' + name + '","Value":"' + value + '"},';
}

function get3DSData()
{
	var obj3DS = document.getElementById("threeDSJSON");
	var data3DS = null;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Is the 3DS JSON data field defined?
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	if(obj3DS == null)
	{
		obj3DS = document.createElement('input');
		obj3DS.setAttribute('type', 'hidden');
		obj3DS.setAttribute('name', 'threeDSJSON');        
		obj3DS.setAttribute('value', '');        
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 3DS JSON data not provided, default to mock data
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	if(obj3DS.value.length < 1 && !obj3DS.hasAttribute('ship-firstName'))
	{
		obj3DS.setAttribute('bill-email', 'carduser@worldpay.com');
		obj3DS.setAttribute('bill-phone', '+1 713-895-2000');
		obj3DS.setAttribute('bill-firstname', 'Valued'); 
		obj3DS.setAttribute('bill-lastname', 'Customer');
		obj3DS.setAttribute('bill-line1', '19500 Credit Card Ln');
		obj3DS.setAttribute('bill-line2', 'Suite 300');
		obj3DS.setAttribute('bill-town', 'Houston');
		obj3DS.setAttribute('bill-regionisocode', 'TX');
		obj3DS.setAttribute('bill-postalcode', '77001');
		obj3DS.setAttribute('bill-countryisocode', '840'); // need to send ISO 3166 country code - 840 = US
		obj3DS.setAttribute('bill-phone', '+1 713-895-2000');
		
		obj3DS.setAttribute('ship-firstname', 'Valued'); 
		obj3DS.setAttribute('ship-lastname', 'Customer');
		obj3DS.setAttribute('ship-line1', '19500 Credit Card Ln');
		obj3DS.setAttribute('ship-line2', 'Suite 300');
		obj3DS.setAttribute('ship-town', 'Houston');
		obj3DS.setAttribute('ship-regionisocode', 'TX');
		obj3DS.setAttribute('ship-postalcode', '77001');
		obj3DS.setAttribute('ship-countryisocode', '840'); // need to send ISO 3166 country code - 840 = US 
		obj3DS.setAttribute('ship-phone', '+1 713-895-2000');
	}

	data3DS = obj3DS.value;
	
	if(data3DS.length < 1)
	{
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Scan the 3DS JSON field for expected attributes
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		data3DS = '{ "Fields": { "Field": [';
		data3DS += jsonField('ShippingFirstName', obj3DS.getAttribute('ship-firstname'), 'Valued'); 
		data3DS += jsonField('ShippingLastName', obj3DS.getAttribute('ship-lastname'), 'Customer');
		data3DS += jsonField('ShippingAddress1', obj3DS.getAttribute('ship-line1'), '19500 Credit Card Ln');
		data3DS += jsonField('ShippingAddress2', obj3DS.getAttribute('ship-line2'), 'Suite 300');
		data3DS += jsonField('ShippingCity', obj3DS.getAttribute('ship-town'), 'Houston');
		data3DS += jsonField('ShippingState', obj3DS.getAttribute('ship-regionisocode'), 'TX');
		data3DS += jsonField('ShippingPostalCode', obj3DS.getAttribute('ship-postalcode'), '77070');
		data3DS += jsonField('ShippingCountryCode', obj3DS.getAttribute('ship-countryisocode'), 'USA');
		data3DS += jsonField('ShippingPhone', ("" + obj3DS.getAttribute('ship-phone')).replace(/[' '-]/g, ''), '+17138950000');

		data3DS += jsonField('MobilePhone', ("" + obj3DS.getAttribute('bill-phone')).replace(/[' '-]/g, ''), obj3DS.getAttribute('ship-phone'));
		data3DS += jsonField('Email', obj3DS.getAttribute('bill-email'), 'carduser@worldpay.com');
		data3DS += jsonField('BillingFirstName', obj3DS.getAttribute('bill-firstname'), obj3DS.getAttribute('ship-firstname'));
		data3DS += jsonField('BillingLastName', obj3DS.getAttribute('bill-lastname'), obj3DS.getAttribute('ship-lastname'));
		data3DS += jsonField('BillingAddress1', obj3DS.getAttribute('bill-line1'), obj3DS.getAttribute('ship-line1'));
		data3DS += jsonField('BillingAddress2', obj3DS.getAttribute('bill-line2'), obj3DS.getAttribute('ship-line2'));
		data3DS += jsonField('BillingCity', obj3DS.getAttribute('bill-town'), obj3DS.getAttribute('ship-town'));
		data3DS += jsonField('BillingState', obj3DS.getAttribute('bill-regionisocode'), obj3DS.getAttribute('ship-regionisocode'));
		data3DS += jsonField('BillingPostalCode', obj3DS.getAttribute('bill-postalcode'), obj3DS.getAttribute('ship-postalcode'));
		data3DS += jsonField('BillingCountryCode', obj3DS.getAttribute('bill-countryisocode'), obj3DS.getAttribute('ship-countryisocode'));
		data3DS += jsonField('BillingPhone', ("" + obj3DS.getAttribute('bill-phone')).replace(/[' '-]/g, ''), obj3DS.getAttribute('ship-phone'));
		data3DS = data3DS.substr(0, data3DS.length - 1) + ']}}';
	}

	return data3DS;
}

function getBillingDataAndsubmitHostedIFrame(){
	
	
	var obj3DS = document.getElementById("threeDSJSON");
	
	obj3DS.setAttribute('bill-firstname', document.getElementById("address.firstName").value); 
	obj3DS.setAttribute('bill-lastname', document.getElementById("address.surname").value);
	obj3DS.setAttribute('bill-line1', document.getElementById("address.line1").value);
	obj3DS.setAttribute('bill-line2', document.getElementById("address.line2").value);
	obj3DS.setAttribute('bill-town', document.getElementById("address.townCity").value);
	obj3DS.setAttribute('bill-regionisocode', document.getElementById("address.region").value);
	obj3DS.setAttribute('bill-postalcode', document.getElementById("address.postcode").value);
	obj3DS.setAttribute('bill-countryisocode', '840'); // need to send ISO 3166 country code - 840 = US
	obj3DS.setAttribute('bill-phone', document.getElementById("address.phone").value); 
	
	submitHostedIFrame();
	
}

//For Saved Cards 3DS
function getBillingDataAndsubmitHostedIFrameToken(index){
	
	var obj3DS = document.getElementById("threeDSJSON");
	
	obj3DS.setAttribute('bill-firstname', document.getElementById("xiBilling_fname_"+index).value); 
	obj3DS.setAttribute('bill-lastname', document.getElementById("xiBilling_lname_"+index).value);
	obj3DS.setAttribute('bill-line1', document.getElementById("xiBilling_line1_"+index).value);
	obj3DS.setAttribute('bill-line2', document.getElementById("xiBilling_line2_"+index).value);
	obj3DS.setAttribute('bill-town', document.getElementById("xiBilling_town_"+index).value);
	obj3DS.setAttribute('bill-regionisocode', document.getElementById("xiBilling_region_"+index).value);
	obj3DS.setAttribute('bill-postalcode', document.getElementById("xiBilling_postalcode_"+index).value);
	obj3DS.setAttribute('bill-countryisocode', document.getElementById("xiBilling_countryIso_"+index).value); // need to send ISO 3166 country code - 840 = US
	obj3DS.setAttribute('bill-phone', document.getElementById("xiBilling_phone_"+index).value); 
	
	//assign the payment method id to hidden field to be available in sop payment response controller
    document.getElementById("selectedPaymentMethodId").value = document.getElementById("xiSelectedPaymentMethodId_"+index).value;
	
	if(document.getElementById("xiThreeDS").value === "true" && document.getElementById("xiThreeDS").value !== undefined)
	{
		submitHostedIFrameToken(index);	
	}
	else
	{
		//3DS is disabled for this store therefore flow via OOTB controller without 3DS.
		document.getElementById("savedTokenForm").submit();
		
	}
	
}	


