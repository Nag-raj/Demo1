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


/**
 * This class contains the XiPay Batch ID to retrieve the status of a submitted batch for settlement
 */
public class GetBatchStatusRequest extends AbstractRequest
{
	private String strBatchID;


	/**
	 * the default constructor
	 */
	public GetBatchStatusRequest()
	{
		this(null, null);
	}

	/**
	 * the constructor takes a Merchant Transaction Code
	 */
	public GetBatchStatusRequest(final String merchantTransactionCode)
	{
		this(merchantTransactionCode, null);
	}

	/**
	 * the constructor takes a Merchant Transaction Code and the XiPay Batch ID
	 */
	public GetBatchStatusRequest(final String merchantTransactionCode, final String strBatchID)
	{
		super(merchantTransactionCode);
		this.strBatchID = strBatchID;
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
