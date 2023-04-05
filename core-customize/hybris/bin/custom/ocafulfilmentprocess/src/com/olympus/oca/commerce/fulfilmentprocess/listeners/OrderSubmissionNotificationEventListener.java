package com.olympus.oca.commerce.fulfilmentprocess.listeners;


import com.olympus.oca.commerce.fulfilmentprocess.event.OrderSubmissionNotificationEvent;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.log4j.Logger;

public class OrderSubmissionNotificationEventListener extends AbstractEventListener<OrderSubmissionNotificationEvent> {

    private static final Logger LOG = Logger.getLogger(OrderSubmissionNotificationEventListener.class);
    private BusinessProcessService businessProcessService;
    private ModelService modelService;

    protected SiteChannel getSiteChannelForEvent(OrderSubmissionNotificationEvent event) {
        final BaseSiteModel site = event.getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.site", site);
        return site.getChannel();
    }

    @Override
    protected void onEvent(OrderSubmissionNotificationEvent event) {
        final StoreFrontCustomerProcessModel orderSubmissionNotificationProcess = (StoreFrontCustomerProcessModel) getBusinessProcessService()
                .createProcess("orderSubmissionNotificationProcess-" + event.getCustomer() + "-" + System.currentTimeMillis(),
                        "orderSubmissionNotificationProcess");

        orderSubmissionNotificationProcess.setCustomer(event.getCustomer());
        orderSubmissionNotificationProcess.setSite(event.getSite());
        orderSubmissionNotificationProcess.setLanguage(event.getLanguage());
        orderSubmissionNotificationProcess.setCurrency(event.getCurrency());
        orderSubmissionNotificationProcess.setStore(event.getBaseStore());
        getModelService().save(orderSubmissionNotificationProcess);
        getBusinessProcessService().startProcess(orderSubmissionNotificationProcess);

        LOG.info("An email has been triggered for user::" + event.getCustomer().getContactEmail());
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
}
