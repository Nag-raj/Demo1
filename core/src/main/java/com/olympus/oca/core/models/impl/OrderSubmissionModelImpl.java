package com.olympus.oca.core.models.impl;

import com.olympus.oca.core.models.OrderSubmissionModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = {OrderSubmissionModel.class },
        resourceType = "olympus/components/ordersubmission",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class OrderSubmissionModelImpl implements OrderSubmissionModel {

    @ValueMapValue
     public String orderConfirmationTitle;
    @ValueMapValue
    public String orderConfirmDescription;
    @ValueMapValue
    public String shippingAddress;
    @ValueMapValue
    public String paymentMethod;
    @ValueMapValue
    public String datePlaced;
    @ValueMapValue
    public String orderNumber;
    @ValueMapValue
    public String poNumber;
    @ValueMapValue
    public String totalPrice;
    @ValueMapValue
    public String orderStatusLabel;
    @ValueMapValue
    public String modelNumber;
    @ValueMapValue
    public String product;
    @ValueMapValue
    public String description;
    @ValueMapValue
    public String quantity;
    @Override
    public String getOrderConfirmationTitle() {
        return orderConfirmationTitle;
    }

    @Override
    public String getOrderConfirmationDescription() {
        return orderConfirmDescription;
    }

    @Override
    public String getShippingAddress() {
        return shippingAddress;
    }

    @Override
    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public String getDatePlaced() {
        return datePlaced;
    }

    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

    @Override
    public String getPONumber() {
        return poNumber;
    }

    @Override
    public String getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String getOrderStatusLabel() {
        return orderStatusLabel;
    }

    @Override
    public String getModelNumber() {
        return modelNumber;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getQuantity() {
        return quantity;
    }
}
