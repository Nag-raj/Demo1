package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaOrderEntryPopulatorTest extends TestCase {

    @InjectMocks
    private OcaOrderEntryPricePopulator ocaOrderEntryPopulator;
    @Mock
    private PriceDataFactory priceDataFactory;

    final OrderEntryData target = new OrderEntryData();

    @Test
    public void testPopulate(){
        double contractPrice = 200;
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        OrderEntryData target = new OrderEntryData();
        AbstractOrderModel abstractOrderModel1 = new AbstractOrderModel();
        CurrencyModel currencyModel = new CurrencyModel();
        source.setContractPrice(contractPrice);
        source.setOrder(abstractOrderModel1);
        abstractOrderModel1.setCurrency(currencyModel);
        currencyModel.setSymbol("$");
        ProductModel productModel = new ProductModel();
        productModel.setCode("N123456");
        productModel.setModelNumber("12345");
        source.setProduct(productModel);
        ocaOrderEntryPopulator.populate(source,target);
        assertEquals(String.valueOf(contractPrice),target.getContractPrice());
        assertEquals("$200.00",target.getFormattedContractPrice());

    }

    @Test
    public void populateBasePriceNotNullTest(){
        double contractPrice = 200;
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        OrderEntryData target = new OrderEntryData();
        AbstractOrderModel abstractOrderModel1 = new AbstractOrderModel();
        CurrencyModel currencyModel = new CurrencyModel();
        source.setContractPrice(contractPrice);
        source.setOrder(abstractOrderModel1);
        source.setBasePrice(123.11);
        abstractOrderModel1.setCurrency(currencyModel);
        currencyModel.setSymbol("$");
        ProductModel productModel = new ProductModel();
        productModel.setCode("N123456");
        productModel.setModelNumber("12345");
        source.setProduct(productModel);
        ocaOrderEntryPopulator.populate(source,target);
        assertEquals(String.valueOf(contractPrice),target.getContractPrice());
        assertEquals("$200.00",target.getFormattedContractPrice());

    }
}