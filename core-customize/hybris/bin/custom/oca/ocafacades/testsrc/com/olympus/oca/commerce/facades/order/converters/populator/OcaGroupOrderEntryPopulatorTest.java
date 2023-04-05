package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import junit.framework.TestCase;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaGroupOrderEntryPopulatorTest extends TestCase {

    private OcaGroupOrderEntryPopulator populator;

    @Mock
    private AbstractOrderModel source;


    @Before
    public void setUp() {

        populator = new OcaGroupOrderEntryPopulator();
    }

    @Test
    public void testPopulate()  {
        List<OrderEntryData> entries = new ArrayList<>();
        AbstractOrderData target = new AbstractOrderData();
        OrderEntryData entry1 = new OrderEntryData();
        ProductData product1 = new ProductData();
        product1.setBaseProduct("base product");
        entry1.setProduct(product1);
        entries.add(entry1);
        target.setEntries(entries);

        populator.populate(source, target);

        List<OrderEntryData> groupedEntries = target.getEntries();
        assertNotNull(groupedEntries);
        assertEquals(1, groupedEntries.size());
    }
@Test
public void shouldPopulateEntries()  {

    List<OrderEntryData> entries = new ArrayList<>();
    AbstractOrderData target = new AbstractOrderData();
    OrderEntryData entry1 = new OrderEntryData();
    ProductData product1 = new ProductData();
    product1.setBaseProduct("baseProduct1");
    entry1.setProduct(product1);
    entries.add(entry1);

    OrderEntryData entry2 = new OrderEntryData();
    ProductData product2 = new ProductData();
    product2.setBaseProduct("baseProduct2");
    entry2.setProduct(product2);
    entries.add(entry2);

    OrderEntryData entry3 = new OrderEntryData();
    entry3.setProduct(product1);
    entries.add(entry3);
    target.setEntries(entries);

    List<OrderEntryData> variantsOfEntry1 = new ArrayList<>();
    variantsOfEntry1.add(entry1);
    variantsOfEntry1.add(entry3);
    entry1.setOtherVariants(variantsOfEntry1);


    populator.populate(source, target);


    List<OrderEntryData> result = target.getEntries();
    assertEquals(2, result.size());
    OrderEntryData resultEntry1 = result.get(0);
    assertEquals("baseProduct1", resultEntry1.getProduct().getBaseProduct());
    assertEquals(3, resultEntry1.getOtherVariants().size());
    assertEquals(entry1, resultEntry1);
    assertEquals(entry3, resultEntry1.getOtherVariants().get(1));
    OrderEntryData resultEntry2 = result.get(1);
    assertEquals("baseProduct2", resultEntry2.getProduct().getBaseProduct());
    assertEquals(entry2, resultEntry2);
}
}














