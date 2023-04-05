/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2020 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.acceleratorservices.payment.strategies.CreditCardPaymentInfoCreateStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentTransactionStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */

@UnitTest
public class DefaultPaymetricServiceUnitTest
{

	@InjectMocks
	private final DefaultPaymetricService defaultPaymetricService = new DefaultPaymetricService();

	@Mock
	private CreditCardPaymentInfoCreateStrategy creditCardPaymentInfoCreateStrategy;
	@Mock
	private PaymentTransactionStrategy paymentTransactionStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	private SessionService sessionService;

	//private CreditCardPaymentInfoModel cardPaymentInfoModel;

	@Mock
	private CartService cartService;

	private PaymentSubscriptionResultItem subscriptionResult;

	private final CustomerModel customerModel = new CustomerModel();
	private final CreditCardPaymentInfoModel cardPaymentInfoModel = new CreditCardPaymentInfoModel();

	Map resultMap = new HashMap<String, String>();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		resultMap.put("billTo_city", "Burlington");
		resultMap.put("billTo_email", "sanket.kulkarni2@fisglobal.com");
		resultMap.put("billTo_titleCode", "mr");
		resultMap.put("billTo_country", "US");
		resultMap.put("billTo_lastName", "Kulkarni");
		resultMap.put("billTo_street1", "1 Main St.");
		resultMap.put("card_cardType", "001");
		resultMap.put("currency", "USD");
		resultMap.put("card_accountNumber", "4457012346670009");
		resultMap.put("amount", "0");

		resultMap.put("card_nameOnCard", "Sanket K");
		resultMap.put("billTo_postalCode", "01803-3747");
		resultMap.put("card_cvNumber", "349");
		resultMap.put("billTo_firstName", "Sanket");
		resultMap.put("billTo_state", "MD");
		resultMap.put("card_expirationMonth", "2");
		resultMap.put("card_expirationYear", "2021");
		//resultMap.put("savePaymentInfo", "true");
		//Create a hashMap;


	}

	@Test
	public void shouldGetSuccessfulPreAuthorization()
	{

		final CartModel cart = new CartModel();
		cart.setCurrency(new CurrencyModel("USD", "$"));
		when(cartService.getSessionCart()).thenReturn(cart);

		when(creditCardPaymentInfoCreateStrategy.saveSubscription(customerModel, new CustomerInfoData(), new SubscriptionInfoData(),
				new PaymentInfoData(), false)).thenReturn(cardPaymentInfoModel);



		subscriptionResult = defaultPaymetricService.completeSopCreatePaymentSubscription(customerModel, false, resultMap);
		assertNotNull(subscriptionResult);
	}

}
