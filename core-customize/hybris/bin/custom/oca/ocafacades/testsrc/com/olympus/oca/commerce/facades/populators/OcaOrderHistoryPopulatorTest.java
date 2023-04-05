package com.olympus.oca.commerce.facades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaOrderHistoryPopulatorTest {
    @InjectMocks
    private OcaOrderHistoryPopulator populator;

    @Mock
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @Mock
    private OrderEntryModel source;


    private OrderHistoryData target = new OrderHistoryData();

    @Mock
    private OrderEntryData entry;

    @Mock
    private OrderModel orderModel;
    @Mock
    private CurrencyModel currencyModel;
    @Mock
    private AbstractOrderModel abstractOrderModel;
    final Date date = new Date();
    List<AbstractOrderEntryModel> list = new ArrayList<>();
    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd',' yyyy");


    private static final Logger LOGGER = Logger.getLogger(OcaOrderHistoryPopulator.class);
    @Before
    public void setUp() {
        populator = new OcaOrderHistoryPopulator();
        when(source.getOrder()).thenReturn(orderModel);
        when(orderModel.getCode()).thenReturn("123");
        when(source.getOrder().getPurchaseOrderNumber()).thenReturn("PONUMBER");
        when(source.getOrder().getErpOrderNumber()).thenReturn("ERPNUMBER");
        when(source.getOrder().getEntries()).thenReturn(list);
        when(source.getCreationtime()).thenReturn(date);
        when(source.getOrder()).thenReturn(orderModel);
        when(orderModel.getTotalPrice()).thenReturn(50.0);
        when(orderModel.getCurrency()).thenReturn(currencyModel);
        when(currencyModel.getSymbol()).thenReturn("$");
    }

    @Test
    public void populateWithEntries() {

        populator.populate(source, target);
        assertEquals("123", target.getCode());
        assertEquals("PONUMBER", target.getPurchaseOrderNumber());
        assertEquals("ERPNUMBER", target.getErpOrderNumber());
        assertEquals(formatter.format(date),target.getCreationTime());
        assertEquals("$50.00",target.getTotalPrice());

    }

}