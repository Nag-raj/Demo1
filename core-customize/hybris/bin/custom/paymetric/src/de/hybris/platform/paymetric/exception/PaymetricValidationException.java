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
package de.hybris.platform.paymetric.exception;

import javax.validation.ValidationException;


/**
 * Paymetric extension specific exception class
 */
public class PaymetricValidationException extends ValidationException
{

	/**
	 * public constructor for handling error message.
	 */
	public PaymetricValidationException(final String error)
	{
		super(error);
	}

	/**
	 * no arg public constructor
	 */
	public PaymetricValidationException()
	{
		super();
	}
}
