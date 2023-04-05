<%--
    Document   : invokeSE.jsp
    Author     : Alexander Perez
    Company    : Paymetric
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="javax.ws.rs.client.Entity" %>
<%@page import="javax.ws.rs.client.Invocation" %>
<%@page import="javax.ws.rs.client.WebTarget" %>
<%@page import="javax.ws.rs.client.Client" %>
<%@page import="javax.ws.rs.client.ClientBuilder" %>
<%@page import="javax.ws.rs.core.Response" %>
<%@page import="org.owasp.encoder.Encode"%>

<%
String strStart = "[START]";
String result = "";
String ipAddress = request.getRemoteHost();
Entity<String> ent = Entity.text(strStart);
Client client = ClientBuilder.newBuilder().build();
WebTarget webTarget = client.target("http://" + ipAddress + ":5555");
Invocation.Builder iBuilder = null;
Response res = null;


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create the standard HTTP Headers for all BillPay calls
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
iBuilder = webTarget.request();
iBuilder.header("Content-Length", "" + Integer.toString(strStart.getBytes().length));


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Send the request
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
res = iBuilder.post(ent);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Get the Response?
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
if(res != null)
{
	result = res.readEntity(String.class);
	res.close();
}
request.setAttribute("responsePacketHtml", Encode.forHtml(result)); 

%>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="cache-control" content="max-age=0" />
	<meta http-equiv="cache-control" content="no-cache" />
	<meta charset="ISO-8859-1" />
	<title>SecureEntry Response</title>
    <script type="text/javascript" >
    function sendResponse()
    {
		var objXml = document.getElementById("DebugPacket");
		
		if(window.parent.document != window.document)
		{
			if(typeof window.parent.SecureEntryResponse == "function")
			{
				window.parent.SecureEntryResponse(objXml.textContent);
				return;
			}
	    }
		objXml.style.display = 'inline';
    }
    </script>
</head>
<body onload="sendResponse();">
	<div class="sampleBody">
	    <pre id="DebugPacket" style="display: none"><code class="xml hljs" id="ResponseSpan">${responsePacketHtml}</code></pre>
	</div>
</body>
</html>
