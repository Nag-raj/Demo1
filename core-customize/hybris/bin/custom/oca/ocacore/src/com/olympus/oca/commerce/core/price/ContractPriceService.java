package com.olympus.oca.commerce.core.price;

import java.util.Date;

public interface ContractPriceService {
    boolean isContractPriceExpired(final Date contractPriceFetchedAt);
}
