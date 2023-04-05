/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.paymetric.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.methods.CardPaymentService;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.payment.strategy.TransactionCodeGenerator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Utilities;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;


@IntegrationTest
public class PaymetricIntegrationTest extends ServicelayerTest
{
	/**
	 * some currency symbol other than EUR
	 */
	private static final String CURRENCY_SYMBOL = Currency.getInstance(Locale.US).getSymbol();
	private static final String TEST_CC_NUMBER = "4457012346670009"; //vantiv token
	private static final int TEST_CC_EXPIRATION_MONTH = 12;
	private static final int TEST_CC_EXPIRATION_YEAR_VALID = (Calendar.getInstance().get(Calendar.YEAR) + 2);
	private static final int TEST_CC_EXPIRATION_YEAR_EXPIRED = (Calendar.getInstance().get(Calendar.YEAR) - 2);
	private static final BigDecimal AMOUNT = BigDecimal.valueOf(55); // Front?
	private static final BigDecimal NEGATIVE_AMOUNT = BigDecimal.valueOf(-55);


	@Resource
	private PaymentService paymentService;
	@Resource
	private CardPaymentService cardPaymentService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private ProductService productService;
	@Resource
	private UnitService unitService;
	@Resource
	private CatalogService catalogService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CommandFactoryRegistry paymetricCommandFactoryRegistry;
	@Resource
	private CalculationService calculationService;

	@Resource
	private CommonI18NService commonI18NService;

