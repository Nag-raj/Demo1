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
package de.hybris.platform.paymetric.constants;


import de.hybris.platform.paymetric.service.impl.DefaultPaymetricService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymetric.sdk.XIConfig;
import com.primesys.xipayws.CCrypto;
import com.primesys.xipayws.CTimer;
import com.primesys.xipayws.CXiPay;
import com.primesys.xipayws.CXiPayMgr;
import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;
import com.primesys.xipayws.XiPay30WSStub.InfoItem;


/**
 * Global class for all Paymetric constants. You can add global constants for your extension into this class.
 */
public final class PaymetricConstants extends GeneratedPaymetricConstants
{
	public static final String VERSION = "PEM-v2.1.1";
	public static final String EXTENSIONNAME = "paymetric";
	public static final String PLATFORM_LOGO_CODE = "paymetricPlatformLogo";

	private static DefaultPaymetricService paymetricService = null;
	private static final String XI_URL = "paymetric.xi.url";
	private static final String XI_REFRESH = "paymetric.xi.refresh";
	private static final String XI_CREDIT_CARD_GUID = "paymetric.xi.credit-card.guid";
	private static final String XI_CREDIT_CARD_PSK = "paymetric.xi.credit-card.psk";
	private static final String XI_CREDIT_CARD_TYPES = "paymetric.xi.credit-card.types";
	private static final String XI_CREDIT_CARD_3DS = "paymetric.xi.credit-card.3ds";
	private static final String XI_CREDIT_CARD_IFRAME_AUTOSIZE_HEIGHT = "paymetric.xi.credit-card.iframe.autosize.height";
	private static final String XI_CREDIT_CARD_IFRAME_AUTOSIZE_WIDTH = "paymetric.xi.credit-card.iframe.autosize.width";
	private static final String XI_CREDIT_CARD_IFRAME_TEMPLATE = "paymetric.xi.credit-card.iframe.template";
	private static final String XI_CREDIT_CARD_IFRAME_CSS = "paymetric.xi.credit-card.iframe.css";
	private static final String XI_CREDIT_CARD_TMS = "paymetric.xi.credit-card.tms";


	private static final String XI_ECHECK_GUID = "paymetric.xi.echeck.guid";
	private static final String XI_ECHECK_PSK = "paymetric.xi.echeck.psk";
	private static final String XI_ECHECK_IFRAME_TEMPLATE = "paymetric.xi.echeck.iframe.template";
	private static final String XI_ECHECK_IFRAME_CSS = "paymetric.xi.echeck.iframe.css";

	private static final String XI_PAYPAL_ENVIRONMENT = "paymetric.xi.paypal.environment";
	private static final String XI_PAYPAL_CLIENT_ID = "paymetric.xi.paypal.client-id";
	private static final String XI_PAYPAL_MERCHANT_EMAIL = "paymetric.xi.paypal.merchant-email";

	private static final String XI_ALIPAY_ORDER_DESCRIPTION = "paymetric.xi.alipay.order-description";

	private static final String XI_APM_TYPE = "paymetric.xi.apm.types";
	private static final String XI_HOST_URI = "paymetric.xi.hostUri";

	private static final String PROFILING = "paymetric.profiling";
	private static final String LOGGING = "paymetric.logging";
	private static final String AXIS_LOGGING = "paymetric.axis-logging";
	private static final String ENDPOINT = "paymetric.endpoint";
	private static final String USER = "paymetric.user";
	private static final String PASSWORD = "paymetric.password";
	private static final String VALIDATE = "paymetric.validate";
	private static final String ALLOW_RAW_CARDS = "paymetric.allow-raw-cards";
	private static final String SIMULATE = "paymetric.simulate";
	private static final String SIMULATE_DELAY = "paymetric.simulate-delay";
	private static final String OBJECT_TTL = "paymetric.object-ttl";
	private static final String OBJECT_COUNT = "paymetric.object-count";
	private static final String TIMEOUT = "paymetric.timeout";


