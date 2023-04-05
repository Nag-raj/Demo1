
function invokeSE()
{
	var request = new XMLHttpRequest();
    var params = "[START]";
    var uri = "http://127.0.0.1:5555";
//    var uri = "http://localhost:7216/api/P2PEResponse/secureentry";


//    debugger;
    console.log("SE Request - " + params);
    try
    {
    	request.onreadystatechange = function() {
        	console.log("readyState: " + this.readyState + ", status: " + this.status + ", responseText: " + request.responseText);
        		};
        request.open("POST", uri, true);
        request.setRequestHeader("Accept", "application/xml, text/plain");
        request.setRequestHeader("Content-type", "text/plain");
        request.send(params);
    }
    catch (err)
    {
        alert(err.message);
    }
    console.log("SE Request - SENT");
}


function triggerSE()
{
    try
    {
        var strDetails = "invokeSE.jsp";
        var xiFrameDoc = getNewIFrame("seFrame");

    	xiFrameDoc.location.href = strDetails;
    }
    catch (err)
    {
    	alert(err.message);
    }
}

function getNewIFrame(strFrame)
{
    var xiFrame = document.getElementById(strFrame);
    var xiFrameDoc = (xiFrame.contentWindow || xiFrame.contentDocument);
    
    if (xiFrameDoc.document)
    	xiFrameDoc = xiFrameDoc.document;

    return xiFrameDoc;
}


function pad(num, size)
{
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return s;
}

function processPayment(strAction)
{
    var strWhere = "processPayment()";
	var iMonth = parseInt(document.getElementById("ccMonth").value);
	var iYear = parseInt(document.getElementById("ccYear").value);
	var strCardNo = document.getElementById("ccNumber").value;
	var strType = "ERROR";
	var dtNow = new Date();
	
	if(IsToken(strCardNo) == false)
	{
		if(luhnCheck(strCardNo))
    		DisplayMessage(strWhere, strType, "Tokenization has not completed, please click the Submit button again...");
		else DisplayMessage(strWhere, strType, "Card Number is not an XiSecure Token");
		return false;
	}
	if(iYear < dtNow.getFullYear())
	{
		DisplayMessage(strWhere, strType, "INVALID expiration year");
		return false;
	}
	if(iYear == dtNow.getFullYear())
	{
		if(iMonth < dtNow.getMonth())
    	{
			DisplayMessage(strWhere, strType, "INVALID expiration month");
			return false;
    	}
	}
	
	if(document.getElementById("ccType").value.indexOf("A") == 0)
	{
		if(document.getElementById("cvv").value.length != 4)
    	{
			DisplayMessage(strWhere, strType, "INVALID CVV");
			return false;
    	}
	}
	else if(document.getElementById("cvv").value.length != 3)
	{
		DisplayMessage(strWhere, strType, "INVALID CVV");
		return false;
	}
	
    swapToken();
    document.getElementById("ccExp").value = pad(iYear, 4) + "" + pad(iMonth, 2);
    
    if(strAction.length != 0)
    	return true;

    DisplayMessage(strWhere, "INFO", "Token '" + gv_strToken + "' would have been posted...'");
    return false;
}
