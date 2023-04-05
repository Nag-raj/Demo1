/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.paymetric.service.impl;

import de.hybris.platform.acceleratorservices.payment.data.AuthReplyData;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.payment.impl.DefaultAcceleratorPaymentService;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.paymetric.commands.XiPayCardValidationCommand;
import de.hybris.platform.paymetric.constants.PaymetricConstants;
import de.hybris.platform.paymetric.service.PaymetricService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CTimer;
import com.primesys.xipayws.CXiPay;


/**
 * An Instance of this class will be injected by spring framework as a default payment service for hybris.
 */
@SuppressWarnings("unused")
public class DefaultPaymetricService extends DefaultAcceleratorPaymentService implements PaymetricService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPaymetricService.class);
	private static final String XIPAYRQ = "xiPayCardValidationRQ";
	private static final String XIPAYRS = "xiPayCardValidationRS";
	private MediaService mediaService;
	private FlexibleSearchService flexibleSearchService;
	private SessionService sessionService;

	/**
	 * public method to get the SessionService instance
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * public method to set the SessionService instance
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * public constructor to perform necessary object instantiation steps
	 */
	public DefaultPaymetricService()
	{
		CXmlHelper.addAliases("PaymentData", PaymentData.class, LOG);
		CXmlHelper.addAliases("PaymentSubscriptionResultItem", PaymentSubscriptionResultItem.class, LOG);
		CXiPay.setLogging(PaymetricConstants.getLoggingEnabled());
		CXiPay.setProfiling(PaymetricConstants.getProfilingEnabled());
		if (PaymetricConstants.getPaymetricService() == null)
		{
			PaymetricConstants.setPaymetricService(this);
		}
	}


	public ModelService getModelService2()
	{
		return getModelService();
	}

	public CartService getCartService2()
	{
		return getCartService();
	}


	/**
	 * public method to get the hybris logo URL
	 */
	@Override
	public String getHybrisLogoUrl(final String logoCode)
	{
		final MediaModel media = mediaService.getMedia(logoCode);

		// Keep in mind that with Slf4j you don't need to check if debug is enabled, it is done under the hood.
		LOG.debug("Found media [code: {}]", media.getCode());

		return media.getURL();
	}

	/**
	 * public method to create catalog unaware logo
	 */
	@Override
	public void createLogo(final String logoCode)
	{
		final Optional<CatalogUnawareMediaModel> existingLogo = findExistingLogo(logoCode);

		final CatalogUnawareMediaModel media = existingLogo.isPresent() ? existingLogo.get()
				: getModelService().create(CatalogUnawareMediaModel.class);
		media.setCode(logoCode);
		media.setRealFileName("sap-hybris-platform.png");
		getModelService().save(media);

		mediaService.setStreamForMedia(media, getImageStream());
	}

	private final static String FIND_LOGO_QUERY = "SELECT {" + CatalogUnawareMediaModel.PK + "} FROM {"
			+ CatalogUnawareMediaModel._TYPECODE + "} WHERE {" + CatalogUnawareMediaModel.CODE + "}=?code";

	private Optional<CatalogUnawareMediaModel> findExistingLogo(final String logoCode)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(FIND_LOGO_QUERY);
		fQuery.addQueryParameter("code", logoCode);

		try
		{
			return Optional.of(flexibleSearchService.searchUnique(fQuery));
		}
		catch (final SystemException e)
		{
			return Optional.empty();
		}
	}

	private InputStream getImageStream()
	{
		return DefaultPaymetricService.class.getResourceAsStream("/paymetric/sap-hybris-platform.png");
	}

	/**
	 * public method to set the mediaService instance
	 */
	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * public method to set flexibleSearchService
	 */
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * public method to begin the creation of payment subscription in HOP
	 */
	@Override
	public PaymentData beginHopCreatePaymentSubscription(final String siteName, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customer, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress)
	{
		final String strWhere = PaymetricConstants.getWhere("beginHopCreatePaymentSubscription");
		final CTimer timer = (PaymetricConstants.getProfilingEnabled() ? new CTimer() : null);
		PaymentData result = null;


		try
		{
			result = super.beginHopCreatePaymentSubscription(siteName, responseUrl, merchantCallbackUrl, customer, cardInfo,
					paymentAddress);
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris RESPONSE\n" + CXmlHelper.toXml(result, LOG));
			}
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}

	/**
	 * public method to complete the creation of payment subscription in HOP
	 */
	@Override
	public PaymentSubscriptionResultItem completeHopCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters)
	{
		final String strWhere = PaymetricConstants.getWhere("completeHopCreatePaymentSubscription");
		final CTimer timer = (PaymetricConstants.getProfilingEnabled() ? new CTimer() : null);
		PaymentSubscriptionResultItem result = null;


		try
		{
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris REQUEST\n" + CXmlHelper.toXml(parameters, LOG));
			}

			result = super.completeHopCreatePaymentSubscription(customerModel, saveInAccount, parameters);
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris RESPONSE\n" + CXmlHelper.toXml(result, LOG));
			}
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}


	/**
	 * public method to begin the creation of payment subscription in SOP
	 */
	@Override
	public PaymentData beginSopCreatePaymentSubscription(final String siteName, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customer, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress)
	{
		final String strWhere = PaymetricConstants.getWhere("beginSopCreatePaymentSubscription");
		final CTimer timer = (PaymetricConstants.getProfilingEnabled() ? new CTimer() : null);
		PaymentData result = null;


		try
		{
			PaymetricConstants.loadXIConfig();
			result = super.beginSopCreatePaymentSubscription(siteName, responseUrl, merchantCallbackUrl, customer, cardInfo,
					paymentAddress);
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris RESPONSE\n" + CXmlHelper.toXml(result, LOG));
			}
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}

	/**
	 * public method to complete the creation of payment subscription in SOP
	 */
	@Override
	public PaymentSubscriptionResultItem completeSopCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters)
	{
		final String strWhere = PaymetricConstants.getWhere("completeSopCreatePaymentSubscription");
		final CTimer timer = (PaymetricConstants.getProfilingEnabled() ? new CTimer() : null);
		final CartModel cart = getCartService().getSessionCart();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		final XiPayCardValidationCommand preAuthorize = new XiPayCardValidationCommand();
		final PaymentSubscriptionResultItem subscriptionResult = new PaymentSubscriptionResultItem();
		CreditCardPaymentInfoModel cardPaymentInfoModel = null;
		CreateSubscriptionResult result = null;
		Session session = null;
		final boolean cronJobRun = false;
		boolean bSuccess = false;
		String strDecision = "";
		String strReasonCode = "";
		String strCIT_MIT = "";
		String cit_mit[] = null;
		String requestId = "";
		final String baseStore = (parameters.get("ybaseStore") != null) && (parameters.get("ybaseStore").trim() != "") ? parameters
				.get("ybaseStore") : "cronjob";
		
		String ecRoutingNumber =  null;
		String ecAccountType   =  null;
		String ecComapnyName   =  null;

		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Perform request data validations
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (parameters.getOrDefault("card_cardType", "").isEmpty())
			{
				addErrorField("card_cardType", "UNABLE to determine Card Type", errors);
				PaymetricConstants.forcedLog(LOG, strWhere, "UNABLE to determine Card Type");
			}
			if (parameters.getOrDefault("billTo_country", "").isEmpty())
			{
				addErrorField("billTo_country", "UNABLE to determine Country Code", errors);
				PaymetricConstants.forcedLog(LOG, strWhere, "UNABLE to determine Country Code");
			}
			if (cart != null && cart.getCurrency() != null && cart.getCurrency().getIsocode() != null)
			{
				parameters.put("currency", cart.getCurrency().getIsocode());
			}
			else
			{
				addErrorField("currency", "UNABLE to determine Currency Code", errors);
				PaymetricConstants.forcedLog(LOG, strWhere, "UNABLE to determine Currency Code");
			}
			if (!errors.isEmpty())
			{
				subscriptionResult.setErrors(errors);
				return subscriptionResult;
			}

				
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set the session service
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if("EC".equalsIgnoreCase(parameters.get("card_cardType").trim())) {
				ecRoutingNumber =  parameters.getOrDefault("ec_routing_number", "").trim();
				ecAccountType 	=  parameters.getOrDefault("ec_account_type", "").trim();
				ecComapnyName 	=  parameters.getOrDefault("ec_company_name", "").trim();
			}
			
			session = (sessionService.hasCurrentSession() ? sessionService.getCurrentSession() : null);
			if (session != null)
			{
				session.setAttribute("issueNumber", parameters.get("card_cvNumber"));

				if(parameters.containsKey("card_cavv"))
				{
					session.setAttribute("card_cavv", parameters.get("card_cavv"));
				}

				if (parameters.containsKey("card_xid"))
				{
					session.setAttribute("card_xid", parameters.get("card_xid"));
				}

				if (parameters.containsKey("card_dstranid"))
				{
					session.setAttribute("card_dstranid", parameters.get("card_dstranid"));
				}

				if (parameters.containsKey("card_parEsStatus"))
				{
					session.setAttribute("card_parEsStatus", parameters.get("card_parEsStatus"));
				}

				if (parameters.containsKey("card_eciFlag"))
				{
					session.setAttribute("card_eciFlag", parameters.get("card_eciFlag"));
				}

				if (parameters.containsKey("card_ThreeDSVersion"))
				{
					session.setAttribute("card_ThreeDSVersion", parameters.get("card_ThreeDSVersion"));
				}

			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set the appropriate CIT/MIT flag value
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			strCIT_MIT = (parameters.containsKey("savePaymentInfo") ? "CSTO" : "CGEN");
			parameters.put("TR_TRANS_MSGTYPE", strCIT_MIT);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Perform the Card Validation with XiPay...If data validation did not produce any errors
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			parameters.put("includeSopData", "" + PaymetricConstants.getIncludeSopRequest());
			parameters.put("PM_AVS_MUST_PASS", "" + PaymetricConstants.getAvsMustPass());
			parameters.put("PM_CVV_MUST_PASS", "" + PaymetricConstants.getCvvMustPass());
			parameters.put("cronJob", "" + cronJobRun);
			result = preAuthorize.perform(parameters);
			if (session != null)
			{
				final HashMap<String, String> cvRS = new HashMap<String, String>();
				final AuthReplyData ard = result.getAuthReplyData();
				final PaymentInfoData pid = result.getPaymentInfoData();
				final CustomerInfoData cid = result.getCustomerInfoData();
				final OrderInfoData oid = result.getOrderInfoData();

				if (ard != null)
				{
					cvRS.put("ccAuthReplyAuthorizationCode", ard.getCcAuthReplyAuthorizationCode());
					cvRS.put("ccAuthReplyAuthorizedDateTime", ard.getCcAuthReplyAuthorizedDateTime());
					cvRS.put("cvnDecision", (ard.getCvnDecision() ? "true" : "false"));
					cvRS.put("ccAuthReplyAvsCode", ard.getCcAuthReplyAvsCode());
					cvRS.put("ccAuthReplyCvCode", ard.getCcAuthReplyCvCode());
					cvRS.put("ccAuthReplyAmount", ard.getCcAuthReplyAmount().toString());
					cvRS.put("ccAuthReplyReasonCode", ard.getCcAuthReplyReasonCode().toString());
					cvRS.put("ccAuthReplyProcessorResponse", ard.getCcAuthReplyProcessorResponse());
					cvRS.put("ccAuthReplyAvsCodeRaw", ard.getCcAuthReplyAvsCodeRaw());
				}

				if (pid != null)
				{
					cvRS.put("cardAccountHolderName", pid.getCardAccountHolderName());
					cvRS.put("cardAccountNumber", pid.getCardAccountNumber());
					cvRS.put("cardCardType", pid.getCardCardType());
					cvRS.put("cardStartMonth", pid.getCardStartMonth());
					cvRS.put("cardStartYear", pid.getCardStartYear());
					cvRS.put("cardExpirationMonth", pid.getCardExpirationMonth().toString());
					cvRS.put("cardExpirationYear", pid.getCardExpirationYear().toString());
				}

				if (cid != null)
				{
					cvRS.put("billToPhoneNumber", cid.getBillToPhoneNumber());
					cvRS.put("billToEmail", cid.getBillToEmail());
					cvRS.put("billToFirstName", cid.getBillToFirstName());
					cvRS.put("billToLastName", cid.getBillToLastName());
					cvRS.put("billToStreet1", cid.getBillToStreet1());
					cvRS.put("billToStreet2", cid.getBillToStreet2());
					cvRS.put("billToCity", cid.getBillToCity());
					cvRS.put("billToState", cid.getBillToState());
					cvRS.put("billToPostalCode", cid.getBillToPostalCode());
					cvRS.put("billToCountry", cid.getBillToCountry());
				}

				if (oid != null)
				{
					cvRS.put("Comments", oid.getComments());
					cvRS.put("OrderNumber", oid.getOrderNumber());
					cvRS.put("OrderPageRequestToken", oid.getOrderPageRequestToken());
					cvRS.put("OrderPageTransactionType", oid.getOrderPageTransactionType());
					cvRS.put("RecurringSubscriptionInfoPublicSignature", oid.getRecurringSubscriptionInfoPublicSignature());
					cvRS.put("SubscriptionTitle", oid.getSubscriptionTitle());
					cvRS.put("TaxAmount", oid.getTaxAmount());
					cvRS.put("OrderPageIgnoreAVS", (BooleanUtils.isTrue(oid.getOrderPageIgnoreAVS()) ? "true" : "false"));
					cvRS.put("OrderPageIgnoreCVN", (BooleanUtils.isTrue(oid.getOrderPageIgnoreCVN()) ? "true" : "false"));
				}

				session.setAttribute(XIPAYRS, cvRS);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Process the XiPay Response
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			strDecision = result.getDecision();
			cit_mit = strDecision.split("\\-");
			strDecision = cit_mit[0];
			requestId = result.getRequestId();
			strReasonCode = result.getReasonCode().toString();
			bSuccess = (result.getReasonCode() == 100);
			subscriptionResult.setSuccess(bSuccess);
			subscriptionResult.setDecision(strDecision);
			subscriptionResult.setResultCode(strReasonCode);

			if (bSuccess)
			{
				Assert.notNull(result.getAuthReplyData(), "AuthReplyData cannot be null");
				Assert.notNull(result.getCustomerInfoData(), "CustomerInfoData cannot be null");
				Assert.notNull(result.getOrderInfoData(), "OrderInfoData cannot be null");
				Assert.notNull(result.getPaymentInfoData(), "PaymentInfoData cannot be null");
				Assert.notNull(result.getSignatureData(), "SignatureData cannot be null");
				Assert.notNull(result.getSubscriptionInfoData(), "SubscriptionInfoData cannot be null");
				Assert.notNull(result.getSubscriptionSignatureData(), "SubscriptionSignatureData cannot be null");

				getPaymentTransactionStrategy().savePaymentTransactionEntry(customerModel, requestId, result.getOrderInfoData());
				cardPaymentInfoModel = getCreditCardPaymentInfoCreateStrategy().saveSubscription(customerModel,
						result.getCustomerInfoData(), result.getSubscriptionInfoData(), result.getPaymentInfoData(), saveInAccount);

				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Set the appropriate CIT/MIT flag value for the new cardPaymentInfoModel and save the network id(s)
				// select * from {CreditCardPaymentInfo} - To check data being saved
				// select * from {PaymentTransactionEntry} - To check data being saved
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if (strCIT_MIT.equalsIgnoreCase("CSTO"))
				{
					strCIT_MIT = "CUSE";
				}
				cardPaymentInfoModel.setTR_TRANS_MSGTYPE(strCIT_MIT);
				if (cit_mit.length > 1)
				{
					cardPaymentInfoModel.setIN_TRANS_VISATRANSID(cit_mit[1]);
				}
				if (cit_mit.length > 2)
				{
					cardPaymentInfoModel.setIN_TRANS_VISATRANSID2(cit_mit[2]);
				}
				if (cit_mit.length > 3)
				{
					cardPaymentInfoModel.setIN_TRANS_BANKNETDATE(cit_mit[3]);
				}

				if("EC".equalsIgnoreCase(parameters.get("card_cardType").trim())){
					
					cardPaymentInfoModel.setEC_ROUTING_NUMBER(ecRoutingNumber);
					cardPaymentInfoModel.setEC_ACCOUNT_TYPE(ecAccountType);
					cardPaymentInfoModel.setEC_COMPANY_NAME(ecComapnyName);
				}
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Save the credit card information which now includes CIT/MIT data
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				cardPaymentInfoModel.setSubscriptionValidated(true);
				subscriptionResult.setStoredCard(cardPaymentInfoModel);
				getModelService().save(cardPaymentInfoModel);
			}
			else
			{
				if (PaymetricConstants.getCardValidationSaveFailures(baseStore))
				{
					getPaymentTransactionStrategy().savePaymentTransactionEntry(customerModel, requestId, result.getOrderInfoData());
				}
				strDecision = "Card Validation (pre-authorization) FAILED: Decision=" + strDecision;
				strDecision += ", ReasonCode=" + strReasonCode;
				strDecision += ", ProcessorResponse=" + result.getAuthReplyData().getCcAuthReplyProcessorResponse();
				throw new Exception(strDecision);
			}
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris RESPONSE\n" + CXmlHelper.toXml(subscriptionResult, LOG));
			}
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return subscriptionResult;
	}

	private void addErrorField(final String strName, final String strMsg, final Map<String, PaymentErrorField> errors)
	{
		final PaymentErrorField errorField = new PaymentErrorField();

		errorField.setName(strName);
		errorField.setMissing(true);
		errors.put(strMsg, errorField);
	}

	private PaymentErrorField addErrorField(final String strData)
	{
		final PaymentErrorField errorField = new PaymentErrorField();

		errorField.setName(strData);
		errorField.setInvalid(true);
		return errorField;
	}
}
