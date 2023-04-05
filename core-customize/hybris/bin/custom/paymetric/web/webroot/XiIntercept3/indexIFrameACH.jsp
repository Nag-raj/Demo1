<%--
    Document   : indexIFrameACH.jsp
    Author     : Alexander Perez
    Company    : Paymetric
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.XIConfig"%>

<%
String strAction = XIConfig.getParameter(request, "SAP");
String strMethod = "";
String strSite = "";
String strStore = (XIConfig.getParameter(request, "store") != null ? XIConfig.getParameter(request, "store") : "");
String strURL = String.format("XiInterceptIFrameACHRQ.jsp?store=%s", strStore);


if(!strAction.isEmpty())
{
	if(strAction.indexOf("_GET_") != -1)
		strMethod = "GET";
	else strMethod = "POST";
	strAction = "SAPEVENT:" + strAction;
}

try
{
	strSite = request.getScheme() + "://" + request.getServerName();
	if (request.getServerPort() != 80)
	{
		strSite = strSite + ":" + request.getServerPort();
	}
	strSite = strSite + request.getRequestURI();
	strSite = strSite.substring(0, strSite.lastIndexOf("/") + 1);
	strURL = strSite + strURL;

    request.setAttribute("site", strURL);
}
catch (Exception ex)
{
	ex.printStackTrace();
}

