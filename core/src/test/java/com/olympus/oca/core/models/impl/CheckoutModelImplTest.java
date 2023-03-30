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
class CheckoutModelImplTest {

    private final AemContext context = AppAemContext.newAemContext();

    private CheckoutModelImpl checkoutModel;

    @BeforeEach
    public void setup() throws Exception {
        context.load().json("/com/olympus/oca/core/models/impl/Checkout.json","/content");
        Resource myResource= context.resourceResolver().getResource("/content");
        context.currentResource(myResource);
        checkoutModel = context.request().adaptTo(CheckoutModelImpl.class);
    }
    @Test
    void test() {
        Assertions.assertEquals("this is disclaimer test",checkoutModel.getDisclaimerText());
        Assertions.assertEquals("this is terms ",checkoutModel.getTermsofuseText());
        Assertions.assertEquals("/content/olympus-customer-portal/us/en/my-olympus/reset-password.html",checkoutModel.getTermsofusePath());
        Assertions.assertEquals("/content/olympus-customer-portal/us/en/my-olympus/reset-password.html",checkoutModel.getCheckoutPagePath());
        Assertions.assertEquals("/content/olympus-customer-portal/us/en/my-olympus/reset-password.html",checkoutModel.getShopPagePath());
        Assertions.assertEquals("Payment Method",checkoutModel.getPaymentMethodText());
        Assertions.assertEquals("If you proceed without entering credit card information",checkoutModel.getPaymentMethodDesc());
        Assertions.assertEquals("Pay with Credit Card",checkoutModel.getPayWithCreditCard());
        Assertions.assertEquals("Pay via Invoice",checkoutModel.getPayViaInvoice());
    }

}
