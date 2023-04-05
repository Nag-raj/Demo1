package com.olympus.oca.commerce.core.cart.impl;

import com.olympus.oca.commerce.core.cart.dao.OcaCartDao;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;
import com.olympus.oca.commerce.core.price.ContractPriceService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.*;

import java.util.Date;
import java.util.List;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOcaCartServiceTest {
    private static final String CARRIER_CODE = "testCarrierCode";
    private static final String CARRIER_ACCOUNT = "testCarrierAccount";
    private static final String EXPECTED_B2B_UNIT_ID = "testExpectedB2bUnitId";
    private static final String NOT_EXPECTED_B2B_UNIT_ID = "testNotExpectedB2bUnitId";

    @Spy
    @InjectMocks
    private DefaultOcaCartService classUnderTest;

    @Mock
    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
    @Mock
    private ContractPriceService contractPriceService;
    @Mock
    private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
    @Mock
    private ModelService modelService;
    @Mock
    private OcaCartDao cartDao;

    @Mock
    private ShippingCarrierModel shippingCarrier;
    @Mock
    private DeliveryOptionModel deliveryOption;
    @Mock
    private CommerceCartParameter parameter;
    @Mock
    private CartModel cart;
    @Mock
    private B2BUnitModel b2bUnit;

    @Test
    public void testGetShippingCarrierListForThirdParty() {
        Mockito.when(cartDao.getShippingCarrierListForThirdParty()).thenReturn(List.of(shippingCarrier));
        classUnderTest.getShippingCarrierListForThirdParty();
        Mockito.verify(cartDao).getShippingCarrierListForThirdParty();
        Mockito.verify(classUnderTest).getShippingCarrierListForThirdParty();
        Mockito.verifyNoMoreInteractions(cartDao, classUnderTest);
        Mockito.verifyNoInteractions(shippingCarrier, b2bUnitService, contractPriceService, commerceCartCalculationStrategy);
    }

    @Test
    public void testUpdateSessionCart_withUnit() {
        Mockito.doReturn(cart).when(classUnderTest).getSessionCart();
        Mockito.when(cart.getUnit()).thenReturn(b2bUnit);
        Mockito.when(b2bUnit.getUid()).thenReturn(EXPECTED_B2B_UNIT_ID);
        classUnderTest.updateSessionCart(EXPECTED_B2B_UNIT_ID, false);
        Mockito.verify(cart, Mockito.times(2)).getUnit();
        Mockito.verify(b2bUnit).getUid();
        Mockito.verifyNoInteractions(b2bUnitService, modelService, commerceCartCalculationStrategy);
        Mockito.verify(classUnderTest, Mockito.never()).getCommerceCartParameterForCart(Mockito.any(CartModel.class));
        Mockito.verify(commerceCartCalculationStrategy, Mockito.never()).calculateCart(Mockito.any(CommerceCartParameter.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateSessionCart_nullUnitUnknownUnitUid() {
        Mockito.doReturn(cart).when(classUnderTest).getSessionCart();
        Mockito.when(b2bUnitService.getUnitForUid(NOT_EXPECTED_B2B_UNIT_ID)).thenReturn(null);
        classUnderTest.updateSessionCart(NOT_EXPECTED_B2B_UNIT_ID, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateSessionCart_presentUnitUnknownUnitUid() {
        Mockito.doReturn(cart).when(classUnderTest).getSessionCart();
        Mockito.when(cart.getUnit()).thenReturn(b2bUnit);
        Mockito.when(b2bUnit.getUid()).thenReturn(EXPECTED_B2B_UNIT_ID);
        Mockito.when(b2bUnitService.getUnitForUid(NOT_EXPECTED_B2B_UNIT_ID)).thenReturn(null);
        classUnderTest.updateSessionCart(NOT_EXPECTED_B2B_UNIT_ID, false);
    }

    @Test
    public void testUpdateSessionCart_noCalculation() {
        Mockito.doReturn(cart).when(classUnderTest).getSessionCart();
        Mockito.when(b2bUnitService.getUnitForUid(NOT_EXPECTED_B2B_UNIT_ID)).thenReturn(b2bUnit);
        classUnderTest.updateSessionCart(NOT_EXPECTED_B2B_UNIT_ID, false);
        Mockito.verify(cart).setUnit(b2bUnit);
        Mockito.verify(cart).setContractPriceFetchedAt(null);
        Mockito.verify(modelService).save(cart);
        Mockito.verify(classUnderTest, Mockito.never()).getCommerceCartParameterForCart(Mockito.any(CartModel.class));
        Mockito.verify(commerceCartCalculationStrategy, Mockito.never()).calculateCart(Mockito.any(CommerceCartParameter.class));
    }

    @Test
    public void testUpdateSessionCart_withCartCalculation() {
        Mockito.doReturn(parameter).when(classUnderTest).getCommerceCartParameterForCart(cart);
        Mockito.doReturn(cart).when(classUnderTest).getSessionCart();
        Mockito.when(b2bUnitService.getUnitForUid(NOT_EXPECTED_B2B_UNIT_ID)).thenReturn(b2bUnit);
        classUnderTest.updateSessionCart(NOT_EXPECTED_B2B_UNIT_ID, true);
        Mockito.verify(cart).setUnit(b2bUnit);
        Mockito.verify(cart).setContractPriceFetchedAt(null);
        Mockito.verify(modelService).save(cart);
        Mockito.verify(classUnderTest).getCommerceCartParameterForCart(cart);
        Mockito.verify(commerceCartCalculationStrategy).calculateCart(parameter);
    }

    @Test
    public void testGetSessionCart_withoutContractPriceFetch() {
        Mockito.doReturn(cart).when(classUnderTest).getSessionCartFromSuper();
        classUnderTest.getSessionCart();
        Mockito.verify(classUnderTest, Mockito.never()).getCommerceCartParameterForCart(Mockito.any(CartModel.class));
        Mockito.verifyNoInteractions(commerceCartCalculationStrategy);
    }

    @Test
    public void testGetSessionCart_withContractPriceFetch() {
        Mockito.doReturn(cart).when(classUnderTest).getSessionCartFromSuper();
        Mockito.when(contractPriceService.isContractPriceExpired(Mockito.any(Date.class))).thenReturn(true);
        Mockito.when(cart.getContractPriceFetchedAt()).thenReturn(new Date());
        Mockito.doReturn(parameter).when(classUnderTest).getCommerceCartParameterForCart(cart);
        classUnderTest.getSessionCart();
        Mockito.verify(classUnderTest).getCommerceCartParameterForCart(cart);
        Mockito.verify(commerceCartCalculationStrategy).recalculateCart(parameter);
    }

    @Test
    public void testGetDeliveryOptions() {
        Mockito.when(cartDao.getDeliveryOptions(CARRIER_CODE)).thenReturn(List.of(deliveryOption));
        classUnderTest.getDeliveryOptions(CARRIER_CODE);
        Mockito.verify(cartDao).getDeliveryOptions(CARRIER_CODE);
        Mockito.verify(classUnderTest).getDeliveryOptions(CARRIER_CODE);
        Mockito.verifyNoMoreInteractions(cartDao, classUnderTest);
        Mockito.verifyNoInteractions(deliveryOption, b2bUnitService, contractPriceService, commerceCartCalculationStrategy);
    }

    @Test
    public void testGetShippingCarrierForCode() {
        Mockito.when(cartDao.getShippingCarrierForCode(CARRIER_CODE)).thenReturn(shippingCarrier);
        classUnderTest.getShippingCarrierForCode(CARRIER_CODE);
        Mockito.verify(cartDao).getShippingCarrierForCode(CARRIER_CODE);
        Mockito.verify(classUnderTest).getShippingCarrierForCode(CARRIER_CODE);
        Mockito.verifyNoMoreInteractions(cartDao, classUnderTest);
        Mockito.verifyNoInteractions(shippingCarrier, b2bUnitService, contractPriceService, commerceCartCalculationStrategy);
    }

    @Test
    public void testGetCommerceCartParameterForCart() {
        CommerceCartParameter result = classUnderTest.getCommerceCartParameterForCart(cart);
        Assert.assertEquals(cart, result.getCart());
        Assert.assertTrue(result.isEnableHooks());
        Mockito.verifyNoInteractions(cart);
    }
}