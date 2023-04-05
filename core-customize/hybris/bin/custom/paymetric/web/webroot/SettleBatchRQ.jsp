<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Settle Batch Command - REQUEST</title>
    <script type="text/javascript">

        function addDetails()
        {
            var xiTranIDs = document.getElementById("xiTranIDs");
            var xiTranID = document.getElementById("xiTranID");
            var xiMID = document.getElementById("xiMID");
            var xiCardType = document.getElementById("xiCardType");
            var xiCurrency = document.getElementById("xiCurrency");
            var xiSettleAmount = document.getElementById("xiSettleAmount");
            var newOption = document.createElement("option");
            var strDetails = "";

            strDetails = xiTranID.value;
            strDetails += ", " + xiMID.value;
            strDetails += ", " + xiCardType.value;
            strDetails += ", " + xiCurrency.value;
            strDetails += ", " + xiSettleAmount.value;
            newOption.text = strDetails;
            xiTranIDs.add(newOption);

            xiTranID.value = "";
            //xiMID.value = "";
            xiCardType.value = "";
            //xiCurrency.value = "";
            xiSettleAmount.value = "";
        }

        function getDetails()
        {
            var xiTranIDs = document.getElementById("xiTranIDs");
            var strDetails = "";

            for (var i = 0; i < xiTranIDs.length; i++)
            {
                if (i > 0)
                    strDetails += ";" + xiTranIDs.options[i].value;
                else strDetails = xiTranIDs.options[i].value;
            }
            document.getElementById("settleBatch").value = strDetails;
        }

    </script>
</head >
<body>
    <H1>Settle Batch Command</H1>
    <hr />
    <br />
    <table style="width:600px" border="0">
        <thead>
            <tr>
                <td style="background-color:cornflowerblue"><b>Transactions to Settle</b></td>
                <td>&nbsp;</td>
                <td style="background-color:cornflowerblue"><b>Transaction Details</b></td>
            </tr>
        </thead>
        <tr>
            <td>
                <select id="xiTranIDs" size="10" style="width:100%">
                </select>
            </td>
            <td>&nbsp;</td>
            <td style="width:50%">
                <table>
                    <tr>
                        <td style="text-align:right">Merchant ID:&nbsp;</td>
                        <td><input id="xiMID" type="text" /></td>
                    </tr>
                    <tr>
                        <td style="text-align:right">Transaction ID:&nbsp;</td>
                        <td><input id="xiTranID" type="text" /></td>
                    </tr>
                    <tr>
                        <td style="text-align:right">Card Type:&nbsp;</td>
                        <td><input id="xiCardType" type="text" /></td>
                    </tr>
                    <tr>
                        <td style="text-align:right">Currency:&nbsp;</td>
                        <td><input id="xiCurrency" type="text" /></td>
                    </tr>
                    <tr>
                        <td style="text-align:right">Settlement Amount:&nbsp;</td>
                        <td><input id="xiSettleAmount" type="text" /></td>
                    </tr>
                    <tr>
                        <td colspan="2">&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="2" style="text-align:center">
                            <input id="cmdAdd" type="button" value="Add" onclick="addDetails()" />
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="3" style="text-align:center">
                <form action="SettleBatchRS.jsp" method="post">
                    <input id="settleBatch" name="settleBatch" type="hidden" />
                    <input id="cmdAdd" type="submit" value="Settle Batch" onclick="getDetails();" />
                </form>
            </td>
        </tr>
    </table>
</body>
</html >
