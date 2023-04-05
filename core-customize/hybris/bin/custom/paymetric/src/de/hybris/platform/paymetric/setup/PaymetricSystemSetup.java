/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.paymetric.setup;

import static de.hybris.platform.paymetric.constants.PaymetricConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.paymetric.constants.PaymetricConstants;
import de.hybris.platform.paymetric.service.PaymetricService;


/**
 * You may use the @SystemSetup annotation in any ServiceLayer class to hook ServiceLayer code into the Hybris
 * initialization and update life-cycle events. In this way you can provide a means for creating essential and project
 * data for any extension.
 *
 */

@SystemSetup(extension = PaymetricConstants.EXTENSIONNAME)
@SuppressWarnings("unused")
public class PaymetricSystemSetup
{
	private final PaymetricService paymetricService;

	/**
	 * public constructor to set the paymetricService instance
	 */
	public PaymetricSystemSetup(final PaymetricService paymetricService)
	{
		this.paymetricService = paymetricService;
	}

	/**
	 * public method to upload impex files (if any) while Initialization and to create Essential data for paymetric
	 * extension.
	 */
	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		try
		{
			paymetricService.createLogo(PLATFORM_LOGO_CODE);
		}
		catch (final Exception ex)
		{
			ex.getMessage();
		}
	}


}
