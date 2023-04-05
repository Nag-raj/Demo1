/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.constants.OcaoccConstants;
import com.olympus.oca.commerce.dto.user.PlaceOrderWsDTO;
import com.olympus.oca.commerce.facades.order.OcaOrderFacade;
import com.olympus.oca.commerce.facades.order.impl.DefaultOcaCheckoutFacade;
import com.olympus.oca.commerce.helper.OcaOrdersHelper;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersListData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryFiltersListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryFiltersWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.RecentlyOrderedProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.PaymentAuthorizationException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * The Class OcaOrdersController.
 */
@Controller
@ApiVersion("v2")
@Tag(name = "OCA Orders Controller")
public class OcaOrdersController extends OcaBaseController {

    /**
     * The order facade.
     */
    @Resource(name = "orderFacade")
    private OcaOrderFacade orderFacade;

    @Resource(name = "ocaOrdersHelper")
    private OcaOrdersHelper ordersHelper;

    @Resource(name = "userFacade")
    protected UserFacade userFacade;

    @Resource(name = "cartLoaderStrategy")
    private CartLoaderStrategy cartLoaderStrategy;

    @Resource(name = "b2bCartFacade")
    private CartFacade cartFacade;

    @Resource(name = "b2BPlaceOrderCartValidator")
    private Validator placeOrderCartValidator;

    @Resource(name = "ocaPlaceOrderValidator")
    private Validator ocaPlaceOrderValidator;

    @Resource(name = "b2bCheckoutFacade")
    private DefaultOcaCheckoutFacade b2bCheckoutFacade;

