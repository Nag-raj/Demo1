package com.olympus.oca.commerce.core.CRMCustomer.dao.impl;

import com.olympus.oca.commerce.core.CRMCustomer.dao.CrmCustomerAccountInviteDao;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;

public class DefaultCrmCustomerAccountInviteDao implements CrmCustomerAccountInviteDao {
    protected static final String QUERY
            = "Select {pk} from {CRMCustomerAccountInvite as crmCustomerAccount JOIN InviteStatus as status ON {crmCustomerAccount.inviteStatus}={status.pk}} Where {status.code} IN ('SEND_INVITE','PROCESSED_BY_SAP_COMMERCE') AND ({crmCustomerAccount.inviteCount} <3 OR {crmCustomerAccount.inviteCount} IS NULL)";

    private final FlexibleSearchService flexibleSearchService;

    public DefaultCrmCustomerAccountInviteDao(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    @Override
    public List<CRMCustomerAccountInviteModel> getCustomerInvites() {
        final FlexibleSearchQuery flexibleQuery = getFlexibleQuery();
        final SearchResult<CRMCustomerAccountInviteModel> result = flexibleSearchService.search(flexibleQuery);
        final List<CRMCustomerAccountInviteModel> list = result.getResult();
        return list != null && !list.isEmpty() ? list : Collections.emptyList();
    }

    protected FlexibleSearchQuery getFlexibleQuery() {
        return new FlexibleSearchQuery(QUERY);
    }
}