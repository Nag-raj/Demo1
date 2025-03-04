package com.olympus.oca.commerce.core.strategies.impl;

import com.olympus.oca.commerce.core.enums.ShippingMethodType;
import com.olympus.oca.commerce.integrations.freight.service.FreightCostIntegrationService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;

public class DefaultOcaFindDeliveryCostStrategy implements FindDeliveryCostStrategy {

    private final FreightCostIntegrationService freightCostIntegrationService;
    private final ModelService modelService;

    public DefaultOcaFindDeliveryCostStrategy(FreightCostIntegrationService freightCostIntegrationService, ModelService modelService) {
        this.freightCostIntegrationService = freightCostIntegrationService;
        this.modelService = modelService;
    }

    @Override
    public PriceValue getDeliveryCost(AbstractOrderModel order) {
        double totalDeliveryCost = 0.0;

        if (null != order.getSelectedShippingMethod() && ShippingMethodType.SHIP_BY_OLYMPUS.equals(order.getSelectedShippingMethod())) {
            AbstractOrderModel orderWithFreightCost = freightCostIntegrationService.fetchFreightCostForCart(order);

            for (final AbstractOrderEntryModel e : orderWithFreightCost.getEntries()) {
                if (null != e.getFreightPrice()) {
                    totalDeliveryCost += e.getFreightPrice();
                }
            }
        }
        return new PriceValue(order.getCurrency().getIsocode(), totalDeliveryCost, order.getNet());
    }

}