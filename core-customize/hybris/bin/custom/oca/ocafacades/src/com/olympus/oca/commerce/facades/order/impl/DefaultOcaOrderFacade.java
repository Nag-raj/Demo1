/**
 *
 */
package com.olympus.oca.commerce.facades.order.impl;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.model.B2BUnitNickNameModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductData;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductListData;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.localization.Localization;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.olympus.oca.commerce.core.order.OcaOrderService;
import com.olympus.oca.commerce.facades.order.OcaOrderFacade;


/**
 * The Class DefaultOcaOrderFacade.
 */
public class DefaultOcaOrderFacade extends DefaultOrderFacade implements OcaOrderFacade
{

	public static final String TYPE_ORDERSTATUS = "type.orderstatus.";
	public static final String NAME = ".name";

	private static final Logger LOG = LoggerFactory.getLogger(DefaultOcaOrderFacade.class);

	private OcaOrderService orderService;

	private EnumerationService enumerationService;

	@Resource
	private ConfigurationService configurationService;

	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	private Converter<ConsignmentEntryModel, ConsignmentEntryData> consignmentEntryConverter;

	private Converter<OrderModel,FilterAccountData> accountFilterConverter;
	private Converter<OrderModel,FilterAddressData> addressFilterConverter;
	private Converter<OrderModel,FilterStatusData> statusFilterConverter;


	private static final String STATUS_MSG_1 = "ocafacades.statusMessage1";
	private static final String STATUS_MSG_2 = "ocafacades.statusMessage2";

	private static final String FED_EX = "fedex";
	private static final String UPS ="ups";


	/** The recently ordered product converter. */
	private Converter<OrderEntryModel, RecentlyOrderedProductData> recentlyOrderedProductConverter;
	private Converter<OrderEntryModel, OrderHistoryData> ocaOrderHistoryConverter;

	/**
	 * Gets the recently ordered products.
	 *
	 * @param orgUnitId
	 *                     the org unit id
	 * @return the recently ordered products
	 */
	@Override
	public RecentlyOrderedProductListData getRecentlyOrderedProducts(final String orgUnitId)
	{
		final RecentlyOrderedProductListData recentlyOrderedProductListData = new RecentlyOrderedProductListData();
		recentlyOrderedProductListData.setRecentlyOrderedProducts(
				Converters.convertAll(getOrderService().getRecentlyOrderedProducts((CustomerModel) getUserService().getCurrentUser(),
						getB2bUnitService().getUnitForUid(orgUnitId)), getRecentlyOrderedProductConverter()));
		return recentlyOrderedProductListData;
	}

