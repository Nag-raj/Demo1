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
package de.hybris.platform.paymetric.service;

/**
 * Interface created for the purpose of coding to interface standard
 */
public interface PaymetricService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);
}
