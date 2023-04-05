package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaOrderEntryPricePopulatorTest {
    private static final String CURRENCY_SYMBOL = "$";
    private static final String FORMATTED_CONTRACT_PRICE = "$1,000.00";
    private static final String FORMATTED_LIST_PRICE = "$2,000.00";
    private static final String MODEL_NUMBER = "123-Test-Model-Number";
    private static final Double CONTRACT_PRICE = 1000d;
    private static final Double LIST_PRICE = 2000d;

    @Spy
    @InjectMocks
    private OcaOrderEntryPricePopulator classUnderTest;

    @Mock
    private AbstractOrderModel abstractOrder;
    @Mock
    private CurrencyModel currency;
    @Mock
    private ProductModel product;
    @Mock
    private AbstractOrderEntryModel source;
    @Mock
    private OrderEntryData target;

    @Before
    public void setUp() throws Exception {
        Mockito.when(currency.getSymbol()).thenReturn(CURRENCY_SYMBOL);
        Mockito.when(product.getModelNumber()).thenReturn(MODEL_NUMBER);
    }

    @Test
    public void testPopulate_sourceWithoutOrder() {
        classUnderTest.populate(source, target);
        Mockito.verify(source).getOrder();
        Mockito.verifyNoInteractions(target);
    }

    @Test
    public void testPopulate_sourceOrderNoCurrency() {
        Mockito.when(source.getOrder()).thenReturn(abstractOrder);
        classUnderTest.populate(source, target);
        Mockito.verify(source, Mockito.times(2)).getOrder();
        Mockito.verifyNoInteractions(target);
        Mockito.verify(abstractOrder).getCurrency();

    }

    @Test
    public void testPopulate_populateContractPrice() {
        Mockito.when(source.getOrder()).thenReturn(abstractOrder);
        Mockito.when(source.getProduct()).thenReturn(product);
        Mockito.when(abstractOrder.getCurrency()).thenReturn(currency);
        Mockito.when(source.getContractPrice()).thenReturn(CONTRACT_PRICE);

        classUnderTest.populate(source, target);

        Mockito.verify(source, Mockito.times(3)).getOrder();
        Mockito.verify(source).getProduct();
        Mockito.verify(product).getModelNumber();

        Mockito.verify(abstractOrder, Mockito.times(2)).getCurrency();
        Mockito.verify(target).setFormattedContractPrice(FORMATTED_CONTRACT_PRICE);
        Mockito.verify(target).setContractPrice(String.valueOf(CONTRACT_PRICE));
        Mockito.verify(target).setModelNumber(MODEL_NUMBER);
    }

    @Test
    public void testPopulate_populateListPrice() {
        Mockito.when(source.getOrder()).thenReturn(abstractOrder);
        Mockito.when(source.getProduct()).thenReturn(product);
        Mockito.when(abstractOrder.getCurrency()).thenReturn(currency);
        Mockito.when(source.getListPrice()).thenReturn(LIST_PRICE);

        classUnderTest.populate(source, target);

        Mockito.verify(source, Mockito.times(3)).getOrder();
        Mockito.verify(source).getProduct();
        Mockito.verify(product).getModelNumber();

        Mockito.verify(abstractOrder, Mockito.times(2)).getCurrency();
        Mockito.verify(target).setFormattedListPrice(FORMATTED_LIST_PRICE);
        Mockito.verify(target).setModelNumber(MODEL_NUMBER);
    }

}