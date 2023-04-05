<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.paymetric.sdk.XIConfig"%>

<!DOCTYPE>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="cache-control" content="max-age=0" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta charset="ISO-8859-1">
		<title>3DS - Configuration Reader</title>
	    <script type="text/javascript">
	    function sendThreeDSValues()
	    {
	    	window.parent.getThreeDSValues('<%=XIConfig.CC_3DS.get()%>');
	    }
	    </script>
	</head>
	<body onload="sendThreeDSValues();">
		<p>Setting 3DS Configuration</p>
	</body>
</html>
