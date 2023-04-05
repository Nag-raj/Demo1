package com.olympus.oca.commerce.core.CRMCustomer.dao;

import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;

import java.util.List;

public interface CrmCustomerAccountInviteDao {
    List<CRMCustomerAccountInviteModel> getCustomerInvites();
}

