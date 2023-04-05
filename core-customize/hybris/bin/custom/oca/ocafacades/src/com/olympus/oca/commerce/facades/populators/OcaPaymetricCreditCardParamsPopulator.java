package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.Objects;

import com.olympus.oca.commerce.facades.constants.OcaFacadesConstants;
import com.olympus.oca.commerce.integrations.constants.OcaIntegrationConstants;


public class OcaPaymetricCreditCardParamsPopulator implements Populator<CartModel, Map<String, String>>
{

	private ConfigurationService configurationService;

	@Override
	public void populate(final CartModel source, final Map<String, String> target) throws ConversionException
	{
		final CreditCardPaymentInfoModel paymentInfo = (CreditCardPaymentInfoModel) source.getPaymentInfo();
		final AddressModel billingAddress = paymentInfo.getBillingAddress();
		target.put("card_cardType", getCardType(paymentInfo.getType()));
		target.put("billTo_country", billingAddress.getCountry().getIsocode());
		target.put("currency", source.getCurrency().getIsocode());
		target.put("card_accountNumber", paymentInfo.getNumber());
		target.put("card_nameOnCard", paymentInfo.getCcOwner());
		target.put("card_expirationMonth", paymentInfo.getValidToMonth());
		target.put("card_expirationYear", paymentInfo.getValidToYear());
		target.put("merchantID", configurationService.getConfiguration().getString(OcaIntegrationConstants.PAYMETRIC_GUID));
		target.put("billTo_street1", billingAddress.getStreetname());
		target.put("billTo_city", billingAddress.getTown());
		target.put("billTo_email", source.getUser().getUid());
		if (Objects.nonNull(billingAddress.getRegion()))
		{
			target.put("billTo_state", billingAddress.getRegion().getIsocodeShort());
		}
		target.put("billTo_postalCode", billingAddress.getPostalcode());

	}

	private String getCardType(final CreditCardType creditCardType)
	{
		String card = null;
		if (CreditCardType.VISA.equals(creditCardType))
		{
			card = configurationService.getConfiguration().getString(OcaFacadesConstants.PAYMETRIC_VISA_CARD);
		}
		else if (CreditCardType.MASTER.equals(creditCardType))
		{
			card = configurationService.getConfiguration().getString(OcaFacadesConstants.PAYMETRIC_MASTER_CARD);
		}
		return card;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
