package com.olympus.oca.commerce.integrations.contract.service;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface ContractPriceIntegrationService {
    AbstractOrderEntryModel fetchContractPriceForCartEntry(AbstractOrderEntryModel entry);

    AbstractOrderModel fetchContractPriceForCart(final AbstractOrderModel entry);

}
