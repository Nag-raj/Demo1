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
 * This class contains Transaction ID to retrieve from XiPay
 */
public class GetTransactionRequest extends AbstractRequest
{
	private String strTransactionID;


	/**
	 * the default constructor
	 */
	public GetTransactionRequest()
	{
		this(null, null);
	}

	/**
	 * the constructor takes a Merchant Transaction Code
	 */
	public GetTransactionRequest(final String merchantTransactionCode)
	{
		this(merchantTransactionCode, null);
	}

	/**
	 * the constructor takes a Merchant Transaction Code and the XiPay Transaction ID
	 */
	public GetTransactionRequest(final String merchantTransactionCode, final String strTransactionID)
	{
		super(merchantTransactionCode);
		this.strTransactionID = strTransactionID;
	}

	/**
	 * @return The current XiPay Transaction ID for this request
	 */
	public String getTransactionID()
	{
		return this.strTransactionID;
	}

	/**
	 * @param strTransactionID
	 *           Sets the current XiPay Transaction ID for this request
	 */
	public void setBatchID(final String strTransactionID)
	{
		this.strTransactionID = strTransactionID;
	}
}
