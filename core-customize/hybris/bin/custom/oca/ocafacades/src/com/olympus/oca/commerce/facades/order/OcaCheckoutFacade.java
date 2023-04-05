/**
 *
 */
package com.olympus.oca.commerce.facades.order;

import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import org.springframework.validation.Errors;


public interface OcaCheckoutFacade extends CheckoutFacade
{
	public void savePlaceOrderDataToCart(PlaceOrderData placeOrderParams);

	void validateShippingCarrierAndDeliveryOption(PlaceOrderData placeOrderData, Errors errors);
}
