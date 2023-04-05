/**
 *
 */
package com.olympus.oca.commerce.facades.company;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;


public interface OcaB2BUnitFacade extends B2BUnitFacade
{

	void setDefaultB2BUnit(String b2bUnitId);

	void saveAccessType(AccountPreferencesWsDTO accessType);

	AddressDataList getUnitDeliveryAddresses(String b2bUnitId);

}
