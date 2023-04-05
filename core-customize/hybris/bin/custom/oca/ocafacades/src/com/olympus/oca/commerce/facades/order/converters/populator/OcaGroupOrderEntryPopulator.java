package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.GroupOrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.*;

public class OcaGroupOrderEntryPopulator extends GroupOrderEntryPopulator {

    public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException {
        target.setEntries(groupEntries(target));
    }

    private List<OrderEntryData> groupEntries(AbstractOrderData target) {
        final Map<String, OrderEntryData> baseProductMap = new LinkedHashMap<>();

        for (final OrderEntryData entry : target.getEntries()) {
            final String baseProduct = entry.getProduct().getBaseProduct();

            if (baseProductMap.containsKey(baseProduct)) {
                final OrderEntryData existingCartEntry = baseProductMap.get(baseProduct);

                if (existingCartEntry.getOtherVariants() == null) {
                    existingCartEntry.setOtherVariants(new ArrayList<>());
                }
                existingCartEntry.getOtherVariants().add(entry);
            } else {
                baseProductMap.put(baseProduct, entry);
            }
        }
        return new ArrayList<>(baseProductMap.values());
    }
}
