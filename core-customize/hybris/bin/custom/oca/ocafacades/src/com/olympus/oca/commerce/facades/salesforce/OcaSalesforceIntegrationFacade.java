package com.olympus.oca.commerce.facades.salesforce;

import de.hybris.platform.commercefacades.order.data.SupportTeamContactListData;

public interface OcaSalesforceIntegrationFacade {
    SupportTeamContactListData getSupportTeamContactDetails(String soldTO);
}
