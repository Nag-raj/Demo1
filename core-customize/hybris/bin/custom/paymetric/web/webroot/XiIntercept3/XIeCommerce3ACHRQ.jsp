<%--
    Document   : XIeCommerce3ACHRQ.jsp
    Author     : Alexander Perez
    Company    : Paymetric
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.models.PostPacket.PostPacket"%>
<%@page import="com.paymetric.sdk.XIConfig"%>

<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Forward-Declare global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
String strResponseURL = "XIeCommerce3ACHRS.jsp";
XIConfig xiConfig = null;
PostPacket packetRQ = null;
AccessTokenResponsePacket packetRS = null;

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
	packetRQ = xiConfig.getPostPacket(request, strResponseURL);
	packetRS = AccessTokenUtility.GetPostAccessToken(XIConfig.EC_GUID.get(), XIConfig.EC_PSK.get(), XIConfig.URL.get(), packetRQ);
	if (!(packetRS.getRequestError() + "").equals("null")) 
	{
		String strError = "Status Code=" + packetRS.getRequestError().getStatusCode() + ", Message=" + packetRS.getRequestError().getMessage();
	    if(packetRS.getRequestError().getStatusCode() == 301) 
	    {
	        request.setAttribute("errormessage", strError + "<br/>The Merchant Guid is not configured properly.<br />" + 
	    		"This application will only work with a GUID that has been configured with Cardinal Commerce for 3DS.<br/>" );
	    } 
	    else
	    {
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
	    }
	    request.setAttribute("errorstyle", "class='errorMsg'");
	} 
	else
	{
		request.setAttribute("XIeCommURL", XIConfig.URL.get()); 
		request.setAttribute("MerchantGuid", XIConfig.EC_GUID.get());
	    request.setAttribute("accessToken", packetRS.getResponsePacket().getAccessToken());
		request.setAttribute("SupportECheck", XIConfig.isECheck.get());
		request.setAttribute("site", xiConfig.getSiteURL(request));
	}
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
		<title>XiIntercept v3 - FORM-POST ACH Request</title>
    </head>
    <script src="${XIeCommURL}/Scripts/XIPlugin/XIPlugin-1.1.0.js"></script>
 	
    <script>
	    var xiecommUrl = '${XIeCommURL}' + "/Form";
	    var merchantGuid = '${MerchantGuid}';
	    var accessToken = '${accessToken}';

        gv_eChecks = Boolean("${SupportECheck}".toLowerCase());
        if (parent.gv_eChecks != "undefined")
            parent.gv_eChecks = gv_eChecks;

	    function PaymetricFormPost() 
	    {
	        var cc = document.getElementById('CardNo').value;            
	        var myData = $XIPlugin.createJSRequestPacket(merchantGuid, accessToken);
	        myData.addField($XIPlugin.createField('CardNo', true, cc));
	        
	        $XIPlugin.submit({
	            url: xiecommUrl,
	            data: myData
	        });
	    }
    </script>
    <style>
        label {
            width: 100px;
            float: left;
            text-align: right;
        }

            label.short {
                width: 50px;
            }

        input.ccfields, select {
            width: 300px;
            float: left;
        }

            input.short, select.short {
                width: 75px;
            }

        p {
            clear: both;
            padding: 5px;
        }
    </style>
    <body>
        <form>
            <div class="sampleBodyShrinkToFit">
                 <p>
                     <label for="PaymentType">Bank Account:</label><input type="text" id="CardNo" name="CardNo" value="" class="ccfields" /></p>
                 <p>
                <br/> 
                <input id="xiTokenize" type="button" value="Tokenize..." onclick="PaymetricFormPost(); return false;" />
             </div>
        </form>
        <div id="Status" ${errorstyle}>${errormessage}</div>
    </body>
</html>