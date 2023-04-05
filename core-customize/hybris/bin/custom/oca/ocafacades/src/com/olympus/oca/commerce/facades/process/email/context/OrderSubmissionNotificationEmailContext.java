package com.olympus.oca.commerce.facades.process.email.context;

import com.olympus.oca.commerce.core.model.CustomerAccountInviteProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class OrderSubmissionNotificationEmailContext extends AbstractEmailContext<StoreFrontCustomerProcessModel> {
    @Override
    public void init(final StoreFrontCustomerProcessModel orderSubmissionNotificationProcess, final EmailPageModel emailPageModel) {
        super.init(orderSubmissionNotificationProcess, emailPageModel);
    }

    @Override
    protected BaseSiteModel getSite(StoreFrontCustomerProcessModel businessProcessModel) {
        return businessProcessModel.getSite();
    }

    @Override
    protected CustomerModel getCustomer(StoreFrontCustomerProcessModel businessProcessModel) {
        return businessProcessModel.getCustomer();
    }

    @Override
    protected LanguageModel getEmailLanguage(StoreFrontCustomerProcessModel businessProcessModel) {
        return businessProcessModel.getLanguage();
    }

}
