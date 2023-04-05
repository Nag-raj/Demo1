/**
 *
 */
package com.olympus.oca.commerce.facades.company.impl;

import com.olympus.oca.commerce.core.b2bcustomer.OcaB2BCustomerService;
import com.olympus.oca.commerce.core.b2bunit.OcaB2BUnitService;
import com.olympus.oca.commerce.core.enums.AccessType;
import com.olympus.oca.commerce.core.model.AccountPreferencesModel;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUnitFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import com.olympus.oca.commerce.facades.company.OcaB2BUnitFacade;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public  class DefaultOcaB2BUnitFacade extends DefaultB2BUnitFacade implements OcaB2BUnitFacade
{
	private UserService userService;
	private OcaB2BCustomerService ocaB2BCustomerService;
	private OcaB2BUnitService ocaB2BUnitService;
	private Converter<AddressModel, AddressData> addressConverter;

	@Override
	public void setDefaultB2BUnit(final String b2bUnitId)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		customer.setDefaultB2BUnit(getB2BUnitService().getUnitForUid(b2bUnitId));
		getModelService().save(customer);
	}

	@Override
	public void saveAccessType(AccountPreferencesWsDTO accountPreferences) {
		final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
		if(Objects.nonNull(customer)){
			AccountPreferencesModel accountPreferencesModel = (Objects.nonNull(customer.getAccountPreferences())) ?
					customer.getAccountPreferences() : getModelService().create(AccountPreferencesModel.class);
			if((null==accountPreferencesModel.getAccessType())
					|| (Objects.nonNull((accountPreferencesModel.getAccessType().getCode()))
					&& !accountPreferences.getAccessType().equals(accountPreferencesModel.getAccessType().getCode()))) {
				accountPreferencesModel.setAccessType(AccessType.valueOf(accountPreferences.getAccessType()));
				ocaB2BCustomerService.save(accountPreferencesModel,customer);
			}
		}
	}

	@Override
	public AddressDataList getUnitDeliveryAddresses(String orgUnitId) {
		B2BUnitModel soldTo = getB2BUnitService().getUnitForUid(orgUnitId);
		if(Objects.nonNull(soldTo)) {
			List<AddressData> deliveryAddressList = addressConverter.convertAll(ocaB2BUnitService.getSoldToDeliveryAddresses(soldTo));
			final AddressDataList addressDataList = new AddressDataList();
			addressDataList.setAddresses(deliveryAddressList);
			return addressDataList;
		}
		return null;
	}

	@Override
	public UserService getUserService()
	{
		return userService;
	}

	public void setAddressConverter(Converter<AddressModel, AddressData> addressConverter) {
		this.addressConverter = addressConverter;
	}

	public void setOcaB2BCustomerService(OcaB2BCustomerService ocaB2BCustomerService) {
		this.ocaB2BCustomerService = ocaB2BCustomerService;
	}

	public void setOcaB2BUnitService(OcaB2BUnitService ocaB2BUnitService) {
		this.ocaB2BUnitService = ocaB2BUnitService;
	}

	@Override
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