	private static String CARD_VALIDATION_SAVE_FAILURES = "paymetric.card-validation-save-failures";
	private static String CARD_VALIDATION_ENABLED = "paymetric.card-validation-enabled";
	private static String AUTHORIZATION_ENABLED = "paymetric.authorization-enabled";
	private static String AUTHORIZATION_LINE_ITEM_ENABLED = "paymetric.authorization.line-item.enabled";

	private static String SALESOP_ENABLED = "paymetric.salesop-enabled";
	private static String AUTOCAPTURE_ENABLED = "paymetric.auto-capture.enabled";
	private static String AUTOCAPTURE_INTERVAL = "paymetric.auto-capture.interval";
	private static String CAPTURE_ENABLED = "paymetric.capture-enabled";
	private static String SCHEDULE_ENABLED = "paymetric.schedule-enabled";

	private static final String INCLUDE_SOP_REQUEST = "paymetric.include-sop-request";
	private static String FRAUD_ENABLED = "paymetric.fraud.enabled";
	private static String FRAUD_AUTOVOID = "paymetric.fraud.auto-void";
	private static final String FRAUD_SUCCESS = "paymetric.fraud.result";
	private static final String FRAUD_INFOITEM = "paymetric.fraud.infoitem";
	private static final String AVS_MUST_PASS = "paymetric.avs.must-pass";
	private static final String CVV_MUST_PASS = "paymetric.cvv.must-pass";

	private static final String XSLT_CARD_VALIDATION_TO_XIPAY = "paymetric.xslt.card-validation.to-xipay";
	private static final String XSLT_CARD_VALIDATION_FROM_XIPAY = "paymetric.xslt.card-validation.from-xipay";
	private static final String XSLT_FULL_AUTHORIZATION_TO_XIPAY = "paymetric.xslt.full-authorization.to-xipay";
	private static final String XSLT_FULL_AUTHORIZATION_FROM_XIPAY = "paymetric.xslt.full-authorization.from-xipay";
	private static final String XSLT_CAPTURE_TO_XIPAY = "paymetric.xslt.capture.to-xipay";
	private static final String XSLT_CAPTURE_FROM_XIPAY = "paymetric.xslt.capture.from-xipay";
	private static final String XSLT_STANDALONE_REFUND_TO_XIPAY = "paymetric.xslt.standalone-refund.to-xipay";
	private static final String XSLT_STANDALONE_REFUND_FROM_XIPAY = "paymetric.xslt.standalone-refund.from-xipay";
	private static final String XSLT_VOID_TO_XIPAY = "paymetric.xslt.void.to-xipay";
	private static final String XSLT_VOID_FROM_XIPAY = "paymetric.xslt.void.from-xipay";
	private static final String XSLT_SETTLE_BATCH_TO_XIPAY = "paymetric.xslt.settle-batch.to-xipay";
	private static final String XSLT_SETTLE_BATCH_FROM_XIPAY = "paymetric.xslt.settle-batch.from-xipay";
	private static final String XSLT_GET_BATCH_STATUS_TO_XIPAY = "paymetric.xslt.get-batch-status.to-xipay";
	private static final String XSLT_GET_BATCH_STATUS_FROM_XIPAY = "paymetric.xslt.get-batch-status.from-xipay";
	private static final String XSLT_GET_TRANSACTION_TO_XIPAY = "paymetric.xslt.get-transaction.to-xipay";
	private static final String XSLT_GET_TRANSACTION_FROM_XIPAY = "paymetric.xslt.get-transaction.from-xipay";

	private static final Logger LOG = LoggerFactory.getLogger(PaymetricConstants.class);
	private static CXiPayMgr m_xiPay = null;
	private static XIConfig m_xiConfig = new XIConfig();
	private static CCrypto m_xiCrypto = null;



	public static CXiPayMgr getM_xiPay()
	{
		return m_xiPay;
	}

	public static void setM_xiPay(final CXiPayMgr m_xiPay)
	{
		PaymetricConstants.m_xiPay = m_xiPay;
	}


	public static DefaultPaymetricService getPaymetricService()
	{
		return paymetricService;
	}

	public static void setPaymetricService(final DefaultPaymetricService paymetricService)
	{
		PaymetricConstants.paymetricService = paymetricService;
	}

