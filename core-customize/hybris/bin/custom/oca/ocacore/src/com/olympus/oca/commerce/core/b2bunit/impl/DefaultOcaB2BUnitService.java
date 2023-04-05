package com.olympus.oca.commerce.core.b2bunit.impl;

import com.olympus.oca.commerce.core.b2bunit.OcaB2BUnitService;
import com.olympus.oca.commerce.core.enums.PartnerFunctionCode;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

public class DefaultOcaB2BUnitService implements OcaB2BUnitService {

    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    public DefaultOcaB2BUnitService(B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService){
        this.b2bUnitService = b2bUnitService;
    }

    @Override
    public Set<AddressModel> getSoldToDeliveryAddresses(B2BUnitModel unitModel) {
        return collectDeliveryAddressesForSoldTo(unitModel);
    }

    protected Set<AddressModel> collectDeliveryAddressesForSoldTo(B2BUnitModel soldTo)
    {
        final Set<AddressModel> addresses = new HashSet<AddressModel>();
        //add sold to address - as in most cases sold to will also be a ship to
        addresses.addAll(soldTo.getAddresses());
        collectChildShipToAccountAddresses(soldTo, addresses);
        return addresses;
    }

    protected void collectChildShipToAccountAddresses(B2BUnitModel soldTo, Set<AddressModel> addresses) {
        final Set<B2BUnitModel> branches = b2bUnitService.getBranch(soldTo);
        branches.stream().forEach(unit->{
            if(unit.getPartnerFunctionType() != null
                    && unit.getPartnerFunctionType().contains(PartnerFunctionCode.SHIP_TO)) {
                addresses.addAll(unit.getAddresses());
            }
        });
    }

}
