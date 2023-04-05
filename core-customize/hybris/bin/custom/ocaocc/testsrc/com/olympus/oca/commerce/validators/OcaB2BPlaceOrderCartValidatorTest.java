package com.olympus.oca.commerce.validators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaB2BPlaceOrderCartValidatorTest {
    @Mock
    private Errors errors;

    @Mock
    private CartData cart;

    @Mock
    private B2BPaymentTypeData b2BPaymentTypeData;

    @InjectMocks
    private OcaB2BPlaceOrderCartValidator ocaB2BPlaceOrderCartValidator;

    @Test
    public void testValidateErrorsCartNotCalculated() {

        when(cart.isCalculated()).thenReturn(false);
        when(cart.getDeliveryAddress()).thenReturn(new AddressData());
        when(cart.getPaymentType()).thenReturn(b2BPaymentTypeData);
        when(b2BPaymentTypeData.getCode()).thenReturn("CARD");
        when(cart.getPaymentInfo()).thenReturn(new CCPaymentInfoData());
        ocaB2BPlaceOrderCartValidator.validate(cart, errors);
        verify(errors).reject("cart.notCalculated");
    }

    @Test
    public void testValidateErrorCartDeliveryAddressNotSet() {
        when(cart.isCalculated()).thenReturn(true);
        when(cart.getDeliveryAddress()).thenReturn(null);
        when(cart.getPaymentType()).thenReturn(b2BPaymentTypeData);
        when(b2BPaymentTypeData.getCode()).thenReturn("CARD");
        when(cart.getPaymentInfo()).thenReturn(new CCPaymentInfoData());
        ocaB2BPlaceOrderCartValidator.validate(cart, errors);
        verify(errors).reject("cart.deliveryAddressNotSet");
    }

    @Test
    public void testValidateErrorCartPaymentInfoNotSet() {
        when(cart.isCalculated()).thenReturn(true);
        when(cart.getDeliveryAddress()).thenReturn(new AddressData());
        when(cart.getPaymentType()).thenReturn(b2BPaymentTypeData);
        when(b2BPaymentTypeData.getCode()).thenReturn("CARD");
        when(cart.getPaymentInfo()).thenReturn(null);
        ocaB2BPlaceOrderCartValidator.validate(cart, errors);
        verify(errors).reject("cart.paymentInfoNotSet");
    }

    @Test
    public void testValidateNoErrors() {

        when(cart.isCalculated()).thenReturn(true);
        when(cart.getDeliveryAddress()).thenReturn(new AddressData());
        when(cart.getPaymentType()).thenReturn(b2BPaymentTypeData);
        when(b2BPaymentTypeData.getCode()).thenReturn("CARD");
        when(cart.getPaymentInfo()).thenReturn(new CCPaymentInfoData());
        ocaB2BPlaceOrderCartValidator.validate(cart, errors);
        assertTrue(errors.getAllErrors().isEmpty());
    }
}
