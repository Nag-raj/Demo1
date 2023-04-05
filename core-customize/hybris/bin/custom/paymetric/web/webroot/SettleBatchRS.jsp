<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="de.hybris.platform.paymetric.commands.request.SettleBatchRequest" %>
<%@page import="de.hybris.platform.paymetric.commands.result.SettleBatchResult" %>
<%@page import="de.hybris.platform.paymetric.commands.XiPaySettleBatchCommand" %>
<%@page import="Paymetric.XiPaySoap30.message.ITransactionHeader"%>
<%@page import="com.paymetric.xslt.CXmlHelper"%>
<%@page import="com.paymetric.sdk.XIConfig"%>
<%@page import="org.owasp.encoder.Encode"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.ISODateTimeFormat"%>

<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Get the transactions to be settled
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
String strSettleBatch = XIConfig.getParameter(request, "settleBatch");
String strTrans[] = strSettleBatch.split(";");
int iCount = 0;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create and hydrate the array of XiPay transactions we need to settle
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
ITransactionHeader xiTrans[] = new ITransactionHeader[strTrans.length];
for(final String strTran : strTrans)
{
	String strFields[] = strTran.split(",");
	xiTrans[iCount] = new ITransactionHeader();
	xiTrans[iCount].setTransactionID(strFields[0].trim());
	xiTrans[iCount].setMerchantID(strFields[1].trim());
	xiTrans[iCount].setCardType(strFields[2].trim());
	xiTrans[iCount].setCurrencyKey(strFields[3].trim());
	xiTrans[iCount].setSettlementAmount(strFields[4].trim());
	iCount++;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Generate a unique Batch ID (XiPay requirement)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
DateTime dtNow = new DateTime();
DateTimeFormatter dtf = ISODateTimeFormat.dateHourMinuteSecondMillis();
String strBatchID = dtf.print(dtNow);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Prepare to settle the requested transactions using our uniquelly-generated Batch ID
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
SettleBatchRequest req = new SettleBatchRequest(strBatchID, xiTrans, "[y]" + strBatchID);
SettleBatchResult res = null;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create the Hybris Settle Batch Command and Invoke it to perform the batch settlement
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
XiPaySettleBatchCommand cmd = new XiPaySettleBatchCommand();
res = cmd.perform(req);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create a logger to use with the XML helper class
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
Logger LOG = LoggerFactory.getLogger(cmd.getClass());

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Settle Batch Command - RESPONSE</title>
	</head>
	<body>
		<H1>Settle Batch Command</H1>
		<hr/>
		<br/>
		<table>
	        <thead>
	            <tr>
	                <td colspan="3" style="background-color:cornflowerblue; text-align:center">
	                	<b>Settlement for Batch ID: <% out.print(req.getBatchID()); %></b>
	                </td>
	            </tr>
	            <tr>
	                <td style="background-color:cornflowerblue; text-align:center"><b>REQUEST</b></td>
	                <td>&nbsp;</td>
	                <td style="background-color:cornflowerblue; text-align:center"><b>RESULT</b></td>
	            </tr>
	        </thead>
	        <%
	        	for(iCount = 0; iCount < req.getTransactions().length; iCount++)
	        	{
	        %>
            <tr>
                <td>
                    <textarea readonly="readonly" rows="20" cols="80" style="font-family: 'Courier New'">
	        		<% out.print(CXmlHelper.toXml(req.getTransactions()[iCount], LOG)); %>
                    </textarea>
                </td>
                <td>&nbsp;</td>
                <td>
                    <textarea readonly="readonly" rows="20" cols="80" style="font-family: 'Courier New'">
	        		<% out.print(Encode.forHtml(CXmlHelper.toXml(res.getTransactions()[iCount], LOG))); %>
                    </textarea>
                </td>
            </tr>
	        <%
	        	}
	        %>
	        <tfoot>
	            <tr>
	                <td colspan="3" style="background-color:cornflowerblue; text-align:center">
	                	<b>Schedule RESULT for Batch ID: <% out.print(Encode.forHtml(req.getBatchID())); %></b>
	                </td>
	            </tr>
	            <tr>
	                <td colspan="3" style="text-align:center">
	                    <textarea readonly="readonly" rows="20" cols="80" style="font-family: 'Courier New'">
		        		<% out.print(CXmlHelper.toXml(res.getTransactions()[iCount], LOG)); %>
	                    </textarea>
	                </td>
	            </tr>
	        </tfoot>
		</table>
	</body>
</html>
