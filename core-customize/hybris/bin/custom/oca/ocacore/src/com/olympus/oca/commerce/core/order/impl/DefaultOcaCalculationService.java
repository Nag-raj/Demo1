package com.olympus.oca.commerce.core.order.impl;

import com.olympus.oca.commerce.core.price.ContractPriceService;
import com.olympus.oca.commerce.integrations.contract.service.ContractPriceIntegrationService;
import com.olympus.oca.commerce.integrations.vertex.service.SalesTaxVertexIntegrationService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the order with either list or contract price.
 * The default calculation uses {{@link AbstractOrderEntryModel#getBasePrice()}} and in order to avoid overriding complex order calculation
 * service, we set the base price either to a contract price if available ({{@link AbstractOrderEntryModel#getContractPrice()}} or to the list
 * price {{@link AbstractOrderEntryModel#getListPrice()}}
 */
public class DefaultOcaCalculationService extends DefaultCalculationService {

    private final ContractPriceIntegrationService contractPriceIntegrationService;
    private final OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;
    private final SalesTaxVertexIntegrationService salesTaxVertexIntegrationService;
    private final ContractPriceService contractPriceService;

    public DefaultOcaCalculationService(ContractPriceIntegrationService contractPriceIntegrationService,
                                        OrderRequiresCalculationStrategy orderRequiresCalculationStrategy,
                                        SalesTaxVertexIntegrationService salesTaxVertexIntegrationService,
                                        ContractPriceService contractPriceService) {
        this.contractPriceIntegrationService = contractPriceIntegrationService;
        this.orderRequiresCalculationStrategy = orderRequiresCalculationStrategy;
        this.salesTaxVertexIntegrationService = salesTaxVertexIntegrationService;
        this.contractPriceService = contractPriceService;
    }

    @Override
    protected void resetAllValues(AbstractOrderEntryModel entry) throws CalculationException {
        validateContractPrice(entry);

        entry.setListPrice(getListPrice(entry).getValue());
        //
        entry.setBasePrice(findBasePrice(entry).getValue());
        entry.setSalesTax((double) 0);
        entry.setTaxValues(Collections.emptyList());
        entry.setDiscountValues(Collections.emptyList());
    }

    @Override
    protected PriceValue findBasePrice(AbstractOrderEntryModel entry) throws CalculationException {
        Assert.notNull(entry.getOrder().getCurrency(), "currency must be set on the order level.");

        // we need to consider move this whole logic into the find the base price strategy
        // if the contract price is available, we return it; otherwise, the list price will be returned
        Double listPrice = entry.getListPrice();
        // a safety belt if the list price is not yet initialized (e.g. findBasePrice is called not from resetAllValues)
        if (listPrice == null) {
            listPrice = getListPrice(entry).getValue();
        }

        double basePrice = entry.getContractPrice() != null && entry.getContractPrice() > 0 ? entry.getContractPrice() : listPrice;

        return new PriceValue(entry.getOrder().getCurrency().getIsocode(), basePrice, false);
    }

    @Override
    protected double calculateTotalTaxValues(AbstractOrderModel order, boolean recalculate, int digits, double taxAdjustmentFactor,
                                             Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) {
        if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order)) {
            if (order.getUnit() != null
                && (order.getPaymentAddress() != null || CollectionUtils.isNotEmpty(order.getUnit().getBillingAddresses()))
                && order.getDeliveryAddress() != null) {
                salesTaxVertexIntegrationService.fetchSalesTaxForCart(order);
            }
        }
        return order.getTotalTax();
    }

    @Override
    protected double getTaxCorrectionFactor(Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap, double subtotal, double total,
                                            AbstractOrderModel order) {
        return 1.0;
    }

    protected void validateContractPrice(AbstractOrderEntryModel entry) {
        Assert.notNull(entry, "order entry must not be null");
        if (contractPriceService.isContractPriceExpired(entry.getOrder().getContractPriceFetchedAt())) {
            contractPriceIntegrationService.fetchContractPriceForCart(entry.getOrder());
        }
        if (contractPriceService.isContractPriceExpired(entry.getContractPriceFetchedAt())) {
            contractPriceIntegrationService.fetchContractPriceForCartEntry(entry);
        }
    }

    protected PriceValue getListPrice(AbstractOrderEntryModel entry) throws CalculationException {
        return super.findBasePrice(entry);
    }
}
