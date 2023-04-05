package com.olympus.oca.commerce.core.b2bunit;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.Set;

public interface OcaB2BUnitService {

    Set<AddressModel> getSoldToDeliveryAddresses(B2BUnitModel unitModel);
}
