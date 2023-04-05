<%--
    Document   : XIeCommerce3AliPayRQ.jsp
    Company    : Paymetric
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.models.AliPayPostPacket.AliPayPostPacket"%>
<%@page import="com.paymetric.sdk.XIConfig"%>
<%@ page isELIgnored="false" %>


<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Forward-Declare global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
String strResponseURL = "XIeCommerce3AliPayRS.jsp";
XIConfig xiConfig = null;
AliPayPostPacket packetRQ = null;
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
	packetRQ = xiConfig.getAliPayPacket(request, strResponseURL);
	packetRS = AccessTokenUtility.GetAliPayAccessToken(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(), XIConfig.URL.get(), packetRQ);
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
		request.setAttribute("MerchantGuid", XIConfig.CC_GUID.get());
	    request.setAttribute("accessToken", packetRS.getResponsePacket().getAccessToken());
	    request.setAttribute("signature", packetRS.getResponsePacket().getSignature());
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
		<title>XiIntercept v3 - FORM-POST AliPay Request</title>
    </head>
    
    <script src="${XIeCommURL}/Scripts/XIPlugin/XIPlugin-1.1.0.js"></script> 
    <script src="${XIeCommURL}/Scripts/AliPayPlugin/AliPay-1.0.0.js" type="text/javascript" ></script> 
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
 
    <script>
    var aliPayURL = '/AliPay';
	var xiecommUrl = '${XIeCommURL}' + aliPayURL;

    var merchantGuid = '${MerchantGuid}';
    var accessToken = '${accessToken}';
    
    function submitform(orderData) {
    	
            var email = document.getElementById('Email').value;
        	
            var billingFirstName = document.getElementById('BillingFirstName').value;
            var billingMiddleName = document.getElementById('BillingMiddleName').value;
            var billingLastName = document.getElementById('BillingLastName').value;
            var billingAddress1 = document.getElementById('BillingAddress1').value;
            var billingAddress2 = document.getElementById('BillingAddress2').value;
            var billingCity = document.getElementById('BillingCity').value;
            var billingState = document.getElementById('BillingState').value;
            var billingPostalCode = document.getElementById('BillingPostalCode').value;
            var billingCountryCode = document.getElementById('BillingCountryCode').value;
            var billingPhone = document.getElementById('BillingPhone').value;

            var shippingFirstName = document.getElementById('ShippingFirstName').value;
            var shippingMiddleName = document.getElementById('ShippingMiddleName').value;
            var shippingLastName = document.getElementById('ShippingLastName').value;
            var shippingAddress1 = document.getElementById('ShippingAddress1').value;
            var shippingAddress2 = document.getElementById('ShippingAddress2').value;
            var shippingCity = document.getElementById('ShippingCity').value;
            var shippingState = document.getElementById('ShippingState').value;
            var shippingPostalCode = document.getElementById('ShippingPostalCode').value;
            var shippingCountryCode = document.getElementById('ShippingCountryCode').value;
            var shippingPhone = document.getElementById('ShippingPhone').value;
            var amount = (orderData.amount*100); // Cardinal requires the order amount in cents
            
            var myData = $AliPlugin.createAliPayRequestPacket(merchantGuid, accessToken);
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Email.Address, email));

            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingFirstName, billingFirstName));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingMiddleName, billingMiddleName));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingLastName, billingLastName));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingAddress1, billingAddress1));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingAddress2, billingAddress2));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingCity, billingCity));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingState, billingState));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingPostalCode, billingPostalCode));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingCountryCode, billingCountryCode));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Billing.BillingPhone, billingPhone));

            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingFirstName, shippingFirstName));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingMiddleName, shippingMiddleName));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingLastName, shippingLastName));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingAddress1, shippingAddress1));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingAddress2, shippingAddress2));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingCity, shippingCity));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingState, shippingState));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingPostalCode, shippingPostalCode));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingCountryCode, shippingCountryCode));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Shipping.ShippingPhone, shippingPhone));
            myData.addField($AliPlugin.createField($AliPlugin.AliPayDataEnum.Cost.Amount, amount));
 		
          $AliPlugin.submit({
                url: xiecommUrl,
                data: myData,
            });
        
        }
        
    	// getOrderData is implenented in Hybris JSP
        $(document).ready(function(){
        	if(window.opener != null) {
	        	var orderData = window.opener.getOrderData();  
	        	submitform(orderData);
        	}
       });               
       
    </script>
     <style>
        label {
            width: 200px;
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
     	<p>
            Please wait...You are being redirected to Alipay page.
        </p>
        
        <div id="SampleBodyDiv" class="sampleBodyShrinkToFit" >
            
       
        
        <p style="display:none;">
            <label for="Email">Email:</label><input type="text" id="Email" name="Email" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingFirstName">Billing First Name:</label><input type="text" id="BillingFirstName" name="BillingFirstName" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingMiddleName">Billing Middle Name:</label><input type="text" id="BillingMiddleName" name="BillingMiddleName" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingLastName">Billing Last Name:</label><input type="text" id="BillingLastName" name="BillingLastName" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingAddress1">Billing Address 1:</label><input type="text" id="BillingAddress1" name="BillingAddress1" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingAddress2">Billing Address 2:</label><input type="text" id="BillingAddress2" name="BillingAddress2" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingCity">Billing City:</label><input type="text" id="BillingCity" name="BillingCity" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingState">Billing State:</label><input type="text" id="BillingState" name="BillingState" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingPostalCode">Billing Postal Code:</label><input type="text" id="BillingPostalCode" name="BillingPostalCode" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingCountryCode">Billing Country Code:</label><input type="text" id="BillingCountryCode" name="BillingCountryCode" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="BillingPhone">Billing Phone:</label><input type="text" id="BillingPhone" name="BillingPhone" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingFirstName">Shipping First Name:</label><input type="text" id="ShippingFirstName" name="ShippingFirstName" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingMiddleName">Shipping Middle Name:</label><input type="text" id="ShippingMiddleName" name="ShippingMiddleName" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingLastName">Shipping Last Name:</label><input type="text" id="ShippingLastName" name="ShippingLastName" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingAddress1">Shipping Address 1:</label><input type="text" id="ShippingAddress1" name="ShippingAddress1" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingAddress2">Shipping Address 2:</label><input type="text" id="ShippingAddress2" name="ShippingAddress2" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingCity">Shipping City:</label><input type="text" id="ShippingCity" name="ShippingCity" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingState">Shipping State:</label><input type="text" id="ShippingState" name="ShippingState" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingPostalCode">Shipping Postal Code:</label><input type="text" id="ShippingPostalCode" name="ShippingPostalCode" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingCountryCode">Shipping Country Code:</label><input type="text" id="ShippingCountryCode" name="ShippingCountryCode" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="ShippingPhone">Shipping Phone:</label><input type="text" id="ShippingPhone" name="ShippingPhone" class="ccfields" />
        </p>
        <p style="display:none;">
            <label for="Amount">Total Amount:</label><input type="text" id="Amount" name="Amount" class="ccfields" />
        </p>
		<input type="hidden" id="AccessToken" value="${accessToken}" />
       	<input type="hidden" id="SignedToken" value="${signature}" />
        <br></br>
    </div>
        <div id="Status" ${errorstyle}>${errormessage}</div>
    </body>
</html>