package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class OcaOrderPopulator implements Populator<OrderModel, OrderData> {
    @Override
    public void populate(OrderModel orderModel, OrderData orderData) throws ConversionException {
        if(OrderStatus.CREATED.equals(orderModel.getStatus())){
            orderData.setStatus(OrderStatus.PROCESSING);
        }
    }
}
