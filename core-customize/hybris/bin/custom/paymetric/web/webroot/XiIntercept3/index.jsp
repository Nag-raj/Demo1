<%--
    Document   : index.jsp
    Author     : Alexander Perez
    Company    : Paymetric
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.XIConfig"%>

<%
String strAction = XIConfig.getParameter(request, "SAP");
String strMethod = "";
String strSite = "";


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

    request.setAttribute("site", strSite);
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
		<title>XiIntercept v3 - FORM-POST Implementation Example</title>
		<link rel="stylesheet" href="${site}XIeCommerce3.css" type="text/css" />
		<script type="text/javascript" src="${site}XIeCommerce3.js"></script>
    </head>
    
	<script type="text/javascript">
		
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
	</script>
    <body>
        <h1>Payment Details</h1>
        
	    <div id="light" class="white_content">
	        <iframe id="xiFrame"  style = "width:360px; height:150px;" src="${site}XIeCommerce3RQ.jsp"
	        	onload="return InitForTokenization(true, 'ccNumber', 'cmdSubmit', 'xiFrame', 'light', 'fade');">
	        </iframe>
	    </div>
	    <div id="fade" class="black_overlay"></div>
	    
	    <!-- -->
	    <input id="cmdShowIFrame" type="button" onclick="return ShowIFrame();" value="Show IFRAME..." />&nbsp;&nbsp;
	    <input id="cmdShowIFrame" type="button" onclick="return HideIFrame();" value="Hide IFRAME..." />
			    
	    <form action="<%= strAction %>" method="<%= strMethod %>">
		    <table>
		        <tr>
		            <td align="right" valign="middle">Card Holder Name:&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <input id="ccName" type="text" name="ccName" size="40" value="John Doe" />
		            </td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Credit Card Type:&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <select id="ccType" name="ccType">
		                    <option value="VISA">Visa</option>
		                    <option value="MC">MasterCard</option>
		                    <option value="AMEX">American Express</option>
		                    <option value="DISC">Discover Card</option>
		                </select>
		            </td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Card Number:&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <input id="ccNumber" name="ccNumber" size="30" type="text" value="4444333322221111" onblur="SendIFrameMessage(this.value)" />
		            </td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Card Expiration Date:&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <input id="ccExp" name="ccExp" type="hidden" value="" />
						<select id="ccMonth">
							<option value="01">January</option>
							<option value="02">February</option>
							<option value="03">March</option>
							<option value="04">April</option>
							<option value="05">May</option>
							<option value="06">June</option>
							<option value="07">July</option>
							<option value="08">August</option>
							<option value="09">September</option>
							<option value="10">October</option>
							<option value="11">November</option>
							<option value="12">December</option>
						</select>
						&nbsp;&nbsp;
						<select id="ccYear">
						<%
							java.util.Date dtNow = new java.util.Date();
							int iYears = 2000 + (dtNow.getYear()-100) + 50;
							for(int iYear = iYears - 50; iYear < iYears; iYear++)
							{
						%>
							<option value="<%= iYear %>"><%= iYear %></option>
						<%
							}
						%>
						</select>
		            </td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Security Code (CVV):&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <input id="cvv" name="cvv" type="text" size="4" value="123" />
		            </td>
		        </tr>
		        <tr>
		            <td>&nbsp;</td>
		            <td>&nbsp;</td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">
		                <input id="cmdCancel" type="submit" onclick="return cancelPayment();" value="Cancel" />
		            </td>
		            <td align="right" valign="middle">
		                <input id="cmdSubmit" type="submit" onclick="return processPayment('<%= strAction %>');" value="Submit" />
		            </td>
		        </tr>
		    </table>
	     </form>
<!-- 		<div id="xiStatus" class="xiMessageOverlay"></div> -->
	</body>
</html>
