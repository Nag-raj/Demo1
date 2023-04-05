package com.olympus.oca.commerce.integrations.ping.service;


import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

public interface PingIntegrationService {

    String executeCustomerCreation(CustomerActivationData customerActivationData) throws BusinessException;

    Boolean executeActivation(String pingUserId) throws BusinessException;
}