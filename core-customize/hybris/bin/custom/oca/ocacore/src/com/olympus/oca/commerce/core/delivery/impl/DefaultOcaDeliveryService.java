package com.olympus.oca.commerce.core.delivery.impl;

import com.olympus.oca.commerce.core.strategies.impl.DefaultOcaB2BDeliveryAddressesLookupStrategy;
import de.hybris.platform.commerceservices.delivery.impl.DefaultDeliveryService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.List;

public class DefaultOcaDeliveryService extends DefaultDeliveryService {

    private DefaultOcaB2BDeliveryAddressesLookupStrategy defaultOcaB2BDeliveryAddressesLookupStrategy;

    public DefaultOcaB2BDeliveryAddressesLookupStrategy getDefaultOcaB2BDeliveryAddressesLookupStrategy() {
        return defaultOcaB2BDeliveryAddressesLookupStrategy;
    }

    public void setDefaultOcaB2BDeliveryAddressesLookupStrategy(DefaultOcaB2BDeliveryAddressesLookupStrategy defaultOcaB2BDeliveryAddressesLookupStrategy) {
        this.defaultOcaB2BDeliveryAddressesLookupStrategy = defaultOcaB2BDeliveryAddressesLookupStrategy;
    }

    @Override
    public List<AddressModel> getSupportedDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder,
                                                                    final boolean visibleAddressesOnly)
    {
        return defaultOcaB2BDeliveryAddressesLookupStrategy.getDeliveryAddressesForOrder(abstractOrder, visibleAddressesOnly);
    }
}
