package com.olympus.oca.commerce.core.job;

import com.olympus.oca.commerce.core.CRMCustomer.dao.CrmCustomerAccountInviteDao;
import com.olympus.oca.commerce.core.enums.InviteStatus;
import com.olympus.oca.commerce.core.event.CustomerAccountInviteEvent;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CRMCustomerAccountInviteCronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.configuration.Configuration;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.olympus.oca.commerce.core.job.CRMCustomerAccountInviteJobPerformable.INTERVAL_BETWEEN_TWO_INVITES;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CRMCustomerAccountInviteJobPerformableTest {

    @Spy
    @InjectMocks
    private CRMCustomerAccountInviteJobPerformable classUnderTest;

    @Mock
    private CrmCustomerAccountInviteDao accountInviteDao;
    @Mock
    private EventService eventService;
    @Mock
    private ModelService modelService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private CRMCustomerAccountInviteModel accountInvite;
    @Mock
    private CustomerAccountInviteEvent event;
    @Mock
    private BaseStoreModel baseStore;
    @Mock
    private BaseSiteModel baseSite;
    @Mock
    private LanguageModel language;
    @Mock
    private CurrencyModel currency;
    @Mock
    private Configuration configuration;
    @Mock
    private CRMCustomerAccountInviteCronJobModel cronJob;

    @Before
    public void setUp() throws Exception {
        Mockito.when(baseStore.getCmsSites()).thenReturn(List.of(baseSite));
        Mockito.when(baseStore.getDefaultLanguage()).thenReturn(language);
        Mockito.when(baseStore.getDefaultCurrency()).thenReturn(currency);
    }

    @Test
    public void testPerform_noInvites() {
        Mockito.when(cronJob.getBaseStore()).thenReturn(baseStore);
        Mockito.when(accountInviteDao.getCustomerInvites()).thenReturn(Lists.emptyList());
        PerformResult perform = classUnderTest.perform(cronJob);
        Assert.assertEquals(CronJobResult.SUCCESS, perform.getResult());
        Assert.assertEquals(CronJobStatus.FINISHED, perform.getStatus());
        Mockito.verify(classUnderTest, Mockito.never())
               .publishInviteEvent(Mockito.any(CRMCustomerAccountInviteModel.class), Mockito.any(BaseStoreModel.class));
        Mockito.verifyNoInteractions(accountInvite, baseStore, baseSite, language, currency, configuration);
    }

    @Test
    public void testPerform_newInvite() {
        Mockito.when(cronJob.getBaseStore()).thenReturn(baseStore);
        Mockito.when(accountInvite.getInviteStatus()).thenReturn(InviteStatus.SEND_INVITE);
        Mockito.when(accountInviteDao.getCustomerInvites()).thenReturn(List.of(accountInvite));
        Mockito.doNothing()
               .when(classUnderTest)
               .publishInviteEvent(Mockito.any(CRMCustomerAccountInviteModel.class), Mockito.any(BaseStoreModel.class));
        PerformResult perform = classUnderTest.perform(cronJob);
        Assert.assertEquals(CronJobResult.SUCCESS, perform.getResult());
        Assert.assertEquals(CronJobStatus.FINISHED, perform.getStatus());
        Mockito.verify(accountInvite).setInviteCount(1);
        Mockito.verify(accountInvite).setInviteStatus(InviteStatus.PROCESSED_BY_SAP_COMMERCE);
        Mockito.verify(cronJob, Mockito.times(2)).getBaseStore();
        Mockito.verify(classUnderTest).publishInviteEvent(accountInvite, baseStore);
    }

    @Test
    public void testPerform_existingInviteNoReminder() {
        Mockito.when(accountInvite.getInviteStatus()).thenReturn(InviteStatus.PROCESSED_BY_SAP_COMMERCE);
        Mockito.when(cronJob.getBaseStore()).thenReturn(baseStore);
        Mockito.doReturn(false).when(classUnderTest).isInviteReminderRequired(accountInvite);
        Mockito.when(accountInviteDao.getCustomerInvites()).thenReturn(List.of(accountInvite));
        PerformResult perform = classUnderTest.perform(cronJob);
        Assert.assertEquals(CronJobResult.SUCCESS, perform.getResult());
        Assert.assertEquals(CronJobStatus.FINISHED, perform.getStatus());
        Mockito.verify(accountInvite, Mockito.never()).setInviteStatus(Mockito.any(InviteStatus.class));
        Mockito.verify(classUnderTest, Mockito.never())
               .publishInviteEvent(Mockito.any(CRMCustomerAccountInviteModel.class), Mockito.any(BaseStoreModel.class));
    }

    @Test
    public void testPerform_existingInviteWithReminder() {
        Mockito.when(accountInvite.getInviteCount()).thenReturn(2);
        Mockito.when(accountInvite.getInviteStatus()).thenReturn(InviteStatus.PROCESSED_BY_SAP_COMMERCE);
        Mockito.when(cronJob.getBaseStore()).thenReturn(baseStore);
        Mockito.when(accountInviteDao.getCustomerInvites()).thenReturn(List.of(accountInvite));
        Mockito.doNothing()
               .when(classUnderTest)
               .publishInviteEvent(Mockito.any(CRMCustomerAccountInviteModel.class), Mockito.any(BaseStoreModel.class));
        Mockito.doReturn(true).when(classUnderTest).isInviteReminderRequired(accountInvite);
        PerformResult perform = classUnderTest.perform(cronJob);
        Assert.assertEquals(CronJobResult.SUCCESS, perform.getResult());
        Assert.assertEquals(CronJobStatus.FINISHED, perform.getStatus());
        Mockito.verify(accountInvite).setInviteCount(3);
        Mockito.verify(classUnderTest).publishInviteEvent(accountInvite, baseStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerform_misconfiguredJob() {
        classUnderTest.perform(cronJob);
    }

    @Test
    public void testPublishInviteEvent() {
        Mockito.doReturn(event).when(classUnderTest).getCustomerAccountInviteEvent(accountInvite, baseStore);

        classUnderTest.publishInviteEvent(accountInvite, baseStore);
        Mockito.verify(accountInvite).setLastInviteTimeStamp(Mockito.any(Date.class));
        Mockito.verify(modelService).save(accountInvite);
        Mockito.verify(eventService).publishEvent(event);
    }

    @Test
    public void testIsInviteReminderRequired_required() {
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getInt(INTERVAL_BETWEEN_TWO_INVITES)).thenReturn(5);
        Mockito.when(accountInvite.getLastInviteTimeStamp()).thenReturn(new Date(Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli()));
        Assert.assertTrue(classUnderTest.isInviteReminderRequired(accountInvite));
    }

    @Test
    public void testIsInviteReminderRequired_notRequired() {
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getInt(INTERVAL_BETWEEN_TWO_INVITES)).thenReturn(5);
        Mockito.when(accountInvite.getLastInviteTimeStamp()).thenReturn(new Date(Instant.now().minus(4, ChronoUnit.DAYS).toEpochMilli()));
        Assert.assertFalse(classUnderTest.isInviteReminderRequired(accountInvite));
    }

    @Test
    public void testGetCustomerAccountInviteEvent() {
        Mockito.doReturn(event).when(classUnderTest).createCustomerAccountInviteEventForInvite(accountInvite);
        classUnderTest.getCustomerAccountInviteEvent(accountInvite, baseStore);
        Mockito.verify(event).setBaseStore(baseStore);
        Mockito.verify(event).setSite(baseSite);
        Mockito.verify(event).setLanguage(language);
        Mockito.verify(event).setCurrency(currency);
        Mockito.verify(event).setAccountInvite(accountInvite);
        Mockito.verify(baseStore).getCmsSites();
        Mockito.verify(baseStore).getDefaultLanguage();
        Mockito.verify(baseStore).getDefaultCurrency();
        Mockito.verifyNoMoreInteractions(event, baseStore);
        Mockito.verifyNoInteractions(accountInvite, currency, language, baseSite);
    }

    @Test
    public void testCreateCustomerAccountInviteEventForInvite_mocked() {
        Mockito.doReturn(event).when(classUnderTest).createCustomerAccountInviteEventForInvite(accountInvite);
        classUnderTest.createCustomerAccountInviteEventForInvite(accountInvite);
        Mockito.verifyNoInteractions(accountInvite, event);
    }

    @Test
    public void testCreateCustomerAccountInviteEventForInvite() {
        CustomerAccountInviteEvent customerAccountInviteEventForInvite = classUnderTest.createCustomerAccountInviteEventForInvite(accountInvite);
        Assert.assertEquals(accountInvite, customerAccountInviteEventForInvite.getAccountInvite());
    }
}