<%--
    Document   : XIeCommerce3RS.jsp
    Author     : Alexander Perez
    Company    : Paymetric
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.XIConfig"%>

<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Forward-Declare global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
XIConfig xiConfig = null;
String tokencard = "";
String accessToken = "";
String xml = "";

try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xiConfig = new XIConfig();
	xiConfig.Config(request, "XiIntercept.properties");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Initialize the FORM-POST Packet to provide to XiIntercept
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    accessToken = XIConfig.getParameter(request, "id");
    xml = AccessTokenUtility.GetResponsePacket(XIConfig.EC_GUID.get(), XIConfig.EC_PSK.get(), XIConfig.URL.get(), accessToken);
    request.setAttribute("xmlResponse", xml);
    tokencard =  xiConfig.getElementValue(xml, "Value");
    request.setAttribute("Token", tokencard);
}
catch(Exception ex)
{
	request.setAttribute("errormessage", ex.getMessage());
	request.setAttribute("errorstyle", "class='errorMsg'");
}
%>


<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="cache-control" content="max-age=0" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta charset="ISO-8859-1" />
		<title>XiIntercept v3 - FORM-POST ACH Response</title>
	    <script type="text/javascript" >
		if(window.parent.document != window.document)
		{
			window.parent.showToken('<%=tokencard%>');
	    }
	    </script>
    </head>
    <body>
        <div class="sampleBody">
            <h1>Response</h1>
            <pre id="DebugPacket" style="white-space: pre-wrap; width: 500px;">
            	<textarea rows="10" cols="60">${xmlResponse}</textarea>
            </pre>
	        <div id="Status" ${errorstyle}>${errormessage}</div>
        </div>
    </body>
  </html>