    /**
     * Gets the recently ordered products.
     *
     * @param orgUnitId the org unit id
     * @param fields    the fields
     * @return the recently ordered products
     */
    @Operation(operationId = "recentlyOrderProducts",
               summary = "Gets the recently ordered products of a b2bUnit",
               description = "Gets the recently ordered products of a b2bUnit")
    @RequestMapping(value = "/{baseSiteId}/users/{userId}/orgUnit/{orgUnitId}/orders/products/recent", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public RecentlyOrderedProductListWsDTO getRecentlyOrderedProducts(
            @Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
            @ApiFieldsParam @RequestParam(required = true, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        return getDataMapper().map(orderFacade.getRecentlyOrderedProducts(orgUnitId), RecentlyOrderedProductListWsDTO.class, fields);
    }

    /**
     * Gets the user order history.
     * Due to complexity the method is defined as POST
     *
     * @param currentPage the current page
     * @param pageSize    the page size
     * @param filters     the filters
     * @param sort        the sort
     * @param fields      the fields
     * @param response    the response
     * @param query       the query
     * @return the user order history
     */
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
    @RequestMappingOverride(priorityProperty = "ocaocc.OcaOrdersController.orders.priority")
    @RequestMapping(value = OcaoccConstants.OCC_OVERLAPPING_BASE_SITE_USER_PATH + "/orders", method = RequestMethod.POST)
    @ResponseBody
    @Operation(operationId = "userOrderHistory",
               summary = "Returns order history data for all orders placed by a specified user.",
               description =
                       "Returns order history data for all orders placed by a specified user for a specified base store. The response can display the "
                       + "results across multiple pages, if required.")
    @ApiBaseSiteIdAndUserIdParam
    public OrderHistoryListWsDTO getUserOrderHistory(
            @Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
            @Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
            @Parameter(description = "Request body parameter that contains details such as the order statuses, account number, addressId, sort parameter",
                       required = true) @RequestBody final OrderHistoryFiltersWsDTO filters,
            @Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response,
            @RequestParam(required = false) final String query) {
        final OrderHistoryListWsDTO orderHistoryList = ordersHelper.searchOrderHistory(filters, currentPage, pageSize, sort,
                                                                                       addPaginationField(fields), query);
        setTotalCountHeader(response, orderHistoryList.getPagination());
        return orderHistoryList;
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @RequestMapping(value = OcaoccConstants.OCC_OVERLAPPING_BASE_SITE_USER_PATH + "/placeOrder", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "placeOcaOrder", summary = "Places an Oca Order.", description = "Places an OCA Order.")
    public OrderWsDTO placeOrgOrder(
            @Parameter(description = "Cart identifier: cart code for logged in user, cart guid for anonymous user, 'current' for the last modified cart",
                       required = true) @RequestParam(required = true) final String cartId, @RequestBody final PlaceOrderWsDTO placeOrderParams,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws InvalidCartException, PaymentAuthorizationException {
        validateUser();
        final PlaceOrderData placeOrderData = getDataMapper().map(placeOrderParams, PlaceOrderData.class, fields);
        validatePlaceOrderParams(placeOrderData);

        cartLoaderStrategy.loadCart(cartId);
        final CartData cartData = cartFacade.getCurrentCart();

        validateCart(cartData);
        b2bCheckoutFacade.savePlaceOrderDataToCart(placeOrderData);
        validateAndAuthorizePayment(cartData);

        return getDataMapper().map(b2bCheckoutFacade.placeOrder(new PlaceOrderData()), OrderWsDTO.class, fields);
    }

    private void validatePlaceOrderParams(final PlaceOrderData placeOrderParams) {
        final Errors errors = new BeanPropertyBindingResult(placeOrderParams, "placeOrderParams");
        ocaPlaceOrderValidator.validate(placeOrderParams, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
    @RequestMappingOverride(priorityProperty = "ocaocc.OcaOrdersController.orders.priority")
    @RequestMapping(value = OcaoccConstants.OCC_OVERLAPPING_BASE_SITE_USER_PATH + "/order/filters", method = RequestMethod.POST)
    @ResponseBody
    @Operation(operationId = "userOrderHistory",
               summary = "Returns order history data for all orders placed by a specified user.",
               description =
                       "Returns order history data for all orders placed by a specified user for a specified base store. The response can display "
                       + "the results across multiple pages, if required.")
    @ApiBaseSiteIdAndUserIdParam
    public OrderHistoryFiltersListWsDTO getOrderHistoryFilters(
            @Parameter(description = "Request body parameter that contains details such as the order statuses, account number, addressId, sort parameter",
                       required = true) @RequestBody final OrderHistoryFiltersWsDTO filters,
            @Parameter(description = "Sorting method applied to the return results.") @RequestParam(defaultValue = DEFAULT_FIELD_SET)
            final String fields, final HttpServletResponse response, @RequestParam(required = false) final String query) {
        OrderHistoryFiltersData orderHistoryFiltersData = getDataMapper().map(filters, OrderHistoryFiltersData.class);
        OrderHistoryFiltersListData responseData = getOrderFacade().getOrderHistoryFilters(orderHistoryFiltersData);
        OrderHistoryFiltersListWsDTO responseWsDTO = getDataMapper().map(responseData, OrderHistoryFiltersListWsDTO.class, fields);
        return responseWsDTO;
    }

    /**
     * Adds pagination field to the 'fields' parameter
     *
     * @param fields
     * @return fields with pagination
     */
    protected String addPaginationField(final String fields) {
        String fieldsWithPagination = fields;

        if (StringUtils.isNotBlank(fieldsWithPagination)) {
            fieldsWithPagination += ",";
        }
        fieldsWithPagination += "pagination";

        return fieldsWithPagination;
    }

    protected void setTotalCountHeader(final HttpServletResponse response, final PaginationWsDTO paginationDto) {
        if (paginationDto != null && paginationDto.getTotalResults() != null) {
            response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalResults()));
        }
    }

    protected void validateUser() {
        if (userFacade.isAnonymousUser()) {
            throw new AccessDeniedException("Access is denied");
        }
    }

    protected void validateCart(final CartData cartData) throws InvalidCartException {
        final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
        placeOrderCartValidator.validate(cartData, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }

        try {
            final List<CartModificationData> modificationList = cartFacade.validateCurrentCartData();
            if (CollectionUtils.isNotEmpty(modificationList)) {
                final CartModificationDataList cartModificationDataList = new CartModificationDataList();
                cartModificationDataList.setCartModificationList(modificationList);
                throw new WebserviceValidationException(cartModificationDataList);
            }
        } catch (final CommerceCartModificationException e) {
            throw new InvalidCartException(e);
        }
    }

    protected void validateAndAuthorizePayment(final CartData cartData) throws PaymentAuthorizationException {
        if (CheckoutPaymentType.CARD.getCode().equals(cartData.getPaymentType().getCode()) && !b2bCheckoutFacade.authorizePayment(null)) {
            throw new PaymentAuthorizationException();
        }
    }

    /**
     * Gets the order facade.
     *
     * @return the orderFacade
     */
    public OcaOrderFacade getOrderFacade() {
        return orderFacade;
    }

    /**
     * Sets the order facade.
     *
     * @param orderFacade the orderFacade to set
     */
    public void setOrderFacade(final OcaOrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

}