package com.olympus.oca.commerce.core.product.interceptors;

import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.CAPITAL_MGRP;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.CAPITAL_MG4;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.GHOST_PRODUCT_1_DCS;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.GHOST_PRODUCT_2_MG_4;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.PURCHASABLE_DCS;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.PURCHASABLE_MG4;
import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.TEMP_NON_PURCHASABLE_DCS;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import org.apache.log4j.Logger;
import com.olympus.oca.commerce.core.enums.NonPurchasableDisplayStatus;

import java.util.Arrays;
import java.util.List;

public class OcaProductPrepareInterceptor implements PrepareInterceptor
{

	private static final Logger LOG = Logger.getLogger(OcaProductPrepareInterceptor.class);

	private ConfigurationService configurationService;

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		final List<String> purchasableDCS = Arrays.asList(getConfigurationService().getConfiguration().getString(PURCHASABLE_DCS,"").split(","));
		final List<String> purchasableMG4 = Arrays.asList(getConfigurationService().getConfiguration().getString(PURCHASABLE_MG4).split(","));
		final List<String> tempNonPurchasableDCS = Arrays.asList(getConfigurationService().getConfiguration().getString(TEMP_NON_PURCHASABLE_DCS).split(","));
		final List<String> capitalMG4 = Arrays.asList(getConfigurationService().getConfiguration().getString(CAPITAL_MG4).split(","));
		final List<String> capitalMGRP = Arrays.asList(getConfigurationService().getConfiguration().getString(CAPITAL_MGRP).split(","));
		final List<String> ghostProductMG4 = Arrays.asList(getConfigurationService().getConfiguration().getString(GHOST_PRODUCT_2_MG_4).split(","));
		final List<String> ghostProductDCS = Arrays.asList(getConfigurationService().getConfiguration().getString(GHOST_PRODUCT_1_DCS).split(","));

		if (model instanceof ProductModel)
		{
			if (ctx.isModified(model, ProductModel.DISTRIBUTIONCHAINSTATUS) || ctx.isModified(model, ProductModel.MATERIALGROUP)
					|| ctx.isModified(model, ProductModel.MATERIALGROUP4))
			{

				final ProductModel product = ((ProductModel) model);

				if ((null == product.getDistributionChainStatus() || purchasableDCS.contains(product.getDistributionChainStatus()))
						&& (null == product.getMaterialGroup4() || purchasableMG4.contains(product.getMaterialGroup4())))
				{
					product.setSearchEnabled(true);
					product.setPurchaseEnabled(true);
				}

				if (null != product.getDistributionChainStatus()
						&& tempNonPurchasableDCS.contains(product.getDistributionChainStatus()))
				{
					product.setNonPurchasableDisplayStatus(NonPurchasableDisplayStatus.TEMP_UNAVAILABLE);
				}

				if ((null != product.getMaterialGroup4() && capitalMG4.contains(product.getMaterialGroup4()))
						|| (null != product.getMaterialGroup() && capitalMGRP.contains(product.getMaterialGroup())))
				{
					product.setNonPurchasableDisplayStatus(NonPurchasableDisplayStatus.CONTACT_SALES_REP);

				}
				if ((null != product.getDistributionChainStatus()
						&& tempNonPurchasableDCS.contains(product.getDistributionChainStatus()))
						|| ((null != product.getMaterialGroup4() && capitalMG4.contains(product.getMaterialGroup4()))
								|| (null != product.getMaterialGroup() && capitalMGRP.contains(product.getMaterialGroup()))))
				{
					product.setSearchEnabled(true);
					product.setPurchaseEnabled(false);
				}
				if ((null != product.getMaterialGroup4() && ghostProductMG4.contains(product.getMaterialGroup4()))
						|| ((null != product.getDistributionChainStatus()
								&& ghostProductDCS.contains(product.getDistributionChainStatus()))))
				{
					product.setSearchEnabled(false);
					product.setPurchaseEnabled(false);
				}

				if (!(ctx.isModified(model, ProductModel.SEARCHENABLED) || ctx.isModified(model, ProductModel.PURCHASEENABLED)))
				{
					LOG.info(product.getCode() + " - Product doesn't match any visibility rules");
				}

			}
		}
	}
}
