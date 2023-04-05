package com.olympus.oca.commerce.facades.util;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;

public class OcaCommerceUtils {
    public static final String USD_CURRENCY = "USD";
    public static String getFormattedPrice(Double price, CurrencyModel currency) {
        if(USD_CURRENCY.equalsIgnoreCase(currency.getIsocode())) {
            DecimalFormat decimalFormat = new DecimalFormat(OcaCoreConstants.USD_PATTERN);
            return currency.getSymbol() + decimalFormat.format(price);
        }
        return StringUtils.EMPTY;
    }
}