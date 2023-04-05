<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="de.hybris.platform.paymetric.commands.request.GetTransactionRequest" %>
<%@page import="de.hybris.platform.paymetric.commands.result.GetTransactionResult" %>
<%@page import="de.hybris.platform.paymetric.commands.XiPayGetTransactionCommand" %>
<%@page import="Paymetric.XiPaySoap30.message.ITransactionHeader"%>
<%@page import="com.paymetric.xslt.CXmlHelper"%>
<%@page import="com.paymetric.sdk.XIConfig"%>
<%@page import="org.owasp.encoder.Encode"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Get the transactions to be settled
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
String strTransactionID = XIConfig.getParameter(request, "transaction_id");
int iCount = 0;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Prepare to settle the requested transactions using our uniquelly-generated Batch ID
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
GetTransactionRequest req = new GetTransactionRequest(strTransactionID, strTransactionID);
GetTransactionResult res = null;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create the Hybris Settle Batch Command and Invoke it to perform the batch settlement
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
XiPayGetTransactionCommand cmd = new XiPayGetTransactionCommand();
res = cmd.perform(req);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create a logger to use with the XML helper class
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
Logger LOG = cmd.getLogger();

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Get Transaction Command - RESPONSE</title>
	</head>
	<body>
		<br/>
		<table style="width:100%" border="0">
            <tr>
                <td>
                    <textarea readonly="readonly" rows="20" cols="80" style="font-family: 'Courier New'">
	        		<% out.print(Encode.forHtml(CXmlHelper.toXml(res.getTransaction(), LOG))); %>
                    </textarea>
                </td>
            </tr>
		</table>
	</body>
</html>
