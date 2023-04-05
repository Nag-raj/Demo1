/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.model.CustomerAccountInviteProcessModel;


/**
 * Velocity context for a customer email.
 */
public class CustomerAccountActivationEmailContext extends AbstractEmailContext<CustomerAccountInviteProcessModel>
{

	private static final String INVITATION_COUNT = "invitecount";
	private static final String EMAIL = "email";
	private static final String DISPLAY_NAME = "displayName";
	private static final String ENCODED_URL = "encodedUrl";
	private ConfigurationService configurationService;

	@Override
	public void init(final CustomerAccountInviteProcessModel crmCustomerAccountModel, final EmailPageModel emailPageModel)
	{
		super.init(crmCustomerAccountModel, emailPageModel);

		if (null != crmCustomerAccountModel.getCustomerAccount())
		{
			put(INVITATION_COUNT, crmCustomerAccountModel.getCustomerAccount().getInviteCount());
			put(EMAIL, crmCustomerAccountModel.getCustomerAccount().getEmail());
			put(DISPLAY_NAME, crmCustomerAccountModel.getCustomerAccount().getName());
			final String url = configurationService.getConfiguration().getString(OcaCoreConstants.CUSTOMER_INVITE_LINK);
			final String originalUrl = url + crmCustomerAccountModel.getCustomerAccount().getCustomerID();
			put(ENCODED_URL, originalUrl);

		}
	}

	@Override
	protected LanguageModel getEmailLanguage(final CustomerAccountInviteProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	@Override
	protected BaseSiteModel getSite(final CustomerAccountInviteProcessModel businessProcessModel)
	{
		// XXX Auto-generated method stub
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final CustomerAccountInviteProcessModel businessProcessModel)
	{
		// XXX Auto-generated method stub
		return businessProcessModel.getCustomer();
	}

	/**
	 * @return the configurationService
	 */
	@Override
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *                                the configurationService to set
	 */
	@Override
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


}
