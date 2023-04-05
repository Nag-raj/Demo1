package com.olympus.oca.commerce.core.b2bcustomer.impl;

import com.olympus.oca.commerce.core.b2bcustomer.dao.impl.DefaultOcaB2BCustomerDao;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.bootstrap.annotations.UnitTest;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOcaB2BCustomerServiceTest
{

    @InjectMocks
    DefaultOcaB2BCustomerService customerService;
    @Mock
    private ModelService modelService;
    @Mock
    private DefaultOcaB2BCustomerDao ocaB2BCustomerDao;


    public void setup(){
        customerService=new DefaultOcaB2BCustomerService();
        customerService.setModelService(modelService);
        customerService.setOcaB2BCustomerDao(ocaB2BCustomerDao);
    }
    @Test
    public void testSave()
    {
        AccountPreferencesModel accountPreferences = new AccountPreferencesModel();
        B2BCustomerModel b2BCustomer = new B2BCustomerModel();

        customerService.save(accountPreferences,b2BCustomer);
        assertEquals(accountPreferences, b2BCustomer.getAccountPreferences());
    }

    @Test
    public void testGetDetails(){
        String customerId = "customer1";
        CRMCustomerAccountInviteModel result= new CRMCustomerAccountInviteModel();
        Mockito.when(ocaB2BCustomerDao.getDetails(customerId)).thenReturn(result);
        CRMCustomerAccountInviteModel actual = ocaB2BCustomerDao.getDetails(customerId);
        Assert.assertEquals(result,actual);
    }
    @Test
    public void testFindExistingCustomer(){
        String emailId = "customer1@gmail.com";
        B2BCustomerModel result= new B2BCustomerModel();
        Mockito.when(ocaB2BCustomerDao.findExistingCustomer(emailId)).thenReturn(result);
        B2BCustomerModel actual = ocaB2BCustomerDao.findExistingCustomer(emailId);
        Assert.assertEquals(result,actual);
    }
}

