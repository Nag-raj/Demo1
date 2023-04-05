/**
 *
 */
package com.olympus.oca.commerce.validators;

import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;

import javax.annotation.Resource;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.apache.commons.lang.StringUtils;
import com.olympus.oca.commerce.facades.order.OcaCheckoutFacade;


public class OcaPlaceOrderParamsValidator implements Validator
{

	private static final String ORDER_DELIVERY_OPTION_NOT_SET = "order.deliveryOptionNotSet";
	private static final String ORDER_SHIPPING_CARRIER_NOT_SET = "order.shippingCarrierNotSet";
	private static final String ORDER_PO_NUMBER_NOT_SET = "order.poNumberNotSet";

	@Resource(name = "b2bCheckoutFacade")
	private OcaCheckoutFacade b2bCheckoutFacade;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return false;
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final PlaceOrderData placeOrderData = (PlaceOrderData) target;
		if (StringUtils.isEmpty(placeOrderData.getPoNumber()))
		{
			errors.reject(ORDER_PO_NUMBER_NOT_SET);
		}
		if (StringUtils.isEmpty(placeOrderData.getShippingCarrierCode()))
		{
			errors.reject(ORDER_SHIPPING_CARRIER_NOT_SET);
		}
		if (StringUtils.isEmpty(placeOrderData.getDeliveryOptionCode()))
		{
			errors.reject(ORDER_DELIVERY_OPTION_NOT_SET);
		}

		if (null != placeOrderData.getShippingCarrierCode() && null != placeOrderData.getDeliveryOptionCode())
		{
			b2bCheckoutFacade.validateShippingCarrierAndDeliveryOption(placeOrderData, errors);
		}

	}

}
