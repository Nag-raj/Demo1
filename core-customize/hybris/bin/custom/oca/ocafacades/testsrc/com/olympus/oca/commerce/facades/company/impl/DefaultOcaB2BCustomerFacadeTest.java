package com.olympus.oca.commerce.facades.company.impl;

import com.olympus.oca.commerce.core.b2bcustomer.impl.DefaultOcaB2BCustomerService;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import com.olympus.oca.commerce.integrations.ping.service.impl.DefaultPingIntegrationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOcaB2BCustomerFacadeTest extends TestCase {
    @InjectMocks
    private DefaultOcaB2BCustomerFacade ocaB2BCustomerFacade;
    @Mock
    private DefaultOcaB2BCustomerService ocaB2BCustomerService;
    @Mock
    private DefaultPingIntegrationService pingIntegrationService;
    @Mock
    private Converter<CRMCustomerAccountInviteModel, CustomerActivationData> customerActivationDataConverter;
    @Mock
    private B2BCustomerModel customerModel;

    @Before
    public void setup(){
        ocaB2BCustomerFacade = new DefaultOcaB2BCustomerFacade();
        ocaB2BCustomerFacade.setOcaB2BCustomerService(ocaB2BCustomerService);
        ocaB2BCustomerFacade.setPingIntegrationService(pingIntegrationService);
        ocaB2BCustomerFacade.setCustomerActivationDataConverter(customerActivationDataConverter);
    }
    @Test
    public void testGetDetails(){
        String customerId = "customer1";
        CustomerActivationData customerActivationData =new CustomerActivationData();
        CRMCustomerAccountInviteModel customerAccountInviteModel = new CRMCustomerAccountInviteModel();
        Mockito.when(ocaB2BCustomerService.getDetails(customerId)).thenReturn(customerAccountInviteModel);
        Mockito.when(customerActivationDataConverter.convert(customerAccountInviteModel)).thenReturn(customerActivationData);
        Assert.assertEquals(customerActivationData,ocaB2BCustomerFacade.getDetails(customerId));
    }

    @Test
    public void testFetCustomerInvite(){
        String customerId = "customer1";
        CRMCustomerAccountInviteModel result = new CRMCustomerAccountInviteModel();
        Mockito.when(ocaB2BCustomerService.getDetails(customerId)).thenReturn(result);
        CRMCustomerAccountInviteModel actual = ocaB2BCustomerFacade.getCustomerInvite(customerId);
        Assert.assertEquals(result,actual);
    }

    @Test
    public void testFindExistingCustomer(){
        String emailId = "customer1@gmail.com";
        B2BCustomerModel result = new B2BCustomerModel();
        Mockito.when(ocaB2BCustomerService.findExistingCustomer(emailId)).thenReturn(result);
        B2BCustomerModel actual = ocaB2BCustomerFacade.findExistingCustomer(emailId);
        Assert.assertEquals(result,actual);
    }
    @Test
    public void testActivateAccount() throws BusinessException {

        String emailId = "customer1@gmail.com";
        String pingUserId = "pingUser1";
        Mockito.when(ocaB2BCustomerFacade.findExistingCustomer(emailId)).thenReturn(customerModel);
        Mockito.when(customerModel.getPingUserId()).thenReturn(pingUserId);
        Mockito.when(pingIntegrationService.executeActivation(pingUserId)).thenReturn(true);
        ocaB2BCustomerFacade.activateAccount(emailId);
        Assert.assertFalse(customerModel.isLoginDisabled());
        Assert.assertTrue(customerModel.isActivationStatus());
    }
}