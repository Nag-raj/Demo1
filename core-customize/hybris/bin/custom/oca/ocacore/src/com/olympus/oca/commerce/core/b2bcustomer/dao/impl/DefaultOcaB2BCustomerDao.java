package com.olympus.oca.commerce.core.b2bcustomer.dao.impl;


import com.olympus.oca.commerce.core.b2bcustomer.dao.OcaB2BCustomerDao;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;



public class DefaultOcaB2BCustomerDao implements OcaB2BCustomerDao {


    private FlexibleSearchService flexibleSearchService;

    private static final String B2BCUSTOMERMODEL_QUERY = "SELECT {pk} from {B2BCustomer} WHERE {UID} = (?uid)";
    protected static final String CUSTOMER_QUERY = "SELECT {" + CRMCustomerAccountInviteModel.PK + "} FROM {"
            + CRMCustomerAccountInviteModel._TYPECODE + "} WHERE {" + CRMCustomerAccountInviteModel.CUSTOMERID +"} = ?customerId";

    @Override
    public CRMCustomerAccountInviteModel getDetails(String customerId) {
        final Map<String, Object> params = new HashMap<String, Object>();
        final StringBuffer query = new StringBuffer(CUSTOMER_QUERY);
        params.put("customerId",customerId);
        final SearchResult<CRMCustomerAccountInviteModel> result = getFlexibleSearchService().search(query.toString(), params);
        if(CollectionUtils.isEmpty(result.getResult())){
            throw new ModelNotFoundException("Unable to find CRMCustomerAccountInvite with given input parameters");
        }
        return CollectionUtils.isNotEmpty(result.getResult())?result.getResult().get(0):null;
    }





    @Override
    public B2BCustomerModel findExistingCustomer(String emailId)
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        final StringBuffer query = new StringBuffer(B2BCUSTOMERMODEL_QUERY);
        params.put("uid", emailId);
        final FlexibleSearchQuery flexiQuery = new FlexibleSearchQuery(query.toString(), params);
        final SearchResult<B2BCustomerModel> res = getFlexibleSearchService().search(flexiQuery);
        if (CollectionUtils.isNotEmpty(res.getResult())) {
            return res.getResult().get(0);
        } else {
            return null;
        }
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}

