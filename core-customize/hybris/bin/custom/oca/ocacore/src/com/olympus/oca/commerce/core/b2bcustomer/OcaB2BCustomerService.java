package com.olympus.oca.commerce.core.b2bcustomer;

import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.Set;

public interface OcaB2BCustomerService {

    void save(AccountPreferencesModel accountPreferencesModel, B2BCustomerModel customer);

    public CRMCustomerAccountInviteModel getDetails(String customerId);


    public B2BCustomerModel findExistingCustomer(String emailId);

    void saveCustomer(B2BCustomerModel customer);
    
}
