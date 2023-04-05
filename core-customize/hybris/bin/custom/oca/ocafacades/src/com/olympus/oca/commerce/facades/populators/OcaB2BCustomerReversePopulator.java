package com.olympus.oca.commerce.facades.populators;

import com.olympus.oca.commerce.core.b2bcustomer.OcaB2BCustomerService;
import com.olympus.oca.commerce.core.enums.ConsentType;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.core.model.B2BCustomerConsentModel;
import com.olympus.oca.commerce.core.model.B2BUnitNickNameModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.user.data.CustomerAccountData;
import de.hybris.platform.commercefacades.user.data.CustomerConsentData;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OcaB2BCustomerReversePopulator implements Populator<CustomerActivationData, B2BCustomerModel> {

    private B2BUnitService<B2BUnitModel, UserModel> b2BUnitService;

    private OcaB2BCustomerService ocaB2BCustomerService;

    @Override
    public void populate(CustomerActivationData customerActivationData, B2BCustomerModel b2BCustomerModel) throws ConversionException {
        b2BCustomerModel.setUid(customerActivationData.getEmailId());
        b2BCustomerModel.setEmail(customerActivationData.getEmailId());
        b2BCustomerModel.setLoginDisabled(true);
        b2BCustomerModel.setName(customerActivationData.getName());
        b2BCustomerModel.setCustomerID(customerActivationData.getCustomerId());
        b2BCustomerModel.setContactNo(customerActivationData.getContactID());
        populateGroups(customerActivationData,b2BCustomerModel);
        populateConsents(customerActivationData,b2BCustomerModel);
    }


    private void populateGroups(CustomerActivationData customerActivationData, B2BCustomerModel b2BCustomerModel) {
        Set<PrincipalGroupModel> principalGroups = new HashSet<>();
        List<B2BUnitNickNameModel> nickNameList = new ArrayList<B2BUnitNickNameModel>();
        AccountPreferencesModel accountPreferencesModel= b2BCustomerModel.getAccountPreferences();
        if(accountPreferencesModel==null){
            accountPreferencesModel=new AccountPreferencesModel();
            b2BCustomerModel.setAccountPreferences(accountPreferencesModel);
        }
        if(CollectionUtils.isNotEmpty(customerActivationData.getAccountsInfo())){
            for(CustomerAccountData accountData : customerActivationData.getAccountsInfo()){
                    B2BUnitNickNameModel nickNameModel = new B2BUnitNickNameModel();
                    nickNameModel.setB2bUnitId(accountData.getAccountId());
                    nickNameModel.setNickName(accountData.getAccountNickName());
                    nickNameList.add(nickNameModel);
                    if(accountData.getIsDefault()){
                        B2BUnitModel b2bUnit= getB2BUnitService().getUnitForUid(accountData.getAccountId());
                        b2BCustomerModel.setDefaultB2BUnit(b2bUnit);
                    }

                }
            }
        accountPreferencesModel.setB2BUnitNickNames(nickNameList);
        accountPreferencesModel.setJobCategory(customerActivationData.getJobCategory());
        accountPreferencesModel.setSpecialty(customerActivationData.getSpeciality());

    }

    private void populateConsents(CustomerActivationData customerActivationData, B2BCustomerModel b2BCustomerModel) {
        if(CollectionUtils.isNotEmpty(customerActivationData.getConsentInfo())){
            List<B2BCustomerConsentModel> consentList = new ArrayList<B2BCustomerConsentModel>();
            for(CustomerConsentData consentData : customerActivationData.getConsentInfo()){
                B2BCustomerConsentModel consent = new B2BCustomerConsentModel();
                consent.setConsentGiven(consentData.getConsentGiven());
                consent.setConsentType(ConsentType.valueOf(consentData.getConsentType()));
                consentList.add(consent);
            }

            b2BCustomerModel.getAccountPreferences().setConsentList(consentList);
        }
    }

    public B2BUnitService<B2BUnitModel, UserModel> getB2BUnitService() {
        return b2BUnitService;
    }

    public void setB2BUnitService(B2BUnitService<B2BUnitModel, UserModel> b2BUnitService) {
        this.b2BUnitService = b2BUnitService;
    }

    public OcaB2BCustomerService getOcaB2BCustomerService() {
        return ocaB2BCustomerService;
    }

    public void setOcaB2BCustomerService(OcaB2BCustomerService ocaB2BCustomerService) {
        this.ocaB2BCustomerService = ocaB2BCustomerService;
    }
}
