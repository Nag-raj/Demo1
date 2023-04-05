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

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionSignatureData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.paymetric.constants.PaymetricConstants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymetric.xslt.CTransformer;
import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CTimer;
import com.primesys.xipayws.CXiPay;
import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;


/**
 * Command for handling card authorizations by performing Pre-Auth/Card Validation against the provided credit card by
 * charging for either 0$ or 1$ depending on credit card type
 */
public class XiPayCardValidationCommand extends AbstractCommand<Map<String, String>, CreateSubscriptionResult>
{
	private static Logger LOG = null;
	private static final String XSLT_REFRESH = "paymetric.xslt.card-validation.load";
	private static CTransformer xsltToXiPay = null;
	private static CTransformer xsltFromXiPay = null;


	/**
	 * public constructor to perform necessary steps during CardValidation command instantiation
	 */
	public XiPayCardValidationCommand()
	{
		super();
		if (LOG == null)
		{
			LOG = LoggerFactory.getLogger(this.getClass());
			CXmlHelper.addAliases("CreateSubscriptionResult", CreateSubscriptionResult.class, LOG);
			CXmlHelper.addAliases("PaymentErrorField", PaymentErrorField.class, LOG);
			CXmlHelper.addAliases("CreditCardPaymentInfoModel", CreditCardPaymentInfoModel.class, LOG);
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
				strXslt = readXslt(PaymetricConstants.getXsltCardValidationToXiPay());
				xsltToXiPay = new CTransformer();
				xsltToXiPay.Configure(strXslt);

				strXslt = readXslt(PaymetricConstants.getXsltCardValidationFromXiPay());
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

	@Override
	protected ITransactionHeader[] translateRequest(final Map<String, String> request)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateRequest");
		final ITransactionHeader[] xiTran = new ITransactionHeader[1];


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the request object to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final String xmlPayload = CXmlHelper.toXml(request, LOG);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the request object to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final String strTran = xsltToXiPay.transform(xmlPayload);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the XML to an ITransactionHeader object
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTran[0] = CXmlHelper.fromXml(strTran, LOG);
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



		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Get the successful AVS and CVV codes along with the CVV Info Item
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final String avsCodes = PaymetricConstants.getInfoItem("PM_AVS_CODES", xiTran[0]);
			final String cvvCodes = PaymetricConstants.getInfoItem("PM_CVV_CODES", xiTran[0]);
			final String cvvInfoItem = PaymetricConstants.getInfoItem("PM_CVV_INFOITEM", xiTran[0]);
			final String baseStore = PaymetricConstants.getInfoItem("HYBRIS_BASESTORE", xiTran[0]).trim() != "" ? PaymetricConstants
					.getInfoItem("HYBRIS_BASESTORE", xiTran[0]) : "cronjob";

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Bypass XiPay if the card type is PayPal or AliPay or eCheck
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			if ("PP".equalsIgnoreCase(xiTran[0].getCardType()) || "AL".equalsIgnoreCase(xiTran[0].getCardType()) || "PC".equalsIgnoreCase(xiTran[0].getCardType())  || "EC".equalsIgnoreCase(xiTran[0].getCardType()))
			{
				xiTranRS = xiTran;
				xiTranRS[0].setStatusCode(100);
				xiTranRS[0].setAuthorizationCode(xiTran[0].getTransactionID());
				xiTranRS[0].setAuthorizationDate(Calendar.getInstance());

				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Default to a successful AVS check
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				final String[] avsCode = avsCodes.split("\\-");
				if (avsCode.length > 0)
				{
					xiTranRS[0].setAVSCode(avsCode[0]); // Default to the first code
				}

				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Default to a successful CVV check
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				final String[] cvvCode = cvvCodes.split("\\-");
				if (cvvCode.length > 0)
				{
					final HashMap<String, String> infoItem = new HashMap<String, String>();
					infoItem.put(cvvInfoItem, cvvCode[0]); // Default to the first code
					PaymetricConstants.addInfoItems(xiTranRS[0], infoItem);
				}
			}
			else
			{
				xiPay.setAVSCodes(avsCodes);
				xiPay.setCVVCodes(cvvCodes);
				xiPay.setCVVInfoItem(cvvInfoItem);
				final boolean isEnabled = PaymetricConstants.getCardValidationEnabled(baseStore);
				if (false == isEnabled)
				{
					xiPay.setSimulate(true);
					xiPay.setSimulateDelay(0);
				}

				final String cvv = (xiTran[0].getCardCVV2() != null ? xiTran[0].getCardCVV2() : "");
				final boolean enforceCVV = (cvv.isEmpty() ? false : PaymetricConstants.getCvvMustPass());
				xiTranRS[0] = xiPay.PreAuthorize(xiTran[0], PaymetricConstants.getAvsMustPass(), enforceCVV);
				if (false == isEnabled)
				{
					xiPay.setSimulate(PaymetricConstants.getSimulate());
					xiPay.setSimulateDelay(PaymetricConstants.getSimulateDelay());
				}
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
	protected CreateSubscriptionResult translateResponse(final ITransactionHeader[] xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateResponse");
		CreateSubscriptionResult result = null;


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
			result.setSubscriptionSignatureData(new SubscriptionSignatureData());
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
