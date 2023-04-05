/**
 *
 */
package com.olympus.oca.commerce.facades.payment.impl;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.payment.data.AccessTokenData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.olympus.oca.commerce.core.enums.PartnerFunctionCode;
import com.olympus.oca.commerce.facades.constants.OcaFacadesConstants;
import com.olympus.oca.commerce.facades.payment.OcaPaymentFacade;
import com.olympus.oca.commerce.integrations.payment.service.OcaPaymentIntegrationService;


public class DefaultOcaPaymentFacade implements OcaPaymentFacade
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultOcaPaymentFacade.class);
	private OcaPaymentIntegrationService ocaPaymentIntegrationService;
	private ModelService modelService;
	private CartService cartService;
	private Converter<NodeList, CreditCardPaymentInfoModel> ocaCreditCardPaymentInfoReverseConverter;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Override
	public Optional<AccessTokenData> getAccessToken()
	{
		return ocaPaymentIntegrationService.getAccessToken();
	}

	@Override
	public void addCard(final String accessToken) throws BusinessException
	{
		String errorStatus = null;
		final String cardDetails = ocaPaymentIntegrationService.getCardDetails(accessToken);
		try
		{
			errorStatus = createPaymentInfo(cardDetails);
		}
		catch (final ParserConfigurationException | SAXException | IOException exception)
		{
			LOG.error("Exception while getting paymetric card details", exception);
			throw new BusinessException("Exception occurred while getting paymetric card details");

		}
		if (Objects.nonNull(errorStatus))
		{
			throw new BusinessException("Exception occurred while getting paymetric card details with error code: " + errorStatus);

		}
	}

	protected String createPaymentInfo(final String cardDetails) throws ParserConfigurationException, SAXException, IOException
	{
		String errorStatus = null;
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final Document doc = dBuilder.parse(new InputSource(new StringReader(cardDetails)));
		final NodeList nList = doc.getElementsByTagName(OcaFacadesConstants.FORM_FIELD);
		if (nList.getLength() > 0)
		{
			final CreditCardPaymentInfoModel cardPaymentInfoModel = modelService.create(CreditCardPaymentInfoModel.class);
			ocaCreditCardPaymentInfoReverseConverter.convert(nList, cardPaymentInfoModel);
			final CartModel currentCart = cartService.getSessionCart();
			if (null != currentCart.getUnit())
			{
				setBillingAddress(cardPaymentInfoModel, currentCart);
			}
			currentCart.setPaymentType(CheckoutPaymentType.CARD);
			currentCart.setPaymentInfo(cardPaymentInfoModel);
			modelService.saveAll(cardPaymentInfoModel, currentCart);
		}
		else
		{
			errorStatus = populateErrorStatus(errorStatus, doc);
		}
		return errorStatus;
	}

	/**
	 * @param errorStatus
	 * @param doc
	 * @return
	 */
	private String populateErrorStatus(String errorStatus, final Document doc)
	{
		final NodeList nList = doc.getElementsByTagName(OcaFacadesConstants.REQUEST_ERROR);
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			final Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE)
			{
				final Element eElement = (Element) nNode;
				LOG.error("Failed to fetch response packet.The status code is: "
						+ eElement.getElementsByTagName(OcaFacadesConstants.STATUS_CODE).item(0).getTextContent());
				errorStatus = eElement.getElementsByTagName(OcaFacadesConstants.STATUS_CODE).item(0).getTextContent();
			}
		}
		return errorStatus;
	}

	/**
	 * @param cardPaymentInfoModel
	 * @param currentCart
	 */
	private void setBillingAddress(final CreditCardPaymentInfoModel cardPaymentInfoModel, final CartModel currentCart)
	{
		final B2BUnitModel unit = currentCart.getUnit();
		Optional<AddressModel> billingAddress;
		final Optional<B2BUnitModel> billTo = b2bUnitService.getBranch(unit).stream()
				.filter(b2bUnit -> CollectionUtils.isNotEmpty(b2bUnit.getPartnerFunctionType())
						&& b2bUnit.getPartnerFunctionType().contains(PartnerFunctionCode.BILL_TO))
				.findFirst();
		billingAddress = billTo.map(b2BUnitModel -> b2BUnitModel.getAddresses().stream().findFirst())
							   .orElseGet(() -> unit.getAddresses().stream().findFirst());
		billingAddress.ifPresent(cardPaymentInfoModel::setBillingAddress);
	}

	/**
	 * @param ocaPaymentIntegrationService
	 *                                        the ocaPaymentIntegrationService to set
	 */
	public void setOcaPaymentIntegrationService(final OcaPaymentIntegrationService ocaPaymentIntegrationService)
	{
		this.ocaPaymentIntegrationService = ocaPaymentIntegrationService;
	}

	/**
	 * @param modelService
	 *                        the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @param cartService
	 *                       the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @param ocaCreditCardPaymentInfoReverseConverter
	 *                                                    the ocaCreditCardPaymentInfoReverseConverter to set
	 */
	public void setOcaCreditCardPaymentInfoReverseConverter(
			final Converter<NodeList, CreditCardPaymentInfoModel> ocaCreditCardPaymentInfoReverseConverter)
	{
		this.ocaCreditCardPaymentInfoReverseConverter = ocaCreditCardPaymentInfoReverseConverter;
	}

	/**
	 * @param b2bUnitService
	 *                          the b2bUnitService to set
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
