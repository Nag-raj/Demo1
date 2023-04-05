package com.olympus.oca.commerce.integrations.vertex.service;

import com.olympus.oca.commerce.integrations.exceptions.OcaIntegrationException;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface SalesTaxVertexIntegrationService {

    AbstractOrderModel fetchSalesTaxForCart(AbstractOrderModel candidate) throws OcaIntegrationException;
}
