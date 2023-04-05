package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.core.strategies.impl.DefaultOcaB2BDeliveryAddressesLookupStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;


import java.util.List;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaB2BCartAddressValidatorTest {
    public static final String TEST_ADDRESS_ID = "test_addressId";
    public static final String CART_DELIVERY_ADDRESS_INVALID = "cart.deliveryAddressInvalid";
    @Mock
    private CartService cartService;
    
    @Mock
    private CartModel cartModel;
    @Mock
    private DefaultOcaB2BDeliveryAddressesLookupStrategy defaultOcaB2BDeliveryAddressesLookupStrategy;

    @Mock
    private Errors errors;

    @InjectMocks
    private OcaB2BCartAddressValidator ocaB2BCartAddressValidator;

    @Test
    public void testValidateAddressInvalid_collectionEmpty()
    {
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(defaultOcaB2BDeliveryAddressesLookupStrategy.getDeliveryAddressesForOrder(cartModel,true)).thenReturn(List.of());
        ocaB2BCartAddressValidator.validate(TEST_ADDRESS_ID,errors);
        verify(errors).reject(CART_DELIVERY_ADDRESS_INVALID);
    }

    @Test
    public void testValidateAddressInvalid_addressIdBlank()
    {
        ocaB2BCartAddressValidator.validate(StringUtils.EMPTY,errors);
        verify(errors).reject(CART_DELIVERY_ADDRESS_INVALID);
        verifyNoInteractions(cartService);
        verifyNoInteractions(defaultOcaB2BDeliveryAddressesLookupStrategy);
    }

}
