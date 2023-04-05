package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BUnitPopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Objects;

public class OcaB2BUnitPopulator implements Populator<B2BUnitModel, B2BUnitData> {


    private Converter<AddressModel, AddressData> addressConverter;

    @Override
    public void populate(B2BUnitModel source, B2BUnitData target) {
        if (CollectionUtils.isNotEmpty(target.getAddresses()))
        {
            if(Objects.nonNull(source.getShippingAddress())){
               AddressData shippingAddressData = getAddressConverter().convert(source.getShippingAddress());
            for(AddressData addressData : target.getAddresses()) {
                if(shippingAddressData.getId().equalsIgnoreCase(addressData.getId())){
                    addressData.setDefaultAddress(Boolean.TRUE);
                }
            }
            }
        }
    }

    protected Converter<AddressModel, AddressData> getAddressConverter()
    {
        return addressConverter;
    }

    @Required
    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
    {
        this.addressConverter = addressConverter;
    }
}
