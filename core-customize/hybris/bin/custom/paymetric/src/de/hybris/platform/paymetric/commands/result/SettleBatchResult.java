/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.commands.result;

import de.hybris.platform.payment.commands.result.AbstractResult;

import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;


/**
 * This class contains an array of XiPay {@link ITransactionHeader} objects that were submitted for settlement with
 * their individual XiPay status
 */
public class SettleBatchResult extends AbstractResult
{
	private ITransactionHeader[] xiTransactions;


	/**
	 * @return The current array of XiPay {@link ITransactionHeader} objects
	 */
	public ITransactionHeader[] getTransactions()
	{
		return this.xiTransactions;
	}

	/**
	 * @param xiTransactions
	 *           - Sets the current array of XiPay {@link ITransactionHeader} objects
	 */
	public void setTransactions(final ITransactionHeader[] xiTransactions)
	{
		this.xiTransactions = xiTransactions;
	}

}
