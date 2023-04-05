package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.exceptions.PaymetricAccessTokenException;
import com.olympus.oca.commerce.facades.payment.OcaPaymentFacade;
import com.olympus.oca.commerce.integrations.payment.service.OcaPaymentIntegrationService;
import de.hybris.platform.commercefacades.payment.data.AccessTokenData;
import de.hybris.platform.commercewebservicescommons.dto.payment.AccessTokenWsDTO;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Objects;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/payments")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Tag(name = "OCA Payments")
public class OcaPaymentsController extends OcaBaseController{

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "ocaPaymentFacade")
    private OcaPaymentFacade ocaPaymentFacade;

    @Operation(operationId = "getAccessToken", summary = "Gets the paymetric token")
    @RequestMapping(value = "/accessToken", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public AccessTokenWsDTO getAccessToken(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws PaymetricAccessTokenException {
        Optional<AccessTokenData> accessTokenOptional = ocaPaymentFacade.getAccessToken();
        if (accessTokenOptional.isEmpty()) {
            throw new PaymetricAccessTokenException("Unable to retrieve the paymetric access token");
        }
        return dataMapper.map(accessTokenOptional.get(), AccessTokenWsDTO.class, fields);
    }

    @Operation(operationId = "addCard", summary = "Adds the credit card payment info to cart")
    @RequestMapping(value = "/addCard", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void addCard(@ApiFieldsParam
                                  @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
                                  final String fields, @RequestParam
                                  final String accessToken) throws BusinessException
    {
         ocaPaymentFacade.addCard(accessToken);
    }

}
