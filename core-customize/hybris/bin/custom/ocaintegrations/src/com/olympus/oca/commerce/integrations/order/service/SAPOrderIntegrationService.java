package com.olympus.oca.commerce.integrations.order.service;

import com.olympus.oca.commerce.integrations.exceptions.OcaIntegrationException;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface SAPOrderIntegrationService {
    AbstractOrderModel submitOrderToSapBTP(AbstractOrderModel candidate) throws OcaIntegrationException;
}
