/**
 *
 */
package com.olympus.oca.commerce.facades.payment;

import de.hybris.platform.commercefacades.payment.data.AccessTokenData;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.Optional;


public interface OcaPaymentFacade
{
	Optional<AccessTokenData> getAccessToken();

	void addCard(String accessToken) throws BusinessException;

}
