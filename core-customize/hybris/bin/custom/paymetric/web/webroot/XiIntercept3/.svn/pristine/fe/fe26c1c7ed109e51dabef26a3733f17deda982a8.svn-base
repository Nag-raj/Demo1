<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="com.paymetric.sdk.AccessTokenUtility"%>
<%@page import="com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket"%>
<%@page import="com.paymetric.sdk.models.APMPacket.APMPacket"%>
<%@page import="com.paymetric.sdk.XIConfig"%>
<%@page import="java.io.DataOutputStream"%>
<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Forward-Declare global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
String xmlPacket = null;
String strError = null;
XIConfig xiConfig = null;
APMPacket packetRQ = null;
AccessTokenResponsePacket packetRS = null;

try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Load the XiIntercept configuration file
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xiConfig = new XIConfig();
	xiConfig.Config(request, "XiIntercept.properties");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Initialize the APM packet for XiIntercept
	// http://localhost:8080/XiIntercept3/XiInterceptAPMHpp.jsp?store=&apm=KONBINI-SSL&orderCode=1501348814&orderAmount=11500.00&hpp=true&description=PCORDER-0050829528&shopperEmailAddress=rohitjpuserppd3pc%40test.com&currencyCode=JPY&exponent=0&shopperCountryCode=JP&telephoneNumber=09011122222&lastName=%E6%B5%9C%E7%94%B0
	// https://electronics.local:9002/paymetric/XiIntercept3/XiInterceptAPMHpp.jsp?store=rodanandfields-jp&apm=KONBINI-SSL&orderCode=1500009024&orderAmount=6700.00&hpp=true&description=CONSULTANTENROLLORDER-0050008030&shopperEmailAddress=sgadireddy%40rodanandfields.com&currencyCode=JPY&exponent=0&shopperCountryCode=JP&telephoneNumber=34545645645&lastName=%E6%B5%81
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	packetRQ = xiConfig.getApmPacketHpp(request);
	xmlPacket = AccessTokenUtility.marshal(packetRQ);


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Get an Access Token for this request
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	packetRS = AccessTokenUtility.GetAPMAccessToken(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(), XIConfig.URL.get(), xmlPacket);
	if(packetRS != null)
	{
		xmlPacket = AccessTokenUtility.marshal(packetRS);
	}
	else
	{
		xmlPacket = "<Error/>";
	}
	xmlPacket = "<?xml version=\"1.0\"?>\n" + xmlPacket;
	response.setContentType("application/xml");
	response.setHeader("Content-Disposition", "inline");
	response.getOutputStream().write(xmlPacket.getBytes("UTF-8"));
}
catch(Exception ex)
{
	ex.printStackTrace();
}

%>
