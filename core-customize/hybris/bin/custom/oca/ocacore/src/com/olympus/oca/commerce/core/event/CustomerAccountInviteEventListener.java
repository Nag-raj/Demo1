package com.olympus.oca.commerce.core.event;

import com.olympus.oca.commerce.core.model.CustomerAccountInviteProcessModel;
import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;

public class CustomerAccountInviteEventListener extends AbstractAcceleratorSiteEventListener<CustomerAccountInviteEvent> {
    private static final Logger LOG = Logger.getLogger(CustomerAccountInviteEventListener.class);

    private final BusinessProcessService businessProcessService;
    private final ModelService modelService;

    public CustomerAccountInviteEventListener(BusinessProcessService businessProcessService, ModelService modelService) {
        this.businessProcessService = businessProcessService;
        this.modelService = modelService;
    }

    @Override
    protected void onSiteEvent(final CustomerAccountInviteEvent event) {
        final CustomerAccountInviteProcessModel customerAccountInviteProcess = getConfiguredBusinessProcessForEvent(event);

        modelService.save(customerAccountInviteProcess);
        businessProcessService.startProcess(customerAccountInviteProcess);

        LOG.info("An email has been triggered for user::" + event.getAccountInvite().getEmail());
    }

    @Override
    protected SiteChannel getSiteChannelForEvent(final CustomerAccountInviteEvent event) {
        final BaseSiteModel site = event.getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.site", site);
        return site.getChannel();
    }

    protected CustomerAccountInviteProcessModel getConfiguredBusinessProcessForEvent(CustomerAccountInviteEvent event) {
        CustomerAccountInviteProcessModel process = createBusinessProcessForEvent(event);
        process.setCustomerAccount(event.getAccountInvite());
        process.setSite(event.getSite());
        process.setLanguage(event.getLanguage());
        process.setCurrency(event.getCurrency());
        process.setStore(event.getBaseStore());
        return process;
    }

    protected CustomerAccountInviteProcessModel createBusinessProcessForEvent(CustomerAccountInviteEvent event) {
        return businessProcessService.createProcess("customerAccountActivationProcess-" + event.getAccountInvite() + "-" + System.currentTimeMillis(),
                                                    "customerAccountActivationProcess");
    }
}
