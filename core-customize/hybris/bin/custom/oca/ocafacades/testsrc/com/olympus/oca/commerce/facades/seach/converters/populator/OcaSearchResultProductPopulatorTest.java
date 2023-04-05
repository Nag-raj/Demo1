package com.olympus.oca.commerce.facades.seach.converters.populator;

import com.olympus.oca.commerce.facades.search.converters.populator.OcaSearchResultProductPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaSearchResultProductPopulatorTest {
    @InjectMocks
    private  OcaSearchResultProductPopulator searchResultPopulator;
    private List<SearchResultValueData> variants;
    @Mock
    private Converter<SearchResultValueData, ProductData> searchResultProductConverter;
    @Before
    public void setup(){
        searchResultPopulator = new OcaSearchResultProductPopulator();
        searchResultPopulator.setSearchResultProductConverter(searchResultProductConverter);
    }
    @Test
    public void testVisibilityFlags()
    {

        SearchResultValueData source = new SearchResultValueData();
        ProductData target = new ProductData();

        //case 1 - purchase enabled false
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("purchaseEnabled", Boolean.FALSE);
        source.setValues(map);

        searchResultPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isFalse();

        //case 2 - purchase enabled true
        map.put("purchaseEnabled", Boolean.TRUE);
        source.setValues(map);

        searchResultPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isTrue();

        //case 3 - purchase enabled null
        map.put("purchaseEnabled", null);
        source.setValues(map);

        searchResultPopulator.populate(source,target);

        assertThat(target.isPurchaseEnabled()).isFalse();


        map.put("baseProductName", "Base Product");
        map.put("nonPurchasableDisplayStatus", "Not Available");

        variants = new ArrayList<SearchResultValueData>();
        SearchResultValueData variant1 = new SearchResultValueData();
        variant1.setValues(map);
        variants.add(variant1);

        source.setValues(map);
        source.setVariants(variants);


        searchResultPopulator.populate(source,target);
        assertNotNull(target.getOtherVariants());
        assertEquals("Base Product", target.getBaseProductName());
        assertEquals("Not Available", target.getNonPurchasableDisplayStatus());
    }

}
