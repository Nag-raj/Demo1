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
package de.hybris.platform.paymetric.controller;

import de.hybris.platform.payment.commands.request.StandaloneRefundRequest;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.methods.CardPaymentService;
import de.hybris.platform.paymetric.service.impl.DefaultPaymetricService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CXiPay;


@Controller
@SuppressWarnings("unused")
public class PaymetricHelloController
{
	private static Logger LOG = LoggerFactory.getLogger(PaymetricHelloController.class);

	@Autowired
	private DefaultPaymetricService paymetricService;

	@Autowired
	private CardPaymentService cardPaymentService;

	@Autowired
	private ModelService modelService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		return "welcome";
	}

	@RequestMapping(value = "/authorize", method = RequestMethod.POST)
	public @ResponseBody String authorize(@RequestBody final String request)
	{
		SubscriptionAuthorizationRequest req = null;
		AuthorizationResult res = null;
		String result = "";

		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Perform the Hybris Authorization Command and send the response
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (cardPaymentService != null && request != null && !request.isEmpty())
			{
				req = CXmlHelper.fromXml(request, LOG);
				res = cardPaymentService.authorize(req);
				result = CXmlHelper.toXml(res, LOG);
			}
		}
		catch (final Exception ex)
		{
			if (LOG != null)
			{
				LOG.error(CXiPay.stackTraceToString(ex));
			}
		}

		return result;
	}

	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public @ResponseBody String refund(@RequestBody final String request)
	{
		StandaloneRefundRequest req = null;
		RefundResult res = null;
		String result = "";

		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Perform the Hybris Authorization Command and send the response
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (cardPaymentService != null && request != null && !request.isEmpty())
			{
				req = CXmlHelper.fromXml(request, LOG);
				res = cardPaymentService.refundStandalone(req);
				result = CXmlHelper.toXml(res, LOG);
			}
		}
		catch (final Exception ex)
		{
			if (LOG != null)
			{
				LOG.error(CXiPay.stackTraceToString(ex));
			}
		}

		return result;
	}
}
