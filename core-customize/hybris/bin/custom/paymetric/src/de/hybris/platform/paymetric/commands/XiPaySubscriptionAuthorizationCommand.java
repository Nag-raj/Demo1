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


import de.hybris.platform.acceleratorservices.payment.dao.impl.DefaultCreditCardPaymentSubscriptionDao;
import de.hybris.platform.commerceservices.customer.dao.impl.DefaultCustomerAccountDao;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.paymetric.constants.PaymetricConstants;
import de.hybris.platform.paymetric.exception.PaymetricValidationException;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
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
import com.primesys.xipayws.XiPay30WSStub.InfoItem;


/**
 * Command for handling card authorizations. Authorized amount of money remains "locked" on card's account until it is
 * captured or authorization is reversed (cancelled) or authorization is expired. REGEX to find the duration of all
 * lines containing "XiPaySubscriptionAuthorizationCommand", "PEM" and "Duration":
 *
 * ^.*?\bXiPaySubscriptionAuthorizationCommand\b.*?\bPEM\b.*?\bDuration\b.*?$
 *
 */
@SuppressWarnings("unused")
public class XiPaySubscriptionAuthorizationCommand extends AbstractCommand<SubscriptionAuthorizationRequest, AuthorizationResult>
		implements SubscriptionAuthorizationCommand
{
	private DefaultCreditCardPaymentSubscriptionDao customerSubscriptionModel;
	private DefaultCustomerAccountDao customerAccountDao;
	private CartService cartService;
	private SessionService sessionService;
	private FlexibleSearchService flexibleSearchService;
	private BaseStoreService baseStoreService;
	private static Logger LOG = null;
	private static final String XSLT_REFRESH = "paymetric.xslt.full-authorization.load";
	private static final String XIPAYRQ = "xiPayAuthorizationRQ";
	private static final String XIPAYRS = "xiPayAuthorizationRS";
	private static CTransformer xsltToXiPay = null;
	private static CTransformer xsltFromXiPay = null;
	private static final List<Integer> ACCEPTED_ORDER_STATUS_CODE = Arrays.asList(100, 200, 300, 400, 500, 600, 101, 110, 210,
			310);


	/**
	 * @return the customerAccountDao
	 */
	public DefaultCustomerAccountDao getCustomerAccountDao()
	{
		return customerAccountDao;
	}

	/**
	 * @param customerAccountDao
	 *           the customerAccountDao to set
	 */
	public void setCustomerAccountDao(final DefaultCustomerAccountDao customerAccountDao)
	{
		this.customerAccountDao = customerAccountDao;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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



	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * public Constructor to perform tasks necessary while instantiation
	 */
	public XiPaySubscriptionAuthorizationCommand()
	{
		super();
		if (LOG == null)
		{
			LOG = LoggerFactory.getLogger(this.getClass());
			CXmlHelper.addAliases("SubscriptionAuthorizationRequest", SubscriptionAuthorizationRequest.class, LOG);
			CXmlHelper.addAliases("AuthorizationResult", AuthorizationResult.class, LOG);
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
				strXslt = readXslt(PaymetricConstants.getXsltFullAuthorizationToXiPay());
				xsltToXiPay = new CTransformer();
				xsltToXiPay.Configure(strXslt);

				strXslt = readXslt(PaymetricConstants.getXsltFullAuthorizationFromXiPay());
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
	 * Get CustomerSubscriptionModel
	 */
	public DefaultCreditCardPaymentSubscriptionDao getCustomerSubscriptionModel()
	{
		return customerSubscriptionModel;
	}

	/**
	 * Set CustomerSubscriptionModel
	 */
	@Required
	public void setCustomerSubscriptionModel(final DefaultCreditCardPaymentSubscriptionDao customerSubscriptionModel)
	{
		this.customerSubscriptionModel = customerSubscriptionModel;
	}




	@Override
	protected ITransactionHeader[] translateRequest(final SubscriptionAuthorizationRequest request)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateRequest");
		final ITransactionHeader[] xiTran = new ITransactionHeader[1];
		final Calendar now = Calendar.getInstance();
		final SimpleDateFormat sdFmt = new SimpleDateFormat("HH:mm:ss.SSS");
		Session session = null;
		AbstractOrderModel cartOrder = null;
		CreditCardPaymentInfoModel paymentInfo = null;
		AddressModel address = null;
		String xmlPayload = null;
		String customerNo = "";
		String deviceFP = "";
		String issueNumber = null;
		String ipAddress = null;
		String fromBackoffice = null;
		String totalTax = "0.00";
		boolean cronJobRun = false;
		int iPos = -1;
		String baseStore="";



		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine if we are running from a CRON job (Auto-Ship/Replenishment)
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (this.getCartService().hasSessionCart())
			{
				cronJobRun = false;
				cartOrder = cartService.getSessionCart();
				paymentInfo = (CreditCardPaymentInfoModel) cartOrder.getPaymentInfo();
				totalTax = cartOrder.getTotalTax().toString();
				session = (sessionService.hasCurrentSession() ? sessionService.getCurrentSession() : null);
				baseStore = cartOrder != null ? cartOrder.getSite().getUid() : "cronjob";
			}
			else
			{
				cronJobRun = true;

				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Use the different subscription id (if provided) from SubscriptionAuthorizationRequest to get the payment info.
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				paymentInfo = getCustomerSubscriptionModel().findCreditCartPaymentBySubscription(request.getSubscriptionID());

				if (paymentInfo != null)
				{
					paymentInfo.setTR_TRANS_MSGTYPE("MUSE");
				}

				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// If Order Code is provided as a part of CustomFields, Retrieve that order instance from Hybris DB. Payment Info associated
				// with that order will be used for authorization.
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				if (request.getCv2() != null && request.getCv2().startsWith("<"))
				{

					final Map<String, String> nodes = CXmlHelper.fromXml(request.getCv2(), LOG);
					if (nodes != null && nodes.containsKey("orderCode") && nodes.containsKey("baseStore"))
					{
						baseStore=nodes.get("baseStore");
						cartOrder = getOrderFromCodeAndStore(nodes.get("orderCode"), baseStore);


						if (cartOrder != null)
						{
							paymentInfo = (CreditCardPaymentInfoModel) cartOrder.getPaymentInfo();

							if (paymentInfo != null)
							{
								paymentInfo.setTR_TRANS_MSGTYPE("MUSE");
							}

							totalTax = cartOrder.getTotalTax().toString();
						}

					}
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Validate Credit Card Details from the Request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (paymentInfo == null)
			{
				throw new PaymetricValidationException("PaymentInfo CANNOT BE NULL");
			}
			if (request.getCurrency() == null || request.getCurrency().getCurrencyCode().isEmpty())
			{
				throw new PaymetricValidationException("UNABLE to determine Currency Code");
			}
			if (paymentInfo.getType() == null || paymentInfo.getType().getCode().isEmpty())
			{
				throw new PaymetricValidationException("Credit Card Type CANNOT BE NULL");
			}
			if (PaymetricConstants.getAvsMustPass())
			{
				if ((address = paymentInfo.getBillingAddress()) == null)
				{
					throw new PaymetricValidationException("Credit Card Billing Address CANNOT BE NULL");
				}
				if (address.getCountry() == null)
				{
					throw new PaymetricValidationException("Credit Card Billing Address Region Country CANNOT BE NULL");
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set Customer Number
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			customerNo = paymentInfo.getUser().getUid();
			if ((iPos = customerNo.indexOf('|')) != -1)
			{
				customerNo = customerNo.substring(iPos);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Retrieve custom attributes from current Session
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (!cronJobRun && session != null)
			{
				issueNumber = (String) session.getAttribute("issueNumber"); // SET in DefaultPaymetricService.java
				session.removeAttribute("issueNumber");

				deviceFP = (String) session.getAttribute("deviceFingerprint"); // SET in SummaryCheckoutStepController.java
				session.removeAttribute("deviceFingerprint");

				ipAddress = (String) session.getAttribute("ipAddress"); // SET in SummaryCheckoutStepController.java
				session.removeAttribute("ipAddress");
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set Issue Number
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (paymentInfo.getIssueNumber() == null)
			{
				paymentInfo.setIssueNumber((issueNumber == null ? null : Integer.valueOf(issueNumber)));
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set Device Fingerprint
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (deviceFP == null)
			{
				deviceFP = UUID.randomUUID().toString();
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set Customer IP Address
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (request.getShippingInfo() != null)
			{
				request.getShippingInfo().setIpAddress((ipAddress == null ? "127.0.0.1" : ipAddress));
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the request object to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xmlPayload = CXmlHelper.toXml(request, LOG);
			final Document xmlDoc = CXmlHelper.DocumentFromString(xmlPayload, LOG);
			final Element root = xmlDoc.getRootElement();

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Construct the missing XML payload for the request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			root.addContent(element("totalTax", totalTax));
			root.addContent(element("avsMustPass", "" + PaymetricConstants.getAvsMustPass()));
			root.addContent(element("avsMustPass", "" + PaymetricConstants.getAvsMustPass()));
			root.addContent(element("cvvMustPass", "" + PaymetricConstants.getCvvMustPass()));
			root.addContent(element("cronJob", "" + cronJobRun));
			root.addContent(element("fraudEnabled", "" + PaymetricConstants.getFraudEnabled(baseStore)));
			root.addContent(element("fraudResult", "" + PaymetricConstants.getFraudResult()));
			root.addContent(element("customerNumber", customerNo));
			root.addContent(element("deviceFingerprint", deviceFP));
			root.addContent(element("baseStore", "" + baseStore));

			if (request.getCv2() != null && request.getCv2().startsWith("<"))
			{
				final Map<String, String> nodes = CXmlHelper.fromXml(request.getCv2(), LOG);
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
			// Add Payment Information
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			root.addContent(getPaymentInfo(paymentInfo));
			paymentInfo.setIssueNumber(null);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// If the shopping Cart or Order exists, add Shipping, Coupons, D
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (cartOrder != null)
			{
				root.addContent(element("merchantRefenceNumber", cartOrder.getCode()));
				getShippingInfo(cartOrder, root.getChild("shippingInfo"));
				root.addContent(getCouponCodesInfo(cartOrder));
				root.addContent(getDiscountsInfo(cartOrder));
				root.addContent(getPromotionsInfo(cartOrder));

				if(PaymetricConstants.getAuthorizationLineItemEnabled(baseStore))
				{
					root.addContent(getLineItemsInfo(cartOrder));
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
			// Load/Reload and Compile/Recompile XSLT?
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (xsltToXiPay == null || xsltFromXiPay == null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XSLTs ARE NOT CONFIGURED");
				PaymetricConstants.setXsltRefresh(XSLT_REFRESH, "true");
				loadXslt();
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the request object to XML
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			final String strTran = xsltToXiPay.transform(xmlPayload);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Convert the XML to an ITransactionHeader object
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTran[0] = CXmlHelper.fromXml(strTran, LOG);
			xiTran[0].setAuthorizationDate(now);
			xiTran[0].setAuthorizationTime(sdFmt.format(now.getTime()));
			xiTran[0].setSettlementAmount(xiTran[0].getAmount());
			xiTran[0].setSettlementDate(now);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine if the authorization request is originating from the Hybris backoffice
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			fromBackoffice = request.getMerchantTransactionCode();
			fromBackoffice = (null != fromBackoffice && fromBackoffice.toLowerCase().startsWith("-backoffice-") ? "backoffice" : "");
			xiTran[0].setAuthorizedThroughCartridge(fromBackoffice);
		}
		catch (final PaymetricValidationException ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Paymetric Validation Exception", ex);
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


	/**
	 * public method to get PaymentInfo object
	 */
	public Element getPaymentInfo(final CreditCardPaymentInfoModel paymentInfo)
	{
		final Element payInfo = element("paymentInfo");

		payInfo.addContent(getCreditCardInfo(paymentInfo));
		payInfo.addContent(getBillingInfo(paymentInfo.getBillingAddress()));

		return payInfo;
	}

	/**
	 * public method to get CreditCardInfo object
	 */
	public Element getCreditCardInfo(final CreditCardPaymentInfoModel paymentInfo)
	{
		final Element cardInfo = element("creditCard");
		final XiPayCreditCard type = XiPayCreditCard.fromXiPay(paymentInfo.getType().name());
		Session session = null;
		String strTmp = null;

		cardInfo.addContent(element("cardType", type.toXiPay()));
		cardInfo.addContent(element("cardNumber", paymentInfo.getNumber()));

		strTmp = (paymentInfo.getCcOwner() != null ? paymentInfo.getCcOwner().trim() : "");
		cardInfo.addContent(element("cardOwner", strTmp));

		strTmp = String.format("%02d", Integer.valueOf(paymentInfo.getValidToMonth()));
		cardInfo.addContent(element("cardExpirationMonth", strTmp));

		strTmp = String.format("%04d", Integer.valueOf(paymentInfo.getValidToYear()));
		cardInfo.addContent(element("cardExpirationYear", strTmp));


		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Retrieve CIT/MIT data
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		strTmp = (paymentInfo.getTR_TRANS_MSGTYPE() == null ? "" : paymentInfo.getTR_TRANS_MSGTYPE());

		cardInfo.addContent(element("TR_TRANS_MSGTYPE", strTmp));

		strTmp = (paymentInfo.getIN_TRANS_VISATRANSID() == null ? "" : paymentInfo.getIN_TRANS_VISATRANSID());
		cardInfo.addContent(element("IN_TRANS_VISATRANSID", paymentInfo.getIN_TRANS_VISATRANSID()));

		strTmp = (paymentInfo.getIN_TRANS_VISATRANSID2() == null ? "" : paymentInfo.getIN_TRANS_VISATRANSID2());
		cardInfo.addContent(element("IN_TRANS_VISATRANSID2", paymentInfo.getIN_TRANS_VISATRANSID2()));

		strTmp = (paymentInfo.getIN_TRANS_BANKNETDATE() == null ? "" : paymentInfo.getIN_TRANS_BANKNETDATE());
		cardInfo.addContent(element("IN_TRANS_BANKNETDATE", paymentInfo.getIN_TRANS_BANKNETDATE()));


		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Retrieve EC data
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		strTmp = (paymentInfo.getEC_ROUTING_NUMBER() == null ? "" : paymentInfo.getEC_ROUTING_NUMBER());

		cardInfo.addContent(element("EC_ROUTING_NUMBER", strTmp));
		
		strTmp = (paymentInfo.getEC_ACCOUNT_TYPE() == null ? "" : paymentInfo.getEC_ACCOUNT_TYPE());

		cardInfo.addContent(element("EC_ACCOUNT_TYPE", strTmp));
		
		strTmp = (paymentInfo.getEC_COMPANY_NAME() == null ? "" : paymentInfo.getEC_COMPANY_NAME());

		cardInfo.addContent(element("EC_COMPANY_NAME", strTmp));
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Set any 3DS data needed for the full-authorization
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (this.getCartService().hasSessionCart())
		{
			session = (sessionService.hasCurrentSession() ? sessionService.getCurrentSession() : null);
		}
		if (session != null)
		{
         ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         // All these values are set in DefaultPaymetricService.java
         ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         cardInfo.addContent(element("TR_ECOMM_VBVCAVV", session.getAttribute("card_cavv")));
         cardInfo.addContent(element("TR_ECOMM_VBVXID", session.getAttribute("card_xid")));
         cardInfo.addContent(element("TR_ECOMM_XID", session.getAttribute("card_dstranid")));
         cardInfo.addContent(element("TR_ECOMM_PARESSTATUS", session.getAttribute("card_parEsStatus")));
         cardInfo.addContent(element("TR_ECOMM_IND", session.getAttribute("card_eciFlag")));
         cardInfo.addContent(element("TR_ECOMM_3DSVERSION", session.getAttribute("card_ThreeDSVersion")));

         ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         // Remove them from the current session
         ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         session.removeAttribute("card_cavv");
         session.removeAttribute("card_dstranid");
         session.removeAttribute("card_parEsStatus");
         session.removeAttribute("card_eciFlag");
         session.removeAttribute("card_ThreeDSVersion");
         session.removeAttribute("card_xid");
		}

		if (paymentInfo.getIssueNumber() != null)
		{
			strTmp = String.format(
					(type == XiPayCreditCard.AmericanExpress || type == XiPayCreditCard.PrivateLabelCard ? "%04d" : "%03d"),
					paymentInfo.getIssueNumber());
			cardInfo.addContent(element("cardCVV", strTmp));
		}

		return cardInfo;
	}

	/**
	 * public method to get BillingInfo object
	 */
	public Element getBillingInfo(final AddressModel address)
	{
		final Element billInfo = element("billingInfo");
		final RegionModel region = address.getRegion();
		final CountryModel country = address.getCountry();

		billInfo.addContent(element("cardHolderName1", address.getFirstname()));
		billInfo.addContent(element("cardHolderName2", address.getLastname()));
		billInfo.addContent(element("cardHolderName", address.getFirstname() + " " + address.getLastname()));
		billInfo.addContent(element("cardHolderAddress1", address.getStreetname()));
		billInfo.addContent(element("cardHolderAddress2", address.getStreetnumber()));
		billInfo.addContent(element("cardHolderCity", address.getTown()));
		billInfo.addContent(element("cardHolderCountry", country.getIsocode()));
		billInfo.addContent(element("cardHolderState", (region != null ? region.getIsocodeShort() : "")));
		billInfo.addContent(element("cardHolderZip", address.getPostalcode()));
		billInfo.addContent(element("cardHolderEmail", address.getEmail()));
		billInfo.addContent(element("cardHolderPhoneNumber", address.getPhone1()));

		return billInfo;
	}

	/**
	 * public method to get ShippingInfo object
	 */
	public void getShippingInfo(final AbstractOrderModel cart, final Element shipping)
	{
		final AddressModel address = cart.getDeliveryAddress();
		final RegionModel region = (address != null ? address.getRegion() : null);
		final String strMethod = (cart.getDeliveryMode().getCode() != null ? cart.getDeliveryMode().getCode() : "");

		shipping.addContent(element("shippingMethod", strMethod));
		shipping.addContent(element("shippingCost", cart.getDeliveryCost().toString()));
		if(region != null)
		{
			final Element state = (shipping.getChild("state") != null ? shipping.getChild("state") : null);

			if (state != null)
			{
				state.setText(region.getIsocodeShort() != null ? region.getIsocodeShort() : null);
			}
		}
	}

	/**
	 * public method to get CouponCodesInfo object
	 */
	public Element getCouponCodesInfo(final AbstractOrderModel cart)
	{
		final Element coupons = element("coupons");
		Collection<String> couponCodes = null;

		try
		{
			final Method method = CartModel.class.getMethod("getAppliedCouponCodes");
			couponCodes = (Collection<String>) method.invoke(cart);
		}
		catch (final Exception ex)
		{
			couponCodes = null;
		}

		if (couponCodes != null)
		{
			for (final String coupon : couponCodes)
			{
				coupons.addContent(element("coupon", coupon));
			}
		}

		return coupons;
	}


	/**
	 * public method to get DiscountsInfo instance
	 */
	public Element getDiscountsInfo(final AbstractOrderModel cart)
	{
		final Element discounts = element("discounts");
		final List<DiscountModel> discountModels = cart.getDiscounts();
		final List<DiscountValue> globaDisc = cart.getGlobalDiscountValues();
		Element discount = null;

		if (discountModels != null)
		{
			for (final DiscountModel discountModel : discountModels)
			{
				discount = element("discount");
				discount.addContent(element("code", discountModel.getCode()));
				discount.addContent(element("name", discountModel.getName()));
				discount.addContent(
						element("currency", (discountModel.getCurrency() != null ? discountModel.getCurrency().getIsocode() : "")));
				discount.addContent(element("value", (discountModel.getValue() != null ? discountModel.getValue().toString() : "")));
				discounts.addContent(discount);
			}
		}

		if(globaDisc != null)
		{
			for (final DiscountValue discountModel : globaDisc)
			{
				discount = element("discount");
				discount.addContent(element("code", discountModel.getCode()));
				discount.addContent(element("name", discountModel.getCode()));
				discount.addContent(element("currency", discountModel.getCurrencyIsoCode()));
				discount.addContent(element("value", Double.toString(discountModel.getValue())));
				discounts.addContent(discount);
			}
		}

		return discounts;
	}



	/**
	 * public method to get PromotionsInfo
	 */
	public Element getPromotionsInfo(final AbstractOrderModel cart)
	{
		final Element promotions = element("promotions");
		final Set<PromotionResultModel> promoModels = cart.getAllPromotionResults();
		Collection<PromotionOrderEntryConsumedModel> promoEntries = null;
		Method method = null;
		Element promotion = null;
		final DecimalFormat fmt = new DecimalFormat("#####0.00");
		Double amount = 0.0D;
		String msgFired = "N/A";


		try
		{
			method = PromotionResultModel.class.getMethod("getMessageFired");
		}
		catch (final Exception ex)
		{
			method = null;
		}

		if (promoModels != null)
		{
			for (final PromotionResultModel promoModel : promoModels)
			{
				if (promoModel != null)
				{
					if ((promoEntries = promoModel.getConsumedEntries()) != null)
					{
						for (final PromotionOrderEntryConsumedModel promoEntry : promoEntries)
						{
							amount = promoEntry.getOrderEntry().getBasePrice() - promoEntry.getOrderEntry().getTotalPrice();
							promotion = element("promotion");
							promotion.addContent(element("code", promoModel.getPromotion().getCode()));
							if (method != null)
							{
								try
								{
									msgFired = (String) method.invoke(promoModel);
								}
								catch (final Exception ex)
								{
									msgFired = "N/A";
								}
							}

							promotion.addContent(element("description", msgFired));
							promotion.addContent(element("amount", fmt.format(amount)));
							promotions.addContent(promotion);
						}
					}
				}
			}
		}

		return promotions;
	}



	/**
	 * public method to get LineItemsInfo
	 */
	public Element getLineItemsInfo(final AbstractOrderModel cart)
	{
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		final Element lineItems = element("lineItems");
		Element lineItem = null;
		Double taxApp = 0.0D;
		ProductModel prod = null;


		for (final AbstractOrderEntryModel entry : entries)
		{
			taxApp = TaxValue.sumAppliedTaxValues(entry.getTaxValues());
			prod = entry.getProduct();

			lineItem = element("lineItem");
			lineItem.addContent(element("UPC", prod.getCode()));
			lineItem.addContent(element("materialNumber", prod.getCode()));
			lineItem.addContent(element("description", prod.getName()));
			lineItem.addContent(element("salesDocItemNumber", Integer.toString(entry.getEntryNumber() + 1)));
			lineItem.addContent(element("actualInvoicedQuantity", entry.getQuantity().toString()));
			lineItem.addContent(element("salesUnit", entry.getUnit().getCode()));
			lineItem.addContent(element("taxAmount", taxApp.toString()));
			lineItem.addContent(element("netValue", entry.getTotalPrice().toString()));
			lineItems.addContent(lineItem);
		}

		return lineItems;
	}


	@Override
	protected ITransactionHeader[] callXiPay(final CXiPay xiPay, final ITransactionHeader[] xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("callXiPay");
		final ITransactionHeader[] xiTranRS = new ITransactionHeader[1];
		final boolean fromBackoffice = xiTran[0].getAuthorizedThroughCartridge().startsWith("backoffice");
		final CartModel cart = this.getCartService().hasSessionCart() ? this.getCartService().getSessionCart() : null;
		final String baseStore = cart != null ? cart.getSite().getUid() : "cronjob";
		boolean isEnabled = false;


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

			isEnabled = (PaymetricConstants.getAuthorizationEnabled(baseStore) || PaymetricConstants.getSalesOpEnabled(baseStore));

			if (false == isEnabled && false == fromBackoffice)
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
			if (false == isEnabled && false == fromBackoffice)
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
			xiTranRS[0].setMerchantTXN(xiTranRS[0].getMessage());
			xiTranRS[0].setStatusTXN(xiTranRS[0].getMessage());
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		catch (final Exception ex)
		{
			xiTranRS[0] = xiTran[0];
			xiTranRS[0].setStatusCode(-2);
			xiTranRS[0].setMessage("General exception:" + ex.getMessage());
			xiTranRS[0].setMerchantTXN(xiTranRS[0].getMessage());
			xiTranRS[0].setStatusTXN(xiTranRS[0].getMessage());
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
	protected AuthorizationResult translateResponse(final ITransactionHeader[] xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("translateResponse");
		AuthorizationResult result = null;
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
			// Convert the Hybris XML response to a CreateSubscriptionResult object
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			result = CXmlHelper.fromXml(strHybris, LOG);
			if (xiTran[0].getStatusTXN() != null && xiTran[0].getStatusTXN().equalsIgnoreCase("Fraud Call Failed"))
			{
				//result.setTransactionStatus(TransactionStatus.REJECTED);
				result.setTransactionStatusDetails(TransactionStatusDetails.COMMUNICATION_PROBLEM);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine if Fraud was engaged on the XiPay platform and then get the fraud data
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getFraudEnabled(baseStore))
			{
				final String fraudData = fraudResults(xiTran[0]);
				result.setMerchantTransactionCode(fraudData);
			}

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Retrieve fraud results data
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            boolean isDuplicate = false;
			if (result.getMerchantTransactionCode().startsWith("<"))
			{
                final HashMap<String, String> mapFraud = CXmlHelper.fromXml(result.getMerchantTransactionCode(), LOG);
				for (final Entry<String, String> item : mapFraud.entrySet())
				{
					if (item.getKey().contains("TR_FRAUD_RULESEVALUATION") && item.getValue().contains("Duplicate Order"))
					{
                        isDuplicate = true;
                    }
                }
            }

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// If auto-capture is enabled AND the Full-Authorization was successful, capture the authorization (stage/queue for settlement)
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			ITransactionHeader xiTranRS = null;
			if (PaymetricConstants.getAutoCaptureEnabled(baseStore))
			{
				if (result.getTransactionStatus() == TransactionStatus.ACCEPTED)
				{
					if (result.getTransactionStatusDetails() == TransactionStatusDetails.SUCCESFULL)
					{
						if (!isDuplicate)
						{
							xiTranRS = autoCapture(xiTran[0]);
							////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
							// If the Auth is successfull and capture fails so we update the transaction status to ERROR to identify that this particular transaction failed at CAPTURE
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

							if ((null == xiTranRS || !(ACCEPTED_ORDER_STATUS_CODE.contains(xiTranRS.getStatusCode()))))
							{
								result.setTransactionStatus(TransactionStatus.ERROR);
							}
						}
					}
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// If fraud-auto-void is enabled AND Fraud rejected AND the Full-Authorization was successful, VOID the authorization
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getFraudAutoVoid(baseStore))
			{
				if (result.getTransactionStatus() == TransactionStatus.REJECTED)
				{
					if (result.getTransactionStatusDetails() == TransactionStatusDetails.PROCESSOR_DECLINE)
					{
						if (xiTran[0].getStatusCode() == 100)
						{
							autoVoid(xiTran[0]);
						}
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

	private String fraudResults(final ITransactionHeader xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("fraudResults");
		final HashMap<String, String> mapFraud = new HashMap<String, String>();
		String strName = "";
		String strValue = "";

		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Find ALL TR_FRAUD_* Info-Items
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			for (final InfoItem infoItem : xiTran.getInfoItems().getInfoItem())
			{
				if (infoItem != null)
				{
					strName = (infoItem.getKey() != null ? infoItem.getKey() : "");
					if (strName.startsWith("TR_FRAUD_"))
					{
						strValue = (infoItem.getValue() != null ? infoItem.getValue() : "");
						mapFraud.put(strName, strValue);
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
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// END
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return CXmlHelper.toXml(mapFraud, LOG);
	}

	private ITransactionHeader autoCapture(final ITransactionHeader xiTran)
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
			else if(XIConfig.isConfigured.get() && XIConfig.CC_Types.get().indexOf(xiTran.getCardType()) != -1)
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
		return xiTranRS;
	}

	private void autoVoid(final ITransactionHeader xiTran)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("autoVoid");
		final boolean simulation = xiTran.getAuthorizationCode().equalsIgnoreCase("XIOK100");
		final ITransactionHeader xiTranRQ = new ITransactionHeader();
		ITransactionHeader xiTranRS = null;
		CXiPay xiPay = null;

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
			// Set the XiPay transaction details for the Void
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTranRQ.setTransactionID(xiTran.getTransactionID());

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Capture the transaction with XiPay and reset Simulation mode
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (isLogging.get())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay REQUEST\n" + CXmlHelper.toXml(xiTranRQ, LOG));
			}
			xiTranRS = xiPay.Void(xiTran);
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

	private OrderModel getOrderFromCodeAndStore(final String code, final String store)
	{
		final String ORDER_NOT_FOUND_FOR_CODE_AND_BASE_STORE = "Order with guid %s not found for current user in current BaseStore";
		OrderModel result = null;

		if (StringUtils.isBlank(code) || StringUtils.isBlank(store))
		{
			throw new PaymetricValidationException("Order Code Or Store name cannot be Empty.");
		}

		final BaseStoreModel baseStoreModel = getBaseStoreService().getBaseStoreForUid(store);

		try
		{

			result = this.getCustomerAccountDao().findOrderByCodeAndStore(code, baseStoreModel);

		}
		catch (final ModelNotFoundException e)
		{
			throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_CODE_AND_BASE_STORE, code));
		}

		return result;

	}
}
