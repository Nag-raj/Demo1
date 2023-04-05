<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.paymetric.sdk.XIConfig"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>

<%
try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	XIConfig xiConfig = new XIConfig();
	xiConfig.Config(request, "XiIntercept.properties");
	System.out.println("XiIntercept Configuration:\n" + xiConfig.toString());

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create a new Session ID
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String sessionId = URLDecoder.decode(XIConfig.getParameter(request, "sessionId"), "UTF-8");
	String responseURL = URLDecoder.decode(XIConfig.getParameter(request, "responseURL"), "UTF-8");
    request.setAttribute("sessionId", sessionId);
    request.setAttribute("responseURL", responseURL);
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
		<title>XiIntercept - Semafone Page Fragment Implementation Example</title>
    </head>
    
	<script type="text/javascript">
        function resetPayment()
        {
        	document.getElementById("CCINS").selectedIndex = -1;
        	document.getElementById("CCNUM").value = "";
        	document.getElementById("CVVAL").value = "";
        }
	</script>
    <body>
	    <form action="${responseURL}" method="POST">
	    	<input id="sessionId" name="sessionId" value="${sessionId}" type="hidden"/>
	    	<input id="sessionMode" name="sessionMode" value="simulation" type="hidden"/>
		    <table>
		        <tr>
		            <td align="right" valign="middle">Credit Card Type:&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <select id="CCINS" name="CCINS">
		                    <option value="VI">Visa</option>
		                    <option value="MC">MasterCard</option>
		                    <option value="AX">American Express</option>
		                    <option value="DI">Discover Card</option>
		                </select>
		            </td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Card Number:&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <input id="CCNUM" name="CCNUM" size="30" type="text" value="4444333322221111"/>
		            </td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Security Code (CVV):&nbsp;&nbsp;</td>
		            <td valign="middle">
		                <input id="CVVAL" name="CVVAL" type="text" size="4" value="123" />
		            </td>
		        </tr>
		        <tr>
		            <td>&nbsp;</td>
		            <td>&nbsp;</td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">
		                <input id="cmdCancel" type="button" onclick="return resetPayment();" value="Reset" />
		            </td>
		            <td align="right" valign="middle">
		                <input id="cmdSubmit" type="submit" value="Submit" />
		            </td>
		        </tr>
		    </table>
	     </form>
	</body>
</html>
