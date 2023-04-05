package com.olympus.oca.commerce.core.cart.impl;

import com.olympus.oca.commerce.core.cart.OcaCartService;
import com.olympus.oca.commerce.core.cart.dao.OcaCartDao;
import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.enums.ShippingMethodType;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingAccountModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;
import com.olympus.oca.commerce.core.price.ContractPriceService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Date;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class DefaultOcaCartService extends DefaultCartService implements OcaCartService {
    protected static final String SHIP_BY_OLYMPUS_FALLBACK_CODE = "shipByOlympus";

    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
    private ContractPriceService contractPriceService;
    private ConfigurationService configurationService;
    private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
    private OcaCartDao cartDao;

    @Override
    public List<ShippingCarrierModel> getShippingCarrierListForThirdParty() {
        return cartDao.getShippingCarrierListForThirdParty();
    }

    @Override
    public CartModel updateSessionCart(final String b2bUnitId, final boolean calculateCart) {
        final CartModel cart = getSessionCart();
        if (null == cart.getUnit() || !b2bUnitId.equalsIgnoreCase(cart.getUnit().getUid())) {
            final B2BUnitModel unit = b2bUnitService.getUnitForUid(b2bUnitId);
            validateParameterNotNull(unit, String.format("No unit found for uid %s", b2bUnitId));
            cart.setUnit(unit);
            cart.setContractPriceFetchedAt(null);
            getModelService().save(cart);
            if (calculateCart) {
                final CommerceCartParameter parameter = getCommerceCartParameterForCart(cart);
                commerceCartCalculationStrategy.calculateCart(parameter);
            }
        }
        return cart;
    }

    @Override
    public CartModel getSessionCart() {
        CartModel sessionCart = getSessionCartFromSuper();
        boolean shouldFetchContractPrice = contractPriceService.isContractPriceExpired(sessionCart.getContractPriceFetchedAt());
        if (shouldFetchContractPrice) {
            final CommerceCartParameter parameter = getCommerceCartParameterForCart(sessionCart);
            commerceCartCalculationStrategy.recalculateCart(parameter);
        }
        return sessionCart;
    }

    @Override
    public List<DeliveryOptionModel> getDeliveryOptions(final String shippingCarrierCode) {
        return cartDao.getDeliveryOptions(shippingCarrierCode);
    }

    @Override
    public ShippingCarrierModel getShippingCarrierForCode(final String carrierCode) {
        return cartDao.getShippingCarrierForCode(carrierCode);
    }

    @Override
    public void setThirdPartyShippingAccount(final String carrierCode, final String carrierAccount) {
        final CartModel cart = getSessionCart();
        ShippingAccountModel shippingAccount = getModelService().create(ShippingAccountModel.class);
        shippingAccount.setUid(cart.getCode() + "_" + new Date().getTime());
        shippingAccount.setShippingCarrierCode(carrierCode);
        shippingAccount.setAccountReferenceNumber(carrierAccount);
        cart.setThirdPartyShippingAccount(shippingAccount);
        getModelService().saveAll(cart);
    }

    @Override
    public void setShippingMethod(String carrierCode) {
        final CartModel cart = getSessionCart();
        final String shipByOlympusCode = configurationService.getConfiguration()
                                                             .getString(OcaCoreConstants.SHIP_BY_OLYMPUS_CODE, SHIP_BY_OLYMPUS_FALLBACK_CODE);
        if (carrierCode.equalsIgnoreCase(shipByOlympusCode)) {
            cart.setSelectedShippingMethod(ShippingMethodType.SHIP_BY_OLYMPUS);
        } else {
            cart.setSelectedShippingMethod(ShippingMethodType.THIRD_PARTY);
        }
        getModelService().save(cart);
        getCommerceCartParameterForCart(cart);
        commerceCartCalculationStrategy.recalculateCart(getCommerceCartParameterForCart(cart));
    }

    protected CommerceCartParameter getCommerceCartParameterForCart(CartModel candidate) {
        final CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(candidate);
        return parameter;
    }

    // this is a workaround to enable mocking for testing
    protected CartModel getSessionCartFromSuper() {
        return super.getSessionCart();
    }

    public void setB2bUnitService(B2BUnitService<B2BUnitModel, UserModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public void setContractPriceService(ContractPriceService contractPriceService) {
        this.contractPriceService = contractPriceService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setCommerceCartCalculationStrategy(CommerceCartCalculationStrategy commerceCartCalculationStrategy) {
        this.commerceCartCalculationStrategy = commerceCartCalculationStrategy;
    }

    public void setCartDao(OcaCartDao cartDao) {
        this.cartDao = cartDao;
    }
}
