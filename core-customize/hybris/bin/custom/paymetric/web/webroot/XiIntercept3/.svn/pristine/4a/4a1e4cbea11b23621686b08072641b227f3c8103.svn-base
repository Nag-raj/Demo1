<%--
    Document   : XIeCommerceAjaxRQ2.jsp
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
<%@page import="java.util.Base64.Encoder"%>
<%@page import="java.util.Base64.Decoder"%>
<%
	try {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	XIConfig xiConfig = new XIConfig();
	String strSite = xiConfig.getSiteURL(request);
	if(strSite.startsWith("http://localhost:8080"))
	{
		XIConfig.isConfigured.set(false);
	}
	xiConfig.Config(request, "XiIntercept.properties");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	request.setAttribute("XIeCommURL", XIConfig.URL.get());
	request.setAttribute("MerchantGuid", XIConfig.CC_GUID.get());
	request.setAttribute("site", xiConfig.getSiteURL(request));
	
} catch (Exception ex) {
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

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

</head>

<script>
	var merchantGuid = '${MerchantGuid}';
	var accessToken = '${accessToken}';

	function fetchAccessToken() {
		$
				.ajax({
					type : "GET",
					contentType : "application/json",
					url : "${site}" + "api/ajax/access-token",
					cache : false,
					timeout : 600000,
					success : function(data) {
						var jsonObject = JSON.parse(JSON.stringify(data));
						if (jsonObject["responsePacket"]) {

							accessToken = jsonObject["responsePacket"]["accessToken"];
							merchantGuid = jsonObject["responsePacket"]["merchantGuid"];
							PaymetricAjaxPost();
							console
									.log("SUCCESS : "
											+ jsonObject["responsePacket"]["accessToken"]);
							$("#Ajax_Rest_AccessToken_Response")
									.text(jsonObject["responsePacket"]["accessToken"]);
							$("#Ajax_Rest_AccessToken_Response_Body").show();
							$("#tokenize").show();
						} else {
							console.log("Failed to retrieve access token");
							$("#Ajax_Rest_AccessToken_Response")
									.text(
											"Status Code:"
													+ jsonObject["requestError"]["statusCode"]
													+ "\nError Message:"
													+ jsonObject["requestError"]["message"]);
							$("#Ajax_Rest_AccessToken_Response_Body").show();
						}
					},
					error : function(e) {
						console.log(" Error -- Get Access Token ");
						var json = "<h4>Ajax Response</h4>&lt;pre&gt;"
								+ e.responseText + "&lt;/pre&gt;";

						console.log("ERROR : ", e.responseText);

					}
				});

	}

	function PaymetricAjaxPost() {

		
				
    var  formData = "MerchantGuid="+merchantGuid+"&AccessToken="+accessToken+"&FormFields%5B0%5D%5BName%5D=PaymentCreditCard"+
                        "&FormFields%5B0%5D%5BIsToTokenize%5D=true&FormFields%5B0%5D%5BValue%5D=4444333322221111";
				

		$.ajax({
			type : "POST",
			contentType : "application/x-www-form-urlencoded",
			url : '${XIeCommURL}' + '/Ajax',
			data : formData,
			success : function(msg) {
				var message = JSON.parse(JSON.stringify(msg));
				if (message && message["HasPassed"]) {
					$.ajax({
						type : "GET",
						contentType : "text/plain",
						url : "${site}"
								+ "api/ajax/response-packet?accessToken="
								+ accessToken,
						cache : false,
						timeout : 600000,
						success : function(data) {
							console.log("SUCCESS : " + data);
							$("#Ajax_Rest_Response").text(data);
							$("#Ajax_Rest_Response_Body").show();

							$("#Ajax_Rest_Request")
									.text(formData);
							$("#Ajax_Rest_Response_Packet").text(data);
						},
						error : function(e) {
							var json = "<h4>Ajax Response</h4>&lt;pre&gt;"
									+ e.responseText + "&lt;/pre&gt;";

							console.log("ERROR : ", e.responseText);

						}
					});

				} else {
					$("#Ajax_Rest_Response_Packet").text(
							"Error Code:" + message.Code + "\nError Message:"
									+ message.Message);
				}
			},
			error : function(a) {
				var errorResponse = JSON.parse(JSON.stringify(a));
				$("#Ajax_Rest_Response_Packet").text(
						"Error Code:" + errorResponse.Code + "\nError Message:"
								+ errorResponse.Message);
			}
		});
	}
</script>
<style>
p {
	clear: both;
	padding: 5px;
}

* {
	box-sizing: border-box;
}

/* Create three unequal columns that floats next to each other */
.column {
	float: left;
	padding: 10px;
	height: 300px; /* Should be removed. Only for demonstration */
}

.left{
	width: 600px;
}

.right {
	width: 800px;
}

.middle {
	width: 600px;
}

/* Clear floats after the columns */
.row:after {
	content: "";
	display: table;
	clear: both;
}
</style>
<body onload="javascript:fetchAccessToken();">

	<form>
		<div class="row">
			<div class="column left" id="Ajax_Rest_AccessToken_Response_Body" style="background-color: #aaa;">
				<h2>Access Token</h2>
				<pre id="DebugPacket" style="white-space: pre-wrap; width: 500px;">
            	<textarea rows="10" cols="60" id="Ajax_Rest_AccessToken_Response"></textarea>
            </pre>
			</div>
			<div class="column middle" id="Ajax_Rest_Request_Body" style="background-color: #bbb;">
				<h2>Request</h2>
				<pre id="DebugPacket" style="white-space: pre-wrap; width: 500px;">
            	<textarea rows="10" cols="60" id="Ajax_Rest_Request"></textarea>
            </pre>
			</div>
			<div class="column right" id="Ajax_Rest_Response_Packet_Body" style="background-color: #ccc;">
				<h2>Response</h2>
				<pre id="DebugPacket" style="white-space: pre-wrap; width: 500px;">
            	<textarea rows="10" cols="60" id="Ajax_Rest_Response_Packet"></textarea>
            </pre>
			</div>
		</div>
	</form>
	<div id="Status" ${errorstyle}>${errormessage}</div>
</body>
</html>