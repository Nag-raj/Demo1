package com.olympus.oca.commerce.core.price.impl;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.price.ContractPriceService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Date;

public class DefaultContractPriceService implements ContractPriceService {

    private final ConfigurationService configurationService;

    public DefaultContractPriceService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public boolean isContractPriceExpired(Date contractPriceFetchedAt) {
        if (contractPriceFetchedAt == null) {
            return true;
        }

        Date date = new Date();
        long differentDate = Math.abs(date.getTime() - contractPriceFetchedAt.getTime());
        long differentHours = differentDate / (60 * 60 * 1000);
        int timeToLiveHours = configurationService.getConfiguration().getInt(OcaCoreConstants.TIME_TO_LIVE_HOURS, 1);
        return differentHours > timeToLiveHours;
    }
}
