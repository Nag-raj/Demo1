package com.olympus.oca.commerce.core.strategies.impl;

import com.olympus.oca.commerce.core.b2bunit.OcaB2BUnitService;
import com.olympus.oca.commerce.core.enums.PartnerFunctionCode;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.strategies.DeliveryAddressesLookupStrategy;
import de.hybris.platform.commerceservices.util.ItemComparator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class DefaultOcaB2BDeliveryAddressesLookupStrategy implements DeliveryAddressesLookupStrategy {

    private OcaB2BUnitService ocaB2BUnitService;
    @Override
    public List<AddressModel> getDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder, final boolean visibleAddressesOnly)
    {
        final Set<AddressModel> addresses = ocaB2BUnitService.getSoldToDeliveryAddresses(abstractOrder.getUnit());
        if (CollectionUtils.isNotEmpty(addresses))
        {
            return sortAddresses(addresses);
        }
        return Collections.EMPTY_LIST;
    }

    protected List<AddressModel> sortAddresses(final Collection<AddressModel> addresses)
    {
        final ArrayList<AddressModel> result = new ArrayList<>(addresses);
        result.sort(ItemComparator.INSTANCE);
        return result;
    }

    public void setOcaB2BUnitService(OcaB2BUnitService ocaB2BUnitService) {
        this.ocaB2BUnitService = ocaB2BUnitService;
    }
}
