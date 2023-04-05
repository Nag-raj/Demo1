<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket"%>
<%@page import="com.paymetric.sdk.models.Cardinal3DSWithToken.Cardinal3DSWithTokenPacket"%>
<%@page import="com.paymetric.sdk.models.Cardinal3DSWithToken.Cardinal3DSRequestWithToken"%>
<%@page import="com.paymetric.sdk.models.MerchantHtmlPacket.Cardinal3DSAdditionalField"%>
<%@page import="com.paymetric.sdk.XIConfig"%>


<%
try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Forward-Declare global variables
	// http://localhost:8080/XiIntercept3/XiInterceptIFrameTokenRQ.jsp?token=-E803-0002-R2SFN00000000B&expMonth=01&expYear=2025&Amount=12.34
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String strLocation = null;
	String strStore = null;
	XIConfig xiConfig = null;
	Cardinal3DSWithTokenPacket packetRQ = null;
	AccessTokenResponsePacket packetRS = null;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xiConfig = new XIConfig();
	xiConfig.Config(request, "XiIntercept.properties");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Initialize the Saved Token Packet to send to XiIntercept
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String strSite = xiConfig.getSiteURL(request);
	BigDecimal amount = xiConfig.getAmount(request);
	String OrderNumber = xiConfig.getOrderNo(request);
	String CurrencyCode = xiConfig.getCurrency(request);
	String baseStore = xiConfig.getStoreName(request);
	String expMonth = XIConfig.getParameter(request, "expMonth");
	String expYear = XIConfig.getParameter(request, "expYear");
	String strRS = xiConfig.getResponse(request);
	
	if(xiConfig.is3DSEnabledForStore(baseStore))
	{	
		packetRQ = xiConfig.getSavedTokenPacket(request);
		packetRS = AccessTokenUtility.Get3DsWithTokenAccessToken(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(), XIConfig.URL.get(), packetRQ);
		if (!(packetRS.getRequestError() + "").equals("null")) 
		{
			String strError = packetRS.getRequestError().getStatusCode() + "," + packetRS.getRequestError().getMessage();
		    request.setAttribute("errormessage", strError);
		    request.setAttribute("errorstyle", "class='errorMsg'");
		}
		else
		{
		    String accessToken = packetRS.getResponsePacket().getAccessToken();
		    request.setAttribute("XIeCommUrl", XIConfig.URL.get());
		    request.setAttribute("AutoSizeHeight", XIConfig.AutoSizeHeight.get());
		    request.setAttribute("AutoSizeWidth", XIConfig.AutoSizeWidth.get());
		    request.setAttribute("accessToken", accessToken);
		    request.setAttribute("merchantGuid", XIConfig.CC_GUID.get());
				request.setAttribute("threeDSVersion", XIConfig.CC_3DS_Version.get());
		    request.setAttribute("signature", packetRS.getResponsePacket().getSignature());
		    request.setAttribute("iFrameUrl", XIConfig.URL.get() + "/Cardinal3DSWithToken");
		    request.setAttribute("site", strSite);
		    request.setAttribute("amount", amount.toString().replaceAll(".", ""));
		    request.setAttribute("orderNo", OrderNumber);
		    request.setAttribute("currencyCode", CurrencyCode);
		    request.setAttribute("expMonth", expMonth);
		    request.setAttribute("expYear", expYear);
		    request.setAttribute("secureresponse", strSite + "XiInterceptIFrameRS.jsp");
		}
	}
	else
	{
		request.setAttribute("errormessage", "3DS is disabled for this Store front");
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
		<title>XiIntercept v3 - 3DS with Token Request</title>
	    <script src="${XIeCommUrl}/scripts/XIFrame/XIFrame-1.2.0.js" type="text/javascript"></script>
	    <script src="${XIeCommUrl}/scripts/XIPlugin/XIPlugin-1.2.0.js" type="text/javascript"></script>
	    <script type="text/javascript" src="3DS2/paymetric-3ds2.js"></script>
	    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
  		
	    <script type="text/javascript">
	    	var strError = '${errormessage}';
            var xiTokenize = null;
            var xiStatus = null;

			window.addEventListener("message", tokenListener);


      function tokenListener(event)
      {
          var strData = event.data.toString();

          if (strData.indexOf("SUBMIT_TOKEN") != -1)
          {
              submitform();
              return;
          }
			}
	        
      function submitform()
      {
					var merchantGuid = '${merchantGuid}';
					var accessToken = '${accessToken}';
					var amount = '${amount}';
					var signedToken = document.getElementById('SignedToken');
					var DSData = $XIPlugin.createJSRequestPacket(merchantGuid, accessToken);
					var json3DSData = null;
					var iField = 0;
					var fields = null;
					var field = null;
					var obj = null;

					DSData.addField($XIPlugin.createField('Amount', false, amount.replace('.', '')));
					DSData.addAdditional3DSField("OrderNumber","${orderNo}");
					DSData.addAdditional3DSField("CurrencyCode","${currencyCode}");
					DSData.addAdditional3DSField("CardExpMonth","${expMonth}");
					DSData.addAdditional3DSField("CardExpYear","${expYear}");

					// ACSWindowSize field: 01=250x400, 02=390x400, 03=500x600, 04=600x400, 05=Full Page
					DSData.addAdditional3DSField("ACSWindowSize", "03");  
				 
					if(typeof window.parent.get3DSData == "function")
					{
							json3DSData=window.parent.get3DSData();
							obj = JSON.parse(json3DSData);
					    fields = obj.Fields.Field;
					    for(iField = 0; iField < fields.length; iField++)
					    {
								field = fields[iField];
								
								if (field.Value !== undefined && field.Value !== "") {
									DSData.addAdditional3DSField(field.Name, field.Value);
								}
							}
					}

          $XIPlugin.ajax({
              url: '${iFrameUrl}',
              type: "POST",
              data: DSData,
              threeDSVersion: '${threeDSVersion}',
	                  onSuccess: function (msg)
	                  {
	                    	var message = JSON.parse(msg);
	                        if (message && message.data.HasPassed)
	                        {
	                            var accessToken = document.getElementById('AccessToken');
	                            var signedToken = document.getElementById('SignedToken');
	                                                      
	                            window.location = "${secureresponse}?id=" + accessToken.value + "&s=" + signedToken.value;
	                        }
	                        else
	                        {
	                        	if(typeof window.parent.DisplayMessage === "function")
	                        	{
		                        	window.parent.DisplayMessage("XiInterceptIFrameRQ","ERROR",message.data.Message);
	                        	}
	                        }
	                  },
	                  error: function (a)
	                  {
	                      if (a)
	                          $('#Status').html(a.Message).addClass('errorMsg');
	                      else
	                          $('#Status').html('Error').addClass('error');
	                  }
	              });
	        }
	    </script>
	</head>
	<body onload="submitform();">
	    <div class="sampleBody">
	        <div class="payment-content">
                <div class="billing-info">
					<input id="xiTokenize" type="button" value="Tokenize..." onclick="submitform(); return false;" style="display: none" />
	            	<input type="hidden" id="AccessToken" value="${accessToken}" />
	            	<input type="hidden" id="SignedToken" value="${signature}" />
	        	</div>
	        </div>
	        <br/>
	        <div id="Status" ${errorstyle}>${errormessage}</div>
	    </div>
	</body>
</html>