<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.XIConfig"%>

<!DOCTYPE>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="cache-control" content="max-age=0" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta charset="ISO-8859-1">
		<title>PayPal - Configuration Reader</title>
	    <script type="text/javascript">
	    function sendPaypalValues()
	    {
	    	window.parent.sendPaypalConfigs('<%=XIConfig.PP_ENVIRONMENT.get()%>','<%=XIConfig.PP_CLIENTID.get()%>','<%=XIConfig.PP_MERCHANT_EMAIL.get()%>');
	    }
	    </script>
	</head>
	<body onload="sendPaypalValues();">
		<p>Setting PayPal Configuration</p>
	</body>
</html>
