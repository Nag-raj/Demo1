package com.olympus.oca.commerce.facades.salesforce.impl;

import com.olympus.oca.commerce.facades.salesforce.OcaSalesforceIntegrationFacade;
import com.olympus.oca.commerce.integrations.salesforce.service.impl.DefaultSalesforceIntegrationService;
import de.hybris.platform.commercefacades.order.data.SupportTeamContactDetailsData;
import de.hybris.platform.commercefacades.order.data.SupportTeamContactListData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultOcaSalesforceIntegrationFacade implements OcaSalesforceIntegrationFacade {

    private DefaultSalesforceIntegrationService salesforceIntegrationService;

    @Override
    public SupportTeamContactListData getSupportTeamContactDetails(String soldTo) {
        List<Object> userObjList = getSalesforceIntegrationService().getSupportTeamContactDetails(soldTo);
        SupportTeamContactListData teamContactListData = new SupportTeamContactListData();
        List<SupportTeamContactDetailsData> contactList = populateTeamContactDetails(userObjList);
        teamContactListData.setContactList(contactList);

        return teamContactListData;
    }


    protected List<SupportTeamContactDetailsData> populateTeamContactDetails(List<Object> userObjects){
        List<SupportTeamContactDetailsData> userList = new ArrayList<SupportTeamContactDetailsData>();
        for(Object userObj : userObjects){
            SupportTeamContactDetailsData userData = new SupportTeamContactDetailsData();
            Map<String,String> user = (Map)userObj;

            userData.setEmail(user.get("Email"));
            userData.setName(user.get("FirstName")+" "+user.get("LastName"));
            userData.setPhone(user.get("Phone"));
            userData.setDepartment(user.get("HSFE_Specialty__c"));
            userData.setRole(user.get("HSFE_Sub_Specialty__c"));
            userList.add(userData);
        }
        return userList;
    }

    public DefaultSalesforceIntegrationService getSalesforceIntegrationService() {
        return salesforceIntegrationService;
    }

    public void setSalesforceIntegrationService(DefaultSalesforceIntegrationService salesforceIntegrationService) {
        this.salesforceIntegrationService = salesforceIntegrationService;
    }
}