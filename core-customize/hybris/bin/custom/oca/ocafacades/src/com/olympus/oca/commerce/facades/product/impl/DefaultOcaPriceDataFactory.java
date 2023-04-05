package com.olympus.oca.commerce.facades.product.impl;

import com.olympus.oca.commerce.facades.util.OcaCommerceUtils;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.impl.DefaultPriceDataFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import org.springframework.util.Assert;

import java.math.BigDecimal;

public class DefaultOcaPriceDataFactory extends DefaultPriceDataFactory {
    @Override
    public PriceData create(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currency)
    {
        Assert.notNull(priceType, "Parameter priceType cannot be null.");
        Assert.notNull(value, "Parameter value cannot be null.");
        Assert.notNull(currency, "Parameter currency cannot be null.");

        final PriceData priceData = createPriceData();

        priceData.setPriceType(priceType);
        priceData.setValue(value);
        priceData.setCurrencyIso(currency.getIsocode());
        priceData.setFormattedValue(OcaCommerceUtils.getFormattedPrice(Double.valueOf(value.doubleValue()), currency));

        return priceData;
    }
}
