/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2020 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.service;

/**
 * @author Sanket.Kulkarni
 *
 */
 
import javax.servlet.http.HttpServletRequest;


/**
 *
 */
public interface TokenizationService
{
	String getAccessToken(HttpServletRequest request);

	String getResponsePacket(HttpServletRequest request);
	
	String getAccessTokenACH(HttpServletRequest request);
	
	String getResponsePacketACH(HttpServletRequest request);
}
