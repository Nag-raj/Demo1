package com.olympus.oca.commerce.facades.product.converters.populator;

import com.olympus.oca.commerce.facades.search.converters.populator.OcaSearchResultProductPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.variants.model.VariantProductModel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class OcaProductPopulatorTest {

    private final OcaProductPopulator productPopulator = new OcaProductPopulator();
    @Mock
    private LocaleProvider localeProvider;

    @Test
    public void testVisibilityFlags()
    {
        ProductModel source = new ProductModel();
        localeProvider = Mockito.mock(LocaleProvider.class);
        final Locale locale = new Locale("EN");

        ProductData target = new ProductData();
        ((ItemModelContextImpl) (ModelContextUtils.getItemModelContext(source))).setLocaleProvider(localeProvider);
        Mockito.when(localeProvider.getCurrentDataLocale()).thenReturn(locale);

        //case 1 - scenario false
        source.setSearchEnabled(false);
        source.setPurchaseEnabled(false);

        productPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isFalse();
        assertThat(target.isSearchEnabled()).isFalse();

        //case 2 - scenario true
        source.setSearchEnabled(true);
        source.setPurchaseEnabled(true);

        productPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isTrue();
        assertThat(target.isSearchEnabled()).isTrue();
        source.setDescription("product description");
        final VariantProductModel variantProduct = new VariantProductModel();
        variantProduct.setBaseProduct(source);
        source.setName("base product");
        target.setBaseProductName(variantProduct.getBaseProduct().getName());
        productPopulator.populate(source,target);
        Assert.assertEquals("product description",target.getDescription());
        Assert.assertEquals("base product",target.getBaseProductName());

    }

}
