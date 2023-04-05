package com.olympus.oca.commerce.core.event;

import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import com.olympus.oca.commerce.core.model.CustomerAccountInviteProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountInviteEventListenerTest {

    @Spy
    @InjectMocks
    private CustomerAccountInviteEventListener classUnderTest;

    @Mock
    private BusinessProcessService businessProcessService;
    @Mock
    private ModelService modelService;

    @Mock
    private CustomerAccountInviteEvent event;
    @Mock
    private CRMCustomerAccountInviteModel accountInvite;
    @Mock
    private CustomerAccountInviteProcessModel businessProcess;
    @Mock
    private BaseStoreModel baseStore;
    @Mock
    private BaseSiteModel site;
    @Mock
    private CustomerModel customer;
    @Mock
    private LanguageModel language;
    @Mock
    private CurrencyModel currency;

    @Before
    public void setUp() throws Exception {
        Mockito.when(event.getAccountInvite()).thenReturn(accountInvite);
        Mockito.when(event.getSite()).thenReturn(site);
        Mockito.when(event.getLanguage()).thenReturn(language);
        Mockito.when(event.getCurrency()).thenReturn(currency);
        Mockito.when(event.getBaseStore()).thenReturn(baseStore);
        Mockito.when(site.getChannel()).thenReturn(SiteChannel.B2B);
    }

    @Test
    public void testOnSiteEvent() {
        Mockito.doReturn(businessProcess).when(classUnderTest).getConfiguredBusinessProcessForEvent(event);
        classUnderTest.onSiteEvent(event);
        Mockito.verify(modelService).save(businessProcess);
        Mockito.verify(businessProcessService).startProcess(businessProcess);
    }

    @Test
    public void testGetSiteChannelForEvent_withChannel() {
        Assert.assertEquals(SiteChannel.B2B, classUnderTest.getSiteChannelForEvent(event));
        Mockito.verifyNoInteractions(modelService, businessProcessService, accountInvite, language, currency, baseStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSiteChannelForEvent_noChannel() {
        Mockito.when(event.getSite()).thenReturn(null);
        classUnderTest.getSiteChannelForEvent(event);
    }

    @Test
    public void testGetConfiguredBusinessProcessForEvent() {
        Mockito.doReturn(businessProcess).when(classUnderTest).createBusinessProcessForEvent(event);
        classUnderTest.getConfiguredBusinessProcessForEvent(event);
        Mockito.verify(businessProcess).setCustomerAccount(accountInvite);
        Mockito.verify(businessProcess).setSite(site);
        Mockito.verify(businessProcess).setLanguage(language);
        Mockito.verify(businessProcess).setCurrency(currency);
        Mockito.verify(businessProcess).setStore(baseStore);
        Mockito.verifyNoInteractions(modelService, accountInvite, site, language, currency, baseStore);
    }

    @Test
    public void testCreateBusinessProcessForEvent() {
        classUnderTest.createBusinessProcessForEvent(event);
        Mockito.verify(businessProcessService).createProcess(Mockito.anyString(), Mockito.eq("customerAccountActivationProcess"));
        Mockito.verifyNoInteractions(modelService);
        Mockito.verify(event).getAccountInvite();
    }

}