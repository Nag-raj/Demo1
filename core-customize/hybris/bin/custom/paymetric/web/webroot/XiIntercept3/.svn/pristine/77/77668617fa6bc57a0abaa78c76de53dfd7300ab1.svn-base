<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket"%>
<%@page import="com.paymetric.sdk.models.MerchantHtmlPacket.IFrameMerchantHtmlPacket"%>
<%@page import="com.paymetric.sdk.XIConfig"%>


<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Forward-Declare global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
XIConfig xiConfig = null;
IFrameMerchantHtmlPacket packetRQ = null;
AccessTokenResponsePacket packetRS = null;

try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xiConfig = new XIConfig();
	String strSite = xiConfig.getSiteURL(request);
	if(strSite.startsWith("http://localhost:8080"))
	{
		XIConfig.isConfigured.set(false);
	}
	xiConfig.Config(request, "XiIntercept.properties");


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Initialize the IFRAME Template or Custom XML to provide to XiIntercept
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	packetRQ = xiConfig.getIFramePacketACH(request);

	packetRS = AccessTokenUtility.GetIFrameAccessToken(XIConfig.EC_GUID.get(), XIConfig.EC_PSK.get(), XIConfig.URL.get(), packetRQ);
	if (!(packetRS.getRequestError() + "").equals("null")) 
	{
		String strError = "Status Code=" + packetRS.getRequestError().getStatusCode() + ", Message=" + packetRS.getRequestError().getMessage();
        String error = packetRS.getRequestError().toString();
        if (error.contains("com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket")) 
        {
            request.setAttribute("errormessage", strError + "<br/>The access token could not be procured. " + 
        		"Could your merchant guid or shared key be misconfigured?");
        } 
        else
        {
            request.setAttribute("errormessage", strError);
        }
	    request.setAttribute("errorstyle", "class='errorMsg'");
	} 
	else
	{
	    String accessToken = packetRS.getResponsePacket().getAccessToken();
	    request.setAttribute("XIeCommUrl", XIConfig.URL.get());
	    request.setAttribute("AutoSizeHeight", XIConfig.AutoSizeHeight.get());
	    request.setAttribute("AutoSizeWidth", XIConfig.AutoSizeWidth.get());
	    request.setAttribute("accessToken", accessToken);
	    request.setAttribute("signature", packetRS.getResponsePacket().getSignature());
	    request.setAttribute("iFrameUrl", XIConfig.URL.get() + "/view/iframe/" + XIConfig.EC_GUID.get() + "/" + accessToken + "/true");
	    request.setAttribute("site", strSite);
	    request.setAttribute("secureresponse", strSite + "XiInterceptIFrameACHRS.jsp");
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
		<title>XiIntercept v3 - HOSTED-IFRAME ACH Request</title>
	    <script src="${XIeCommUrl}/scripts/XIFrame/XIFrame-1.1.0.js" type="text/javascript"></script>
	    <script type="text/javascript">
	        function submitform()
	        {
	            var iframe = document.getElementsByName('xieCommFrame');
	            
	            if (iframe) 
	            {
	                $XIFrame.submit({
	                    iFrameId: 'xieCommFrame',
	                    targetUrl: iframe[0].getAttribute("src"),
	                    onSuccess: function (msg) {
	                        var message = JSON.parse(msg);
	                        if (message && message.data.HasPassed)
	                        {
	                            var accessToken = document.getElementById('AccessToken');
	                            var signedToken = document.getElementById('SignedToken');
	                            window.location = "${secureresponse}?id=" + accessToken.value + "&s=" + signedToken.value;
	                        }
	                        else
	                        {
	                            alert(message.data.Message);
	                        }
	                    },
	                    onError: function (msg)
	                    {
	                        alert(msg);
	                    }
	                });
	            }
	        }
	
	        function IFrame_OnLoad()
	        {
                var xiTokenize = document.getElementById('xiTokenize');
                if (xiTokenize)
                {
                	if(typeof window.parent.InitForTokenization == 'undefined')
                		xiTokenize.style.display = 'block';
                	else xiTokenize.style.display = 'none';
		        }
                
                var iframe = document.getElementsByName('xieCommFrame');
                if (iframe)
                {
                    $XIFrame.onload({
                        iFrameId: 'xieCommFrame',
                        targetUrl: iframe[0].getAttribute("src"),
                        autosizeheight: ${AutoSizeHeight},
                        autosizewidth: ${AutoSizeWidth},
                        onSuccess: function (msg) 
                        {
                            console.log('A form for the merchant guid and access token combination is loading in the iFrame successfully.');
                        },
                        onError: function (msg) {
                            console.log('A form for the merchant guid and access token combination has FAILED to load.');
                        }
                    });
                }
	        }
	    </script>
	</head>
	<body>
	    <div class="sampleBody">
	        <div class="payment-content">
                <div class="billing-info">
                    <div id="iframewrapper">
                        <iframe id="xieCommFrame" name="xieCommFrame" style="border:none;display: block" src="${iFrameUrl}" onload="IFrame_OnLoad();return false;"></iframe>
                    </div>
					<input id="xiTokenize" type="button" value="Tokenize..." onclick="submitform(); return false;" style="display: none" />
	            	<input type="hidden" id="AccessToken" value="${accessToken}" />
	            	<input type="hidden" id="SignedToken" value="${signature}" />
	        	</div>
	        </div>
	        <div id="Status" ${errorstyle}>${errormessage}</div>
	    </div>
	</body>
</html>