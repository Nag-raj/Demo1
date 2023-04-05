package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaOrderSavingsPopulatorTest {

    @InjectMocks
    private OcaOrderSavingsPopulator orderPopulator;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private AddressModel addressModel;

    @Test
    public void testAddContractSavings() {
        AbstractOrderModel source = new AbstractOrderModel();
        AbstractOrderData target = new AbstractOrderData();
        CurrencyModel currencyModel = new CurrencyModel();
        AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
        entry1.setListPrice(10.0);
        entry1.setQuantity(2L);
        entry1.setContractPrice(5.0);

        AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
        entry2.setListPrice(20.0);
        entry2.setQuantity(1L);
        entry2.setContractPrice(15.0);

        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        entries.add(entry1);
        entries.add(entry2);
        source.setEntries(entries);
        source.setCurrency(currencyModel);
        currencyModel.setSymbol("$");

        source.setDeliveryCost(10.0);
        source.setDeliveryAddress(addressModel);
        source.setCurrency(currencyModel);
        source.setCreationtime(new Date());

        orderPopulator.populate(source, target);

        assertEquals(Double.valueOf(40.0), target.getListPriceSubTotal());
        assertEquals("$40.00",target.getFormattedListPriceSubTotal());
        assertEquals(Double.valueOf(15.0), target.getContractSavings());
        assertEquals("$15.00",target.getFormattedContractSavings());
    }

}