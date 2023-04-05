package com.olympus.oca.commerce.core.order;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersListData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;
import java.util.Set;


public interface OcaOrderService
{

	public List<OrderEntryModel> getRecentlyOrderedProducts(final CustomerModel customer, final B2BUnitModel unit);

	SearchPageData<OrderEntryModel> getOrderList(B2BCustomerModel customerModel, BaseStoreModel store,
												 OrderHistoryFiltersData orderHistoryFiltersData, PageableData pageableData, String query);

	SearchResult getOrderHistoryFilters( final B2BCustomerModel currentCustomer, final OrderHistoryFiltersData orderHistoryFiltersData);

	SearchResult getSoldTosForOrderHistoryFilters(final B2BCustomerModel currentCustomer);
}
