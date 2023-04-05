<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Get Transaction Command</title>
    <script type="text/javascript">

        function getTransaction()
        {
            try
            {
                var xiTransactionID = document.getElementById("xiTransactionID");
                var strDetails = "GetTransactionRS.jsp?transaction_id=";
                var xiFrameDoc = getIFrame();

                if(xiTransactionID.value != null && xiTransactionID.value.length > 0)
                {
                	strDetails += encodeURIComponent(xiTransactionID.value);
                	xiFrameDoc.location.href = strDetails;
                }
            }
            catch (err)
            {
            	alert(err.message);
            }
        }

        function getIFrame()
        {
            var xiFrame = document.getElementById("xiFrame");
            var xiFrameDoc = (xiFrame.contentWindow || xiFrame.contentDocument);
            
            if (xiFrameDoc.document)
            	xiFrameDoc = xiFrameDoc.document;

            return xiFrameDoc;
        }

    </script>
</head >
<body>
    <H1>Get Transaction Command</H1>
    <hr />
    <br />
    <table style="width:700px" border="0">
        <tr>
            <td style="text-align:right">Transaction ID:&nbsp;</td>
            <td><input id="xiTransactionID" size="60" type="text" />&nbsp;&nbsp;
                <input id="cmdGetTransaction" type="button" value="Get Transaction" onclick="getTransaction();" />
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2" style="text-align:center">
                <iframe id="xiFrame"  style = "width:100%; height:600px;" src="about:blank"></iframe>
            </td>
        </tr>
    </table>
</body>
</html >
