<%--
    Document   : XIeCommerceAjaxRQ.jsp
    Author     : Kishore Kanumalla, Srirup Das
    Company    : Paymetric
--%>
<%--
    Here are some of the XiIntecept Error codes and messages. Please refer to the XiIntecept Integration Guide for more error codes and messages.
    
    Error Code				Description		
    -100: 					Indicates an unexpected error occurred processing the server request
	-101: 					Indicates the web request could not be created
	-102: 					Indicates the attempt to retrieve the response failed
	-103: 					Indicates no response packet was returned from the server
	-104: 					Indicates the response packet was invalid and could not be deserialized
	 100: 					Internal DI eComm Error
	 101: 					Merchant GUID is Invalid
	 102: 					Signature Validation Failed
	 103: 					Request Packet is Invalid
	 104: 					DI eComm Session has Expired
	 105: 					DI eComm Session Error
	 106: 					Error Decrypting Data
	 107: 					Get Access Token Error
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page
	import="com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket"%>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.models.PostPacket.PostPacket"%>
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
try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	XIConfig xiConfig = new XIConfig();
	xiConfig.Config(request, "XiIntercept.properties");


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		request.setAttribute("XIeCommURL", XIConfig.URL.get()); 
		request.setAttribute("MerchantGuid", XIConfig.CC_GUID.get());
		request.setAttribute("site", xiConfig.getSiteURL(request));
	
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
<title>XiIntercept v3 - HOSTED-IFRAME Request</title>
<link href="IFrameStyleSheet.css" type="text/css" rel="stylesheet" />
<link href="XIeCommerce3.css" type="text/css" rel="stylesheet" />
<script src="XiInterceptIFrameRQ.js"></script>
<script src="${XIeCommURL}/scripts/XIFrame/XIFrame-1.2.0.js"
	type="text/javascript"></script>
<script src="${XIeCommURL}/scripts/XIPlugin/XIPlugin-1.2.0.js"
	type="text/javascript"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

</head>

<script>
	var merchantGuid = '${MerchantGuid}';
	var accessToken = '${accessToken}';

	function fetchAccessToken() {
		$.ajax({
			type : "GET",
			contentType : "application/json",
			url : "${site}" + "api/ajax/access-token",
			cache : false,
			timeout : 600000,
			success : function(data) {
				var jsonObject = JSON.parse(JSON.stringify(data));
				if(jsonObject["responsePacket"]){
					accessToken  = jsonObject["responsePacket"]["accessToken"];
					merchantGuid = jsonObject["responsePacket"]["merchantGuid"];
					console.log("SUCCESS : " + jsonObject["responsePacket"]["accessToken"]);
				}else{
					console.log("Failed to retrieve access token");
					$("#Ajax_Rest_Response").text("Status Code:"+jsonObject["requestError"]["statusCode"]+"\nError Message:"+jsonObject["requestError"]["message"]);
        			$("#Ajax_Rest_Response_Body").show();
				}
			},
			error : function(e) {
				console.log(" Error -- Get Access Token ");
				var json = "<h4>Ajax Response</h4>&lt;pre&gt;" + e.responseText
						+ "&lt;/pre&gt;";

				console.log("ERROR : ", e.responseText);

			}
		});

	}

	

	function PaymetricAjaxPost() {
		var cc = document.getElementById('CardNo').value;
		var myData = $XIPlugin.createJSRequestPacket(merchantGuid, accessToken);
		myData.addField($XIPlugin.createField('PaymentCreditCard', true, cc));
		
		$XIPlugin.ajax({
            url: '${XIeCommURL}' + '/Ajax',
            type: "POST",
            data: myData,
	                  success: function (msg)
	                  {
	                    	var message = JSON.parse(JSON.stringify(msg));
	                        if (message && message["HasPassed"])
	                        {
	                        	$.ajax({
	                        		type : "GET",
	                        		contentType : "text/plain",
	                        		url : "${site}" + "api/ajax/response-packet?accessToken=" + accessToken,
	                        		cache : false,
	                        		timeout : 600000,
	                        		success : function(data) {
	                        			console.log("SUCCESS : " + data);
	                        			$("#Ajax_Rest_Response").text(data);
	                        			$("#Ajax_Rest_Response_Body").show();
	                        		},
	                        		error : function(e) {
	                        			var json = "<h4>Ajax Response</h4>&lt;pre&gt;" + e.responseText
	                        					+ "&lt;/pre&gt;";

	                        			console.log("ERROR : ", e.responseText);

	                        		}
	                        	});                        
	                            
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
	                	  var errorResponse = JSON.parse(JSON.stringify(a));
	                      $("#Ajax_Rest_Response").text("Error Code:"+errorResponse.Code+"\nError Message:"+errorResponse.Message);
	                  }
	              });
	}

	function passOrderNumber(args) {
		document.getElementById('CardNo').value = args; //assign order number value  O-XXXXXX for paypal integration
		document.getElementById('xiTokenize').click();
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
<body onload="javascript:fetchAccessToken();">
	<form>
		<div class="sampleBodyShrinkToFit">
			<p>
				<label for="PaymentType">Credit Card:</label><input type="text"
					id="CardNo" name="CardNo" value="" class="ccfields" />
			</p>
			<p>
				<br /> <input id="xiTokenize" type="button" value="Tokenize..."
					onclick="PaymetricAjaxPost(); return false;" />
		</div>
		<div class="sampleBody" id="Ajax_Rest_Response_Body" style="display:none">
            <h1>Response</h1>
            <pre id="DebugPacket" style="white-space: pre-wrap; width: 500px;">
            	<textarea rows="10" cols="60" id="Ajax_Rest_Response"></textarea>
            </pre>
        </div>
	</form>
	<div id="Status" ${errorstyle}>${errormessage}</div>
</body>
</html>