package com.olympus.oca.commerce.controllers;

import com.google.common.collect.Lists;
import com.olympus.oca.commerce.dto.user.CustomerActivationWsDTO;
import com.olympus.oca.commerce.facades.company.OcaB2BCustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.commercewebservicescommons.dto.user.CustomerCreationResponseWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;

import org.springframework.http.MediaType;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
@RequestMapping(value = "/{baseSiteId}/customers")
@ApiVersion("v2")
@Tag(name = "OCA B2B Customer Activation")
public class OcaB2BCustomerActivationController extends OcaBaseCommerceController {

    @Resource(name = "ocaB2BCustomerFacade")
    private OcaB2BCustomerFacade ocaB2BCustomerFacade;
    @Resource(name = "ocaAccountInviteValidator")
    private Validator ocaAccountInviteValidator;

    @Resource(name = "ocaAccountVerificationValidator")
    private Validator ocaAccountVerificationValidator;

    @Resource(name = "ocaCreateCustomerValidator")
    private Validator ocaCreateCustomerValidator;

    @Secured({"ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP"})
    @Operation(operationId = "getsDetailsOfCustomer",
               summary = "Gets Details Of Customer Account.",
               description = "Gets Details Of Customer Account..")
    @RequestMapping(value = "getsDetailsOfCustomer", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdParam
    public CustomerActivationWsDTO getDetails(
            @Parameter(description = "The id of the B2B Customer.", required = true) @RequestParam(required = true) final String customerId,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {

        validateCustomerId(customerId);
        return getDataMapper().map(ocaB2BCustomerFacade.getDetails(customerId), CustomerActivationWsDTO.class, fields);
    }

    private void validateCustomerId(final String customerId) {
        final Errors errors = new BeanPropertyBindingResult(customerId, "Customer Id");
        ocaAccountInviteValidator.validate(customerId, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    @Operation(operationId = "activateAccount", summary = "activating account", description = "Activating account")
    @RequestMapping(value = "activateAccount", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdParam
    public void activateAccount(@Parameter(name = "emailId") @RequestParam(required = true) final String emailId,
                                @Parameter(description = "The id of the B2B Unit.", required = true) @ApiFieldsParam
                                @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws BusinessException {
        String decodedEmailId = decodeEmail(emailId);
        validateAccountStatus(decodedEmailId);
        ocaB2BCustomerFacade.activateAccount(decodedEmailId);
    }


    private void validateAccountStatus(final String emailId) {
        final Errors errors = new BeanPropertyBindingResult(emailId, "Email Id");
        ocaAccountVerificationValidator.validate(emailId, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    private String decodeEmail(String emailId) {

            byte[] decodedBytes = Base64.getDecoder().decode(emailId);
            return new String(decodedBytes, StandardCharsets.UTF_8);
    }


    @Secured({"ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP"})
    @PostMapping(value = "/createCustomer", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "create Customer", summary = "Customer creation from CRMInvite", description = "Customer creation from CRMInvite")
    @ResponseBody
    @ApiBaseSiteIdParam
    public CustomerCreationResponseWsDTO createCustomer(@Parameter(name = "activation Object", required = true) @RequestBody final CustomerActivationWsDTO customerActivationDto,
                                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                        final HttpServletRequest httpRequest) throws BusinessException {

        CustomerActivationData customerActivationData = getDataMapper().map(customerActivationDto, CustomerActivationData.class);
        final Errors errors = new BeanPropertyBindingResult(customerActivationData, "customerActivationData");
        ocaCreateCustomerValidator.validate(customerActivationData, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
        return getDataMapper().map(ocaB2BCustomerFacade.createB2BCustomer(customerActivationData), CustomerCreationResponseWsDTO.class, fields);
    }
}
