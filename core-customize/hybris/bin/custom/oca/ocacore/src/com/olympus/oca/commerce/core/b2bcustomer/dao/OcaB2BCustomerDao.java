package com.olympus.oca.commerce.core.b2bcustomer.dao;


import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
public interface OcaB2BCustomerDao {

    public CRMCustomerAccountInviteModel getDetails(String customerId);

    public B2BCustomerModel findExistingCustomer(String emailId);
}

