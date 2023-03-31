package com.olympus.oca.core.models.impl;

import com.olympus.oca.core.models.CheckoutModel;
import com.olympus.oca.core.utils.LinkUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {
		CheckoutModel.class }, resourceType = "olympus/components/commerce/checkoutpage", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class CheckoutModelImpl implements CheckoutModel {

	@SlingObject
	private ResourceResolver resolver;

	@ValueMapValue
	private String disclaimer;

	@ValueMapValue
	private String termsofuse;

	@ValueMapValue
	private String termsofuseurl;

	@ValueMapValue
	private String checkoutPagePath;

	@ValueMapValue
	private String shopPagePath;
	
	@ValueMapValue
	private String paymentMethodText;

	@ValueMapValue
	private String paymentMethodDesc;

	@ValueMapValue
	private String payWithCreditCard;

	@ValueMapValue
	private String payViaInvoice;

	@Override
	public String getPaymentMethodText() {
		return paymentMethodText;
	}

	@Override
	public String getPaymentMethodDesc() {
		return paymentMethodDesc;
	}

	@Override
	public String getPayWithCreditCard() {
		return payWithCreditCard;
	}

	@Override
	public String getPayViaInvoice() {
		return payViaInvoice;
	}

	@Override
	public String getDisclaimerText() {
		return disclaimer;
	}

	@Override
	public String getTermsofuseText() {
		return termsofuse;
	}

	@Override
	public String getTermsofusePath() {
		if (resolver != null && termsofuseurl != null && !StringUtils.isEmpty(termsofuseurl)) {
			return LinkUtils.resolve(resolver, termsofuseurl);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getCheckoutPagePath() {
		if (resolver != null && checkoutPagePath != null && !StringUtils.isEmpty(checkoutPagePath)) {
			return LinkUtils.resolve(resolver, checkoutPagePath);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getShopPagePath() {
		if (resolver != null && shopPagePath != null && !StringUtils.isEmpty(shopPagePath)) {
			return LinkUtils.resolve(resolver, shopPagePath);
		}
		return StringUtils.EMPTY;
	}
}
