package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.facades.company.OcaB2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@ApiVersion("v2")
@Secured({ "ROLE_B2BADMINGROUP", "ROLE_TRUSTED_CLIENT"})
@Tag(name = "OCA Organizational Unit Management")
public class OcaOrgUnitsController extends OcaBaseController {

    private static final String OBJECT_NAME_ORG_UNIT = "OrgUnit";
    private static final String UNIT_NOT_FOUND_MESSAGE = "Organizational unit with id [%s] was not found";

    @Resource(name = "b2bUnitFacade")
    protected OcaB2BUnitFacade ocaB2BUnitFacade;

    @Secured({ "ROLE_B2BADMINGROUP", "ROLE_TRUSTED_CLIENT"})
    @GetMapping(value = "/ocaOrgUnits/{orgUnitId}/addresses", produces = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(operationId = "getOrgUnitDeliveryAddresses", summary = "Get organizational unit delivery addresses", description = "Retrieves organizational unit delivery addresses")
    @ApiBaseSiteIdAndUserIdParam
    public AddressListWsDTO getOrgUnitDeliveryAddresses(
            @Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        AddressDataList addressDataList = ocaB2BUnitFacade.getUnitDeliveryAddresses(orgUnitId);
        if(Objects.isNull(addressDataList))
        {
            throw new NotFoundException(String.format(UNIT_NOT_FOUND_MESSAGE, sanitize(orgUnitId)));
        }
        return getDataMapper().map(addressDataList, AddressListWsDTO.class, fields);
    }

}
