<%--
    Document   : testSE.jsp
    Author     : Alexander Perez
    Company    : Paymetric
--%>

<%@page import="com.paymetric.sdk.XIConfig"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

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
		<title>SecureEntry Implementation Example</title>
	    <script type="text/javascript" >

	    function invokeSE()
	    {
	    	var request = new XMLHttpRequest();
	        var params = "[START]";
	        var uri = "http://127.0.0.1:5555";


	        console.log("SE Request - " + params);
	        try
	        {
	        	request.onreadystatechange = function() {
	            	console.log("readyState: " + this.readyState + ", status: " + this.status + ", responseText: " + request.responseText);
	            	if(this.readyState == 4 && this.status == 200)
	            	{
	            		SecureEntryResponse(request.responseText);
	            	}
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

	    function SecureEntryResponse(seData)
	    {
	    	var strToken = "[TOKEN]";
	    	if(seData.startsWith(strToken))
	    	{
	    		var items = seData.substring(strToken.length).split(",");
	    		var cardType = "";
	    		
	    		document.getElementById("ccNumber").value = items[0];
	    		document.getElementById("ccMonth").value = items[1].substring(2);
	    		document.getElementById("ccYear").value = "20" + items[1].substring(0, 2);
	    		switch(items[3])
	    		{
	    		case "0": cardType = "VISA"; break;
	    		case "1": cardType = "MC"; break;
	    		case "2": cardType = "AMEX"; break;
	    		case "3": cardType = "DISC"; break;
	    		case "4": cardType = "JCB"; break;
	    		case "5": cardType = "DINERS"; break;
	    		case "6": cardType = "MAESTRO"; break;
	    		case "7": cardType = "DANKORT"; break;
	    		case "8": cardType = "SOLO"; break;
	    		case "9": cardType = "SWITCH"; break;
	    		case "10": cardType = "CHINA"; break;
	    		default: cardType = "N/A"; break;
	    		}
	    		document.getElementById("ccType").value = cardType;
	    		document.getElementById("cvv").value = "";
	    		document.getElementById("ccName").value = (items.length > 7 ? items[7] : "N/A");
	    	}
	    }
	    </script>
    </head>
    
    <body>
        <h1>Payment Details</h1>
			    
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
		                    <option value="JCB">Japan Credit Bureau</option>
		                    <option value="DINERS">Diners Club International</option>
		                    <option value="MAESTRO">Maestro</option>
		                    <option value="DANKORT">Dankort Debit Card</option>
		                    <option value="SOLO">Solo Debit Card</option>
		                    <option value="SWITCH">Switch Debit Card</option>
		                    <option value="CHINA">China Unionpay</option>
		                    <option value="N/A">Unknown</option>
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
		                <input id="cmdSubmit" type="button" onclick="return invokeSE();" value="Invoke SE" />
		            </td>
		        </tr>
		    </table>
	     </form>
	</body>
</html>
