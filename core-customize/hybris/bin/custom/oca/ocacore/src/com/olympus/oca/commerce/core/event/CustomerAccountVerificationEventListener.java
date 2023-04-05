package com.olympus.oca.commerce.core.event;



import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;

public class CustomerAccountVerificationEventListener extends AbstractEventListener<CustomerAccountVerificationEvent> {

    private static final Logger LOG = Logger.getLogger(CustomerAccountVerificationEventListener.class);
    private BusinessProcessService businessProcessService;
    private ModelService modelService;


    protected SiteChannel getSiteChannelForEvent(CustomerAccountVerificationEvent event) {
        final BaseSiteModel site = event.getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.site", site);
        return site.getChannel();
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    protected void onEvent(CustomerAccountVerificationEvent event) {
        final StoreFrontCustomerProcessModel customerAccountVerification =(StoreFrontCustomerProcessModel) getBusinessProcessService().
                createProcess("customerAccountVerificationProcess-" +event.getCustomer() + "-" + System.currentTimeMillis(),
                        "customerAccountVerificationProcess");
        customerAccountVerification.setCustomer(event.getCustomer());
        customerAccountVerification.setSite(event.getSite());
        customerAccountVerification.setLanguage(event.getLanguage());
        customerAccountVerification.setCurrency(event.getCurrency());
        customerAccountVerification.setStore(event.getBaseStore());
        modelService.save(customerAccountVerification);
        getBusinessProcessService().startProcess(customerAccountVerification);
        LOG.info("An email has been triggered for user::" + event.getCustomer().getUid());
    }
}
