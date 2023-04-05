package com.olympus.oca.commerce.facades.product.converters.populator;

import com.olympus.oca.commerce.facades.util.OcaCommerceUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaVariantOptionDataPopulatorTest extends TestCase {
    @InjectMocks
    private OcaVariantOptionDataPopulator ocaVariantOptionDataPopulator;
    @Mock
    private CommonI18NService commonI18NService;



    private VariantOptionData variantOptionData;
    @Before
    public void setup(){
        ocaVariantOptionDataPopulator = new OcaVariantOptionDataPopulator();
        ocaVariantOptionDataPopulator.setCommonI18NService(commonI18NService);
    }

    @Test
    public void testPopulate(){
        VariantProductModel source = new VariantProductModel();
        VariantOptionData target = new VariantOptionData();
        final PriceData priceData = new PriceData();
        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setSymbol("$");
        priceData.setValue(BigDecimal.valueOf(100));
        priceData.setCurrencyIso("US");
        target.setPriceData(priceData);
        Mockito.when(commonI18NService.getCurrency("US")).thenReturn(currencyModel);
        ocaVariantOptionDataPopulator.populate(source,target);
        assertEquals("$100.00",target.getPriceData().getFormattedValue());
    }
}