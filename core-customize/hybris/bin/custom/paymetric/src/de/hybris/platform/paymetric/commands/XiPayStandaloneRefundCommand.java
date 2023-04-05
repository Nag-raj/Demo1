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
import de.hybris.platform.payment.commands.StandaloneRefundCommand;
import de.hybris.platform.payment.commands.request.StandaloneRefundRequest;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.paymetric.constants.PaymetricConstants;
import de.hybris.platform.paymetric.exception.PaymetricValidationException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axis2.AxisFault;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.paymetric.sdk.XIConfig;
import com.paymetric.xslt.CTransformer;
import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CTimer;
import com.primesys.xipayws.CXiPay;
import com.primesys.xipayws.IConstants.XiPayCreditCard;
import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;


/**
 * Command for handling stand-alone refunds. Stand-alone refund means to return back money to customer account not
 * associated with any order or previous transactions. Just passes money from one account to another contrary to
 *
 */
@SuppressWarnings("unused")
public class XiPayStandaloneRefundCommand extends AbstractCommand<StandaloneRefundRequest, RefundResult>
		implements StandaloneRefundCommand<StandaloneRefundRequest>
{
	private static Logger LOG = null;
	private static final String XSLT_REFRESH = "paymetric.xslt.standalone-refund.load";
	private static final String XIPAYRQ = "xiPayRefundRQ";
	private static final String XIPAYRS = "xiPayRefundRS";
	private static CTransformer xsltToXiPay = null;
	private static CTransformer xsltFromXiPay = null;
	private CartService cartService;
	private SessionService sessionService;


	/**
	 * public constructor to perform necessary steps during StandaloneRefund command instantiation
	 */
	public XiPayStandaloneRefundCommand()
	{
		super();
		if (LOG == null)
		{
			LOG = LoggerFactory.getLogger(this.getClass());
			CXmlHelper.addAliases("StandaloneRefundRequest", StandaloneRefundRequest.class, LOG);
			CXmlHelper.addAliases("RefundResult", RefundResult.class, LOG);
			PaymetricConstants.setXsltRefresh(XSLT_REFRESH, "true");
		}
	}

	@Override
	public Logger getLogger()
	{
		return LOG;
	}

	@Override
	protected synchronized void loadXslt()
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
				strXslt = readXslt(PaymetricConstants.getXsltStandaloneRefundToXiPay());
				xsltToXiPay = new CTransformer();
				xsltToXiPay.Configure(strXslt);

				strXslt = readXslt(PaymetricConstants.getXsltStandaloneRefundFromXiPay());
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
	 * Get CartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * Set CartService
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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


	@Override
	protected ITransactionHeader[] translateRequest(final StandaloneRefundRequest request)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateRequest");
		final ITransactionHeader[] xiTran = new ITransactionHeader[1];
		BillingInfo address = null;
		CardInfo creditCard = null;
		String xmlPayload = null;
		boolean cronJobRun = false;

		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine if we are running from a CRON job (Auto-Ship/Replenishment)
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			cronJobRun = (cartService.hasSessionCart() ? false : true);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Get Credit Card Details from the Request and Validate its Contents
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (request.getCurrency() == null || request.getCurrency().getCurrencyCode().isEmpty())
			{
				throw new PaymetricValidationException("UNABLE to determine Currency Code");
			}
			if ((creditCard = request.getCard()) == null)
			{
				throw new PaymetricValidationException("Credit Card information CANNOT BE NULL");
			}
			if (creditCard.getCardType() == null || creditCard.getCardType().getCode().isEmpty())
			{
				throw new PaymetricValidationException("Credit Card Type CANNOT BE NULL");
			}
			if (PaymetricConstants.getAvsMustPass())
			{
				if ((address = creditCard.getBillingInfo()) == null)
				{
					throw new PaymetricValidationException("Credit Card Billing Address CANNOT BE NULL");
				}
				if (address.getRegion() == null)
				{
					throw new PaymetricValidationException("Credit Card Billing Address Region CANNOT BE NULL");
				}
				if (address.getCountry() == null || address.getCountry().isEmpty())
				{
					throw new PaymetricValidationException("UNABLE to determine Country Code");
				}
			}


			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the request object to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xmlPayload = CXmlHelper.toXml(request, LOG);
			final Document xmlDoc = CXmlHelper.DocumentFromString(xmlPayload, LOG);
			final Element root = xmlDoc.getRootElement();

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Adjust the Card Type
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final Element cardType = root.getChild("card").getChild("cardType");
			final XiPayCreditCard type = XiPayCreditCard.fromXiPay(cardType.getText());
			cardType.setText(type.toXiPay());

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Construct the missing XML payload for the request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			root.addContent(element("cronJob", "" + cronJobRun));
			if (request.getBillTo().getStreet2() != null && request.getBillTo().getStreet2().startsWith("<"))
			{
				final Map<String, String> nodes = CXmlHelper.fromXml(request.getBillTo().getStreet2(), LOG);
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
            // Construct the FINAL xml to be used during the XSLT transform
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
		}
		catch (final PaymetricValidationException ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "PaymetricValidationException", ex);
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
		final ITransactionHeader[] xiTranRS = new ITransactionHeader[1];
		final CartModel cart = this.getCartService().hasSessionCart() ? this.getCartService().getSessionCart() : null;
		final String baseStore = cart != null ? cart.getSite().getUid() : "cronjob";



		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// This info items MUST BE set during the XSLT transformation
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTran[0].setAuthorizedThroughCartridge(null);
			xiPay.setAVSCodes(PaymetricConstants.getInfoItem("PM_AVS_CODES", xiTran[0]));
			xiPay.setCVVCodes(PaymetricConstants.getInfoItem("PM_CVV_CODES", xiTran[0]));
			xiPay.setCVVInfoItem(PaymetricConstants.getInfoItem("PM_CVV_INFOITEM", xiTran[0]));

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine if Simulation mode is enabled
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (false == PaymetricConstants.getAuthorizationEnabled(baseStore))
			{
				xiPay.setSimulate(true);
				xiPay.setSimulateDelay(0);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine if we are performing standard authorizations or sales operation
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getSalesOpEnabled(baseStore))
			{
				xiTranRS[0] = xiPay.Sale(xiTran[0]);
			}
			else
			{
				xiTranRS[0] = xiPay.Authorize(xiTran[0]);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Reset Simulation mode
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (false == PaymetricConstants.getAuthorizationEnabled(baseStore))
			{
				xiPay.setSimulate(PaymetricConstants.getSimulate());
				xiPay.setSimulateDelay(PaymetricConstants.getSimulateDelay());
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
	protected RefundResult translateResponse(final ITransactionHeader[] xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateResponse");
		RefundResult result = null;
		//final CartModel cart = cartService.getSessionCart();
		final CartModel cart = this.getCartService().hasSessionCart() ? this.getCartService().getSessionCart() : null;
		final String baseStore = cart != null ? cart.getSite().getUid() : "cronjob";


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
			// Convert the Hybris XML response to a RefundResult object
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			result = CXmlHelper.fromXml(strHybris, LOG);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// If auto-capture is enabled AND the Refund was successful, capture the Refund (stage/queue for settlement)
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getAutoCaptureEnabled(baseStore))
			{
				if (result.getTransactionStatus() == TransactionStatus.ACCEPTED)
				{
					if (result.getTransactionStatusDetails() == TransactionStatusDetails.SUCCESFULL)
					{
						autoCapture(xiTran[0]);
					}
				}
			}
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

	private void autoCapture(final ITransactionHeader xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("autoCapture");
		final boolean simulation = xiTran.getAuthorizationCode().equalsIgnoreCase("XIOK100");
		final boolean cronJobRun = (cartService.hasSessionCart() ? false : true);
		final Calendar now = Calendar.getInstance();
		SimpleDateFormat sdFmt = null;
		ITransactionHeader xiTranRS = null;
		CXiPay xiPay = null;
		String batchID = null;
		final CartModel cart = this.getCartService().hasSessionCart() ? this.getCartService().getSessionCart() : null;
		final String baseStore = cart != null ? cart.getSite().getUid() : "cronjob";


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Acquire an XiPay instance and determine if in Simulation mode
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiPay = PaymetricConstants.getXiPayInstance(xiTran.getMerchantID(), xiTran.getCardType(), xiTran.getCurrencyKey());
			if (simulation)
			{
				xiPay.setSimulate(true);
				xiPay.setSimulateDelay(0);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine the proper Batch ID for this transaction
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (cronJobRun)
			{
				batchID = "auto_%s"; // Auto-Ship (Replenishment) orders
			}
			else if (XIConfig.isConfigured.get() && XIConfig.CC_Types.get().indexOf(xiTran.getCardType()) != -1)
			{
				batchID = "adhoc_%s"; // Store-Front orders
			}
			else
			{
				batchID = "apm_%s"; // Alternate-Payment-Method orders
			}

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
			xiTran.setBatchID(String.format(batchID, sdFmt.format(now.getTime())));
			xiTran.setSettlementAmount(xiTran.getAmount());

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Capture the transaction with XiPay and reset Simulation mode
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (isLogging.get())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay REQUEST\n" + CXmlHelper.toXml(xiTran, LOG));
			}
			xiTranRS = xiPay.Capture(xiTran);
			if (simulation)
			{
				xiPay.setSimulate(PaymetricConstants.getSimulate());
				xiPay.setSimulateDelay(PaymetricConstants.getSimulateDelay());
			}
			if (isLogging.get())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay RESPONSE\n" + CXmlHelper.toXml(xiTranRS, LOG));
			}
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Release the XiPay instance
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			PaymetricConstants.setXiPayInstance(xiPay);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// END
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}
	}
}
