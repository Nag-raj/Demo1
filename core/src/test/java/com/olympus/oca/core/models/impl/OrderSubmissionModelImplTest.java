package com.olympus.oca.core.models.impl;

import com.olympus.oca.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })

class OrderSubmissionModelImplTest {


    private final AemContext context = AppAemContext.newAemContext();

    private OrderSubmissionModelImpl orderSubmissionModel;

    @BeforeEach
    public void setup() throws Exception {
        context.load().json( "/com/olympus/oca/core/models/impl/OrderSubmission.json","/content");
        Resource myResource = context.resourceResolver()
                .getResource("/content");
        context.currentResource(myResource);
        orderSubmissionModel= context.request().adaptTo(OrderSubmissionModelImpl.class);
    }

    @Test
    void Test() {
        Assertions.assertEquals("Thank you", orderSubmissionModel.getOrderConfirmationTitle());
        Assertions.assertEquals("Your order is processing .you will recieve an email", orderSubmissionModel.getOrderConfirmationDescription());
        Assertions.assertEquals("Shipping Address", orderSubmissionModel.getShippingAddress());
        Assertions.assertEquals("Payment Method", orderSubmissionModel.getPaymentMethod());
        Assertions.assertEquals("6/3/2023", orderSubmissionModel.getDatePlaced());
        Assertions.assertEquals("41638742", orderSubmissionModel.getOrderNumber());
        Assertions.assertEquals("68742985", orderSubmissionModel.getPONumber());
        Assertions.assertEquals("20000", orderSubmissionModel.getTotalPrice());
        Assertions.assertEquals("Processing", orderSubmissionModel.getOrderStatusLabel());
        Assertions.assertEquals("Variance",orderSubmissionModel.getProduct());
        Assertions.assertEquals("Item#", orderSubmissionModel.getModelNumber());
        Assertions.assertEquals("Description", orderSubmissionModel.getDescription());
        Assertions.assertEquals("Quantity", orderSubmissionModel.getQuantity());
    }
}