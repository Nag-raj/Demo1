package com.olympus.oca.commerce.facades.b2bcustomer.converters.populator;

import com.olympus.oca.commerce.core.enums.ConsentType;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commercefacades.user.data.CustomerAccountData;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.commercefacades.user.data.CustomerConsentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;


public class OcaB2BCustomerAccountInvitePopulator implements Populator<CRMCustomerAccountInviteModel, CustomerActivationData> {

    private EnumerationService enumerationService;


    @Override
    public void populate(CRMCustomerAccountInviteModel source, CustomerActivationData target) throws ConversionException {
        target.setCustomerId(source.getCustomerID());
        target.setName(source.getName());
        target.setEmailId(source.getEmail());
        target.setUid(source.getUid());
        target.setContactID(source.getContactID());
        if (null!=source.getTitle()) {
            target.setTitle(source.getTitle().getName());
        }
        final List<PrincipalGroupModel> groups = source.getGroups().stream().filter(group -> group instanceof B2BUnitModel).collect(Collectors.toList());
        final List<CustomerAccountData> accountDataList = new ArrayList();
        groups.stream().forEach(group-> {accountDataList.add(populateAccount( group,source.getDefaultB2BUnit()));
        });
        target.setAccountsInfo(accountDataList);
        target.setConsentInfo(populateConsentTypes());
    }
    protected CustomerAccountData populateAccount(final PrincipalGroupModel group , final B2BUnitModel defaultB2BUnit){
        CustomerAccountData customerAccountData = new CustomerAccountData();
        customerAccountData.setAccountId(group.getUid());
        if (null != defaultB2BUnit)
        {
            customerAccountData.setIsDefault(group.getUid().equals(defaultB2BUnit.getUid()));
        }
        customerAccountData.setMaskedAccountId("XXX"+ group.getUid().substring(3));
        return customerAccountData;
    }


    protected List<CustomerConsentData> populateConsentTypes(){
        final List<ConsentType> consentTypes = enumerationService.getEnumerationValues(ConsentType.class);
        final List<CustomerConsentData> consentlist = new ArrayList();
        consentTypes.stream().forEach(consentType ->consentlist.add(createConsentData(consentType)) );
        return consentlist;
    }

    private CustomerConsentData createConsentData(ConsentType consentType) {
        CustomerConsentData consent = new CustomerConsentData();
        consent.setConsentGiven(Boolean.FALSE);
        consent.setConsentType(consentType.getCode());
        return consent;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }
}