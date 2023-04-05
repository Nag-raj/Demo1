package com.olympus.oca.commerce.facades.company.impl;



import com.olympus.oca.commerce.core.b2bcustomer.OcaB2BCustomerService;
import com.olympus.oca.commerce.core.event.CustomerAccountVerificationEvent;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import com.olympus.oca.commerce.facades.company.OcaB2BCustomerFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import com.olympus.oca.commerce.integrations.ping.service.impl.DefaultPingIntegrationService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import com.olympus.oca.commerce.core.enums.InviteStatus;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.*;


public class DefaultOcaB2BCustomerFacade implements OcaB2BCustomerFacade {


    private OcaB2BCustomerService ocaB2BCustomerService;
    private DefaultPingIntegrationService pingIntegrationService;
    private Converter<CRMCustomerAccountInviteModel, CustomerActivationData> customerActivationDataConverter;

    private Converter<CustomerActivationData, B2BCustomerModel> ocaB2BCustomerReverseConverter;

    private ModelService modelService;

    private Converter<UserModel, CustomerData> customerConverter;
    private BaseSiteService baseSiteService;
    private CommonI18NService commonI18NService;
    private EventService eventService;
    @Override
    public CustomerActivationData getDetails(String customerId) {

        CustomerActivationData customerActivationData = customerActivationDataConverter.convert(getOcaB2BCustomerService().getDetails(customerId));
        return customerActivationData;
    }

    @Override
    public CRMCustomerAccountInviteModel getCustomerInvite(String customerId) {
        return ocaB2BCustomerService.getDetails(customerId);
    }


    @Override
    public B2BCustomerModel findExistingCustomer(String emailId) {
        return  ocaB2BCustomerService.findExistingCustomer(emailId);
    }

    @Override
    public void activateAccount(String emailId) throws BusinessException {
        B2BCustomerModel customer = findExistingCustomer(emailId);
        if (Objects.nonNull(customer)) {
            String pingUserId = customer.getPingUserId();
            if(Boolean.TRUE.equals(pingIntegrationService.executeActivation(pingUserId))){
                customer.setLoginDisabled(false);
                customer.setActivationStatus(true);
                ocaB2BCustomerService.saveCustomer(customer);
            }
        }
        }



    @Override
    public CustomerData createB2BCustomer(CustomerActivationData customerActivationData) throws BusinessException {
        if (Objects.nonNull(findExistingCustomer(customerActivationData.getEmailId()))) {
            throw new AmbiguousIdentifierException(
                    "ERROR_EMAIL_ID_ALREADY_EXISTS");
        } else {
            B2BCustomerModel ocaCustomerModel = getModelService().create(B2BCustomerModel.class);
            getOcaB2BCustomerReverseConverter().convert(customerActivationData, ocaCustomerModel);
            CRMCustomerAccountInviteModel accountInvite = ocaB2BCustomerService.getDetails(customerActivationData.getCustomerId());
            populateAddressesAndGroups(accountInvite, ocaCustomerModel);
            getModelService().saveAll(ocaCustomerModel);
            try {
                String pingUserId = pingIntegrationService.executeCustomerCreation(customerActivationData);
                    ocaCustomerModel.setPingUserId(pingUserId);
                    getModelService().save(ocaCustomerModel);
                    if(accountInvite != null) {
                        accountInvite.setInviteStatus(InviteStatus.PROCESSED_BY_CUSTOMER);
                    }
                    getModelService().save(accountInvite);
                    triggerVerificationEmail(ocaCustomerModel);
                    return customerConverter.convert(ocaCustomerModel);
            } catch (BusinessException exp) {
                getModelService().remove(ocaCustomerModel);
                throw new BusinessException("Customer with email id: " + customerActivationData.getEmailId() + " cannot be created,Please try again later");

            }

        }
    }
public void triggerVerificationEmail(B2BCustomerModel ocaCustomerModel){
    CustomerAccountVerificationEvent verificationInviteEvent = new CustomerAccountVerificationEvent();
    AbstractEvent commerceEvent = initializeCommerceEvent(verificationInviteEvent, ocaCustomerModel);
    eventService.publishEvent(commerceEvent);
}
    private void populateAddressesAndGroups(CRMCustomerAccountInviteModel accountInvite, B2BCustomerModel b2BCustomerModel) {
        if(null!=accountInvite) {
            List<AddressModel> clonedAddresses = new ArrayList<>();
            for (AddressModel address : accountInvite.getAddresses()) {
                AddressModel clonedAddress = (AddressModel)this.getModelService().clone(address);
                clonedAddress.setOwner(b2BCustomerModel);
                clonedAddresses.add(clonedAddress);
            }
            b2BCustomerModel.setAddresses(clonedAddresses);
            b2BCustomerModel.setGroups(new HashSet<PrincipalGroupModel>(accountInvite.getGroups()));
        }

    }
    private AbstractEvent initializeCommerceEvent(CustomerAccountVerificationEvent verificationEvent , B2BCustomerModel ocaCustomerModel){
        BaseSiteModel baseSiteModel = baseSiteService.getCurrentBaseSite();
        BaseStoreModel baseStoreModel = baseSiteModel.getStores().stream().findFirst().orElse(null);
        verificationEvent.setSite(baseSiteModel);
        verificationEvent.setBaseStore(baseStoreModel);
        verificationEvent.setLanguage(commonI18NService.getCurrentLanguage());
        verificationEvent.setCustomer(ocaCustomerModel);
        return verificationEvent;
    }

    public OcaB2BCustomerService getOcaB2BCustomerService() {
        return ocaB2BCustomerService;
    }

    public void setOcaB2BCustomerService(OcaB2BCustomerService ocaB2BCustomerService) {
        this.ocaB2BCustomerService = ocaB2BCustomerService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public Converter<CustomerActivationData, B2BCustomerModel> getOcaB2BCustomerReverseConverter() {
        return ocaB2BCustomerReverseConverter;
    }

    public void setOcaB2BCustomerReverseConverter(Converter<CustomerActivationData, B2BCustomerModel> ocaB2BCustomerReverseConverter) {
        this.ocaB2BCustomerReverseConverter = ocaB2BCustomerReverseConverter;
    }

    public DefaultPingIntegrationService getPingIntegrationService() {
        return pingIntegrationService;
    }

    public void setPingIntegrationService(DefaultPingIntegrationService pingIntegrationService) {
        this.pingIntegrationService = pingIntegrationService;
    }

    public Converter<UserModel, CustomerData> getCustomerConverter() {
        return customerConverter;
    }

    public void setCustomerConverter(Converter<UserModel, CustomerData> customerConverter) {
        this.customerConverter = customerConverter;
    }

    public Converter<CRMCustomerAccountInviteModel, CustomerActivationData> getCustomerActivationDataConverter() {
        return customerActivationDataConverter;
    }

    public void setCustomerActivationDataConverter(Converter<CRMCustomerAccountInviteModel, CustomerActivationData> customerActivationDataConverter) {
        this.customerActivationDataConverter = customerActivationDataConverter;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public EventService getEventService() {
        return eventService;
    }


    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
