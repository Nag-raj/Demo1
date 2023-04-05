package com.olympus.oca.commerce.core.order.impl;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.order.OcaOrderService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.ErrorConstants.B2B_UNIT_NOT_FOUND;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


import com.olympus.oca.commerce.core.order.dao.OcaOrderDao;


public class DefaultOcaOrderService extends DefaultOrderService implements OcaOrderService
{
	private OcaOrderDao ocaOrderDao;
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	private UserService userService;
	private ConfigurationService configurationService;

	@Override
	public List<OrderEntryModel> getRecentlyOrderedProducts(final CustomerModel customer, final B2BUnitModel unit)
	{
		final int productCount = getConfigurationService().getConfiguration()
				.getInt(OcaCoreConstants.RECENTLY_ORDERED_PRODUCT_COUNT, 4);
		final List<ProductModel> products = new ArrayList();
		final List<OrderEntryModel> entries = new ArrayList();
		getOcaOrderDao().getRecentlyOrderedProducts(customer, unit).forEach((entry) -> {
			if (products.size() < productCount && !products.contains(entry.getProduct()))
			{
				products.add(entry.getProduct());
				entries.add(entry);
			}
		});
		return entries;
	}

	public SearchPageData<OrderEntryModel> getOrderList(final B2BCustomerModel customerModel, final BaseStoreModel store,
														final OrderHistoryFiltersData orderHistoryFiltersData, final PageableData pageableData, final String query)
	{
		validateParameterNotNull(customerModel, "Customer model cannot be null");
		validateParameterNotNull(store, "Store must not be null");
		validateParameterNotNull(pageableData, "PageableData must not be null");
		List<B2BUnitModel> b2bModelList = null;

		b2bModelList = validateB2BUnitsForCurrentUser(customerModel, orderHistoryFiltersData);
		return getOcaOrderDao().getOrderList(customerModel, store, orderHistoryFiltersData, b2bModelList, pageableData, query);
	}

	@Override
	public SearchResult getOrderHistoryFilters(final B2BCustomerModel currentCustomer, OrderHistoryFiltersData orderHistoryFiltersData) {
		List<B2BUnitModel> b2bModelList = validateB2BUnitsForCurrentUser(currentCustomer, orderHistoryFiltersData);
		//fetch units filter data
		return getOcaOrderDao().getOrderHistoryFilters(b2bModelList, OcaCoreConstants.ADDRESS_FILTER_TYPE);
	}

	@Override
	public SearchResult getSoldTosForOrderHistoryFilters(final B2BCustomerModel currentCustomer)
	{
		List<B2BUnitModel> b2bModelList = validateB2BUnitsForCurrentUser(currentCustomer, new OrderHistoryFiltersData());
		return getOcaOrderDao().getOrderHistoryFilters(b2bModelList, StringUtils.EMPTY);
	}
	private List<B2BUnitModel> validateB2BUnitsForCurrentUser(final B2BCustomerModel currentCustomer,
															 final OrderHistoryFiltersData orderHistoryFiltersData)
	{
		final List<B2BUnitModel> currentCustomerUnits = getCustomerUnits(currentCustomer);
		if (orderHistoryFiltersData.getAccountNumber()!= null && orderHistoryFiltersData.getAccountNumber().length() != 0)
		{
			return getB2BUnitFilters(orderHistoryFiltersData, currentCustomerUnits);
		}else{
			return currentCustomerUnits;
		}
	}
	private List<B2BUnitModel> getB2BUnitFilters(OrderHistoryFiltersData orderHistoryFiltersData, List<B2BUnitModel> currentCustomerUnits) {
		List<B2BUnitModel> b2bModelList = new ArrayList<>();
		final List<String> filterDataAccountNumber =
				Arrays.asList(orderHistoryFiltersData.getAccountNumber().split(","));
		filterDataAccountNumber.stream().forEach(filterDataUnit ->{
			AtomicBoolean accountExists = new AtomicBoolean(false);
			currentCustomerUnits.stream().forEach(unit-> {
				if(unit.getUid().equalsIgnoreCase(filterDataUnit)) {
					b2bModelList.add(unit);
					accountExists.set(true);
				}
			});
			if(!accountExists.get())
			{
				throw new ModelNotFoundException(B2B_UNIT_NOT_FOUND);
			}
		});
		return b2bModelList;
	}

	private List<B2BUnitModel> getCustomerUnits(B2BCustomerModel currentCustomer) {
		if(currentCustomer == null || CollectionUtils.isEmpty(currentCustomer.getGroups())) {
			return Collections.emptyList();
		}

		List<B2BUnitModel> list = new ArrayList<>();
		for (PrincipalGroupModel principalGroupModel : currentCustomer.getGroups()) {
			if (principalGroupModel instanceof B2BUnitModel) {
				list.add((B2BUnitModel) principalGroupModel);
			}
		}
		return list;
	}

	public OcaOrderDao getOcaOrderDao()
	{
		return ocaOrderDao;
	}

	public void setOcaOrderDao(final OcaOrderDao ocaOrderDao)
	{
		this.ocaOrderDao = ocaOrderDao;
	}

	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
