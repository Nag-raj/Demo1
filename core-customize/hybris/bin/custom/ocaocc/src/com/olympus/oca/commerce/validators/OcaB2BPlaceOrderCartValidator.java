/**
 *
 */
package com.olympus.oca.commerce.validators;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartData;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OcaB2BPlaceOrderCartValidator implements Validator {

    @Override
    public boolean supports(final Class<?> arg0) {
        return String.class.isAssignableFrom(arg0);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final CartData cart = (CartData) target;

        if (!cart.isCalculated()) {
            errors.reject("cart.notCalculated");
        }

        if (cart.getDeliveryAddress() == null) {
            errors.reject("cart.deliveryAddressNotSet");
        }

        if (CheckoutPaymentType.CARD.getCode().equals(cart.getPaymentType().getCode())) {
            if (cart.getPaymentInfo() == null) {
                errors.reject("cart.paymentInfoNotSet");
            }
        }
    }
}
