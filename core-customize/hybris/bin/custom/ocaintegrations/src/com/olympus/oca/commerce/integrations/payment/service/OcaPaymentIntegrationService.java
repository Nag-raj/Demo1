package com.olympus.oca.commerce.integrations.payment.service;

import de.hybris.platform.commercefacades.payment.data.AccessTokenData;

import java.util.Optional;

public interface OcaPaymentIntegrationService {
    Optional<AccessTokenData> getAccessToken();
    String getCardDetails(String accessToken);
}
