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
package de.hybris.platform.paymetric.commands.request;

import de.hybris.platform.payment.commands.request.AbstractRequest;

import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;


/**
 * This class contains an array of XiPay {@link ITransactionHeader} objects and their associated Batch ID to be
 * submitted for settlement
 */
public class SettleBatchRequest extends AbstractRequest
{
	private ITransactionHeader[] xiTransactions;
	private String strBatchID;


	/**
	 * the default constructor
	 */
	public SettleBatchRequest()
	{
		this(null, null, null);
	}

	/**
	 * the constructor takes a Merchant Transaction Code
	 */
	public SettleBatchRequest(final String merchantTransactionCode)
	{
		this(merchantTransactionCode, null, null);
	}

	/**
	 * the constructor takes a Merchant Transaction Code, an array of XiPay {@link ITransactionHeader} objects and their
	 * associated Batch ID
	 */
	public SettleBatchRequest(final String merchantTransactionCode, final ITransactionHeader[] xiTransactions,
			final String strBatchID)
	{
		super(merchantTransactionCode);
		this.strBatchID = strBatchID;
		this.xiTransactions = xiTransactions;
	}

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

	/**
	 * @return The current XiPay Batch ID for this request
	 */
	public String getBatchID()
	{
		return this.strBatchID;
	}

	/**
	 * @param strBatchID
	 *           Sets the current XiPay Batch ID for this request
	 */
	public void setBatchID(final String strBatchID)
	{
		this.strBatchID = strBatchID;
	}
}
