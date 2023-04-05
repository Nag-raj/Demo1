package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.core.strategies.impl.DefaultOcaB2BDeliveryAddressesLookupStrategy;
import de.hybris.platform.order.CartService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

public class OcaB2BCartAddressValidator  implements Validator {

    @Resource(name = "cartService")
    private CartService cartService;
    @Resource(name = "defaultOcaB2BDeliveryAddressesLookupStrategy")
    private DefaultOcaB2BDeliveryAddressesLookupStrategy defaultOcaB2BDeliveryAddressesLookupStrategy;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(final Object obj, final Errors error) {

        final String addressId = (String) obj;
        if (StringUtils.isBlank(addressId) || CollectionUtils
                .isEmpty(defaultOcaB2BDeliveryAddressesLookupStrategy.getDeliveryAddressesForOrder(cartService.getSessionCart(), true)))
        {
            error.reject("cart.deliveryAddressInvalid");
        }

    }
}