	private PaymetricConstants()
	{
		//empty to avoid instantiating this constant class
	}

	private static String decryptProperty(final String encryptedString)
	{
		final CTimer timer = (getProfilingEnabled() ? new CTimer() : null);
		final String strWhere = getWhere("decryptProperty");
		String result = null;

		try
		{
			if (m_xiCrypto == null)
			{
				m_xiCrypto = new CCrypto("DESede", Config.getParameter(XI_CREDIT_CARD_PSK));
			}
			result = (encryptedString != null && !encryptedString.isEmpty() ? m_xiCrypto.Decrypt(encryptedString) : encryptedString);
		}
		catch (final Exception ex)
		{
			forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null)
			{
				forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}

	/**
	 * Get the profile value set in local.properties
	 */
	public static boolean getProfilingEnabled()
	{
		return Config.getBoolean(PROFILING, false);
	}

	/**
	 * Get the logging-enabled value set in local.properties
	 */
	public static boolean getLoggingEnabled()
	{
		return Config.getBoolean(LOGGING, false);
	}

	/**
	 * Get the axis-logging value set in local.properties
	 */
	public static boolean getAxisLogging()
	{
		return Config.getBoolean(AXIS_LOGGING, false);
	}

	/**
	 * Get the endpoint value set in local.properties
	 */
	public static String getEndpoint()
	{
		return Config.getString(ENDPOINT, null);
	}

	/**
	 * Get the user value set in local.properties
	 */
	public static String getUser()
	{
		String user = decryptProperty(Config.getString(USER, ""));

		if (user != null && !user.toLowerCase().startsWith("paymetric\\"))
		{
			user = "PAYMETRIC\\" + user;
		}

		return user;
	}

	/**
	 * Get the password value set in local.properties
	 */
	public static String getPassword()
	{
		return decryptProperty(Config.getString(PASSWORD, ""));
	}

	/**
	 * Get the validate value set in local.properties
	 */
	public static boolean getValidate()
	{
		return Config.getBoolean(VALIDATE, true);
	}

	/**
	 * Get the allow-raw-cards value set in local.properties
	 */
	public static boolean getAllowRawCards()
	{
		return Config.getBoolean(ALLOW_RAW_CARDS, false);
	}

	/**
	 * Get the simulate value set in local.properties
	 */
	public static boolean getSimulate()
	{
		return Config.getBoolean(SIMULATE, false);
	}

	/**
	 * Get the simulate-delay value set in local.properties
	 */
	public static int getSimulateDelay()
	{
		return Config.getInt(SIMULATE_DELAY, 2) * 1000;
	}

	/**
	 * Get the object-ttl value set in local.properties
	 */
	public static long getObjectTTL()
	{
		// Defaults to 5 minutes (300 seconds)
		return Config.getLong(OBJECT_TTL, 300) * 1000;
	}

	/**
	 * Get the object-count value set in local.properties
	 */
	public static long getObjectCount()
	{
		return Config.getLong(OBJECT_COUNT, 60);
	}

	/**
	 * Get the object-count value set in local.properties
	 */
	public static int getTimeout()
	{
		return Config.getInt(TIMEOUT, 30);
	}

	/**
	 * Get the card-validation-save-failures value set in local.properties
	 */
	public static boolean getCardValidationSaveFailures(final String baseStore)
	{
		CARD_VALIDATION_SAVE_FAILURES = "paymetric." + baseStore + ".card-validation-save-failures";
		if (null == Config.getParameter(CARD_VALIDATION_SAVE_FAILURES))
		{
			CARD_VALIDATION_SAVE_FAILURES = "paymetric.card-validation-save-failures";
		}
		return Config.getBoolean(CARD_VALIDATION_SAVE_FAILURES, false);
	}

	/**
	 * Get the card-validation-enabled value set in local.properties
	 */
	public static boolean getCardValidationEnabled(final String baseStore)
	{
		CARD_VALIDATION_ENABLED = "paymetric." + baseStore + ".card-validation-enabled";
		if (null == Config.getParameter(CARD_VALIDATION_ENABLED))
		{
			CARD_VALIDATION_ENABLED = "paymetric.card-validation-enabled";
		}
		return Config.getBoolean(CARD_VALIDATION_ENABLED, true);
	}

	/**
	 * Get the authorization-enabled value set in local.properties
	 */
	public static boolean getAuthorizationEnabled(final String baseStore)
	{
		AUTHORIZATION_ENABLED = "paymetric." + baseStore + ".authorization-enabled";
		if (null == Config.getParameter(AUTHORIZATION_ENABLED))
		{
			AUTHORIZATION_ENABLED = "paymetric.authorization-enabled";
		}
		return Config.getBoolean(AUTHORIZATION_ENABLED, true);
	}

	/**
	 * Get the authorization-line-item-enabled value set in local.properties
	 */
	public static boolean getAuthorizationLineItemEnabled(final String baseStore)
	{
		AUTHORIZATION_LINE_ITEM_ENABLED = "paymetric." + baseStore + ".authorization.line-item.enabled";
		if (null == Config.getParameter(AUTHORIZATION_LINE_ITEM_ENABLED))
		{
			AUTHORIZATION_LINE_ITEM_ENABLED = "paymetric.authorization.line-item.enabled";
		}
		return Config.getBoolean(AUTHORIZATION_LINE_ITEM_ENABLED, true);
	}

	/**
	 * Get the salesop-enabled value set in local.properties
	 */
	public static boolean getSalesOpEnabled(final String baseStore)
	{
		SALESOP_ENABLED = "paymetric." + baseStore + ".salesop-enabled";
		if (null == Config.getParameter(SALESOP_ENABLED))
		{
			SALESOP_ENABLED = "paymetric.salesop-enabled";
		}
		return Config.getBoolean(SALESOP_ENABLED, false);
	}

	/**
	 * Get the auto-capture.enabled value set in local.properties
	 */
	public static boolean getAutoCaptureEnabled(final String baseStore)
	{
		AUTOCAPTURE_ENABLED = "paymetric." + baseStore + ".auto-capture.enabled";
		if (null == Config.getParameter(AUTOCAPTURE_ENABLED))
		{
			AUTOCAPTURE_ENABLED = "paymetric.auto-capture.enabled";
		}
		return Config.getBoolean(AUTOCAPTURE_ENABLED, false);
	}

	/**
	 * Get the auto-capture.interval value set in local.properties
	 */
	public static String getAutoCaptureInterval(final String baseStore)
	{
		AUTOCAPTURE_INTERVAL = "paymetric." + baseStore + ".auto-capture.interval";
		if (null == Config.getParameter(AUTOCAPTURE_INTERVAL))
		{
			AUTOCAPTURE_INTERVAL = "paymetric.auto-capture.interval";
		}
		return Config.getString(AUTOCAPTURE_INTERVAL, "hourly");
	}

	/**
	 * Get the capture-enabled value set in local.properties
	 */
	public static boolean getCaptureEnabled(final String baseStore)
	{
		CAPTURE_ENABLED = "paymetric." + baseStore + ".capture-enabled";
		if (null == Config.getParameter(CAPTURE_ENABLED))
		{
			CAPTURE_ENABLED = "paymetric.capture-enabled";
		}
		return Config.getBoolean(CAPTURE_ENABLED, false);
	}

	/**
	 * Get the schedule-enable value set in local.properties
	 */
	public static boolean getScheduleEnabled(final String baseStore)
	{
		SCHEDULE_ENABLED = "paymetric." + baseStore + ".schedule-enabled";
		if (null == Config.getParameter(SCHEDULE_ENABLED))
		{
			SCHEDULE_ENABLED = "paymetric.schedule-enabled";
		}
		return Config.getBoolean(SCHEDULE_ENABLED, false);
	}

	/**
	 * Get the include-sop-request value set in local.properties
	 */
	public static boolean getIncludeSopRequest()
	{
		return Config.getBoolean(INCLUDE_SOP_REQUEST, false);
	}

	/**
	 * Get the fraud-enabled value set in local.properties
	 */
	public static boolean getFraudEnabled(final String baseStore)
	{
		FRAUD_ENABLED = "paymetric." + baseStore + ".fraud.enabled";
		if (null == Config.getParameter(FRAUD_ENABLED))
		{
			FRAUD_ENABLED = "paymetric.fraud.enabled";
		}
		return Config.getBoolean(FRAUD_ENABLED, false);
	}


	/**
	 * Get the fraud auto-void value set in local.properties
	 */
	public static boolean getFraudAutoVoid(final String baseStore)
	{
		FRAUD_AUTOVOID = "paymetric." + baseStore + ".fraud.auto-void"; 
		if (null == Config.getParameter(FRAUD_AUTOVOID))
		{
			FRAUD_AUTOVOID = "paymetric.fraud.auto-void";
		}
		return Config.getBoolean(FRAUD_AUTOVOID, false);
	}

	/**
	 * Get the fraud-result value set in local.properties
	 */
	public static String getFraudResult()
	{
		return Config.getString(FRAUD_SUCCESS, "0");
	}

	/**
	 * Get the fraud-infoitem value set in local.properties
	 */
	public static String getFraudInfoItem()
	{
		return Config.getString(FRAUD_INFOITEM, "TR_FRAUD_RESULTSTATUS");
	}

	/**
	 * Get the avs-must-pass value set in local.properties
	 */
	public static boolean getAvsMustPass()
	{
		return Config.getBoolean(AVS_MUST_PASS, false);
	}

	/**
	 * Get the cvv-must-pass value set in local.properties
	 */
	public static boolean getCvvMustPass()
	{
		return Config.getBoolean(CVV_MUST_PASS, false);
	}

	/**
	 * Get the xslt-refresh value set in local.properties
	 */
	public static boolean getXsltRefresh(final String key)
	{
		final boolean result = Config.getBoolean(key, false);

		if (result)
		{
			setXsltRefresh(key, "false");
		}

		return result;
	}

	/**
	 * Set the value for xslt refresh
	 */
	public static void setXsltRefresh(final String key, final String value)
	{
		Config.setParameter(key, value);
	}

	/**
	 * Get the xslt file name for card validation (to XiPay) set in local.properties
	 */
	public static String getXsltCardValidationToXiPay()
	{
		return Config.getString(XSLT_CARD_VALIDATION_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for card validation (from XiPay) set in local.properties
	 */
	public static String getXsltCardValidationFromXiPay()
	{
		return Config.getString(XSLT_CARD_VALIDATION_FROM_XIPAY, null);
	}

	/**
	 * Get the xslt file name for full-authorization (to XiPay) set in local.properties
	 */
	public static String getXsltFullAuthorizationToXiPay()
	{
		return Config.getString(XSLT_FULL_AUTHORIZATION_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for full-authorization (from XiPay) set in local.properties
	 */
	public static String getXsltFullAuthorizationFromXiPay()
	{
		return Config.getString(XSLT_FULL_AUTHORIZATION_FROM_XIPAY, null);
	}

	/**
	 * Get the xslt file name for capture (to XiPay) set in local.properties
	 */
	public static String getXsltCaptureToXiPay()
	{
		return Config.getString(XSLT_CAPTURE_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for capture (from XiPay) set in local.properties
	 */
	public static String getXsltCaptureFromXiPay()
	{
		return Config.getString(XSLT_CAPTURE_FROM_XIPAY, null);
	}

	/**
	 * Get the xslt file name for standalone-refund (to XiPay) set in local.properties
	 */
	public static String getXsltStandaloneRefundToXiPay()
	{
		return Config.getString(XSLT_STANDALONE_REFUND_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for standalone-refund (from XiPay) set in local.properties
	 */
	public static String getXsltStandaloneRefundFromXiPay()
	{
		return Config.getString(XSLT_STANDALONE_REFUND_FROM_XIPAY, null);
	}

	/**
	 * Get the xslt file name for void (to XiPay) set in local.properties
	 */
	public static String getXsltVoidToXiPay()
	{
		return Config.getString(XSLT_VOID_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for void (from XiPay) set in local.properties
	 */
	public static String getXsltVoidFromXiPay()
	{
		return Config.getString(XSLT_VOID_FROM_XIPAY, null);
	}

	/**
	 * Get the xslt file name for settle batch (to XiPay) set in local.properties
	 */
	public static String getXsltSettleBatchToXiPay()
	{
		return Config.getString(XSLT_SETTLE_BATCH_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for settle batch (from XiPay) set in local.properties
	 */
	public static String getXsltSettleBatchFromXiPay()
	{
		return Config.getString(XSLT_SETTLE_BATCH_FROM_XIPAY, null);
	}

	/**
	 * Get the xslt file name for get batch status (to XiPay) set in local.properties
	 */
	public static String getXsltGetBatchStatusToXiPay()
	{
		return Config.getString(XSLT_GET_BATCH_STATUS_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for get batch status (from XiPay) set in local.properties
	 */
	public static String getXsltGetBatchStatusFromXiPay()
	{
		return Config.getString(XSLT_GET_BATCH_STATUS_FROM_XIPAY, null);
	}

	/**
	 * Get the xslt file name for get transaction (to XiPay) set in local.properties
	 */
	public static String getXsltGetTransactionToXiPay()
	{
		return Config.getString(XSLT_GET_TRANSACTION_TO_XIPAY, null);
	}

	/**
	 * Get the xslt file name for get transaction (from XiPay) set in local.properties
	 */
	public static String getXsltGetTransactionFromXiPay()
	{
		return Config.getString(XSLT_GET_TRANSACTION_FROM_XIPAY, null);
	}

	/**
	 * public method to get an instance of XiPay
	 */
	public static CXiPay getXiPayInstance(final String routingMerchantIDs, final String cardTypes, final String currencies)
	{
		final String strWhere = getWhere("getXiPayInstance");
		final CTimer timer = (getProfilingEnabled() ? new CTimer() : null);
		CXiPay xiPay = null;


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Create an instance of the XiPay object pool manager?
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			CXiPay.setLogging(getLoggingEnabled());
			CXiPay.setProfiling(getProfilingEnabled());
			if (getM_xiPay() == null)
			{
				setM_xiPay(new CXiPayMgr());
				getM_xiPay().profiling = (timer != null);
				getM_xiPay().logging = getLoggingEnabled();
				getM_xiPay().Configure(getEndpoint(), getUser(), getPassword(), cardTypes, currencies, routingMerchantIDs, "A;X",
						"M;X",
						"TR_CARD_CIDRESPCODE", VERSION, getAllowRawCards(), getValidate(), getAxisLogging(), getSimulate(),
						getSimulateDelay(), getObjectTTL(),getTimeout());
			}
			else
			{
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Make sure we have up-to-date configuration
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				synchronized (getM_xiPay())
				{
					getM_xiPay().profiling = (timer != null);
					getM_xiPay().logging = getLoggingEnabled();
					getM_xiPay().endpoint = getEndpoint();
					getM_xiPay().user = getUser();
					getM_xiPay().password = getPassword();
					getM_xiPay().cardTypes = cardTypes;
					getM_xiPay().currencies = currencies;
					getM_xiPay().merchantIDs = routingMerchantIDs;
					getM_xiPay().allowRawCards = (getAllowRawCards() ? Boolean.TRUE : Boolean.FALSE);
					getM_xiPay().validate = (getValidate() ? Boolean.TRUE : Boolean.FALSE);
					getM_xiPay().loggingEnabled = (getAxisLogging() ? Boolean.TRUE : Boolean.FALSE);
					getM_xiPay().simulate = (getSimulate() ? Boolean.TRUE : Boolean.FALSE);
					getM_xiPay().simulateDelay = getSimulateDelay();
					getM_xiPay().expirationTime = getObjectTTL();
					getM_xiPay().timeout = getTimeout();
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Obtain an instance of the XiPay class from the object pool
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (null != (xiPay = getM_xiPay().checkOut()))
			{
				xiPay.setAllowedCardTypes(cardTypes);
				xiPay.setAllowedCurrencies(currencies);
				xiPay.setAllowedMerchantIDs(routingMerchantIDs);
			}
		}
		catch (final Exception ex)
		{
			forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null && getM_xiPay() != null)
			{
				final String tmp = "Duration=" + timer.getElapsedTimeString() + " - XiPay Connections(Available="
						+ getM_xiPay().unlocked.size() + ", In-Use=" + getM_xiPay().locked.size() + ")";
				forcedLog(LOG, strWhere, tmp);
			}
		}

		return xiPay;
	}

	/**
	 * set the instance of XiPay
	 */
	public static void setXiPayInstance(CXiPay xiPay)
	{
		final String strWhere = getWhere("setXiPayInstance");
		final CTimer timer = (getProfilingEnabled() ? new CTimer() : null);


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Make the supplied XiPay instance available again
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (getM_xiPay() != null && xiPay != null)
			{
				getM_xiPay().checkIn(xiPay);
			}
			else
			{
				xiPay = null;
			}
		}
		catch (final Exception ex)
		{
			forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null && getM_xiPay() != null)
			{
				final String tmp = "Duration=" + timer.getElapsedTimeString() + " - XiPay Connections(Available="
						+ getM_xiPay().unlocked.size() + ", In-Use=" + getM_xiPay().locked.size() + ")";
				forcedLog(LOG, strWhere, tmp);
			}
		}
	}

	/**
	 * Get the String representation of paymetric version and a method name
	 */
	public static final String getWhere(final String strMethod)
	{
		return VERSION + " [" + Thread.currentThread().getId() + "] " + strMethod + "() ";
	}

	/**
	 * public method to get the InfoItem by passing the key
	 */
	public static String getInfoItem(final String strKey, final ITransactionHeader xiTran)
	{
		String strValue = "";

		for (final InfoItem infoItem : xiTran.getInfoItems().getInfoItem())
		{
			if (infoItem != null && infoItem.getKey() != null && infoItem.getKey().equalsIgnoreCase(strKey))
			{
				strValue = (infoItem.getValue() != null ? infoItem.getValue() : "");
				break;
			}
		}

		return strValue;
	}

	/**
	 * public method to add the infoitem to the existing infoitems
	 */
	public static void addInfoItems(final ITransactionHeader xiTran, final Map<String, String> newInfoItems)
	{
		InfoItem[] xiItems = null;
		final ArrayList<InfoItem> items = new ArrayList<InfoItem>();

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Validate arguments
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (xiTran == null)
		{
			return;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Copy all the original InfoItems
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (xiTran.getInfoItems() != null)
		{
			for (final InfoItem item : xiTran.getInfoItems().getInfoItem())
			{
				items.add(item);
			}
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Copy the NEW InfoItems
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		newInfoItems.forEach((key, value) -> items.add(new InfoItem(key, value)));
		xiItems = new InfoItem[items.size()];
		items.toArray(xiItems);
		xiTran.setInfoItems(xiItems);

		return;
	}

	/**
	 * public method to set the logger, logging level, method name and the message for logging purpose
	 */
	public static void forcedLog(final Logger logger, final Level level, final String strWhere, final String message,
			final Throwable t)
	{
		switch (level.toInt())
		{
			case Level.ERROR_INT:
				if (t == null)
				{
					logger.error(strWhere + message);
				}
				else
				{
					logger.error(strWhere + message, t);
				}
				break;

			default:
				logger.info(strWhere + message);
				break;
		}
	}

	/**
	 * overloaded public method to set the logger, method name and the message for logging purpose
	 */
	public static void forcedLog(final Logger LOG, final String strWhere, final String message, final Throwable t)
	{
		forcedLog(LOG, Level.ERROR, strWhere, message + " -- " + CXiPay.stackTraceToString(t), null);
	}

	/**
	 * overloaded public method to set the logger, method name and the message for logging purpose
	 */
	public static void forcedLog(final Logger LOG, final String strWhere, final String message)
	{
		forcedLog(LOG, Level.INFO, strWhere, message, null);
	}

	/**
	 * public method to load the XiIntercept properties during server startup OR when configured through HAC
	 */
	public static void loadXIConfig()
	{
		final String strWhere = getWhere("loadXIConfig");
		final CTimer timer = (getProfilingEnabled() ? new CTimer() : null);



		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Force reloading of XiIntercept configuration?
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (Config.getBoolean(XI_REFRESH, false))
			{
				Config.setParameter(XI_REFRESH, "false");
				XIConfig.isConfigured.set(false);
				forcedLog(LOG, strWhere, "BEGIN Loading/Reloading XiIntercept configuration");
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Load XiIntercept configuration?
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (!XIConfig.isConfigured.get())
			{
				final Properties props = new Properties();

				Config.setParameter(XI_REFRESH, "false");
				props.setProperty(XI_URL, Config.getString(XI_URL, "https://qaapp02.xisecurenet.com/diecomm"));
				props.setProperty(XI_CREDIT_CARD_GUID, Config.getString(XI_CREDIT_CARD_GUID, ""));
				props.setProperty(XI_CREDIT_CARD_PSK, Config.getString(XI_CREDIT_CARD_PSK, ""));
				props.setProperty(XI_CREDIT_CARD_TYPES, Config.getString(XI_CREDIT_CARD_TYPES, ",VI-MC-AX-DI"));
				props.setProperty(XI_CREDIT_CARD_3DS, Config.getString(XI_CREDIT_CARD_3DS, "false"));
				props.setProperty(XI_CREDIT_CARD_IFRAME_AUTOSIZE_HEIGHT,
						Config.getString(XI_CREDIT_CARD_IFRAME_AUTOSIZE_HEIGHT, "true"));
				props.setProperty(XI_CREDIT_CARD_IFRAME_AUTOSIZE_WIDTH,
						Config.getString(XI_CREDIT_CARD_IFRAME_AUTOSIZE_WIDTH, "true"));
				props.setProperty(XI_CREDIT_CARD_IFRAME_TEMPLATE,
						Config.getString(XI_CREDIT_CARD_IFRAME_TEMPLATE, ",CreditCardCVV"));
				props.setProperty(XI_CREDIT_CARD_IFRAME_CSS, Config.getString(XI_CREDIT_CARD_IFRAME_CSS,
						",https://qaapp02.xisecurenet.com/diecomm/Content/IFrameStyleSheet.css"));

				props.setProperty(XI_CREDIT_CARD_TMS, Config.getString(XI_CREDIT_CARD_TMS, ""));

				props.setProperty(XI_ECHECK_GUID, Config.getString(XI_ECHECK_GUID, ""));
				props.setProperty(XI_ECHECK_PSK, Config.getString(XI_ECHECK_PSK, ""));
				props.setProperty(XI_ECHECK_IFRAME_TEMPLATE, Config.getString(XI_ECHECK_IFRAME_TEMPLATE, ",ACHIFrame.xml"));
				props.setProperty(XI_ECHECK_IFRAME_CSS,
						Config.getString(XI_ECHECK_IFRAME_CSS,
								",https://qaapp02.xisecurenet.com/diecomm/Content/IFrameStyleSheet.css"));

				props.setProperty(XI_PAYPAL_ENVIRONMENT, Config.getString(XI_PAYPAL_ENVIRONMENT, ""));
				props.setProperty(XI_PAYPAL_CLIENT_ID, decryptProperty(Config.getString(XI_PAYPAL_CLIENT_ID, "")));
				props.setProperty(XI_PAYPAL_MERCHANT_EMAIL, decryptProperty(Config.getString(XI_PAYPAL_MERCHANT_EMAIL, "")));

				props.setProperty(XI_ALIPAY_ORDER_DESCRIPTION, Config.getString(XI_ALIPAY_ORDER_DESCRIPTION, ""));

				props.setProperty(XI_APM_TYPE, Config.getString(XI_APM_TYPE, ""));
				props.setProperty(XI_HOST_URI, Config.getString(XI_HOST_URI, ""));


				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Copy custom XI properties?
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				final Map<String, String> params = Config.getAllParameters();
				for (final Entry<String, String> item : params.entrySet())
				{
					if (item.getKey().startsWith("XI."))
					{
						props.setProperty(item.getKey(), item.getValue());
					}
				}

				m_xiConfig.loadConfiguration(props);
				forcedLog(LOG, strWhere, "Loaded/Reloaded XiIntercept configuration");


			}
		}
		catch (final Exception ex)
		{
			forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null)
			{
				forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}
	}
}
