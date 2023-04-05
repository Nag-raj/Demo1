package com.olympus.oca.commerce.facades.order.converters.populator;

import com.olympus.oca.commerce.facades.util.OcaCommerceUtils;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

public class OcaOrderEntryPricePopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {

    @Override
    public void populate(AbstractOrderEntryModel source, OrderEntryData target) throws ConversionException {
        if (null != source.getOrder() && null != source.getOrder().getCurrency()) {
            if (null != source.getContractPrice() && source.getContractPrice() > 0) {
                String formattedPrice = OcaCommerceUtils.getFormattedPrice(source.getContractPrice(), source.getOrder().getCurrency());
                target.setContractPrice(String.valueOf(source.getContractPrice()));
                target.setFormattedContractPrice(formattedPrice);
            }
            if (null != source.getListPrice() && source.getListPrice() > 0) {
                String formattedListPrice = OcaCommerceUtils.getFormattedPrice(source.getListPrice(), source.getOrder().getCurrency());
                target.setFormattedListPrice(formattedListPrice);
            }
            Optional.ofNullable(source.getProduct().getModelNumber()).ifPresent(target::setModelNumber);
        }
    }
}