	private CommandFactoryRegistry serverCommandFactoryRegistry;

	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PaymetricIntegrationTest.class.getName());

	@SuppressWarnings(
	{ "resource", "deprecation" })
	@BeforeClass
	public static void prepare() throws Exception //NOPMD
	{
		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();
		LOG.debug("Preparing...");

		final ApplicationContext appCtx = Registry.getApplicationContext();
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		if (beanFactory.getRegisteredScope("tenant") == null)
		{
			beanFactory.registerScope("tenant", new de.hybris.platform.spring.TenantScope());
		}
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
		LOG.info("Loading paymetricTest-spring.xml...");
		xmlReader.loadBeanDefinitions(new ClassPathResource("paymetricTest-spring.xml"));
	}

	@Before
	public void setUp() throws Exception
	{
		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();
		LOG.debug("Preparing...");

		final ApplicationContext appCtx = Registry.getApplicationContext();
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		if (beanFactory.getRegisteredScope("tenant") == null)
		{
			beanFactory.registerScope("tenant", new de.hybris.platform.spring.TenantScope());
		}
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
		LOG.info("Loading paymetricTest-spring.xml...");
		xmlReader.loadBeanDefinitions(new ClassPathResource("paymetricTest-spring.xml"));

		LOG.debug("Set up in progress...");
		final TransactionCodeGenerator generator = new MockTransactionCodeGenerator();
		((PaymetricPaymentService) paymentService).setTransactionCodeGenerator(generator);

		createCoreData();
		createDefaultCatalog();
		createOrder();

		// For some reason a different commandFactoryRegistry is set by Spring here and in the cardPaymentService
		if (cardPaymentService instanceof DefaultCardPaymentServiceImpl)
		{
			LOG.info("Setting the commandFactoryRegistry: " + paymetricCommandFactoryRegistry.getClass().getName());
			final DefaultCardPaymentServiceImpl dCardPaymentService = (DefaultCardPaymentServiceImpl) cardPaymentService;

			serverCommandFactoryRegistry = dCardPaymentService.getCommandFactoryRegistry();
			dCardPaymentService.setCommandFactoryRegistry(paymetricCommandFactoryRegistry);
		}
	}

	@After
	public void tearDown()
	{
		LOG.debug("Tear down in progress...");
		if (cardPaymentService instanceof DefaultCardPaymentServiceImpl)
		{
			final DefaultCardPaymentServiceImpl dCardPayentService = (DefaultCardPaymentServiceImpl) cardPaymentService;
			dCardPayentService.setCommandFactoryRegistry(serverCommandFactoryRegistry);
		}
	}

	public static void createCoreData() throws Exception
	{
		LOG.info("Creating essential data for core ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.emptyMap(), null);
		// importing test csv
		importCsv("/test/testBasics.csv", "windows-1252");
		LOG.info("Finished creating essential data for core in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public static void createDefaultCatalog() throws Exception
	{
		LOG.info("Creating test catalog ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();

		importCsv("/test/testCatalog.csv", "windows-1252");

		// checking imported stuff
		final CatalogVersionService catalogVersionService = (CatalogVersionService) Registry.getApplicationContext()
				.getBean("catalogVersionService");
		final ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		final ProductService productService = (ProductService) Registry.getApplicationContext().getBean("productService");
		final CategoryService categoryService = (CategoryService) Registry.getApplicationContext().getBean("categoryService");
		assertNotNull(catalogVersionService);
		assertNotNull(modelService);

		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		assertNotNull(version);
		JaloSession.getCurrentSession().getSessionContext().setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS,
				Collections.singletonList(modelService.toPersistenceLayer(version)));
		//setting catalog to session and admin user
		final CategoryModel category = categoryService.getCategoryForCode("testCategory0");
		assertNotNull(category);
		final ProductModel product = productService.getProductForCode("testProduct0");
		assertNotNull(product);
		assertEquals(category, product.getSupercategories().iterator().next());

		LOG.info("Finished creating test catalog in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public void createOrder() throws InvalidCartException, Exception
	{
		LOG.info("Creating order ...");

		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setIsocode(CURRENCY_SYMBOL);
		currency.setSymbol(CURRENCY_SYMBOL);
		currency.setBase(Boolean.TRUE);
		currency.setActive(Boolean.TRUE);
		currency.setConversion(Double.valueOf(1));
		modelService.save(currency);

		final ProductModel product0 = productService.getProductForCode("testProduct0");
		final PriceRowModel prmodel0 = modelService.create(PriceRowModel.class);
		prmodel0.setCurrency(currency);
		prmodel0.setMinqtd(Long.valueOf(1));
		prmodel0.setNet(Boolean.TRUE);
		prmodel0.setPrice(Double.valueOf(5.00));
		prmodel0.setUnit(unitService.getUnitForCode("kg"));
		prmodel0.setProduct(product0);
		prmodel0.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel0, product0));


		final ProductModel product1 = productService.getProductForCode("testProduct1");
		final PriceRowModel prmodel1 = modelService.create(PriceRowModel.class);
		prmodel1.setCurrency(currency);
		prmodel1.setMinqtd(Long.valueOf(1));
		prmodel1.setNet(Boolean.TRUE);
		prmodel1.setPrice(Double.valueOf(7.00));
		prmodel1.setUnit(unitService.getUnitForCode("kg"));
		prmodel1.setProduct(product1);
		prmodel1.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel1, product1));

		final ProductModel product2 = productService.getProductForCode("testProduct2");
		final PriceRowModel prmodel2 = modelService.create(PriceRowModel.class);
		prmodel2.setCurrency(currency);
		prmodel2.setMinqtd(Long.valueOf(1));
		prmodel2.setNet(Boolean.TRUE);
		prmodel2.setPrice(Double.valueOf(7.00));
		prmodel2.setUnit(unitService.getUnitForCode("kg"));
		prmodel2.setProduct(product2);
		prmodel2.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel2, product2));

		final CartModel cart = cartService.getSessionCart();
		//final UserModel user = userService.getCurrentUser();
		cartService.addNewEntry(cart, product0, 2, null);
		cartService.addNewEntry(cart, product1, 2, null);
		cartService.addNewEntry(cart, product2, 2, null);
	}

	////////////////////   T E S T I N G  S T A R T S   H E R E   /////////////////////////////
	@Test
	public void shouldGetSuccessfulAuthorizationOnly()
	{

		//final BillingInfo billingInfo = createBillingInfo();
		final AddressModel deliveryAddress = createDeliveryAddress();
		//	final CardInfo card = getCardInfo(billingInfo, true);
		final Currency currency = getCurrency();
		CartModel cart = null;
		try
		{
			cart = getCart();
		}
		catch (final InvalidCartException e)
		{

			e.printStackTrace();
		}
		cartService.setSessionCart(cart);

		LOG.info("Before calling Authorization");

		//	final PaymentTransactionEntryModel ptem = paymentService.authorize("173412-TXN-CODE-43142356", AMOUNT,
		//		(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, deliveryAddress, card);

		final PaymentTransactionEntryModel transactionEntryModel = paymentService.authorize("173412-43142356", AMOUNT,
				(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, "187901950", "123", "paymetric");

		LOG.info("After calling Authorization");

		assertEquals("Amount doesn't match!", AMOUNT.longValue(), transactionEntryModel.getAmount().longValue());
		assertNotNull("Payment Transaction shouldn't ne null!", transactionEntryModel.getPaymentTransaction());
		assertNotNull("Payment Transaction ID shouldn't be null!", transactionEntryModel.getAUTRA());
		assertEquals("ONLINE_VAN_USD", transactionEntryModel.getMERCH());
		assertNotNull("Auth code shouldn't be null!", transactionEntryModel.getAUNUM());
		assertEquals("Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(),
				transactionEntryModel.getTransactionStatus());
		assertEquals("Transaction Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				transactionEntryModel.getTransactionStatusDetails());
		assertEquals("Type doesn't match!", PaymentTransactionType.AUTHORIZATION.name(), transactionEntryModel.getType().getCode());
		final long difference = Calendar.getInstance().getTime().getTime() - transactionEntryModel.getTime().getTime();
		final long hour = 1000l * 60l * 60l;
		assertTrue("The txn did not happen in the last hour! [diff=" + difference + " milis]", (difference < hour));

		LOG.info("Auth Ptem Request token value :" + transactionEntryModel.getRequestToken());
	}


	@Test
	public void shouldGetSuccessfulAuthorizationFollowedByCaptureOperation() throws CalculationException, InvalidCartException
	{

		final AddressModel deliveryAddress = createDeliveryAddress();
		final Currency currency = getCurrency();
		CartModel cart = null;

		cart = getCart();

		cartService.setSessionCart(cart);

		// create TXN
		final PaymentTransactionModel cartTX = new PaymentTransactionModel();
		cartTX.setCode("TEST_PT_CODE");
		modelService.save(cartTX);

		// assign the cart to the transaction
		cartTX.setOrder(cart);
		modelService.save(cartTX);

		LOG.info("Before calling Authorization");

		final PaymentTransactionEntryModel cartAuthPtem = paymentService.authorize("173412-43142357", AMOUNT,
				(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, "187901950", "123", "paymetric");

		LOG.info("After calling Authorization");

		final String cartPtemTxStatus = cartAuthPtem.getTransactionStatus();

		// create an order from the cart
		cart.setDeliveryAddress(deliveryAddress);

		final OrderModel order = orderService.createOrderFromCart(cart);
		modelService.save(order);
		calculationService.calculate(order);

		// The old cartTX is not valid for capture any more, get the txn from the order instead
		final PaymentTransactionModel orderTX = order.getPaymentTransactions().iterator().next();
		orderTX.setEntries(Arrays.asList(cartAuthPtem));
		orderTX.setPaymentProvider("paymetric");
		orderTX.setRequestToken(cartAuthPtem.getRequestToken());
		orderTX.setRequestId(cartAuthPtem.getRequestId());

		// capture attempt
		if (TransactionStatus.ACCEPTED.name().equals(cartPtemTxStatus))
		{
			final PaymentTransactionEntryModel cartPtemCapture = paymentService.capture(orderTX);
			assertEquals("Capture Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(),
					cartPtemCapture.getTransactionStatus());
			assertNotSame("Two different transactions must not be identical!", cartAuthPtem, cartPtemCapture);
		}
	}

	// Transactions that are in 'Authorized' status only can be voided.
	// Captured transactions cannot be voided and need to issue 'Refund' for them.
	@Test
	public void shouldGetSuccessfulAuthorizationFollowedByVoidOperation() throws InvalidCartException, CalculationException
	{
		final AddressModel deliveryAddress = createDeliveryAddress();
		final Currency currency = getCurrency();
		CartModel cart = null;

		cart = getCart();

		cartService.setSessionCart(cart);

		// create TXN
		final PaymentTransactionModel cartTX = new PaymentTransactionModel();
		cartTX.setCode("TEST_PT_CODE");
		modelService.save(cartTX);

		// assign the cart to the transaction
		cartTX.setOrder(cart);
		modelService.save(cartTX);

		final PaymentTransactionEntryModel cartAuthPtem = paymentService.authorize("173412-43142357", AMOUNT,
				(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, "187901950", "123", "paymetric");

		final String cartPtemTxStatus = cartAuthPtem.getTransactionStatus();

		// create an order from the cart
		cart.setDeliveryAddress(deliveryAddress);


		// void attempt
		if (TransactionStatus.ACCEPTED.name().equals(cartPtemTxStatus))
		{
			final PaymentTransactionEntryModel cartPtemVoided = paymentService.cancel(cartAuthPtem);
			assertNotNull("should not be null", cartPtemVoided);
			assertEquals("Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(),
					cartPtemVoided.getTransactionStatus());

		}

	}

	@Test
	public void shouldResultInSuccessfulRefundFollowedByCaptureOperation()throws InvalidCartException, CalculationException
	{


		final AddressModel deliveryAddress = createDeliveryAddress();
		final Currency currency = getCurrency();
		CartModel cart = null;
		try
		{
			cart = getCart();
		}
		catch (final InvalidCartException e)
		{

			e.printStackTrace();
		}
		cartService.setSessionCart(cart);

		// refund transaction will have negative amount in the request
		final PaymentTransactionEntryModel refundTransactionEntryModel = paymentService.authorize("173412-43142356",
				NEGATIVE_AMOUNT, (Currency) modelService.toPersistenceLayer(currency), deliveryAddress, "187901950", "123",
				"paymetric");

		assertNotNull("Refund Payment Transactions should not be empty ", refundTransactionEntryModel);
		assertSame("Auth amount does not match", TransactionStatus.ACCEPTED.name(), refundTransactionEntryModel.getTransactionStatus());
		
		final String cartPtemTxStatus = refundTransactionEntryModel.getTransactionStatus();

		// create an order from the cart
		cart.setDeliveryAddress(deliveryAddress);

		final OrderModel order = orderService.createOrderFromCart(cart);
		modelService.save(order);
		calculationService.calculate(order);

		// The old cartTX is not valid for capture any more, get the txn from the order instead
		final PaymentTransactionModel orderTX = order.getPaymentTransactions().iterator().next();
		orderTX.setEntries(Arrays.asList(refundTransactionEntryModel));
		orderTX.setPaymentProvider("paymetric");
		orderTX.setRequestToken(refundTransactionEntryModel.getRequestToken());
		orderTX.setRequestId(refundTransactionEntryModel.getRequestId());

		// capture attempt
		if (TransactionStatus.ACCEPTED.name().equals(cartPtemTxStatus))
		{
			final PaymentTransactionEntryModel cartPtemCapture = paymentService.capture(orderTX);
			assertEquals("Capture Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(),
					cartPtemCapture.getTransactionStatus());
			assertNotSame("Two different transactions must not be identical!", refundTransactionEntryModel, cartPtemCapture);
		}
	}

	@Test
	public void shouldFailVoidOperationWhenCapturedTransactionAttemptedToVoid() throws InvalidCartException, CalculationException
	{


		final AddressModel deliveryAddress = createDeliveryAddress();
		final Currency currency = getCurrency();
		CartModel cart = null;

		cart = getCart();

		cartService.setSessionCart(cart);

		// create TXN
		final PaymentTransactionModel cartTX = new PaymentTransactionModel();
		cartTX.setCode("TEST_PT_CODE");
		modelService.save(cartTX);

		// assign the cart to the transaction
		cartTX.setOrder(cart);
		modelService.save(cartTX);


		final PaymentTransactionEntryModel cartAuthPtem = paymentService.authorize("173412-43142357", AMOUNT,
				(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, "187901950", "123", "paymetric");

		final String cartPtemTxStatus = cartAuthPtem.getTransactionStatus();

		// create an order from the cart
		cart.setDeliveryAddress(deliveryAddress);

		final OrderModel order = orderService.createOrderFromCart(cart);
		modelService.save(order);
		calculationService.calculate(order);

		// very surprising, the old cartTX is not valid for capture any more, get the txn from the order instead
		final PaymentTransactionModel orderTX = order.getPaymentTransactions().iterator().next();
		orderTX.setEntries(Arrays.asList(cartAuthPtem));
		orderTX.setPaymentProvider("paymetric");
		orderTX.setRequestToken(cartAuthPtem.getRequestToken());
		orderTX.setRequestId(cartAuthPtem.getRequestId());

		// capture and void attempt should fail
		if (TransactionStatus.ACCEPTED.name().equals(cartPtemTxStatus))
		{
			final PaymentTransactionEntryModel cartPtemCapture = paymentService.capture(orderTX);
			assertEquals("Capture Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(),
					cartPtemCapture.getTransactionStatus());
			assertNotSame("Two different transactions must not be identical!", cartPtemTxStatus, cartPtemCapture);

			final PaymentTransactionEntryModel cartPtemVoided = paymentService.cancel(cartAuthPtem);

			assertEquals("Transaction Status doesn't match!",
					TransactionStatus.REJECTED.name(),
					cartPtemVoided.getTransactionStatus());
		}


	}



	////////////////////   T E S T I N G    E N D S   H E R E   /////////////////////////////

	private DebitPaymentInfoModel getPaymentInfo(final CartModel cart, final UserModel user)
	{
		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setCode(UUID.randomUUID().toString());
		paymentInfo.setBank("MyBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");
		return paymentInfo;
	}

	private AddressModel getDeliveryAddress(final UserModel user)
	{
		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Albert");
		deliveryAddress.setLastname("Einstein");
		deliveryAddress.setTown("Munich");
		return deliveryAddress;
	}

	private CartModel getCart() throws InvalidCartException
	{
		final ProductModel product0 = productService.getProductForCode("testProduct0");
		final ProductModel product1 = productService.getProductForCode("testProduct1");
		final ProductModel product2 = productService.getProductForCode("testProduct2");
		final CartModel cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product0, 2, null);
		cartService.addNewEntry(cart, product1, 2, null);
		cartService.addNewEntry(cart, product2, 2, null);

		final DeliveryModeModel dmm = new DeliveryModeModel("DCODE1");
		cart.setDeliveryMode(dmm);
		cart.setSite(new BaseSiteModel("electronics"));
		cart.setPaymentInfo(createCreditCardPaymentInfo());
		//	cartService.setSessionCart(cart);

		return cart;
	}

	private CreditCardPaymentInfoModel createCreditCardPaymentInfo()
	{

		final CreditCardPaymentInfoModel cardPaymentInfoModel = modelService.create(CreditCardPaymentInfoModel.class);
		cardPaymentInfoModel.setBillingAddress(createDeliveryAddress());
		//cardPaymentInfoModel.setCode(createCustomer().getUid() + "_" + UUID.randomUUID());
		//cardPaymentInfoModel.setUser(createCustomer());
		cardPaymentInfoModel.setSubscriptionId("SUBID9999");
		cardPaymentInfoModel.setNumber(TEST_CC_NUMBER);
		cardPaymentInfoModel.setType(CreditCardType.VISA);
		cardPaymentInfoModel.setCcOwner("John Doe");
		cardPaymentInfoModel.setValidToMonth(String.valueOf("12"));
		cardPaymentInfoModel.setValidToYear(String.valueOf("2025"));
		cardPaymentInfoModel.setSaved(false);
		cardPaymentInfoModel.setCode("CODE1234");
		cardPaymentInfoModel.setUser(userService.getCurrentUser());

		modelService.save(cardPaymentInfoModel);

		return cardPaymentInfoModel;


	}

	private CustomerModel createCustomer() {

		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setType(CustomerType.GUEST);
		customerModel.setName("John Doe");
		customerModel.setAddresses(Arrays.asList(createDeliveryAddress()));
		customerModel.setDefaultShipmentAddress(createDeliveryAddress());
		customerModel.setCustomerID("CUSTID00005");
		customerModel.setDefaultPaymentAddress(createDeliveryAddress());
		customerModel.setUid("UID999");

		modelService.save(customerModel);

		return customerModel;

	}

	private Currency getCurrency()
	{
		return Currency.getInstance("USD");
	}

	private CardInfo getCardInfo(final BillingInfo billingInfo, final boolean valid)
	{

		final CardInfo card = new CardInfo();
		card.setCardHolderFullName("John Doe");
		card.setCardNumber(TEST_CC_NUMBER);
		card.setExpirationMonth(Integer.valueOf(TEST_CC_EXPIRATION_MONTH));
		if (valid)
		{
			card.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR_VALID));
		}
		else
		{
			card.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR_EXPIRED));
		}
		card.setBillingInfo(billingInfo);
		card.setCardType(CreditCardType.VISA);
		return card;
	}

	private AddressModel createDeliveryAddress()
	{
		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(userService.getCurrentUser());
		deliveryAddress.setFirstname("Sanket");
		deliveryAddress.setLastname("Kulkarni");
		deliveryAddress.setTown("Houston");
		deliveryAddress.setAppartment("2106");
		deliveryAddress.setBillingAddress(true);

		final CountryModel country = commonI18NService.getCountry("US");
		deliveryAddress.setCountry(country);
		//deliveryAddress.setRegion(commonI18NService.getRegion(country, country.getIsocode() + "-" + "MD"));
		deliveryAddress.setPostalcode("77070");
		deliveryAddress.setEmail("sanketk@gmail.com");
		deliveryAddress.setPhone1("8974353212");

		modelService.saveAll(Arrays.asList(deliveryAddress));
		return deliveryAddress;
	}

	private BillingInfo createBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setCity("Mountain View");
		billingInfo.setCountry("US");
		billingInfo.setEmail("nobody@cybersource.com");
		billingInfo.setFirstName("John");
		billingInfo.setIpAddress("10.7" + "." + "7.7");
		billingInfo.setLastName("Doe");
		billingInfo.setPhoneNumber("650-965-6000");
		billingInfo.setPostalCode("94043");
		billingInfo.setState("CA");
		billingInfo.setStreet1("1295 Charleston Road");
		return billingInfo;
	}

	public class MockTransactionCodeGenerator implements TransactionCodeGenerator
	{
		private int suffix = 4711;

		@Override
		public String generateCode(final String base)
		{
			suffix++;
			return base + "-" + suffix;
		}
	}
}
