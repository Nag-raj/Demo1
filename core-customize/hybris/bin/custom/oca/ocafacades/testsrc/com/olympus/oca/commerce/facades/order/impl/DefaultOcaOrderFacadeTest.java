package com.olympus.oca.commerce.facades.order.impl;

import com.olympus.oca.commerce.core.order.OcaOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductData;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductListData;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOcaOrderFacadeTest {
    @InjectMocks
    DefaultOcaOrderFacade defaultOcaOrderFacade;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultOcaOrderFacade.class);
    @Mock
    private OcaOrderService orderService;

    @Mock
    BaseStoreService baseStoreService;
    @Mock
    UserService userService;
    @Mock
    private EnumerationService enumerationService;
    @Mock
    private ConfigurationService configurationService;

    /** The b 2 b unit service. */
    @Mock
    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
    @Mock
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
    @Mock
    private Converter<ConsignmentEntryModel, ConsignmentEntryData> consignmentEntryConverter;

    /** The recently ordered product converter. */
    @Mock
    private Converter<OrderEntryModel, RecentlyOrderedProductData> recentlyOrderedProductConverter;
    @Mock
    private Converter<OrderEntryModel, OrderHistoryData> ocaOrderHistoryConverter;

    @Mock
    RecentlyOrderedProductListData recentlyOrderedProductListData;
    @Mock
    SearchPageData<OrderEntryModel> orderEntryList;

    @Mock
    List<ConsignmentData> consignments;
    @Mock
    List<OrderEntryModel> entries;

    @Mock
    Converter<OrderEntryModel, OrderHistoryData> convertPageDataConverter;
    @Mock
    SearchPageData<OrderEntryModel> convertPageDataSource;

    @Mock
    ConsignmentModel populateConsignmentsSource;
    @Mock
    List<OrderEntryModel> populateConsignmentsEntries;
    @Mock
    List<ConsignmentData> populateConsignmentsConsignments;

    @Mock
    List<ConsignmentEntryModel> consignmentEntryList;

    @Mock
    Set<ConsignmentEntryModel> mediate;
    @Mock
    Stream<ConsignmentEntryModel> mediateStream;

    @Mock
    Stream<ConsignmentEntryModel> endStream;

    @Mock
    ProductModel p1;

    @Before
    public void setup(){
        defaultOcaOrderFacade = new DefaultOcaOrderFacade();
        defaultOcaOrderFacade.setUserService(userService);
        defaultOcaOrderFacade.setB2bUnitService(b2bUnitService);
        defaultOcaOrderFacade.setOrderService(orderService);
        defaultOcaOrderFacade.setBaseStoreService(baseStoreService);
        defaultOcaOrderFacade.setRecentlyOrderedProductConverter(recentlyOrderedProductConverter);
        defaultOcaOrderFacade.setConsignmentEntryConverter(consignmentEntryConverter);
        defaultOcaOrderFacade.setEnumerationService(enumerationService);
        defaultOcaOrderFacade.setOcaOrderHistoryConverter(ocaOrderHistoryConverter);
        defaultOcaOrderFacade.setOrderEntryConverter(orderEntryConverter);
    }

    @Test
    public void testGetRecentlyOrderedProducts(){
        final String orgUnitId = "Rusty";
        RecentlyOrderedProductListData actualReturnedData = defaultOcaOrderFacade.getRecentlyOrderedProducts(orgUnitId);
        assertTrue(actualReturnedData.getRecentlyOrderedProducts() != null);
    }

    @Test
    public void testGetPagedOrderHistoryForStatuses(){

        final PageableData pageableData = new PageableData();
        final OrderHistoryFiltersData orderHistoryFiltersData = new OrderHistoryFiltersData();
        final String query = "Select * from {order}";

        B2BCustomerModel currentCustomer = new B2BCustomerModel();
        Mockito.when(userService.getCurrentUser()).thenReturn(currentCustomer);

        BaseStoreModel currentBaseStore = new BaseStoreModel();
        Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);


        Mockito.when(orderService.getOrderList(currentCustomer,currentBaseStore,orderHistoryFiltersData,pageableData,query)).thenReturn(orderEntryList);

        SearchPageData<OrderHistoryData> searchPageData = new SearchPageData<OrderHistoryData>();

        SearchPageData<OrderHistoryData> actualSearchPageData = defaultOcaOrderFacade.getPagedOrderHistoryForStatuses(pageableData,orderHistoryFiltersData,query);
        assertEquals(SearchPageData.class, defaultOcaOrderFacade.getPagedOrderHistoryForStatuses(pageableData,orderHistoryFiltersData,query).getClass());
    }
    //test this with just mock
    @Test
    public void testConvertPageData_mock(){
        final SearchPageData<OrderHistoryData> result = defaultOcaOrderFacade.convertPageData(convertPageDataSource, convertPageDataConverter);
        assertEquals(convertPageDataSource.getPagination(),result.getPagination());
        assertEquals(convertPageDataSource.getSorts(),result.getSorts());
    }
    @Test
    public void testConvertPageData(){
        SearchPageData<OrderEntryModel> source = new SearchPageData<OrderEntryModel>();
        PaginationData paginationData = new PaginationData();
        SortData sortData = new SortData();
        source.setPagination(paginationData);
        source.setSorts(new ArrayList(Arrays.asList(sortData)));
        ConsignmentEntryModel c1 = new ConsignmentEntryModel();
        OrderEntryData odata = new OrderEntryData();

        AbstractOrderModel order1 = new OrderModel();
        order1.setCode("Order1");

        ConsignmentModel con1 = new ConsignmentModel();
        Set<ConsignmentModel> set1 =  new HashSet<ConsignmentModel>();
        set1.add(con1);
        order1.setConsignments(set1);
        AbstractOrderModel order2 = new OrderModel();
        ConsignmentModel con2 = new ConsignmentModel();
        Set<ConsignmentEntryModel> consignmentEntryModelsSet1 = new HashSet<ConsignmentEntryModel>();
        Set<ConsignmentEntryModel> consignmentEntryModelsSet2 = new HashSet<ConsignmentEntryModel>();

        ConsignmentEntryModel conEntry1 = new ConsignmentEntryModel();
        ConsignmentEntryModel conEntry2 = new ConsignmentEntryModel();
        consignmentEntryModelsSet1.addAll(Arrays.asList(conEntry1,conEntry2));
        consignmentEntryModelsSet2.add(conEntry2);
        con2.setConsignmentEntries(consignmentEntryModelsSet1);
        con1.setConsignmentEntries(consignmentEntryModelsSet2);
        Set<ConsignmentModel> set2 =  new HashSet<ConsignmentModel>();
        set1.add(con2);
        order2.setConsignments(set2);
        order2.setCode("Order2");


        OrderEntryModel o1 = new OrderEntryModel();
        o1.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
        o1.setOrder(order1);

        OrderEntryModel o2 = new OrderEntryModel();
        o2.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
        o2.setOrder(order1);
        source.setResults(new ArrayList<OrderEntryModel>(Arrays.asList(o1,o2)));
        ProductData prd = new ProductData();
        prd.setBaseProduct("BaseProduct");
        prd.setName("Prd1");
        OrderEntryData o3 = new OrderEntryData();
        o3.setProduct(prd);

        OrderEntryModel oEntry = new OrderEntryModel();
        p1.setName("Product");
        oEntry.setProduct(p1);
        OrderHistoryData oHD = new OrderHistoryData();
        oHD.setCode("OrderHistoryCode");
        oHD.setEntries(new ArrayList<OrderEntryData>(Arrays.asList(o3)));



        when(ocaOrderHistoryConverter.convert(any(OrderEntryModel.class))).thenReturn(oHD);

        try (MockedStatic<Converters> mockedStatic = Mockito.mockStatic(Converters.class)) {
            mockedStatic.when(() -> Converters.convertAll(new ArrayList<OrderEntryModel>(Arrays.asList(o1,o2)),orderEntryConverter)).thenReturn(new ArrayList<OrderEntryData>(Arrays.asList(o3)));

            SearchPageData<OrderHistoryData> result= defaultOcaOrderFacade.convertPageData(source,ocaOrderHistoryConverter);
            System.out.println(result.getResults().get(0));

            assertEquals("OrderHistoryCode", result.getResults().get(0).getCode());
        }
    }

    @Test
    public void testGroupUnconsignedEntries(){

        OrderHistoryData target = new OrderHistoryData();
        OrderModel order1 = new OrderModel();
        order1.setCode("Order1");
        ProductData p1 = new ProductData();
        p1.setName("Product1");

        ProductData p2 = new ProductData();
        p2.setName("Product2");

        ProductData p3 = new ProductData();
        p3.setName("Product3");

        OrderEntryData e1 = new OrderEntryData();
        e1.setProduct(p1);
        p1.setBaseProduct("Product4");

        OrderEntryData e2 = new OrderEntryData();
        e2.setProduct(p2);
        p2.setBaseProduct("Product5");

        OrderEntryData e3 = new OrderEntryData();
        e3.setProduct(p3);
        p3.setBaseProduct("Product4");


        target.setEntries(new ArrayList<OrderEntryData>(Arrays.asList(e1,e2,e3)));

        defaultOcaOrderFacade.groupUnconsignedEntries(order1,target);

        assertEquals("Product1" , target.getEntries().get(0).getProduct().getName());
        assertEquals("Product2" , target.getEntries().get(1).getProduct().getName());
    }

    @Test
    public void testPopulateConsignments_whenCollectionIsNotEmpty(){
        ConsignmentModel source = new ConsignmentModel();
        source.setCode("Code");
        source.setStatus(ConsignmentStatus.SHIPPED);
        OrderEntryModel orderEntry = new OrderEntryModel();
        ConsignmentEntryModel consignment = new ConsignmentEntryModel();
        consignment.setOrderEntry(orderEntry);
        Set<ConsignmentEntryModel> set = new HashSet<>();
        set.add(consignment);
        source.setConsignmentEntries(set);

        List<OrderEntryModel> orderEntries = new ArrayList<OrderEntryModel>();
        orderEntries.add(orderEntry);

        List<ConsignmentData> consignments = new ArrayList<ConsignmentData>();

        defaultOcaOrderFacade.populateConsignments(source, orderEntries,consignments);

        assertEquals(source.getStatus(), consignments.get(consignments.size()-1).getStatus());
        assertEquals(source.getCode(), consignments.get(consignments.size()-1).getCode());
    }

    @Test
    public void testPopulateConsignments_PrivateSetStatusMessageAndDisclaimer(){
        final String STATUS_MSG_1 = "ocafacades.statusMessage1";
        final String STATUS_MSG_2 = "ocafacades.statusMessage2";

        ConsignmentModel source = new ConsignmentModel();
        Date date = new GregorianCalendar(2000, 6 - 1, 21).getTime();
        source.setShippingDate(date);
        source.setCode("Code");
        source.setStatus(ConsignmentStatus.READY_FOR_PICKUP);
        OrderEntryModel orderEntry = new OrderEntryModel();
        ConsignmentEntryModel consignment = new ConsignmentEntryModel();
        consignment.setOrderEntry(orderEntry);
        Set<ConsignmentEntryModel> set = new HashSet<>();
        set.add(consignment);
        source.setConsignmentEntries(set);

        List<OrderEntryModel> orderEntries = new ArrayList<OrderEntryModel>();
        orderEntries.add(orderEntry);

        List<ConsignmentData> consignments = new ArrayList<ConsignmentData>();




        try (MockedStatic<Localization> mockedStatic = Mockito.mockStatic(Localization.class)) {
            mockedStatic.when(() -> Localization.getLocalizedString(STATUS_MSG_2)).thenReturn("We'll provide an estimated time soon.");

            defaultOcaOrderFacade.populateConsignments(source, orderEntries,consignments);

            assertEquals(new SimpleDateFormat("dd-MM-yyyy").format(source.getShippingDate()), consignments.get(consignments.size()-1).getShippingDate());
            assertEquals("We'll provide an estimated time soon.",consignments.get(consignments.size()-1).getStatusMessage());
            assertEquals(source.getStatus(),consignments.get(consignments.size()-1).getStatus());
        }

    }

    @Test
    public void testPopulateConsignments_PrivateSetStatusMessageAndDisclaimer_emptyShippingDate(){
        final String STATUS_MSG_1 = "ocafacades.statusMessage1";
        final String STATUS_MSG_2 = "ocafacades.statusMessage2";

        ConsignmentModel source = new ConsignmentModel();
        source.setShippingDate(null);
        source.setCode("Code");
        source.setStatus(ConsignmentStatus.READY_FOR_PICKUP);
        OrderEntryModel orderEntry = new OrderEntryModel();
        ConsignmentEntryModel consignment = new ConsignmentEntryModel();
        consignment.setOrderEntry(orderEntry);
        Set<ConsignmentEntryModel> set = new HashSet<>();
        set.add(consignment);
        source.setConsignmentEntries(set);

        List<OrderEntryModel> orderEntries = new ArrayList<OrderEntryModel>();
        orderEntries.add(orderEntry);

        List<ConsignmentData> consignments = new ArrayList<ConsignmentData>();
        ConsignmentData consignmentData = new ConsignmentData();


        try (MockedStatic<Localization> mockedStatic = Mockito.mockStatic(Localization.class)) {
            mockedStatic.when(() -> Localization.getLocalizedString(STATUS_MSG_1)).thenReturn("Your order should be shipped on");

            defaultOcaOrderFacade.populateConsignments(source, orderEntries,consignments);

            assertEquals("Your order should be shipped on",consignments.get(consignments.size()-1).getStatusMessage());
            assertEquals(source.getStatus(),consignments.get(consignments.size()-1).getStatus());
        }
    }

    @Test
    public void testPopulateUnconsignedEntries(){

        p1.setName("Product1");
        OrderEntryModel order1 = new OrderEntryModel();
        order1.setProduct(p1);
        OrderEntryModel order2 = new OrderEntryModel();
        order2.setProduct(p1);
        ConsignmentModel con1 = new ConsignmentModel();
        ConsignmentModel con2 = new ConsignmentModel();

        ConsignmentEntryModel con3 = new ConsignmentEntryModel();
        con3.setOrderEntry(order1);
        ConsignmentEntryModel con4 = new ConsignmentEntryModel();
        con4.setOrderEntry(order2);

        Set<ConsignmentModel>  conSet1 = new HashSet<ConsignmentModel>();
        Set<ConsignmentEntryModel>  conSet2 = new HashSet<ConsignmentEntryModel>();
        conSet1.addAll(Arrays.asList(con1,con2));
        conSet2.addAll(Arrays.asList(con3,con4));

        con1.setConsignmentEntries(conSet2);
        con2.setConsignmentEntries(conSet2);

        final OrderModel source = new OrderModel();
        source.setConsignments(conSet1);
        final List<OrderEntryModel> entries = new ArrayList<OrderEntryModel>(Arrays.asList(order1,order2));
        final OrderHistoryData target = new OrderHistoryData();


        defaultOcaOrderFacade.populateUnconsignedEntries(source,entries,target);
        assertTrue(entries.size() == 0);
    }
}