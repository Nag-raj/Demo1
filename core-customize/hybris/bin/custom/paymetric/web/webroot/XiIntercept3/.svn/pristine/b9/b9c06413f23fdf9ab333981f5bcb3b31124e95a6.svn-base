<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.owasp.encoder.Encode"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.XIConfig"%>
<%@page import="java.io.StringReader"%>
<%@page import="java.io.StringWriter"%>
<%@page import="javax.xml.transform.OutputKeys"%>
<%@page import="javax.xml.transform.Source"%>
<%@page import="javax.xml.transform.Transformer"%>
<%@page import="javax.xml.transform.TransformerFactory"%>
<%@page import="javax.xml.transform.stream.StreamResult"%>
<%@page import="javax.xml.transform.stream.StreamSource"%>
<%@page import="java.util.Base64.Encoder" %>
<%@page import="java.util.Base64.Decoder" %>


<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Forward-Declare global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
String strLocation = null;
String strSite = null;
String status = null;
String accessToken = null;
String[] splitTokens = null;
String outXML = "";
String xmlRS = null;
StringWriter stringWriter = null;
Source xmlInput = null;
StreamResult xmlOutput = null;
TransformerFactory transformerFactory = null;
Transformer transformer = null;
XIConfig xiConfig = null;
Decoder decoder = null;
Encoder encoder = null;

	
try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xiConfig = new XIConfig();
	strSite = xiConfig.getSiteURL(request);
	if(strSite.startsWith("http://localhost:8080"))
	{
		XIConfig.isConfigured.set(false);
	}
	xiConfig.Config(request, "XiIntercept.properties");


	stringWriter = new StringWriter();
	status = XIConfig.getParameter(request, "status");
    if(!status.toLowerCase().contains("cancel")) 
    {
        accessToken = XIConfig.getParameter(request, "id");
    	xmlRS = AccessTokenUtility.GetResponsePacket(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(), XIConfig.URL.get(), accessToken);
    	xmlInput = new StreamSource(new StringReader(xmlRS));
    	xmlOutput = new StreamResult(stringWriter);
    	transformerFactory = TransformerFactory.newInstance();
    	transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(xmlInput, xmlOutput);
        outXML += xmlOutput.getWriter().toString();

        request.setAttribute("ResponseXML", Encode.forHtml(outXML));
    }
    else
    {
        request.setAttribute("ResponseXML", "The user has cancelled using the payment page."); 
    }
}
catch (Exception ex)
{
    request.setAttribute("errormessage", ex.getMessage());
    request.setAttribute("errorstyle", "class='errorMsg'");
}
finally
{
	if (null != stringWriter)
    {
		stringWriter.close();
    }
}
%>


<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="cache-control" content="max-age=0" />
	<meta http-equiv="cache-control" content="no-cache" />
	<meta charset="ISO-8859-1" />
	<title>XiIntercept v3 - HOSTED-IFRAME Response</title>
    <script type="text/javascript" >
    function sendResponse()
    {
		var objXml = document.getElementById("DebugPacket");
		var strXml = (objXml.textContent ? objXml.textContent : objXml.innerText);

		if(window.frameElement)
        {
	        window.parent.postMessage(window.frameElement.id + "~" + strXml, "*");
        }
    }
    </script>
	<style>
		code {
		  font-family: Consolas,"courier new";
		  color: crimson;
		  background-color: #f1f1f1;
		  padding: 2px;
		  font-size: 12px;
		  overflow: auto;
		  width: 100%;
		  height: 100%;
		}
	</style>
</head>
<body onload="sendResponse();">
	<div class="sampleBody">
	    <pre lang="xml" id="DebugPacket" style="display: none"><code class="xml hljs" id="ResponseSpan">${ResponseXML}</code></pre>
        <div id="Status" ${errorstyle}>${errormessage}</div>
	</div>
</body>
</html>
