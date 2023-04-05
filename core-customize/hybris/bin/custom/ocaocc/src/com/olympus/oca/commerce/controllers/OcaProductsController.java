package com.olympus.oca.commerce.controllers;

import com.olympus.oca.commerce.dto.user.CategoryHierarchyDataList;
import com.olympus.oca.commerce.dto.user.CategoryHierarchyListWsDTO;
import com.olympus.oca.commerce.facades.category.impl.DefaultOcaCategoryFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@Tag(name = "OCA Products")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/ocaProducts")
public class OcaProductsController {

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;
    @Resource(name = "ocaCategoryFacade")
    private DefaultOcaCategoryFacade defaultOcaCategoryFacade;

    @RequestMapping(value = "/categoryHierarchy", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getCategoryHierarchy",
               summary = "Gets the category hierarchy structure of categories",
               description = "Returns information "
                             + "about the"
                             + "  category hierarchy that exists in a catalog version available for the current base store.")
    @ApiBaseSiteIdParam
    public CategoryHierarchyListWsDTO getCategoryHierarchy(@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields) {
        final CategoryHierarchyDataList categoryHierarchyDataList = new CategoryHierarchyDataList();
        categoryHierarchyDataList.setCategoryHierarchy(defaultOcaCategoryFacade.getFilteredCategory());
        return dataMapper.map(categoryHierarchyDataList, CategoryHierarchyListWsDTO.class, fields);
    }

}