%>
<!DOCTYPE html>
<html>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="cache-control" content="max-age=0" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta charset="ISO-8859-1" />
		<title>XiIntercept v3 - Hosted IFrame Implementation with E-Check Support</title>
		<link rel="stylesheet" href="${site}XIeCommerce3.css" type="text/css" />
		<script type="text/javascript" src="${site}xml2json.js"></script>
		<script type="text/javascript" src="${site}XIeCommerce3.js"></script>
		<script type="text/javascript">
		function handleXiInterceptResponse()
		{
			var iFields = 0;
			var iField = 0;
			var fields = null;
			var field = null;
			debugger;


			// Get The PaymetricResponse Fields
			fields = gv_jsonObj.PaymetricResponse.Fields.FormField;
			iFields = fields.length;
			for(iField = 0; iField < iFields; iField++)
			{
				field = fields[iField];
				
				if(field.Name == "Account Number")
					document.getElementById("ec-acnum").value = field.Value;
				else if(field.Name == "Routing Number")
					document.getElementById("ec-rtnum").value = field.Value;
				else if(field.Name == "Name on Account")
					document.getElementById("ec-noa").value = field.Value;
				else if(field.Name == "Bank Name")
					document.getElementById("ec-bn").value = field.Value;
				else if(field.Name == "Account Type")
				{
					if(field.Value == "checking")
					{
						document.getElementById("ec-at-checking").checked = true;
						document.getElementById("ec-at-savings").checked = false;
					}
					else
					{
						document.getElementById("ec-at-checking").checked = false;
						document.getElementById("ec-at-savings").checked = true;
					}
				}
			}
        }
        
        function processPayment(strAction)
        {
            var strWhere = "processPayment()";
        	var strToken = document.getElementById("ec-acnum").value;
        	var strType = "ERROR";
        	var dtNow = new Date();
        	
        	if(IsToken(strToken) == false)
        	{
           		DisplayMessage(strWhere, strType, "Tokenization has not completed, please click the Submit button again...");
        		return false;
        	}
            if(strAction.length != 0)
            	return true;

            DisplayMessage(strWhere, "INFO", "Token '" + strToken + "' would have been posted...'");
            return false;
		}
		</script>
    </head>
    <body>
        <h1>Payment Details</h1>
        <iframe id="xiFrameHosted" style = "width:420px; height:250px;" src="${site}"></iframe>
		<br/>
	    <form action="<%= strAction %>" method="<%= strMethod %>">
			<div name="ec-content" id="ec-content" style="">
			    <div name="div" id="div" class="PaymentDetailHeader">Enter your bank information</div>
			    <div name="div" id="div">
			        <div name="valmsg-ec-at" id="valmsg-ec-at" class="valmsg-ac-type" xi-elem="ec-at" data-msg-for="ec-at"></div>
			        <label name="lbl-ec-at-checking" id="lbl-ec-at-checking" xi-elem="ec-at-checking" for="ec-at-checking">Checking</label>
			        <input type="radio" name="ec-at" id="ec-at-checking" xi-group="ec-at-select" xi-name="Account Type" xi-elem="ec-at" value="checking" data-rule-required="true" data-msg-required="Please select the account type" xi-error-msg-style="text" aria-required="true">
			        <label name="lbl-ec-at-savings" id="lbl-ec-at-savings" xi-elem="ec-at-savings" for="ec-at-savings">Savings</label>
			        <input type="radio" name="ec-at" id="ec-at-savings" xi-group="ec-at-select" xi-name="Account Type" xi-elem="ec-at" value="savings" data-rule-required="true" data-msg-required="Please select the account type" xi-error-msg-style="text" aria-required="true">
			    </div>
			    <div name="div" id="div">
			        <label name="lbl-ec-acnum" id="lbl-ec-acnum" xi-elem="ec-acnum" for="ec-acnum">Account Number</label>
			        <input type="text" name="ec-acnum" id="ec-acnum" xi-tokenize="1" xi-name="Account Number" xi-elem="ec-acnum" data-rule-required="true" data-msg-required="Please enter the account number" xi-error-msg-style="imageTooltip" autocomplete="off" aria-required="true">
			        <div name="valmsg-ec-acnum" id="valmsg-ec-acnum" xi-error-class="valmsg-img" xi-elem="ec-acnum" data-msg-for="ec-acnum"></div>
			    </div>
			    <div name="div" id="div">
			        <label name="lbl-ec-rtnum" id="lbl-ec-rtnum" xi-elem="ec-rtnum" for="ec-rtnum">Routing Number</label>
			        <input type="text" name="ec-rtnum" id="ec-rtnum" xi-name="Routing Number" xi-elem="ec-rtnum" data-rule-required="true" data-msg-required="Please enter the routing number" xi-error-msg-style="imageTooltip" autocomplete="off" aria-required="true">
			        <div name="valmsg-ec-rtnum" id="valmsg-ec-rtnum" xi-error-class="valmsg-img" xi-elem="ec-rtnum" data-msg-for="ec-rtnum"></div>
			    </div>
			    <div name="div" id="div">
			        <label name="lbl-ec-noa" id="lbl-ec-noa" xi-elem="ec-noa" for="ec-noa">Name on Account</label>
			        <input type="text" name="ec-noa" id="ec-noa" xi-name="Name on Account" xi-elem="ec-noa" data-rule-required="true" data-msg-required="Please enter the name on the account" xi-error-msg-style="imageTooltip" autocomplete="off" aria-required="true">
			        <div name="valmsg-ec-noa" id="valmsg-ec-noa" xi-error-class="valmsg-img" xi-elem="ec-noa" data-msg-for="ec-noa"></div>
			    </div>
			    <div name="div" id="div">
			        <label name="lbl-ec-bn" id="lbl-ec-bn" xi-elem="ec-bn" for="ec-bn">Bank Name</label>
			        <input type="text" name="ec-bn" id="ec-bn" xi-name="Bank Name" xi-elem="ec-bn" data-rule-required="true" data-msg-required="Please enter the bank name" xi-error-msg-style="imageTooltip" autocomplete="off" aria-required="true">
			        <div name="valmsg-ec-bn" id="valmsg-ec-bn" xi-error-class="valmsg-img" xi-elem="ec-bn" data-msg-for="ec-bn"></div>
			    </div>
			</div>
			<div>
				<table>
			        <tr>
			            <td align="right" valign="middle">
			                <input id="cmdSubmit" type="submit" onclick="return processPayment('<%= strAction %>');" value="Submit" />
			            </td>
			            <td align="right" valign="middle">
			            	<button onclick="submitHostedIFrame();" type="button">Tokenize!</button>
			            </td>
			        </tr>
			    </table>
			</div>
	    </form>
	</body>
</html>
