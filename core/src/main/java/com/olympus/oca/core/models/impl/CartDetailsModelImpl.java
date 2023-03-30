package com.olympus.oca.core.models.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.olympus.oca.core.models.CartDetailsModel;
import com.olympus.oca.core.utils.LinkUtils;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {
		CartDetailsModel.class }, resourceType = CartDetailsModelImpl.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class CartDetailsModelImpl implements CartDetailsModel {
	
    protected static final String RESOURCE_TYPE = "olympus/components/commerce/cartdetails";

    @SlingObject
    private ResourceResolver resolver;

	@ValueMapValue
	private String checkoutPagePath;

	@ValueMapValue
	private String shopPagePath;

	@Override
	public String getCheckoutPagePath() {
		if (resolver != null && checkoutPagePath != null && !StringUtils.isEmpty(checkoutPagePath))
		{
			return LinkUtils.resolve(resolver, checkoutPagePath);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getShopPagePath() {
		if (resolver != null && shopPagePath != null && !StringUtils.isEmpty(shopPagePath))
		{
			return LinkUtils.resolve(resolver, shopPagePath);
		}
		return StringUtils.EMPTY;
	}
}
