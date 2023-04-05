/**
 *
 */
package com.olympus.oca.commerce.facades.order.impl;
import com.olympus.oca.commerce.core.cart.OcaCartService;
import com.olympus.oca.commerce.core.cart.impl.DefaultOcaCartService;
import com.olympus.oca.commerce.core.delivery.impl.DefaultOcaDeliveryService;
import com.olympus.oca.commerce.core.enums.OrderSource;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;
import com.olympus.oca.commerce.facades.constants.OcaFacadesConstants;
import com.olympus.oca.commerce.facades.order.OcaCheckoutFacade;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import de.hybris.platform.acceleratorfacades.payment.PaymentFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
public class DefaultOcaCheckoutFacade extends DefaultB2BCheckoutFacade implements OcaCheckoutFacade {

    private static final String ORDER_DELIVERY_OPTION_CODE_INCORRECT = "order.deliveryOptionCodeIncorrect";

    private static final String ORDER_SHIPPING_CARRIER_CODE_INCORRECT = "order.shippingCarrierCodeIncorrect";

    private static final String CART_CHECKOUT_TRANSACTION_NOT_AUTHORIZED = "cart.transation.notAuthorized";
    
    @Resource(name = "cartService")
    private DefaultOcaCartService cartService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "cartService")
    private OcaCartService ocaCartService;
    
    private PaymentFacade paymentFacade;

    private DefaultOcaDeliveryService ocaDeliveryService;
    
    private ConfigurationService configurationService;

 	 private DeliveryModeService deliveryModeService;
 	 
 	 private Converter<CartModel, Map<String, String>> ocaPaymetricCreditCardParamsConverter;



    @Override
    public <T extends AbstractOrderData> T placeOrder(final PlaceOrderData placeOrderData) throws InvalidCartException {
        // for CARD type, transaction must be authorized before placing order
        final boolean isCardtPaymentType = CheckoutPaymentType.CARD.equals(getCart().getPaymentType());
        if (isCardtPaymentType) {
            final List<PaymentTransactionModel> transactions = getCart().getPaymentTransactions();
            boolean authorized = false;
            for (final PaymentTransactionModel transaction : transactions) {
                for (final PaymentTransactionEntryModel entry : transaction.getEntries()) {
                    if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION)
                            && TransactionStatus.ACCEPTED.name()
                                    .equals(entry.getTransactionStatus())) {
                        authorized = true;
                        break;
                    }
                }
            }
            if (!authorized) {
                throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_TRANSACTION_NOT_AUTHORIZED));
            }

        }

        return (T) super.placeOrder();
    }

    @Override
    protected void beforePlaceOrder(final CartModel cartModel)
    {
        super.beforePlaceOrder(cartModel);
        cartModel.setOrderSource(OrderSource.INTERNAL);
    }

    @Override
    protected void afterPlaceOrder(final CartModel cartModel, final OrderModel orderModel) {
        if (orderModel != null && !getModelService().isNew(orderModel)) {
            getCartService().removeSessionCart();
            getModelService().refresh(orderModel);
        }
    }

    @Override
 	public void savePlaceOrderDataToCart(final PlaceOrderData placeOrderParams)
 	{
 		final CartModel cartModel = cartService.getSessionCart();
 		cartModel.setPurchaseOrderNumber(placeOrderParams.getPoNumber());
 		cartModel.setDeliveryOption(placeOrderParams.getDeliveryOptionCode());
 		cartModel.setDeliveryMode(
 				deliveryModeService.getDeliveryModeForCode(configurationService.getConfiguration().getString(OcaFacadesConstants.DEFAULT_DELIVERY_MODE)));
 		cartModel.setAdditionalAddressLine(placeOrderParams.getAdditionalAddressLine());
 		modelService.save(cartModel);
 	}


    @Override
    public void validateShippingCarrierAndDeliveryOption(final PlaceOrderData placeOrderData, final Errors errors) {
        final ShippingCarrierModel shippingCarrierModel = ocaCartService
                .getShippingCarrierForCode(placeOrderData.getShippingCarrierCode());
        if (null == shippingCarrierModel) {
            errors.reject(ORDER_SHIPPING_CARRIER_CODE_INCORRECT);
        } else {
            final List<DeliveryOptionModel> deliveryOptions = ocaCartService
                    .getDeliveryOptions(placeOrderData.getShippingCarrierCode());
            final boolean isDeliveryOptionPresent = deliveryOptions.stream()
                    .anyMatch(deliveryOption -> deliveryOption.getCode()
                            .equalsIgnoreCase(
                                    placeOrderData.getDeliveryOptionCode()));
            if (!isDeliveryOptionPresent) {
                errors.reject(ORDER_DELIVERY_OPTION_CODE_INCORRECT);
            }
        }
    }

    @Override
    public void prepareCartForCheckout() {
        final CartModel cartModel = getCart();
        addDefaultShippingAddressToCart(cartModel);
        if (cartModel != null) {
            getCommerceCheckoutService().calculateCart(createCommerceCheckoutParameter(cartModel, true));
        }
    }

    @Override
    public CartData updateCheckoutCart(final CartData cartData) {
        final CartModel cartModel = getCart();
        if (cartModel == null) {
            return null;
        }
        // set payment type
        if (cartData.getPaymentType() != null) {
            final String newPaymentTypeCode = cartData.getPaymentType().getCode();

            // clear delivery mode and payment details when changing payment type
            if (cartModel.getPaymentType() == null
                    || !newPaymentTypeCode.equalsIgnoreCase(cartModel.getPaymentType().getCode())) {
                cartModel.setDeliveryMode(null);
                cartModel.setPaymentInfo(null);
            }

            setPaymentTypeForCart(newPaymentTypeCode, cartModel);
            // cost center need to be be cleared if using card
            if (CheckoutPaymentType.CARD.getCode().equals(newPaymentTypeCode)) {
                setCostCenterForCart(null, cartModel);
            }
        }

        // set cost center
        if (cartData.getCostCenter() != null) {
            setCostCenterForCart(cartData.getCostCenter().getCode(), cartModel);
        }

        // set purchase order number
        if (cartData.getPurchaseOrderNumber() != null) {
            cartModel.setPurchaseOrderNumber(cartData.getPurchaseOrderNumber());
        }

        // set delivery address
        if (cartData.getDeliveryAddress() != null) {
            setDeliveryAddress(cartData.getDeliveryAddress());
        }

        // set quote request description
        if (cartData.getB2BComment() != null) {
            final B2BCommentModel b2bComment = getModelService().create(B2BCommentModel.class);
            b2bComment.setComment(cartData.getB2BComment().getComment());
            getB2bCommentService().addComment(cartModel, b2bComment);
        }

        getModelService().save(cartModel);
        return getCheckoutCart();

    }

    @Override
    public boolean setDeliveryAddress(final AddressData addressData) {
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            AddressModel addressModel = null;
            if (addressData != null) {
                addressModel = addressData.getId() == null ? createDeliveryAddressModel(addressData, cartModel)
                        : getDeliveryAddressModelForCode(addressData.getId());
            }

            final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
            parameter.setAddress(addressModel);
            parameter.setIsDeliveryAddress(false);
            return getCommerceCheckoutService().setDeliveryAddress(parameter);
        }
        return false;
    }

    protected AddressModel getDeliveryAddressModelForCode(final String code) {
        Assert.notNull(code, "Parameter code cannot be null.");
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            for (final AddressModel address : ocaDeliveryService.getSupportedDeliveryAddressesForOrder(cartModel,
                    false)) {
                if (code.equals(address.getPk().toString())) {
                    return address;
                }
            }
        }
        return null;
    }

    private void addDefaultShippingAddressToCart(CartModel cartModel) {
        final B2BUnitModel unitModel = cartModel.getUnit();
        if (Objects.isNull(cartModel.getDeliveryAddress()) || !isDeliveryAddressValidForCurrentUnit(cartModel)) {
            if (null != unitModel.getShippingAddress()) {
                cartModel.setDeliveryAddress(unitModel.getShippingAddress());
            } else if (CollectionUtils.isNotEmpty(unitModel.getShippingAddresses())) {
                final AddressModel deliveryAddress = unitModel.getShippingAddresses().stream().findFirst().orElse(null);
                cartModel.setDeliveryAddress(deliveryAddress);
            }
        }

        modelService.save(cartModel);
    }

    private Boolean isDeliveryAddressValidForCurrentUnit(CartModel cartModel) {
        if (Objects.nonNull(cartModel.getDeliveryAddress())) {
            return cartModel.getDeliveryAddress().getOwner().equals(cartModel.getUnit());
        }
        return false;
    }
    
   @Override
 	public boolean authorizePayment(final String securityCode)
 	{
 		final CartModel cart = getCart();
 		if (cart == null)
 		{
 			return false;
 		}
 		if (cart.getPaymentInfo() != null && cart.getPaymentInfo() instanceof CreditCardPaymentInfoModel && checkIfCurrentUserIsTheCartUser())
 		{
 			final CreditCardPaymentInfoModel paymentInfo = (CreditCardPaymentInfoModel) cart.getPaymentInfo();
 			final PaymentSubscriptionResultData paymentSubscriptionResultData = paymentFacade
 					.completeSopCreateSubscription(ocaPaymetricCreditCardParamsConverter.convert(cart), false, true);
 			if(null!=paymentSubscriptionResultData && null!=paymentSubscriptionResultData.getStoredCard()) {
 				paymentInfo.setSubscriptionId(paymentSubscriptionResultData.getStoredCard().getSubscriptionId());
 			} else {
 				return false;
 			}
 		}
 		return super.authorizePayment(securityCode);
 	}

    public DefaultOcaDeliveryService getOcaDeliveryService() {
        return ocaDeliveryService;
    }

    public void setOcaDeliveryService(DefaultOcaDeliveryService ocaDeliveryService) {
        this.ocaDeliveryService = ocaDeliveryService;
    }
    
 	public void setConfigurationService(final ConfigurationService configurationService)
 	{
 		this.configurationService = configurationService;
 	}

 	public void setDeliveryModeService(final DeliveryModeService deliveryModeService)
 	{
 		this.deliveryModeService = deliveryModeService;
 	}
 	
 	public void setPaymentFacade(PaymentFacade paymentFacade)
	{
		this.paymentFacade = paymentFacade;
	}
 	
	public void setOcaPaymetricCreditCardParamsConverter(
			Converter<CartModel, Map<String, String>> ocaPaymetricCreditCardParamsConverter)
	{
		this.ocaPaymetricCreditCardParamsConverter = ocaPaymetricCreditCardParamsConverter;
	}

}
