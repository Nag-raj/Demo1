package com.olympus.oca.commerce.controllers;


import com.olympus.oca.commerce.constants.OcaoccConstants;

import com.olympus.oca.commerce.facades.salesforce.impl.DefaultOcaSalesforceIntegrationFacade;
import de.hybris.platform.commercefacades.order.data.SupportTeamContactListData;

import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;

import de.hybris.platform.commercewebservicescommons.dto.order.SupportTeamContactListDataWsDTO;

import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@ApiVersion("v2")
@Tag(name = "OCA Support Data Controller")
public class OcaSupportController {

    @Resource(name = "salesforceIntegrationFacade")
    private DefaultOcaSalesforceIntegrationFacade salesforceIntegrationFacade;

    @Resource (name = "dataMapper")
    private DataMapper dataMapper;

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = OcaoccConstants.OCC_OVERLAPPING_BASE_SITE_USER_PATH
            + "/support/getSupportTeamContactDetails", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "placeOcaOrder", summary = "Fetches Team Contact Details.", description = "Fetches Team Contact Details.")
    public SupportTeamContactListDataWsDTO getSupportTeamContactDetails(
            @Parameter(description = "soldTo : to be used to fetch the Team Contact Details", required = true) @RequestParam(required = true) final String soldTo,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        SupportTeamContactListData teamData = salesforceIntegrationFacade.getSupportTeamContactDetails(soldTo);
        SupportTeamContactListDataWsDTO responseWsDTO = dataMapper.map(teamData,SupportTeamContactListDataWsDTO.class);
        return responseWsDTO;
    }
}