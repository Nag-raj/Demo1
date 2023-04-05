package com.olympus.oca.commerce.core.job;

import com.olympus.oca.commerce.core.CRMCustomer.dao.CrmCustomerAccountInviteDao;
import com.olympus.oca.commerce.core.enums.InviteStatus;
import com.olympus.oca.commerce.core.event.CustomerAccountInviteEvent;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CRMCustomerAccountInviteCronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CRMCustomerAccountInviteJobPerformable extends AbstractJobPerformable<CRMCustomerAccountInviteCronJobModel> {
    protected static final String INTERVAL_BETWEEN_TWO_INVITES = "crmcustomerinvitemail.daysbetweentwoinvites";

    private final CrmCustomerAccountInviteDao accountInviteDao;
    private final EventService eventService;
    private final ModelService modelService;
    private final ConfigurationService configurationService;

    public CRMCustomerAccountInviteJobPerformable(CrmCustomerAccountInviteDao accountInviteDao, EventService eventService, ModelService modelService,
                                                  ConfigurationService configurationService) {
        this.accountInviteDao = accountInviteDao;
        this.eventService = eventService;
        this.modelService = modelService;
        this.configurationService = configurationService;
    }

    @Override
    public PerformResult perform(CRMCustomerAccountInviteCronJobModel cronJob) {
        Assert.notNull(cronJob.getBaseStore(), "CRM customer account invite cronjob must have base store set");

        List<CRMCustomerAccountInviteModel> invites = accountInviteDao.getCustomerInvites();
        if (CollectionUtils.isNotEmpty(invites)) {
            for (CRMCustomerAccountInviteModel crmCustomerInvite : invites) {
                if (InviteStatus.SEND_INVITE.equals(crmCustomerInvite.getInviteStatus())) {
                    crmCustomerInvite.setInviteCount(1);
                    crmCustomerInvite.setInviteStatus(InviteStatus.PROCESSED_BY_SAP_COMMERCE);
                    publishInviteEvent(crmCustomerInvite, cronJob.getBaseStore());
                } else {
                    if (isInviteReminderRequired(crmCustomerInvite)) {
                        crmCustomerInvite.setInviteCount(crmCustomerInvite.getInviteCount() + 1);
                        publishInviteEvent(crmCustomerInvite, cronJob.getBaseStore());
                    }
                }
            }
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    protected void publishInviteEvent(CRMCustomerAccountInviteModel invite, BaseStoreModel baseStore) {
        invite.setLastInviteTimeStamp(new Date());
        modelService.save(invite);

        eventService.publishEvent(getCustomerAccountInviteEvent(invite, baseStore));
    }

    protected boolean isInviteReminderRequired(CRMCustomerAccountInviteModel invite) {
        LocalDate lastInvite = invite.getLastInviteTimeStamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(lastInvite, LocalDate.now());
        return (period.getYears() * 365 + period.getMonths() * 30 + period.getDays()) >= configurationService.getConfiguration()
                                                                                                             .getInt(INTERVAL_BETWEEN_TWO_INVITES);
    }

    protected AbstractEvent getCustomerAccountInviteEvent(CRMCustomerAccountInviteModel invite, BaseStoreModel baseStore) {
        CustomerAccountInviteEvent event = createCustomerAccountInviteEventForInvite(invite);
        event.setBaseStore(baseStore);
        event.setSite(baseStore.getCmsSites().stream().findFirst().orElse(null));
        event.setLanguage(baseStore.getDefaultLanguage());
        event.setCurrency(baseStore.getDefaultCurrency());
        event.setAccountInvite(invite);
        return event;
    }

    protected CustomerAccountInviteEvent createCustomerAccountInviteEventForInvite(CRMCustomerAccountInviteModel invite) {
        return new CustomerAccountInviteEvent(invite);
    }
}
