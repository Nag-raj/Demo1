package com.olympus.oca.commerce.core.event;

import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

public class CustomerAccountInviteEvent extends AbstractCommerceUserEvent<BaseSiteModel> {
    private CRMCustomerAccountInviteModel accountInvite;

    public CustomerAccountInviteEvent(final CRMCustomerAccountInviteModel accountInvite) {
        this.accountInvite = accountInvite;
    }

    public CRMCustomerAccountInviteModel getAccountInvite() {
        return accountInvite;
    }

    public void setAccountInvite(final CRMCustomerAccountInviteModel accountInvite) {
        this.accountInvite = accountInvite;
    }

}
