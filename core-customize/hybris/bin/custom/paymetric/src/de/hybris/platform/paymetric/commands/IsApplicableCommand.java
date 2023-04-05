/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.commands;

import de.hybris.platform.payment.commands.request.IsApplicableCommandReqest;
import de.hybris.platform.payment.commands.result.IsApplicableCommandResult;
import de.hybris.platform.paymetric.constants.PaymetricConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.primesys.xipayws.CTimer;


/**
 * Command that each payment provider must implement - configuration that check if for specified arguments payment
 * provider is applicable
 */
public class IsApplicableCommand implements de.hybris.platform.payment.commands.IsApplicableCommand
{
	private static final Logger LOG = LoggerFactory.getLogger(IsApplicableCommand.class);

	/**
	 * IsApplicableCommand specific perform implementation
	 */
	@Override
	public IsApplicableCommandResult perform(final IsApplicableCommandReqest request)
	{
		final String strWhere = PaymetricConstants.getWhere("perform");
		final CTimer timer = (PaymetricConstants.getProfilingEnabled() ? new CTimer() : null);
		IsApplicableCommandResult result = new IsApplicableCommandResult(false);


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Validate the Hybris Request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (request != null && request.getCard() != null && request.getCard().getCardNumber() != null)
			{
				result = new IsApplicableCommandResult(isToken(request.getCard().getCardNumber()));
			}
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}

	private boolean isToken(String strCardNo)
	{
		boolean bResult = false;

		if (strCardNo != null && strCardNo.length() > 0)
		{
			strCardNo = strCardNo.toUpperCase();
			if (strCardNo.length() == 25 && strCardNo.startsWith("-E"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 14 && strCardNo.startsWith("7"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 16 && strCardNo.startsWith("8"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 16 && strCardNo.startsWith("11"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 17 && strCardNo.startsWith("T"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 19 && strCardNo.startsWith("8"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 19 && strCardNo.startsWith("11"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 19 && strCardNo.startsWith("T"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 20 && strCardNo.startsWith("8"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 20 && strCardNo.startsWith("T"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 24 && strCardNo.startsWith("804424"))
			{
				bResult = true;
			}
			else if (strCardNo.length() == 25 && strCardNo.startsWith("T"))
			{
				bResult = true;
			}
		}

		return bResult;
	}

}
