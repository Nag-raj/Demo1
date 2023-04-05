<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.paymetric.sdk.PCIPal"%>
<%@ page import="com.paymetric.sdk.PCIPal.XIFieldType"%>
<%@ page import="com.paymetric.sdk.PCIPal.EndpointType"%>
<%@ page import="com.paymetric.sdk.XIConfig"%>
<%@ page import="com.paymetric.sdk.XiSecureWSC"%>
<%@ page import="java.util.List"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="org.jdom.Document"%>
<%@ page import="org.jdom.Element"%>
<%@ page import="org.json.JSONObject"%>

<%
Document result = null;
Element root = null;

try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	XIConfig xiConfig = new XIConfig();
	xiConfig.Config(request, "XiIntercept.properties");
	System.out.println("XiIntercept Configuration:\n" + xiConfig.toString());

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read the form-post data
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String semafoneResponse = XIConfig.getParameter(request, "semafoneResponse");
	JSONObject jsonRS = new JSONObject(semafoneResponse);
	String sessionId = jsonRS.getString("sessionID");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Are we running in simulation mode?
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	if(XIConfig.getParameter(request, "sessionMode").equalsIgnoreCase("simulation"))
	{
		PCIPal pci = null;
		String secret = "ad4268948aa546198d44306c9b52cbfe";
		String mid = "NesWaters";
		String cardType = URLDecoder.decode(request.getParameter("CCINS"), "UTF-8");
		String cardNo = URLDecoder.decode(request.getParameter("CCNUM"), "UTF-8");
		String cardCVV = URLDecoder.decode(request.getParameter("CVVAL"), "UTF-8");

    	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Submit the data to the XiSecure SAAS environment
    	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		pci = new PCIPal(EndpointType.QA, secret, mid);
		pci.addXIFieldType(XIFieldType.PaymentType, cardType);
		pci.addXIFieldType(XIFieldType.CreditCard, cardNo);
		pci.addXIFieldType(XIFieldType.ExpirationDate, "209912");
		pci.addXIFieldType(XIFieldType.CVV, cardCVV);
		pci.submitForTokenization(sessionId);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Retrieve the data from the XiSecure SAAS environment
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	if(jsonRS.getString("ppErrorCode").equalsIgnoreCase("Ok") && jsonRS.getString("semafoneErrorCode").equalsIgnoreCase("0"))
	{
		String wscURL = XIConfig.getProperty("XI.semafone.connector", "http://localhost:9080/XiSecureWSC/XiSecure.svc");
		boolean logging = XIConfig.getProperty("XI.semafone.logging", "false").equalsIgnoreCase("true");
		XiSecureWSC xiWSC = new XiSecureWSC(wscURL, logging);
		result = xiWSC.readSessionID(sessionId);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Copy the data to the hidden input fields
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(null != result)
		{
			root = result.getRootElement();
			jsonRS.put("sessionState", root.getChildText("SessionState"));
		    request.setAttribute("SessionState", root.getChildText("SessionState"));
			jsonRS.put("cardToken", root.getChildText("TokenizedFieldValue"));
		    request.setAttribute("CardToken", root.getChildText("TokenizedFieldValue"));
		    request.setAttribute("CardType", jsonRS.getString("cardType"));
			
		    @SuppressWarnings("unchecked")
		    List<Element> values = root.getChild("SessionValues").getChildren();
			for(Element value : values)
			{
				if(value.getChildText("Key").equalsIgnoreCase("CVVAL"))
				{
					jsonRS.put("cardCVV", value.getChildText("Value"));
					request.setAttribute("CardCVV", value.getChildText("Value"));
					break;
				}
			}
		}
	}
	semafoneResponse = jsonRS.toString();
	request.setAttribute("semafoneResponse", semafoneResponse);
}
catch(Exception ex)
{
	ex.printStackTrace();
}

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="cache-control" content="max-age=0" />
		<meta http-equiv="cache-control" content="no-cache" />
		<title>XI for DTMF - Implementation Response</title>
		<script type="text/javascript">
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Send Semafone Response Message
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    function sendSemafoneResponse()
	    {
			if(window.parent.document != window.document)
			{
				var strJson = '${semafoneResponse}';
				window.parent.handleSemafoneResponse(strJson);
				return;
		    }
			document.getElementById("semafoneRS").style.display = 'inline';
	    }
		</script>
	</head>
	<body onload="sendSemafoneResponse();">
		<input id="SessionState" name="SessionState" value="${SessionState}" type="hidden"/>
    	<input id="CardType" name="CardType" value="${CardType}" type="hidden"/>
    	<input id="CardToken" name="CardToken" value="${CardToken}" type="hidden"/>
    	<input id="CardCVV" name="CardCVV" value="${CardCVV}" type="hidden"/>
    	<div id="semafoneRS" style="display: none">
		    <table>
		        <tr>
		            <td align="right" valign="middle">Session State:&nbsp;&nbsp;</td>
		            <td valign="middle">${SessionState}</td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Card Type:&nbsp;&nbsp;</td>
		            <td valign="middle">${CardType}</td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Card Number:&nbsp;&nbsp;</td>
		            <td valign="middle">${CardToken}</td>
		        </tr>
		        <tr>
		            <td align="right" valign="middle">Card CVV:&nbsp;&nbsp;</td>
		            <td valign="middle">${CardCVV}</td>
		        </tr>
		    </table>
		    <pre id="DebugPacket">
		    	<code class="xml json hljs" id="ResponseSpan">${semafoneResponse}</code>
		    </pre>
    	</div>
	</body>
</html>