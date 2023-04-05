/**
 *
 */
package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.GenericVariantProductModel;


/**
 * The Class OcaRecentlyOrderedProductPopulator.
 */
public class OcaRecentlyOrderedProductPopulator implements Populator<OrderEntryModel, RecentlyOrderedProductData>
{

	/**
	 * Populate.
	 *
	 * @param source
	 *                  the source
	 * @param target
	 *                  the target
	 * @throws ConversionException
	 *                                the conversion exception
	 */
	@Override
	public void populate(final OrderEntryModel source, final RecentlyOrderedProductData target) throws ConversionException
	{
		target.setCode(source.getProduct().getCode());
		if (source.getProduct() instanceof GenericVariantProductModel)
		{
			target.setName(((GenericVariantProductModel) source.getProduct()).getBaseProduct().getName());
		}
		target.setQuantity(source.getQuantity().intValue());
		target.setPurchaseEnabled((source.getProduct().isPurchaseEnabled()));
		target.setModelNumber(source.getProduct().getModelNumber());
	}
}
