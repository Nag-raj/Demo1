package com.olympus.oca.core.models.impl;

import com.olympus.oca.core.models.AccountPopupModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = {AccountPopupModel.class },
        resourceType = AccountPopupModelImpl.RESOURCE_TYPE_CONTAINER,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccountPopupModelImpl implements AccountPopupModel {

    protected static final String RESOURCE_TYPE_CONTAINER = "olympus/components/accountPopup";

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String accountsDropDown;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getAccountsDropDown() {
        return accountsDropDown;
    }
}
