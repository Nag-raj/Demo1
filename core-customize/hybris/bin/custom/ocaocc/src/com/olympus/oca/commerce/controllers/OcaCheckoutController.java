/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.facades.cart.OcaCartFacade;
import com.olympus.oca.commerce.facades.order.OcaCheckoutFacade;
import de.hybris.platform.commercefacades.product.data.DeliveryOptionData;
import de.hybris.platform.commercefacades.product.data.DeliveryOptionListData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.DeliveryOptionListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PurchaseOrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.ShippingCarrierListWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.List;

/**
 * The Class OcaCheckoutController.
 */

@Controller
@ApiVersion("v2")
@Tag(name = "OCA Checkout")
public class OcaCheckoutController extends OcaBaseCommerceController {

    /**
     * The cart facade.
     */
    @Resource(name = "cartFacade")
    private OcaCartFacade cartFacade;

    /**
     * The data mapper.
     */
    @Resource(name = "dataMapper")
    protected DataMapper dataMapper;

    @Resource(name = "b2bCheckoutFacade")
    private OcaCheckoutFacade b2bCheckoutFacade;

    /**
     * Gets the shipping carrier list for third party.
     *
     * @param fields the fields
     * @return the shipping carrier list for third party
     */
    @Operation(operationId = "GetShippingCarrierListForAddingThirdPartyCarrier",
               summary = "Get Shipping Carrier List For Adding Third Party Carrier",
               description = "Get Shipping Carrier List For Adding Third Party Carrier")
    @RequestMapping(value = "/{baseSiteId}/shippingCarrierListForThirdParty", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ShippingCarrierListWsDTO getShippingCarrierListForThirdParty(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        return dataMapper.map(cartFacade.getShippingCarrierListForThirdParty(), ShippingCarrierListWsDTO.class, fields);
    }

    /**
     * Sets the third party shipping carrier.
     *
     * @param carrierCode    the carrier code
     * @param carrierAccount the carrier account
     * @param fields         the fields
     */
    @Operation(operationId = "setThirdPartyShippingCarrier",
               summary = "Set Third Party Shipping Carrier",
               description = "Set Third Party Shipping Carrier")
    @RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/setThirdPartyShippingAccount", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void setThirdPartyShippingAccount(
            @Parameter(description = "Shipping Carrier Code", required = true) @RequestParam(required = true) final String carrierCode,
            @Parameter(description = "Carrier Account", required = true) @RequestParam(required = true) final String carrierAccount,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        cartFacade.setThirdPartyShippingAccount(carrierCode, carrierAccount);
    }

    @Operation(operationId = "setShippingCarrier",
            summary = "Set Shipping Carrier to Cart",
            description = "Set Shipping Carrier to Cart")
    @RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/setShippingMethod", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void setShippingCarrierToCart(
            @Parameter(description = "Shipping Carrier Code", required = true) @RequestParam(required = true) final String carrierCode,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        cartFacade.setShippingMethod(carrierCode);
    }

    /**
     * Auto generate PO number.
     *
     * @param b2bUnitId the b 2 b unit id
     * @param fields    the fields
     * @return the purchase order ws DTO
     */
    @Operation(operationId = "AutoGeneratePONumber", summary = "Auto Generate PONumber", description = "Auto Generate PONumber")
    @RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/autogeneratePO", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PurchaseOrderWsDTO autoGeneratePONumber(
            @Parameter(description = "Purchase order number to assign to the checkout cart.") @RequestParam(required = true) final String b2bUnitId,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        validateB2bUnitId(b2bUnitId);
        return dataMapper.map(cartFacade.autoGeneratePONumber(b2bUnitId), PurchaseOrderWsDTO.class, fields);
    }

    /**
     * Gets the delivery option.
     *
     * @param shippingCarrierCode the shipping carrier code
     * @return the delivery option
     */
    @Operation(operationId = "getDeliveryOptions", summary = "getDeliveryOptions", description = "getDeliveryOptions")
    @RequestMapping(value = "/{baseSiteId}/getDeliveryOption", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public DeliveryOptionListWsDTO getDeliveryOption(
            @Parameter(description = "Delivery options based on shipping carrier code") @RequestParam(required = true)
            final String shippingCarrierCode) {
        final List<DeliveryOptionData> deliveryOptions = cartFacade.getDeliveryOptions(shippingCarrierCode);
        final DeliveryOptionListData deliveryOptionListData = new DeliveryOptionListData();
        deliveryOptionListData.setDeliveryOption(deliveryOptions);
        return dataMapper.map(deliveryOptionListData, DeliveryOptionListWsDTO.class);

    }

    @Operation(operationId = "getShippingCarriers",
               summary = "Get Shipping Carriers at Checkout",
               description = "Get Shipping Carriers at Checkout")
    @RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/getShippingCarriers", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ShippingCarrierListWsDTO getShippingCarriers(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        return dataMapper.map(cartFacade.getShippingCarrierList(), ShippingCarrierListWsDTO.class, fields);

    }

    @Operation(operationId = "checkoutPage", summary = "get curret checkout page", description = "checkout page.")
    @RequestMapping(value = "/{baseSiteId}/users/{userId}/{orgUnitId}/carts/current/checkout", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public CartWsDTO getCheckoutdetails(
            @Parameter(description = "Organizational Unit identifier.", required = true) @PathVariable final String orgUnitId,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        b2bCheckoutFacade.prepareCartForCheckout();

        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }
}