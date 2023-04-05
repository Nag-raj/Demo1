package com.olympus.oca.commerce.core.CRMCustomer.dao.impl;

import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.*;

import java.util.List;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCrmCustomerAccountInviteDaoTest {

    @Spy
    @InjectMocks
    private DefaultCrmCustomerAccountInviteDao classUnderTest;

    @Mock
    private FlexibleSearchService flexibleSearchService;
    @Mock
    private FlexibleSearchQuery flexibleQuery;
    @Mock
    private SearchResult invitesSearchResult;
    @Mock
    private CRMCustomerAccountInviteModel accountInvite;

    @Test
    public void testGetCustomerInvites_noInvites() {
        Mockito.when(classUnderTest.getFlexibleQuery()).thenReturn(flexibleQuery);
        Mockito.when(flexibleSearchService.search(flexibleQuery)).thenReturn(invitesSearchResult);
        List<CRMCustomerAccountInviteModel> customerInvites = classUnderTest.getCustomerInvites();
        Assert.assertEquals(0, customerInvites.size());
        Mockito.verify(classUnderTest).getFlexibleQuery();
        Mockito.verify(flexibleSearchService).search(flexibleQuery);
        Mockito.verify(invitesSearchResult).getResult();
    }

    @Test
    public void testGetCustomerInvites_withInvites() {
        Mockito.when(classUnderTest.getFlexibleQuery()).thenReturn(flexibleQuery);
        Mockito.when(flexibleSearchService.search(flexibleQuery)).thenReturn(invitesSearchResult);
        Mockito.when(invitesSearchResult.getResult()).thenReturn(List.of(accountInvite));
        List<CRMCustomerAccountInviteModel> customerInvites = classUnderTest.getCustomerInvites();
        Assert.assertEquals(1, customerInvites.size());
        Assert.assertEquals(accountInvite, customerInvites.get(0));
        Mockito.verify(classUnderTest).getFlexibleQuery();
        Mockito.verify(flexibleSearchService).search(flexibleQuery);
        Mockito.verify(invitesSearchResult).getResult();
    }

    @Test
    public void testGetFlexibleQuery() {
        FlexibleSearchQuery flexibleQuery = classUnderTest.getFlexibleQuery();
        Assert.assertEquals(DefaultCrmCustomerAccountInviteDao.QUERY, flexibleQuery.getQuery());
        Mockito.verifyNoInteractions(flexibleSearchService);
    }
}