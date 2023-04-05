/**
 *
 */
package com.olympus.oca.commerce.core.order.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;


public interface OcaOrderDao
{

	List<OrderEntryModel> getRecentlyOrderedProducts(final CustomerModel customer, final B2BUnitModel unit);

	SearchPageData<OrderEntryModel> getOrderList(CustomerModel customerModel, BaseStoreModel store,
												 OrderHistoryFiltersData orderHistoryFiltersData, List<B2BUnitModel> unit, PageableData pageableData, String query);

	SearchResult<OrderModel> getOrderHistoryFilters(final List<B2BUnitModel> units, String filterType);

}
