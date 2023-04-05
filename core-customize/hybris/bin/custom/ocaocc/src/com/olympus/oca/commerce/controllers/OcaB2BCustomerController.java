/**
 *
 */
package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import com.olympus.oca.commerce.facades.company.OcaB2BUnitFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@ApiVersion("v2")
@Tag(name = "OCA B2B Customer")
public class OcaB2BCustomerController extends OcaBaseController {
    @Resource(name = "b2bUnitFacade")
    private OcaB2BUnitFacade ocaB2BUnitFacade;

    @Resource(name = "ocaAccessTypeValidator")
    private Validator ocaAccessTypeValidator;

    @Operation(operationId = "setDefaultUnit",
               summary = "Sets the default b2b Unit in customer's account.",
               description = "Sets the default b2b Unit in " + "customer's " + "account.")
    @RequestMapping(value = "setDefaultUnit", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdAndUserIdParam
    public void setDefaultUnit(
            @Parameter(description = "The id of the B2B Unit.", required = true) @RequestParam(required = true) final String b2bUnitId,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        validateB2bUnitId(b2bUnitId);
        ocaB2BUnitFacade.setDefaultB2BUnit(b2bUnitId);
    }

    @Operation(operationId = "saveAccessType", summary = "changes the accessType", description = "changes the accessType of a user.")
    @RequestMapping(value = "saveAccessType", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public void saveAccessType(@RequestBody final AccountPreferencesWsDTO accountPreferences,
                               @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        final Errors errors = new BeanPropertyBindingResult(accountPreferences, "accessType");
        ocaAccessTypeValidator.validate(accountPreferences, errors);
        ocaB2BUnitFacade.saveAccessType(accountPreferences);
    }
}
