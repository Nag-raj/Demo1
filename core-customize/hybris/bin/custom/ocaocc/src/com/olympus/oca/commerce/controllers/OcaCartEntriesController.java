package com.olympus.oca.commerce.controllers;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartEntryException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@ApiVersion("v2")
@Tag(name = "OCA Cart Entries ")

public class OcaCartEntriesController extends OcaBaseCommerceController {

    private static final Logger LOG = LoggerFactory.getLogger(OcaB2BCartsController.class);

    @DeleteMapping(value = "/{cartId}/entries/remove/{entryNumber}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeCartEntry", summary = "Deletes cart entry.", description = "Deletes cart entry.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void removeCartEntry(@Parameter(description =
                                                   "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting "
                                                   + "with zero (0).", required = true) @PathVariable final long entryNumber)

            throws CommerceCartModificationException {
        LOG.debug("removeCartEntry: entryNumber = {}", entryNumber);
        final CartData cart = getSessionCart();
        getCartEntryForNumber(cart, entryNumber);
        getCartFacade().updateCartEntry(entryNumber, 0);
    }

    protected static OrderEntryData getCartEntryForNumber(final CartData cart, final long number) {
        final Integer requestedEntryNumber = (int) number;
        return CollectionUtils.emptyIfNull(cart.getEntries()).stream().filter(entry -> {
            if (entry != null) {
                if (requestedEntryNumber.equals(entry.getEntryNumber())) {
                    return true;
                } else if (entry.getOtherVariants() != null) {
                    return entry.getOtherVariants().stream().anyMatch(variant -> requestedEntryNumber.equals(variant.getEntryNumber()));
                }
            }
            return false;
        }).findFirst().orElseThrow(() -> new CartEntryException("Entry not found", CartEntryException.NOT_FOUND, String.valueOf(number)));
    }
}
