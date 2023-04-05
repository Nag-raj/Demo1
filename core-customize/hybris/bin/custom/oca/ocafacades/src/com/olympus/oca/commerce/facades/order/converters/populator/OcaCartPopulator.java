package com.olympus.oca.commerce.facades.order.converters.populator;

import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.enums.PaymentTerm;
import com.olympus.oca.commerce.core.model.HeavyOrderQuestionsModel;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.HeavyOrderQuestionsCartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;


import java.util.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import static java.util.Optional.ofNullable;

/**
 * OcaCartPopulator
 * Adds the shipping notifications to cart model based on the weight and attributes of the product.
 */
public class OcaCartPopulator extends CartPopulator {

    /**
     * Threshold weight limit to decide whether products will be shipped by ground.
     */
    private static final String THRESHOLD_WEIGHT = "150";
    private ModelService modelService;

    private PriceDataFactory priceDataFactory;

    private ConfigurationService configurationService;

    @Resource(name = "b2bCheckoutFacade")
    private CheckoutFacade checkoutFacade;

    @Override
    public void populate(CartModel source, CartData target) {
        addShippingNotifications(source, target);
        addAvailablePaymentTypes(source, target);
    }

    /**
     * This method is to add the boolean values to determine if the order is going to ship by ground and also the gross weight will be in LB.
     *
     * @param source - The cart model object.
     * @param target - The response cart data object.
     */
    public void addShippingNotifications(final CartModel source, final CartData target) {
        AtomicReference<Double> totalShipmentWeight = new AtomicReference<>((double) 0);
        String loadingGroupSBG = getConfigurationService().getConfiguration()
                .getString(OcaCoreConstants.SHIP_BY_GROUND_LOADING_GROUP,"0001");
        String materialGroupSBG = getConfigurationService().getConfiguration()
                .getString(OcaCoreConstants.SHIP_BY_GROUND_MATERIAL_GROUP,"9004");
        source.getEntries().forEach(abstractOrderEntryModel -> {
            ProductModel product = abstractOrderEntryModel.getProduct();
            Optional<String> loadingGroup = ofNullable(product.getLoadingGroup());
            Optional<String> materialGroup = ofNullable(product.getMaterialGroup());
            if (!target.isShipByGround()) {
                target.setShipByGround((loadingGroup.isPresent() ?
                        loadingGroupSBG.equalsIgnoreCase(product.getLoadingGroup())
                        : Boolean.FALSE) ||
                        (materialGroup.isPresent() ?
                                materialGroupSBG.equalsIgnoreCase(product.getMaterialGroup())
                                : Boolean.FALSE));
            }
            totalShipmentWeight.updateAndGet(totalWeight -> totalWeight
                    + (abstractOrderEntryModel.getQuantity() * (ofNullable(product.getGrossWeight()).orElse((double) 0))));
        });
        if (totalShipmentWeight.get() > (Double.parseDouble(THRESHOLD_WEIGHT))) {
            target.setHeavyOrder(true);
            if (Objects.nonNull(source.getHeavyOrderQuestions())) {
                HeavyOrderQuestionsCartData heavyOrderQuestionsCartData = new HeavyOrderQuestionsCartData();
                HeavyOrderQuestionsModel heavyOrderQuestionsModel = source.getHeavyOrderQuestions();
                heavyOrderQuestionsCartData.setEmail(heavyOrderQuestionsModel.getEmail());
                heavyOrderQuestionsCartData.setName(heavyOrderQuestionsModel.getName());
                heavyOrderQuestionsCartData.setPhoneNumber(heavyOrderQuestionsModel.getPhoneNumber());
                heavyOrderQuestionsCartData.setTruckSize(heavyOrderQuestionsModel.getTruckSize());
                heavyOrderQuestionsCartData.setLargeTruckEntry(heavyOrderQuestionsModel.isLargeTruckEntry());
                heavyOrderQuestionsCartData.setLiftAvailable(heavyOrderQuestionsModel.isLiftAvailable());
                heavyOrderQuestionsCartData.setLoadingDock(heavyOrderQuestionsModel.isLoadingDock());
                target.setHeavyOrderQuestions(heavyOrderQuestionsCartData);
            }
        } else {
            target.setHeavyOrder(false);
            source.setHeavyOrderQuestions(null);
            getModelService().save(source);
        }
    }

    private void addAvailablePaymentTypes(final CartModel source, final CartData target){
		final List<B2BPaymentTypeData> paymentTypeDatas = checkoutFacade.getPaymentTypes();
		paymentTypeDatas.stream()
				.forEach(paymentType -> paymentType
						.setIsActive(!(null != source.getUnit() && PaymentTerm.NT01.equals(source.getUnit().getPaymentTerm())
                                       && CheckoutPaymentType.ACCOUNT.getCode().equals(paymentType.getCode()))));

		target.setAvailablePaymentTypes(paymentTypeDatas);
	 }


    @Override
    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Override
    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
