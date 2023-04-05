<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket"%>
<%@page import="com.paymetric.sdk.models.MerchantHtmlPacket.IFrameMerchantHtmlPacket"%>
<%@page import="com.paymetric.sdk.models.MerchantHtmlPacket.Cardinal3DSAdditionalField"%>
<%@page import="com.paymetric.sdk.XIConfig"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>


<%
try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Forward-Declare global variables
	// http://localhost:8080/XiIntercept3/XiInterceptIFrameRQ.jsp?Amount=12.34&Currency=GBP&OrderNo=AP-001
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String strSite = null;
	String baseStore = null;
	XIConfig xiConfig = null;
	IFrameMerchantHtmlPacket packetRQ = null;
	AccessTokenResponsePacket packetRS = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xiConfig = new XIConfig();
	strSite = xiConfig.getSiteURL(request);
	baseStore = xiConfig.getStoreName(request);
	if(strSite.startsWith("http://localhost:8080"))
	{
		XIConfig.isConfigured.set(false);
	}
	xiConfig.Config(request, "XiIntercept.properties");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Initialize the IFRAME Template or Custom XML to provide to XiIntercept
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	packetRQ = xiConfig.getIFramePacket(request);
	if(xiConfig.is3DSEnabledForStore(baseStore))
	{
		String strOrderNo = xiConfig.getOrderNo(request);
		BigDecimal dAmount = xiConfig.getAmount(request);
		String strCurrency = xiConfig.getCurrency(request);
		String strVersion = XIConfig.getProperty("paymetric.xi.credit-card.3ds-version", "2.2.0");

		request.setAttribute("threeDS", "true");
		request.setAttribute("threeDSVersion", strVersion);
    	request.setAttribute("amount", dAmount.toString());
    	request.setAttribute("orderNo", strOrderNo);
    	request.setAttribute("currencyCode", strCurrency);

		List<Cardinal3DSAdditionalField> additionalFields = new ArrayList<Cardinal3DSAdditionalField> ();
        additionalFields.add(new Cardinal3DSAdditionalField("Amount", dAmount.toString()));
        additionalFields.add(new Cardinal3DSAdditionalField("OrderNumber", strOrderNo));
        additionalFields.add(new Cardinal3DSAdditionalField("CurrencyCode", strCurrency));

		packetRQ.SetCardinal3DSProperties(dAmount, strCurrency, strSite + "XiInterceptIFrameRS.jsp", strOrderNo, strVersion, additionalFields);
	}
	else
	{
		request.setAttribute("threeDS", "false");
	}

	packetRS = AccessTokenUtility.GetIFrameAccessToken(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(), XIConfig.URL.get(), packetRQ);
	if (!(packetRS.getRequestError() + "").equals("null")) 
	{
		String strError = packetRS.getRequestError().getStatusCode() + "," + packetRS.getRequestError().getMessage();
	    request.setAttribute("errormessage", strError);
	    request.setAttribute("errorstyle", "class='errorMsg'");
	} 
	else
	{
	    String accessToken = packetRS.getResponsePacket().getAccessToken();
	    String IFrameURL = XIConfig.URL.get() + "/view/iframe/" + XIConfig.CC_GUID.get() + "/" + accessToken + "/true";

	    request.setAttribute("XIeCommUrl", XIConfig.URL.get());
	    request.setAttribute("AutoSizeHeight", XIConfig.AutoSizeHeight.get());
	    request.setAttribute("AutoSizeWidth", XIConfig.AutoSizeWidth.get());
	    request.setAttribute("AutoSizeDelay", XIConfig.getProperty("paymetric.xi.credit-card.iframe.autosize.delay", "1000"));
	    request.setAttribute("accessToken", accessToken);
	    request.setAttribute("merchantGuid", XIConfig.CC_GUID.get());
	    request.setAttribute("signature", packetRS.getResponsePacket().getSignature());
	    request.setAttribute("IFrameURL", IFrameURL);
	    request.setAttribute("site", strSite);
	    request.setAttribute("responseURL", strSite + "XiInterceptIFrameRS.jsp");
	}
}
catch(Exception ex)
{
    request.setAttribute("errormessage", ex.getMessage());
    request.setAttribute("errorstyle", "class='errorMsg'");
}

%>

<!DOCTYPE>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="cache-control" content="max-age=0" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta charset="ISO-8859-1" />
<!-- 		<meta name="viewport" content="width=device-width, initial-scale=1.0"> -->
		<title>XiIntercept v3 - HOSTED-IFRAME Request</title>
    	<link href="IFrameStyleSheet.css" type="text/css" rel="stylesheet" />
    	<link href="XIeCommerce3.css" type="text/css" rel="stylesheet" />
		<script src="XiInterceptIFrameRQ.js"></script>
	    <script src="${XIeCommUrl}/scripts/XIFrame/XIFrame-1.2.0.js" type="text/javascript"></script>
	    <script src="${XIeCommUrl}/scripts/XIPlugin/XIPlugin-1.2.0.js" type="text/javascript"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	    <script type="text/javascript">

			try
			{
			    strError = "${errormessage}";
				merchantGuid = '${merchantGuid}';
				accessToken = '${accessToken}';
				signature = '${signature}';
				amount = '${amount}';
				orderNo = '${orderNo}';
				currencyCode = '${currencyCode}';
			    threeDSVersion = '${threeDSVersion}';
			    threeDS = ${threeDS};
			    autoSizeHeight = ${AutoSizeHeight};
			    autoSizeWidth = ${AutoSizeWidth};
			    autoSizeDelay = ${AutoSizeDelay};
			    responseURL = '${responseURL}' + '?id=' + accessToken + '&s=' + signature;
				xiTokenize = document.getElementById('xiTokenize');
				xiStatus = document.getElementById('Status');

				window.addEventListener("message", clientListener);

// 				debugger;
// 			    var iFrame = document.getElementById('xieCommFrame');
// 		        iFrame.contentWindow.addEventListener("resize", resizer);
			}
			catch (err)
			{
			}

			function resizer(event)
			{
				var strWhere = "resizer()";
			    var strData = event.data.toString();

				console.log(strWhere + " - " + strData);
			}
	    </script>
	</head>
	<body>
	    <div id="IFrameWrapper" class="sampleBody">
	        <div id="XIFrame" class="payment-content">
                <div class="billing-info2">
                    <div id="iframewrapper">
                        <iframe id="xieCommFrame" name="xieCommFrame" style="border:none;" src="${IFrameURL}" 
                        	onload="onLoadIFrame();">
                        </iframe>
                    </div>
					<input id="xiTokenize" class="payment-button" type="button" value="Tokenize..." onclick="submitform(); return false;" style="display: none" />
	        	</div>
	        </div>
	        <br/>
	        <div id="Status" ${errorstyle}>${errormessage}</div>
	    </div>
	</body>
</html>