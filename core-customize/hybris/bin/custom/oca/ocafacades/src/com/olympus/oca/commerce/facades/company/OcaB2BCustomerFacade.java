package com.olympus.oca.commerce.facades.company;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;


import de.hybris.platform.commercefacades.user.data.CustomerData;

public interface OcaB2BCustomerFacade {


    public CustomerActivationData getDetails(String customerId);

    public CRMCustomerAccountInviteModel getCustomerInvite(String customerId);


    B2BCustomerModel findExistingCustomer(String emailId) ;
    void activateAccount(String emailId) throws  BusinessException;


    CustomerData createB2BCustomer(CustomerActivationData customerActivationData) throws BusinessException;

}