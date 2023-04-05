/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.commands;


import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.paymetric.constants.PaymetricConstants;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axis2.AxisFault;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetric.sdk.XIConfig;
import com.paymetric.xslt.CTransformer;
import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CTimer;
import com.primesys.xipayws.CXiPay;
import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;


/**
 * Command for handling card authorization captures. Capturing an authorization means the authorized amount of money is
 * actually transferred from the card holder account to the merchant account. Capture operation requires a previous
 * successful authorization that has not yet expired.
 */
@SuppressWarnings("unused")
public class XiPayCaptureCommand extends AbstractCommand<CaptureRequest, CaptureResult> implements CaptureCommand
{
	private static Logger LOG = null;
	private static final String XSLT_REFRESH = "paymetric.xslt.capture.load";
	private static final String XIPAYRQ = "xiPayCaptureRQ";
	private static final String XIPAYRS = "xiPayCaptureRS";
	private static CTransformer xsltToXiPay = null;
	private static CTransformer xsltFromXiPay = null;
	private SessionService sessionService;
	private CartService cartService;



	/**
	 * public constructor to perform necessary steps during Capture command instantiation
	 */
	public XiPayCaptureCommand()
	{
		if (LOG == null)
		{
			LOG = LoggerFactory.getLogger(this.getClass());
			CXmlHelper.addAliases("CaptureRequest", CaptureRequest.class, LOG);
			CXmlHelper.addAliases("CaptureResult", CaptureResult.class, LOG);
			PaymetricConstants.setXsltRefresh(XSLT_REFRESH, "true");
		}
	}

	@Override
	public Logger getLogger()
	{
		return LOG;
	}

	@Override
	protected void loadXslt()
	{
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Load/Reload and Compile/Recompile XSLT?
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (PaymetricConstants.getXsltRefresh(XSLT_REFRESH))
		{
			final CTimer timer = (isProfiling.get() ? new CTimer() : null);
			final String strWhere = PaymetricConstants.getWhere("loadXslt");
			String strXslt = null;

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Initialize and compile our XSLTs
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			try
			{
				strXslt = readXslt(PaymetricConstants.getXsltCaptureToXiPay());
				xsltToXiPay = new CTransformer();
				xsltToXiPay.Configure(strXslt);

				strXslt = readXslt(PaymetricConstants.getXsltCaptureFromXiPay());
				xsltFromXiPay = new CTransformer();
				xsltFromXiPay.Configure(strXslt);
			}
			catch (final Exception ex)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
			}
			finally
			{
				if (timer != null)
				{
					PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
				}
			}
		}
	}

	/**
	 * method to get the SessionService instance
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * method to set the SessionService instance via spring configuration
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}



	@Override
	protected ITransactionHeader[] translateRequest(final CaptureRequest request)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateRequest");
		final ITransactionHeader[] xiTran = new ITransactionHeader[1];
		final Calendar now = Calendar.getInstance();
		final Session session = (sessionService.hasCurrentSession() ? sessionService.getCurrentSession() : null);
		final CartModel cart = this.getCartService().hasSessionCart() ? this.getCartService().getSessionCart() : null;
		final String baseStore = cart != null ? cart.getSite().getUid() : "cronjob";
		SimpleDateFormat sdFmt = null;
		String xmlPayload = null;
		String batchID = null;


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Get the requestToken JSON string and turn it into a HashMap
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final ObjectMapper objMap = new ObjectMapper();
			final HashMap<String, String> tokenData = objMap.readValue(request.getRequestToken(), HashMap.class);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the request object to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTran[0] = new ITransactionHeader();
			xmlPayload = CXmlHelper.toXml(request, LOG);
			final Document xmlDoc = CXmlHelper.DocumentFromString(xmlPayload, LOG);
			final Element root = xmlDoc.getRootElement();

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Construct the missing XML payload for the request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			batchID = (XIConfig.isConfigured.get() && XIConfig.CC_Types.get().indexOf(tokenData.getOrDefault("CCINS", "")) != -1
					? "adhoc_"
					: "apm_");
			root.addContent(new Element("scheduleEnabled").setText("" + PaymetricConstants.getScheduleEnabled(baseStore)));

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set the XiPay transaction details for the Capture
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getAutoCaptureInterval(baseStore).equalsIgnoreCase("hourly"))
			{
				sdFmt = new SimpleDateFormat("yyyy-MM-dd.HH");
			}
			else
			{
				sdFmt = new SimpleDateFormat("yyyy-MM-dd");
			}
			root.addContent(new Element("currentDateHour").setText(batchID + sdFmt.format(now.getTime())));

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Construct the missing XML payload for the request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (session != null)
			{
				final Map<String, String> nodes = (HashMap<String, String>) session.getAttribute(XIPAYRQ);
				if (nodes != null)
				{
					final Element customFields = element("customFields");
					for (final Entry<String, String> item : nodes.entrySet())
					{
						customFields.addContent(element(item.getKey(), item.getValue()));
					}
					root.addContent(customFields);
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Generate the final XML payload for the request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xmlPayload = CXmlHelper.DocumentToString(xmlDoc, LOG);
			if (isLogging.get())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris REQUEST - FINAL\n" + xmlPayload);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the request object to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final String strTran = xsltToXiPay.transform(xmlPayload);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the XML to an ITransactionHeader object
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTran[0] = CXmlHelper.fromXml(strTran, LOG);
			xiTran[0].setCaptureDate(now);
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return xiTran;
	}


	@Override
	protected ITransactionHeader[] callXiPay(final CXiPay xiPay, final ITransactionHeader[] xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("callXiPay");
		ITransactionHeader[] xiTranRS = new ITransactionHeader[1];
		final CartModel cart = this.getCartService().hasSessionCart() ? this.getCartService().getSessionCart() : null;
		final String baseStore = cart != null ? cart.getSite().getUid() : "cronjob";


		try
		{
			if (PaymetricConstants.getAuthorizationEnabled(baseStore)
					&& (PaymetricConstants.getAutoCaptureEnabled(baseStore) || PaymetricConstants.getCaptureEnabled(baseStore)
							|| "AL".equalsIgnoreCase(xiTran[0].getCardType())))
			{
				xiTranRS[0] = xiPay.Capture(xiTran[0]);
				if (xiTranRS[0].getStatusCode() == 200
						&& (PaymetricConstants.getScheduleEnabled(baseStore) || "AL".equalsIgnoreCase(xiTran[0]
								.getCardType())))
				{
					xiTranRS[0] = xiPay.ScheduleBatch(xiTran[0]);
				}
			}
			else
			{
				xiTranRS = xiTran;
				xiTranRS[0].setStatusCode(200);
			}
		}
		catch (final AxisFault ex)
		{
			xiTranRS[0] = xiTran[0];
			xiTranRS[0].setStatusCode(-1);
			xiTranRS[0].setMessage("XiPay communication failure");
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		catch (final Exception ex)
		{
			xiTranRS[0] = xiTran[0];
			xiTranRS[0].setStatusCode(-2);
			xiTranRS[0].setMessage("General exception:" + ex.getMessage());
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return xiTranRS;
	}


	@Override
	protected CaptureResult translateResponse(final ITransactionHeader[] xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateResponse");
		CaptureResult result = null;


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the XiPay response to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final String xmlPayload = CXmlHelper.toXml(xiTran[0], LOG);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the XiPay response XML to Hybris XML response
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final String strHybris = xsltFromXiPay.transform(xmlPayload);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the Hybris XML response to a CreateSubscriptionResult object
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			result = CXmlHelper.fromXml(strHybris, LOG);
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}
}
