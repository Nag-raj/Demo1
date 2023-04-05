package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.facades.order.OcaCheckoutFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import javax.annotation.Resource;
import java.util.List;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaPlaceOrderParamsValidatorTest {

    @Mock
    private Errors errors;

    private static final String ORDER_DELIVERY_OPTION_NOT_SET = "order.deliveryOptionNotSet";
    private static final String ORDER_SHIPPING_CARRIER_NOT_SET = "order.shippingCarrierNotSet";
    private static final String ORDER_PO_NUMBER_NOT_SET = "order.poNumberNotSet";

    private static final String PO_NUMBER="poNumber";
    private static final String SHIPPING_CARRIER_CODE="shippingCarrierCode";
    private static final String DELIVERY_OPTION_CODE="deliveryOptionCode";


    @Mock
    private OcaCheckoutFacade b2bCheckoutFacade;

    @Mock
    private PlaceOrderData placeOrderData;

    @InjectMocks
    private OcaPlaceOrderParamsValidator ocaPlaceOrderParamsValidator;

    @Before
    public void setUp(){
        doNothing().when(b2bCheckoutFacade).validateShippingCarrierAndDeliveryOption(placeOrderData,errors);
    }
    @Test
    public void testValidateErrorsPONumberNotSet()
    {
        when(placeOrderData.getShippingCarrierCode()).thenReturn(SHIPPING_CARRIER_CODE);
        when(placeOrderData.getDeliveryOptionCode()).thenReturn(DELIVERY_OPTION_CODE);
        when(placeOrderData.getPoNumber()).thenReturn(null);
        ocaPlaceOrderParamsValidator.validate(placeOrderData, errors);
        verify(errors).reject(ORDER_PO_NUMBER_NOT_SET);
        verify(b2bCheckoutFacade).validateShippingCarrierAndDeliveryOption(placeOrderData,errors);
    }

    @Test
    public void testValidateErrorsShippingCarrierNotSet()
    {
        when(placeOrderData.getPoNumber()).thenReturn(PO_NUMBER);
        when(placeOrderData.getDeliveryOptionCode()).thenReturn(DELIVERY_OPTION_CODE);
        when(placeOrderData.getShippingCarrierCode()).thenReturn(null);
        ocaPlaceOrderParamsValidator.validate(placeOrderData, errors);
        verify(errors).reject(ORDER_SHIPPING_CARRIER_NOT_SET);
        verifyNoInteractions(b2bCheckoutFacade);
    }

    @Test
    public void testValidateErrorsDeliveryOptionNotSet()
    {
        when(placeOrderData.getPoNumber()).thenReturn(PO_NUMBER);
        when(placeOrderData.getShippingCarrierCode()).thenReturn(SHIPPING_CARRIER_CODE);
        when(placeOrderData.getDeliveryOptionCode()).thenReturn(null);
        ocaPlaceOrderParamsValidator.validate(placeOrderData, errors);
        verify(errors).reject(ORDER_DELIVERY_OPTION_NOT_SET);
        verifyNoInteractions(b2bCheckoutFacade);
    }
}
