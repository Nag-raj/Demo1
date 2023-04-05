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
 * This class contains the XiPay {@link ITransactionHeader} object returned from XiPay
 */
public class GetTransactionResult extends AbstractResult
{
	private ITransactionHeader xiTransaction;


	/**
	 * @return The current array of XiPay {@link ITransactionHeader} objects
	 */
	public ITransactionHeader getTransaction()
	{
		return this.xiTransaction;
	}

	/**
	 * @param xiTransaction
	 *           - Sets the current XiPay {@link ITransactionHeader} object
	 */
	public void setTransaction(final ITransactionHeader xiTransaction)
	{
		this.xiTransaction = xiTransaction;
	}

}
