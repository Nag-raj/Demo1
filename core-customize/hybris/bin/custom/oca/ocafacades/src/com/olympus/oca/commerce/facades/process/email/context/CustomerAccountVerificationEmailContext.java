package com.olympus.oca.commerce.facades.process.email.context;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Base64;

public class CustomerAccountVerificationEmailContext extends AbstractEmailContext<StoreFrontCustomerProcessModel> {

    private static final String ENCODED_URL = "encodedUrl";
    private ConfigurationService configurationService;

    @Override
    public void init(final StoreFrontCustomerProcessModel crmCustomerAccountModel, final EmailPageModel emailPageModel)
    {
        super.init(crmCustomerAccountModel, emailPageModel);
        if (null != crmCustomerAccountModel.getCustomer()){
            final String url = configurationService.getConfiguration().getString(OcaCoreConstants.CUSTOMER_VERIFICATION_LINK);
            final String email = crmCustomerAccountModel.getCustomer().getUid();
            final String encodedUrl = Base64.getEncoder().encodeToString(email.getBytes());
            final String originalUrl = url + encodedUrl;
            put(ENCODED_URL, originalUrl);
        }
    }

    @Override
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
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
