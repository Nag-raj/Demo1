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
package de.hybris.platform.paymetric.service.impl;

import de.hybris.platform.paymetric.service.TokenizationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.paymetric.sdk.AccessTokenUtility;
import com.paymetric.sdk.XIConfig;
import com.paymetric.sdk.models.AccessTokenResponse.AccessTokenResponsePacket;
import com.paymetric.sdk.models.MerchantHtmlPacket.IFrameMerchantHtmlPacket;


/**
 *
 */
public class DefaultTokenizationService implements TokenizationService
{


	@Resource
	private ConfigurationService configurationService;

	public static final String GUID = XIConfig.CC_GUID.get();

	IFrameMerchantHtmlPacket packetRQ = null;
	AccessTokenResponsePacket packetRS = null;
	XIConfig xiConfig = new XIConfig();
	String IFrameURL = ""; 



	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}



	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}



	@Override
	public String getAccessToken(final HttpServletRequest request)
	{
		try
		{
			final String hostURI = request.getParameter("hostUri");

			packetRQ = xiConfig.getIFramePacket(request, hostURI);
			packetRS = AccessTokenUtility.GetIFrameAccessToken(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(), XIConfig.URL.get(),
					packetRQ);

			if (!(packetRS.getRequestError() + "").equals("null"))
			{
				final String strError = packetRS.getRequestError().getStatusCode() + "," + packetRS.getRequestError().getMessage();
				request.setAttribute("errormessage", strError);
				request.setAttribute("errorstyle", "class='errorMsg'");
			}
			else
			{
				final String accessToken = packetRS.getResponsePacket().getAccessToken();
				IFrameURL = XIConfig.URL.get() + "/view/iframe/" + XIConfig.CC_GUID.get() + "/" + accessToken + "/true";
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return IFrameURL;
	}

	@Override
	public String getResponsePacket(final HttpServletRequest request)
	{

		String response = "";

		try
		{
			final String strAccessToken = request.getParameter("accessToken");
			response = AccessTokenUtility.GetResponsePacket(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(), XIConfig.URL.get(),
					strAccessToken);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return response;
	}
	
	@Override
	public String getAccessTokenACH(final HttpServletRequest request)
	{
		try
		{
			final String hostURI = request.getParameter("hostUri");

			packetRQ = xiConfig.getIFramePacketACH(request, hostURI);
			packetRS = AccessTokenUtility.GetIFrameAccessToken(XIConfig.EC_GUID.get(), XIConfig.EC_PSK.get(), XIConfig.URL.get(),
					packetRQ);

			if (!(packetRS.getRequestError() + "").equals("null"))
			{
				final String strError = packetRS.getRequestError().getStatusCode() + "," + packetRS.getRequestError().getMessage();
				request.setAttribute("errormessage", strError);
				request.setAttribute("errorstyle", "class='errorMsg'");
			}
			else
			{
				final String accessToken = packetRS.getResponsePacket().getAccessToken();
				IFrameURL = XIConfig.URL.get() + "/view/iframe/" + XIConfig.EC_GUID.get() + "/" + accessToken + "/true";
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return IFrameURL;
	}
	
	@Override
	public String getResponsePacketACH(final HttpServletRequest request)
	{

		String response = "";

		try
		{
			final String strAccessToken = request.getParameter("accessToken");
			response = AccessTokenUtility.GetResponsePacket(XIConfig.EC_GUID.get(), XIConfig.EC_PSK.get(), XIConfig.URL.get(),
					strAccessToken);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return response;
	}
}
