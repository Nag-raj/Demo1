package com.olympus.oca.commerce.facades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaB2BUnitPopulatorTest {
    @InjectMocks
    private OcaB2BUnitPopulator ocaB2BUnitPopulator;
    @Mock
    private Converter<AddressModel, AddressData> addressConverter;
    @Before
    public void setUp() {
        ocaB2BUnitPopulator = new OcaB2BUnitPopulator();
        ocaB2BUnitPopulator.setAddressConverter(addressConverter);
    }

    @Test
    public void testPopulate() {
        B2BUnitModel source = new B2BUnitModel();
        AddressModel shippingAddressModel = new AddressModel();
        source.setShippingAddress(shippingAddressModel);

        List<AddressData> addresses = new ArrayList<>();
        AddressData addressData1 = new AddressData();
        addressData1.setId("shippingAddressId");
        AddressData addressData2 = new AddressData();
        addressData2.setId("anotherAddressId");
        addresses.add(addressData1);
        addresses.add(addressData2);
        B2BUnitData target = new B2BUnitData();
        target.setAddresses(addresses);

        AddressData shippingAddressData = new AddressData();
        shippingAddressData.setId("shippingAddressId");
        shippingAddressData.setDefaultAddress(true);

        Mockito.when(addressConverter.convert(shippingAddressModel)).thenReturn(shippingAddressData);
        ocaB2BUnitPopulator.populate(source, target);

        assertTrue(addressData1.isDefaultAddress());
        assertFalse(addressData2.isDefaultAddress());

    }
}