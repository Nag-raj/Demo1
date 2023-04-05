package com.olympus.oca.commerce.facades.order.converters.populator;

import com.olympus.oca.commerce.facades.util.OcaCommerceUtils;
import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class OcaOrderSavingsPopulator extends AbstractOrderPopulator<AbstractOrderModel, AbstractOrderData> {

    private static final String MMMM_DD_YYYY = "MMMM dd',' yyyy";

    @Override
    public void populate(final AbstractOrderModel source, final AbstractOrderData target) {
        addContractSavings(source, target);

        target.setDeliveryCost(source.getDeliveryAddress() != null ? createPrice(source, source.getDeliveryCost()) : null);
        final SimpleDateFormat formatter = new SimpleDateFormat(MMMM_DD_YYYY);
        target.setCreatedDate(formatter.format(source.getCreationtime()));
    }

    protected void addContractSavings(final AbstractOrderModel source, final AbstractOrderData target) {
        double listPriceSubTotal = 0.00;
        double contractSavings = 0.00;

        for (AbstractOrderEntryModel entry : source.getEntries()) {
            if (entry != null && entry.getListPrice() != null) {
                listPriceSubTotal += entry.getListPrice() * entry.getQuantity();
            }
            if (Objects.nonNull(entry) && null != entry.getContractPrice() && entry.getContractPrice() > 0) {
                contractSavings += entry.getListPrice() * entry.getQuantity() - entry.getContractPrice() * entry.getQuantity();
            }
        }
        target.setListPriceSubTotal(listPriceSubTotal);
        target.setFormattedListPriceSubTotal(OcaCommerceUtils.getFormattedPrice(listPriceSubTotal, source.getCurrency()));
        target.setContractSavings(contractSavings);
        target.setFormattedContractSavings(OcaCommerceUtils.getFormattedPrice(contractSavings, source.getCurrency()));
    }
}
