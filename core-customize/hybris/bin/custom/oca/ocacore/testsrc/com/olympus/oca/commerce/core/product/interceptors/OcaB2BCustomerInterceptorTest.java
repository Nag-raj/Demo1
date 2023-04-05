package com.olympus.oca.commerce.core.product.interceptors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.olympus.oca.commerce.core.enums.AccessType;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;

public class OcaB2BCustomerInterceptorTest {

    private OcaB2BCustomerInterceptor interceptor;
    private InterceptorContext context;

    @Before
    public void setUp() {
        interceptor = new OcaB2BCustomerInterceptor();
        context = mock(InterceptorContext.class);
    }

    @Test
    public void testOnPrepare() throws Exception {
        B2BCustomerModel customer = new B2BCustomerModel();
        interceptor.onPrepare(customer, context);
        AccountPreferencesModel accountPreferencesModel= customer.getAccountPreferences();
        assertEquals(AccessType.PLACE_ORDER_AND_CHECK_ORDERSTATUS, accountPreferencesModel.getAccessType());
    }

}
