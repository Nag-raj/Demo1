package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.commerceservices.order.CommerceCardTypeService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.UUID;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.olympus.oca.commerce.facades.constants.OcaFacadesConstants;


public class OcaCreditCardPaymentInfoReversePopulator implements Populator<NodeList, CreditCardPaymentInfoModel>
{
	private UserService userService;
	private CommerceCardTypeService commerceCardTypeService;

	@Override
	public void populate(final NodeList source, final CreditCardPaymentInfoModel target) throws ConversionException
	{
		final UserModel customerModel = userService.getCurrentUser();
		target.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		target.setUser(customerModel);
		for (int temp = 0; temp < source.getLength(); temp++)
		{
			final Node nNode = source.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE)
			{
				final Element eElement = (Element) nNode;
				if (OcaFacadesConstants.CARD_NUMBER
						.equalsIgnoreCase(eElement.getElementsByTagName(OcaFacadesConstants.NAME).item(0).getTextContent()))
				{
					target.setNumber(eElement.getElementsByTagName(OcaFacadesConstants.VALUE).item(0).getTextContent());
				}
				else if (OcaFacadesConstants.CARD_TYPE
						.equalsIgnoreCase(eElement.getElementsByTagName(OcaFacadesConstants.NAME).item(0).getTextContent()))
				{
					final CardType cerditCardType = getCardType(eElement);
					target.setType(cerditCardType == null ? null : cerditCardType.getCode());
				}
				else if (OcaFacadesConstants.CARD_HOLDER_NAME
						.equalsIgnoreCase(eElement.getElementsByTagName(OcaFacadesConstants.NAME).item(0).getTextContent()))
				{
					target.setCcOwner(eElement.getElementsByTagName(OcaFacadesConstants.VALUE).item(0).getTextContent());
				}
				else if (OcaFacadesConstants.EXPIRATION_YEAR
						.equalsIgnoreCase(eElement.getElementsByTagName(OcaFacadesConstants.NAME).item(0).getTextContent()))
				{
					target.setValidToYear(eElement.getElementsByTagName(OcaFacadesConstants.VALUE).item(0).getTextContent());
				}
				else if (OcaFacadesConstants.EXPIRATION_MONTH
						.equalsIgnoreCase(eElement.getElementsByTagName(OcaFacadesConstants.NAME).item(0).getTextContent()))
				{
					target.setValidToMonth(eElement.getElementsByTagName(OcaFacadesConstants.VALUE).item(0).getTextContent());
				}
			}
		}
	}

	private CardType getCardType(final Element eElement)
	{
		final String cardType = eElement.getElementsByTagName(OcaFacadesConstants.VALUE).item(0).getTextContent();
		String commerceCardType = null;
		if (OcaFacadesConstants.VI.equalsIgnoreCase(cardType))
		{
			commerceCardType = OcaFacadesConstants.VISA;
		}
		else if (OcaFacadesConstants.MC.equalsIgnoreCase(cardType))
		{
			commerceCardType = OcaFacadesConstants.MASTER;
		}
		final CardType cerditCardType = commerceCardTypeService.getCardTypeForCode(commerceCardType);
		return cerditCardType;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public void setCommerceCardTypeService(final CommerceCardTypeService commerceCardTypeService)
	{
		this.commerceCardTypeService = commerceCardTypeService;
	}
}
