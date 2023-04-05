/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.service.impl;

import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.commands.request.AuthorizationRequest;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.paymetric.constants.PaymetricConstants;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CTimer;


/**
 * Paymetric's implementation of the {@link DefaultPaymentServiceImpl}
 */
public class PaymetricPaymentService extends DefaultPaymentServiceImpl
{
	private static Logger LOG = LoggerFactory.getLogger(PaymetricPaymentService.class);

	public PaymetricPaymentService()
	{
		super();
	}

	@Resource
	private CartService cartService;



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
	protected PaymentTransactionEntryModel authorizeInternal(final PaymentTransactionModel transaction, final BigDecimal amount,
			final Currency currency, final BillingInfo shippingInfo, final CardInfo card, final String subscriptionID,
			final String cv2, final String paymentProvider)
	{
		final CTimer timer = (PaymetricConstants.getProfilingEnabled() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("authorizeInternal");
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.AUTHORIZATION;
		final String newEntryCode = this.getNewPaymentTransactionEntryCode(transaction, paymentTransactionType);


		//Adding custom fields from Hybris example
		/*
		 * final Map<String, String> customFieldsMap = new HashMap<String, String>(); customFieldsMap.put("ponumber",
		 * "PO-1112"); customFieldsMap.put("salesDocNumber", "SALESDOC1");
		 *
		 * final String customfields = CXmlHelper.toXml(customFieldsMap, LOG);
		 *
		 * // send this customfields string instead of cv2 in "this.getCardPaymentService().authorize()" method below.
		 */

		AuthorizationResult result = null;

		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Perform a Full-Authorization against XiPay
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (subscriptionID == null)
			{
				result = this.getCardPaymentService()
						.authorize(new AuthorizationRequest(newEntryCode, card, currency, amount, shippingInfo));
			}
			else
			{
				result = this.getCardPaymentService().authorize(new SubscriptionAuthorizationRequest(newEntryCode, subscriptionID,
						currency, amount, shippingInfo, cv2, paymentProvider));
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Populate the payment transaction entry for this full authorization
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	      final PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.getModelService()
					.create(PaymentTransactionEntryModel.class);
			entry.setAmount(result.getTotalAmount());
			if (result.getCurrency() != null)
			{
				entry.setCurrency(this.getCommonI18NService().getCurrency(result.getCurrency().getCurrencyCode()));
			}
			entry.setType(paymentTransactionType);
			entry.setTime(result.getAuthorizationTime() == null ? new Date() : result.getAuthorizationTime());
			entry.setPaymentTransaction(transaction);
			entry.setRequestId(result.getRequestId());
			entry.setTransactionStatus(result.getTransactionStatus().toString());
			entry.setTransactionStatusDetails(result.getTransactionStatusDetails().toString());
			entry.setCode(newEntryCode);
			if (subscriptionID != null)
			{
				entry.setSubscriptionID(subscriptionID);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Retrieve fraud results data and assign data needed to PaymentTransactionEntryModel
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (result.getMerchantTransactionCode().startsWith("<"))
			{
				final HashMap<String, String> mapFraud = CXmlHelper.fromXml(result.getMerchantTransactionCode(), LOG);
				for (final Entry<String, String> item : mapFraud.entrySet())
				{
					if (PaymetricConstants.getLoggingEnabled())
					{
						PaymetricConstants.forcedLog(LOG, strWhere, item.getKey() + "=" + item.getValue());
					}
					if (item.getKey().equalsIgnoreCase("TR_FRAUD_RESULTSTATUS"))
					{
						entry.setTR_FRAUD_RESULTSTATUS(item.getValue());
					}
					if (item.getKey().equalsIgnoreCase("TR_FRAUD_SCORE"))
					{
						entry.setTR_FRAUD_SCORE(item.getValue());
					}
					if (item.getKey().contains("TR_FRAUD_RULESEVALUATION") && item.getValue().contains("Duplicate Order"))
					{
						entry.setDUPLICATE_ORDER(Boolean.TRUE);
					}
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Adjust the request token data value as this is needed for Capture and Void commands
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final ObjectMapper objMap = new ObjectMapper();
			final HashMap<String, String> sapData = objMap.readValue(result.getRequestToken(), HashMap.class);
			final HashMap<String, String> tokenData = new HashMap<String, String>();
			tokenData.put("AUTRA", sapData.getOrDefault("AUTRA", ""));
			tokenData.put("CCINS", sapData.getOrDefault("CCINS", ""));
			tokenData.put("CCWAE", sapData.getOrDefault("CCWAE", ""));
			tokenData.put("MERCH", sapData.getOrDefault("MERCH", ""));
			final String requesToken = objMap.writeValueAsString(tokenData);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Save authorization response data for SAP
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			entry.setAUNUM(sapData.getOrDefault("AUNUM", ""));
			entry.setAUTRA(sapData.getOrDefault("AUTRA", ""));
			entry.setAUTWR(sapData.getOrDefault("AUTWR", ""));
			entry.setCCINS(sapData.getOrDefault("CCINS", ""));
			entry.setCCWAE(sapData.getOrDefault("CCWAE", ""));
			entry.setAUTH_DATE(sapData.getOrDefault("AUTHDATE", "").replace("-", ""));
			entry.setAUTH_TIME(
					new StringBuilder(sapData.getOrDefault("AUTHTIME", "").replace(":", "")).replace(4, 6, "").toString());
			entry.setMERCH(sapData.getOrDefault("MERCH", ""));
			entry.setRCAVA(sapData.getOrDefault("RCAVA", ""));
			entry.setRCAVR(sapData.getOrDefault("RCAVR", ""));
			entry.setRCAVZ(sapData.getOrDefault("RCAVZ", ""));
			entry.setRCCVV(sapData.getOrDefault("RCCVV", ""));
			entry.setRCRSP(sapData.getOrDefault("RCRSP", ""));
			entry.setREACT(sapData.getOrDefault("REACT", ""));
			entry.setRTEXT(sapData.getOrDefault("RTEXT", ""));
			entry.setXiPayResponseCode(sapData.getOrDefault("XIPAY", ""));
			entry.setTR_ECOMM_IND(sapData.getOrDefault("DSIND", ""));
			entry.setTR_ECOMM_PARESSTATUS(sapData.getOrDefault("DSPSTATUS", ""));
			entry.setTR_ECOMM_VBVCAVV(sapData.getOrDefault("DSVBVCAVV", ""));
			entry.setTR_ECOMM_VBVXID(sapData.getOrDefault("DSVBVXID", ""));
			entry.setTR_ECOMM_XID(sapData.getOrDefault("DSXID", ""));
			entry.setTR_ECOMM_3DSVERSION(sapData.getOrDefault("DSVERSION", ""));
			entry.setRequestToken(requesToken);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Save the payment transaction for this full-authorization
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			transaction.setRequestId(result.getRequestId());
			transaction.setRequestToken(requesToken);
			transaction.setPaymentProvider(result.getPaymentProvider());
			this.getModelService().save(transaction);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Save the payment transaction entry for this full authorization and refresh its payment transaction
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			this.getModelService().save(entry);
			this.getModelService().refresh(transaction);
			return entry;
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

		return null;
	}
}