	@Override
	public SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
																			final OrderHistoryFiltersData orderHistoryFiltersData, final String query)
	{
		final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<OrderEntryModel> orderEntryList = getOrderService().getOrderList(currentCustomer, currentBaseStore,
				orderHistoryFiltersData, pageableData, query);
		return convertPageData(orderEntryList, getOcaOrderHistoryConverter());
	}


	@Override
	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());

		final Map<OrderModel, List<OrderEntryModel>> orderEntryMap = new LinkedHashMap<>();
		final List<T> orderHistoryList = new ArrayList();
		for (final S orderEntry : source.getResults())
		{
			final OrderModel order = ((OrderEntryModel) orderEntry).getOrder();
			if (!orderEntryMap.containsKey(order))
			{
				final List<OrderEntryModel> orderEntryList = new ArrayList<>();
				orderEntryList.add((OrderEntryModel) orderEntry);
				orderEntryMap.put(order, orderEntryList);
			}
			else
			{
				orderEntryMap.get(order).add((OrderEntryModel) orderEntry);
			}
		}
		for (final Entry<OrderModel, List<OrderEntryModel>> mapEntry : orderEntryMap.entrySet())
		{
			final OrderHistoryData orderHistoryData = ocaOrderHistoryConverter.convert(mapEntry.getValue().get(0));
			populateOrderHistoryData(mapEntry.getKey(), orderHistoryData, mapEntry.getValue());
			orderHistoryList.add((T) orderHistoryData);
		}
		result.setResults(orderHistoryList);
		return result;
	}

	/**
	 * @param key
	 * @param orderHistoryData
	 * @param value
	 */
	private void populateOrderHistoryData(final OrderModel source, final OrderHistoryData target,
										  final List<OrderEntryModel> entries)
	{
		final List<ConsignmentData> consignments = new ArrayList<>();
		for (final ConsignmentModel consignmentModel : source.getConsignments())
		{
			populateConsignments(consignmentModel, entries, consignments);
		}
		target.setConsignments(consignments);
		populateUnconsignedEntries(source, entries, target);
		groupConsignmentEntries(target);
	}

	/**
	 * @param target
	 */
	private void groupConsignmentEntries(final OrderHistoryData target)
	{
		final Map<ConsignmentData, Map<String, ConsignmentEntryData>> groupedConsignmentsMap = new LinkedHashMap<>();
		for (final ConsignmentData consignment : target.getConsignments())
		{
			final List<ConsignmentEntryData> originalConsignmentEntries = consignment.getEntries();
			if (CollectionUtils.isNotEmpty(originalConsignmentEntries) && originalConsignmentEntries.size() > 1)
			{
				Collections.sort(originalConsignmentEntries, (consignmentEntry1, consignmentEntry2) -> (consignmentEntry1
						.getOrderEntry().getProduct().getName().compareTo(consignmentEntry2.getOrderEntry().getProduct().getName())));
			}
			for (final ConsignmentEntryData consignmentEntry : originalConsignmentEntries)
			{
				final String baseProduct = consignmentEntry.getOrderEntry().getProduct().getBaseProduct();
				if (groupedConsignmentsMap.keySet().contains(consignment))
				{
					final Map<String, ConsignmentEntryData> consignmentEntryMap = groupedConsignmentsMap.get(consignment);
					if (consignmentEntryMap.keySet().contains(baseProduct))
					{
						final ConsignmentEntryData existingConsignmentEntry = consignmentEntryMap.get(baseProduct);
						if (CollectionUtils.isEmpty(existingConsignmentEntry.getOtherVariants()))
						{
							final List<ConsignmentEntryData> consignmentEntries = new ArrayList<>();
							existingConsignmentEntry.setOtherVariants(consignmentEntries);
						}
						existingConsignmentEntry.getOtherVariants().add(consignmentEntry);
					}
					else
					{
						consignmentEntryMap.put(baseProduct, consignmentEntry);
						groupedConsignmentsMap.put(consignment, consignmentEntryMap);
					}
				}
				else
				{
					final Map<String, ConsignmentEntryData> newConsignmentEntryMap = new LinkedHashMap<>();
					newConsignmentEntryMap.put(baseProduct, consignmentEntry);
					groupedConsignmentsMap.put(consignment, newConsignmentEntryMap);
				}
			}
			consignment.setEntries(new ArrayList(groupedConsignmentsMap.get(consignment).values()));
		}

	}

	protected void populateUnconsignedEntries(final OrderModel source, final List<OrderEntryModel> entries,
											  final OrderHistoryData target)
	{
		for (final ConsignmentModel consignmentModel : source.getConsignments())
		{
			if (CollectionUtils.isNotEmpty(consignmentModel.getConsignmentEntries()))
			{
				for (final ConsignmentEntryModel consignmentEntryModel : consignmentModel.getConsignmentEntries())
				{
					entries.remove(consignmentEntryModel.getOrderEntry());
				}
			}
		}
		if (CollectionUtils.isNotEmpty(entries))
		{
			target.setEntries(Converters.convertAll(entries, getOrderEntryConverter()));
			groupUnconsignedEntries(source,target);
		}
	}

	void groupUnconsignedEntries(final OrderModel source, final OrderHistoryData target)
	{

		final List<OrderEntryData> entries = target.getEntries();
		final Map<String, OrderEntryData> groupedOrderEntriesMap = new LinkedHashMap<>();
		Collections.sort(entries, (entry1, entry2) -> (entry1.getProduct().getName().compareTo(entry2.getProduct().getName())));
		entries.stream().forEach(entry -> {
			setOrderStatus(entry,source);
			if (groupedOrderEntriesMap.keySet().contains(entry.getProduct().getBaseProduct()))
			{
				final OrderEntryData existingEntry = groupedOrderEntriesMap.get(entry.getProduct().getBaseProduct());
				if (CollectionUtils.isEmpty(existingEntry.getOtherVariants()))
				{
					final List<OrderEntryData> otherVariants = new ArrayList();
					existingEntry.setOtherVariants(otherVariants);
				}
				existingEntry.getOtherVariants().add(entry);
			}
			else
			{
				groupedOrderEntriesMap.put(entry.getProduct().getBaseProduct(), entry);
			}
		});
		target.setEntries(new ArrayList(groupedOrderEntriesMap.values()));
	}

	private OrderEntryData setOrderStatus(OrderEntryData entry, OrderModel source){
		if(OrderStatus.CREATED.equals(source.getStatus())) {
			entry.setEntryStatus(String.valueOf(OrderStatus.PROCESSING));
		}
		return entry;
	}

	public void populateConsignments(final ConsignmentModel source, final List<OrderEntryModel> entries,
									 final List<ConsignmentData> consignments) {

		final List<ConsignmentEntryModel> consignmentEntryList = source.getConsignmentEntries().stream()
				.filter(consEntry -> entries.stream().anyMatch(entry -> entry.equals(consEntry.getOrderEntry())))
				.collect(Collectors.toList());
		ConsignmentData target = null;
		if (CollectionUtils.isNotEmpty(consignmentEntryList)) {
			target = new ConsignmentData();
			target.setCode(source.getCode());
			AbstractOrderModel order = source.getOrder();
			if (ConsignmentStatus.SHIPPED.equals(source.getStatus())) {
				if(source.getTrackingID()!= null) {
					String trackingId = source.getTrackingID().split("-")[0];
					String externalCarrier= source.getTrackingID().split("-")[1].toLowerCase();
					if(externalCarrier!=null){
						if (externalCarrier.contains(FED_EX)) {
							target.setTrackingID(trackingId);
							target.setTrackingUrl(configurationService.getConfiguration().getString(OcaCoreConstants.FEDEX_URL).concat(trackingId));
						}
						if (externalCarrier.contains(UPS)) {
							target.setTrackingID(trackingId);
							target.setTrackingUrl(configurationService.getConfiguration().getString(OcaCoreConstants.UPS_URL).concat(trackingId));
						}}}else {
					target.setTrackingID(source.getTrackingID());
				}
			}
			target.setStatus(source.getStatus());
			target.setConsignmentStatus(getEnumerationService().getEnumerationName(source.getStatus()));
			target.setEntries(Converters.convertAll(consignmentEntryList, getConsignmentEntryConverter()));
			if (ConsignmentStatus.SHIPPED.equals(source.getStatus())
					|| ConsignmentStatus.READY_FOR_PICKUP.equals(source.getStatus())) {
				target.setStatusDate(source.getShippingDate());
			}
			if (!ConsignmentStatus.INVOICED.equals(source.getStatus())
					&& !ConsignmentStatus.CANCELLED.equals(source.getStatus()) && !ConsignmentStatus.SHIPPED.equals(source.getStatus()))
				setStatusMessageAndDisclaimer(source, target);

			consignments.add(target);
		}
	}

	private void setStatusMessageAndDisclaimer(ConsignmentModel source, ConsignmentData target)  {
		String statusMessage = "";
		if (Objects.nonNull(source.getShippingDate())) {
			long noOfDays = getDateDifference(source.getShippingDate());
			boolean deliveryIsOnHold = StringUtils.isNotEmpty(source.getDeliveryHold());
			if((Objects.isNull(source.getShippingDate())) && (noOfDays == 0 && deliveryIsOnHold) || (noOfDays > 14 && !deliveryIsOnHold)
					|| (noOfDays <= 14 && deliveryIsOnHold) || (noOfDays > 14 && deliveryIsOnHold)) {
				statusMessage = STATUS_MSG_1;
				target.setStatusMessage(getConfigurationMessage(statusMessage));
			} else if((noOfDays <= 14 && !deliveryIsOnHold)) {
				statusMessage = STATUS_MSG_2;
				try {
					target.setStatusMessage(getConfigurationMessage(statusMessage)+" "+getShippedOnDate(source.getShippingDate()));
				} catch (ParseException e) {
					LOG.error("Could not parse date", e.getMessage());

				}
			}
			try {
				target.setShippingDate(getShippedOnDate(source.getShippingDate()));
			} catch (ParseException e) {
				LOG.error("Could not parse date", e.getMessage());
			}
		}else if(Objects.isNull(source.getShippingDate())){
			statusMessage = STATUS_MSG_1;
			target.setStatusMessage(getConfigurationMessage(statusMessage));
		}
	}
	@Override
	public OrderHistoryFiltersListData getOrderHistoryFilters(final OrderHistoryFiltersData orderHistoryFiltersData){
		final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
		OrderHistoryFiltersListData filtersData = new OrderHistoryFiltersListData();
		filtersData.setAddressList(
				getFilterAddressListData(
						getOrderService().getOrderHistoryFilters(currentCustomer, orderHistoryFiltersData)));
		filtersData.setAccountList(
				getFilterAccountListData(currentCustomer,
						getOrderService().getSoldTosForOrderHistoryFilters(currentCustomer)));
		filtersData.setStatusList(getFilterStatusListData());
		return filtersData;
	}

	protected Set<FilterAddressData> getFilterAddressListData(SearchResult result) {
		Set<FilterAddressData> addressSet = new HashSet<>();
		if(CollectionUtils.isNotEmpty(result.getResult()))
		{
			result.getResult().stream().forEach(address->{
				if(address instanceof AddressModel)
				{
					FilterAddressData filterAddressData = new FilterAddressData();
					filterAddressData.setAddressId(((AddressModel)address).getPublicKey());
					filterAddressData.setStreetName(((AddressModel)address).getStreetname());
					addressSet.add(filterAddressData);
				}
			});
		}
		return addressSet;
	}

	protected Set<FilterAccountData> getFilterAccountListData(B2BCustomerModel currentCustomer, SearchResult unitResults) {
		Set<FilterAccountData> accSet = new HashSet<>();
		if(CollectionUtils.isNotEmpty(unitResults.getResult()))
		{
			unitResults.getResult().stream().forEach(unit->{
				if(unit instanceof B2BUnitModel)
				{
					accSet.add(getFilterAccountData(currentCustomer, (B2BUnitModel) unit));
				}
			});
		}
		return accSet;
	}

	protected FilterAccountData getFilterAccountData(B2BCustomerModel currentCustomer, B2BUnitModel unit) {
		FilterAccountData filterAccountData = new FilterAccountData();
		filterAccountData.setAccountId(unit.getUid());
		determineNickName(currentCustomer,unit.getUid(), filterAccountData);
		return filterAccountData;
	}


	protected void determineNickName(B2BCustomerModel currentCustomer, String unitId,FilterAccountData filterAccountData) {
		B2BUnitNickNameModel nickName = currentCustomer.getAccountPreferences().getB2BUnitNickNames().stream()
				.filter(nickname -> (unitId.equals(nickname.getB2bUnitId()))).findFirst().orElse(null);
		if(Objects.nonNull(nickName) && StringUtils.isNotEmpty(nickName.getNickName())) {
			filterAccountData.setNickname(nickName.getNickName());
		}else{
			filterAccountData.setNickname(StringUtils.EMPTY);
		}
	}

	protected Set<FilterStatusData> getFilterStatusListData(){
		Set<FilterStatusData> statusSet = new HashSet<>();
		String configuredStatuses = configurationService.getConfiguration().getString(OcaCoreConstants.ORDER_HISTORY_FILTER_STATUSES,"PROCESSING,SHIPPED");
		List<String>  statusList = Arrays.stream(configuredStatuses.split(",")).toList();
		statusList.stream().forEach(status->{
			FilterStatusData filterStatusData = getFilterStatusData(status);
			statusSet.add(filterStatusData);
		});
		return statusSet;
	}

	protected FilterStatusData getFilterStatusData(String status) {
		String statusCode = OrderStatus.valueOf(status).getCode();
		FilterStatusData filterStatusData = new FilterStatusData();
		filterStatusData.setCode(statusCode);
		filterStatusData.setName(Localization.getLocalizedString(TYPE_ORDERSTATUS + statusCode + NAME));
		return filterStatusData;
	}

	private String getConfigurationMessage(String messageKey) {
		return Localization.getLocalizedString(messageKey);
	}
	private long getDateDifference(Date shippingDate) {
		Calendar calender = Calendar.getInstance();
		Date date1 = removeTime(shippingDate);
		Date date2 = removeTime(calender.getTime());
		long diff = date1.getTime()-date2.getTime();
		return TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
	}

	public Date removeTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	private String getShippedOnDate(Date shippingDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		String strDate = dateFormat.format(shippingDate);
		return strDate;
	}

	/**
	 * Gets the order service.
	 *
	 * @return the orderService
	 */
	public OcaOrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * Sets the order service.
	 *
	 * @param orderService
	 *                        the orderService to set
	 */
	public void setOrderService(final OcaOrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 * Gets the b 2 b unit service.
	 *
	 * @return the b2bUnitService
	 */
	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}


	/**
	 * Sets the B 2 b unit service.
	 *
	 * @param b2bUnitService
	 *                          the b2bUnitService to set
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @return the ocaOrderHistoryConverter
	 */
	public Converter<OrderEntryModel, OrderHistoryData> getOcaOrderHistoryConverter()
	{
		return ocaOrderHistoryConverter;
	}

	/**
	 * @param ocaOrderHistoryConverter
	 *                                    the ocaOrderHistoryConverter to set
	 */
	public void setOcaOrderHistoryConverter(final Converter<OrderEntryModel, OrderHistoryData> ocaOrderHistoryConverter)
	{
		this.ocaOrderHistoryConverter = ocaOrderHistoryConverter;
	}

	/**
	 * @return the orderEntryConverter
	 */
	public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	/**
	 * @param orderEntryConverter
	 *                               the orderEntryConverter to set
	 */
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	/**
	 * @return the consignmentEntryConverter
	 */
	public Converter<ConsignmentEntryModel, ConsignmentEntryData> getConsignmentEntryConverter()
	{
		return consignmentEntryConverter;
	}

	/**
	 * @param consignmentEntryConverter
	 *                                     the consignmentEntryConverter to set
	 */
	public void setConsignmentEntryConverter(
			final Converter<ConsignmentEntryModel, ConsignmentEntryData> consignmentEntryConverter)
	{
		this.consignmentEntryConverter = consignmentEntryConverter;
	}

	public EnumerationService getEnumerationService() {
		return enumerationService;
	}

	public void setEnumerationService(EnumerationService enumerationService) {
		this.enumerationService = enumerationService;
	}

	public Converter<OrderModel, FilterAccountData> getAccountFilterConverter() {
		return accountFilterConverter;
	}

	public void setAccountFilterConverter(Converter<OrderModel, FilterAccountData> accountFilterConverter) {
		this.accountFilterConverter = accountFilterConverter;
	}

	public Converter<OrderModel, FilterAddressData> getAddressFilterConverter() {
		return addressFilterConverter;
	}

	public void setAddressFilterConverter(Converter<OrderModel, FilterAddressData> addressFilterConverter) {
		this.addressFilterConverter = addressFilterConverter;
	}

	public Converter<OrderModel, FilterStatusData> getStatusFilterConverter() {
		return statusFilterConverter;
	}

	public void setStatusFilterConverter(Converter<OrderModel, FilterStatusData> statusFilterConverter) {
		this.statusFilterConverter = statusFilterConverter;
	}

	/**
	 * @return the recentlyOrderedProductConverter
	 */
	public Converter<OrderEntryModel, RecentlyOrderedProductData> getRecentlyOrderedProductConverter()
	{
		return recentlyOrderedProductConverter;
	}

	/**
	 * @param recentlyOrderedProductConverter the recentlyOrderedProductConverter to set
	 */
	public void setRecentlyOrderedProductConverter(
			Converter<OrderEntryModel, RecentlyOrderedProductData> recentlyOrderedProductConverter)
	{
		this.recentlyOrderedProductConverter = recentlyOrderedProductConverter;
	}
}
