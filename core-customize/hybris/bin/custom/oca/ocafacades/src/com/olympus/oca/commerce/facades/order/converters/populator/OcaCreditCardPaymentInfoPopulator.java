package com.olympus.oca.commerce.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.CreditCardPaymentInfoPopulator;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.apache.commons.lang3.StringUtils;

public class OcaCreditCardPaymentInfoPopulator extends CreditCardPaymentInfoPopulator{

    private static final int CC_LAST_DIGITS = 4;

    @Override
    public void populate(final CreditCardPaymentInfoModel source, final CCPaymentInfoData target) {
        super.populate(source, target);
        target.setCardNumber(StringUtils.right(source.getNumber(), CC_LAST_DIGITS));
    }

}
