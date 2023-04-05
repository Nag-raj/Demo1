package com.olympus.oca.commerce.core.b2bcustomer.impl;

import com.olympus.oca.commerce.core.b2bcustomer.OcaB2BCustomerService;
import com.olympus.oca.commerce.core.b2bcustomer.dao.impl.DefaultOcaB2BCustomerDao;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultOcaB2BCustomerService implements OcaB2BCustomerService {

    private ModelService modelService;


    private DefaultOcaB2BCustomerDao ocaB2BCustomerDao;


    @Override
    public void save(AccountPreferencesModel accountPreferencesModel, B2BCustomerModel customer) {
        getModelService().save(accountPreferencesModel);
        customer.setAccountPreferences(accountPreferencesModel);
        getModelService().save(customer);
    }

    @Override

    public CRMCustomerAccountInviteModel getDetails(String customerId) {
        return getOcaB2BCustomerDao().getDetails(customerId);
    }

    @Override
    public B2BCustomerModel findExistingCustomer(String emailId) {
        return getOcaB2BCustomerDao().findExistingCustomer(emailId);
    }


    @Override
    public void saveCustomer(B2BCustomerModel customer){
        getModelService().save(customer);
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
    public DefaultOcaB2BCustomerDao getOcaB2BCustomerDao() {
        return ocaB2BCustomerDao;
    }

    public void setOcaB2BCustomerDao(DefaultOcaB2BCustomerDao ocaB2BCustomerDao) {
        this.ocaB2BCustomerDao = ocaB2BCustomerDao;
    }


}
