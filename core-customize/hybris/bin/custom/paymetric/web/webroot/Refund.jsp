<%@page import="de.hybris.platform.servicelayer.session.Session"%>
<%@page trimDirectiveWhitespaces="true" %>
<%@page import="de.hybris.platform.acceleratorservices.payment.dao.impl.DefaultCreditCardPaymentSubscriptionDao"%>
<%@page import="de.hybris.platform.servicelayer.session.SessionService"%>
<%@page import="de.hybris.platform.servicelayer.model.ModelService"%>
<%@page import="de.hybris.platform.acceleratorservices.payment.dao.CreditCardPaymentSubscriptionDao"%>
<%@page import="de.hybris.platform.order.CartService"%>
<%@page import="de.hybris.platform.paymetric.constants.PaymetricConstants" %>
<%@page import="de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest" %>
<%@page import="de.hybris.platform.payment.commands.result.AuthorizationResult" %>
<%@page import="de.hybris.platform.paymetric.commands.XiPaySubscriptionAuthorizationCommand" %>
<%@page import="java.io.DataOutputStream"%>
<%@page import="javax.servlet.http.HttpServletRequest"%>
<%@page import="org.jdom.Document"%>
<%@page import="com.paymetric.xslt.CXmlHelper"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Forward-Declare global variables
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
XiPaySubscriptionAuthorizationCommand cmd = null;
SubscriptionAuthorizationRequest req = null;
AuthorizationResult res = null;
DefaultCreditCardPaymentSubscriptionDao customerSubscriptionModel = null;
CartService cartService = null;
ModelService modelService = null;
SessionService sessionService = null;
Session ses = null;
Document xmlDoc = null;
String xmlPacket = null;
Logger LOG = null;


try
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Allow embedded JSON string in the XML response
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "{}");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Get default model services
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	if(PaymetricConstants.paymetricService != null)
	{
		cartService = PaymetricConstants.paymetricService.getCartService2();
		modelService = PaymetricConstants.paymetricService.getModelService2();
		sessionService = PaymetricConstants.paymetricService.getSessionService();
		customerSubscriptionModel = (DefaultCreditCardPaymentSubscriptionDao)PaymetricConstants.paymetricService.getCreditCardPaymentSubscriptionDao();
		if(sessionService.hasCurrentSession())
		{
			ses = sessionService.getCurrentSession();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create the Hybris Authorization Command and Create a logger to use with the XML helper class
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	cmd = new XiPaySubscriptionAuthorizationCommand();
	cmd.setCartService(cartService);
	cmd.setCustomerSubscriptionModel(customerSubscriptionModel);
	cmd.setSessionService(sessionService);
	LOG = cmd.getLogger();

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Extract the XML request
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xmlDoc = CXmlHelper.DocumentFromStream(request.getInputStream(), LOG);
	xmlPacket = CXmlHelper.DocumentToString(xmlDoc, LOG);
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Perform the Hybris Authorization Command and send the response
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	req = CXmlHelper.fromXml(xmlPacket, LOG);
	res = cmd.perform(req);
	PaymetricConstants.forcedLog(LOG, "Authorize - ", "hasCurrentSession()=" + (ses != null ? "true" : "false") + ", ID=" + ses.getSessionId());
	if(sessionService.hasCurrentSession())
	{
		PaymetricConstants.forcedLog(LOG, "Authorize - ", "sessionService.closeCurrentSession()=BEGIN");
		sessionService.closeCurrentSession();
		PaymetricConstants.forcedLog(LOG, "Authorize - ", "sessionService.closeCurrentSession()=END");
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Send the authorization response
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	xmlPacket = CXmlHelper.toXml(res, LOG);
	if(xmlPacket.startsWith("<?xml") == false)
	{
		xmlPacket = "<?xml version=\"1.0\"?>\n" + xmlPacket;
	}
	response.setContentType("application/xml");
	response.setHeader("Content-Disposition", "inline");
	response.getOutputStream().write(xmlPacket.getBytes("UTF-8"));
}
catch(Exception ex)
{
	ex.printStackTrace();
}
%>
