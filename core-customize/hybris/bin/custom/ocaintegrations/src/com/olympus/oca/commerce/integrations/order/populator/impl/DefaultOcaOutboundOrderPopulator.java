package com.olympus.oca.commerce.integrations.order.populator.impl;

import com.olympus.oca.commerce.core.model.HeavyOrderQuestionsModel;
import com.olympus.oca.commerce.integrations.constants.OcaIntegrationConstants;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchangeb2b.outbound.impl.DefaultB2BPartnerContributor;
import de.hybris.platform.sap.sapcpiadapter.model.*;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.*;

public class DefaultOcaOutboundOrderPopulator implements Populator<AbstractOrderModel, SAPCpiOutboundOrderModel> {

    public static final String SPACE = " ";
    public static final String DELIM = ",";

    @Resource(name = "ocaB2BPartnerContributor")
    private DefaultB2BPartnerContributor partnerContributor;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public void populate(AbstractOrderModel orderModel, SAPCpiOutboundOrderModel outboundOrderModel) throws ConversionException {
        outboundOrderModel.setOrderId(orderModel.getCode());
        if(null != orderModel.getThirdPartyShippingAccount()){
            outboundOrderModel.setShippingCarrierCode(orderModel.getThirdPartyShippingAccount().getShippingCarrierCode());
        }
        outboundOrderModel.setDeliveryOptionCode(orderModel.getDeliveryOption());
        outboundOrderModel.setAdditionalAddressLine(orderModel.getAdditionalAddressLine());
        outboundOrderModel.setPurchaseOrderNumber(orderModel.getPurchaseOrderNumber());

        setSapCpiHeavyOrderQuestionsModel(orderModel, outboundOrderModel);
        setSapCpiOrderItemDetails(orderModel, outboundOrderModel);
        setSapCpiOrderPaymentDetails(orderModel, outboundOrderModel);
        setSapCpiPartnerRoles(orderModel, outboundOrderModel);
        setSapCpiOutboundConfig(outboundOrderModel);
    }

    private void setSapCpiPartnerRoles(AbstractOrderModel orderModel, SAPCpiOutboundOrderModel outboundOrderModel) {
        Set<SAPCpiOutboundPartnerRoleModel> partnerRoleModels = new HashSet<>();

        final List<Map<String, Object>> partnerRows = partnerContributor.createRows((OrderModel) orderModel);

        for(Map<String, Object> partner : partnerRows){
            final SAPCpiOutboundPartnerRoleModel partnerRoleModel = new SAPCpiOutboundPartnerRoleModel();
            final String partnerRoleCode = (String) partner.get(PartnerCsvColumns.PARTNER_ROLE_CODE);
            final String partnerCode = (String) partner.get(PartnerCsvColumns.PARTNER_CODE);
            partnerRoleModel.setPartnerRoleCode(partnerRoleCode);
            partnerRoleModel.setPartnerId(partnerCode);
            partnerRoleModels.add(partnerRoleModel);
        }
        outboundOrderModel.setSapCpiOutboundPartnerRoles(partnerRoleModels);

    }

    private void setSapCpiOrderPaymentDetails(AbstractOrderModel orderModel, SAPCpiOutboundOrderModel outboundOrderModel) {
        if(null != orderModel.getPaymentInfo() && orderModel.getPaymentInfo() instanceof final CreditCardPaymentInfoModel cardPaymentModel){
            final SAPCpiOutboundCardPaymentModel outboundCardPaymentModel = new SAPCpiOutboundCardPaymentModel();
            outboundCardPaymentModel.setCcOwner(cardPaymentModel.getCcOwner());
            outboundCardPaymentModel.setTokenizedCCNumber(cardPaymentModel.getNumber());
            outboundCardPaymentModel.setSubscriptionId(cardPaymentModel.getSubscriptionId());
            outboundCardPaymentModel.setValidToMonth(cardPaymentModel.getValidToMonth());
            outboundCardPaymentModel.setValidToYear(cardPaymentModel.getValidToYear());
            setTransactionIdToOutboundOrder(orderModel, outboundCardPaymentModel);
            outboundOrderModel.setSapCpiOutboundCardPayments(Set.of(outboundCardPaymentModel));
        }
    }

    private void setTransactionIdToOutboundOrder(AbstractOrderModel orderModel, SAPCpiOutboundCardPaymentModel outboundCardPaymentModel) {
        Optional<PaymentTransactionEntryModel> transactionEntry = orderModel.getPaymentTransactions().stream()
                .flatMap(transaction -> transaction.getEntries().stream())
                .filter(entry -> getConfigProperty(OcaIntegrationConstants.PAYMETRIC_AUTH_SUCCESS_RESPONSE_CODE).equals(entry.getXiPayResponseCode()))
                .findFirst();
        String transactionId = transactionEntry.isPresent() ?
                transactionEntry.get().getAUTRA() : StringUtils.EMPTY;
        outboundCardPaymentModel.setTransactionId(transactionId);
    }

    private void setSapCpiOrderItemDetails(AbstractOrderModel orderModel, SAPCpiOutboundOrderModel outboundOrderModel) {
        final Set<SAPCpiOutboundOrderItemModel> sapOrderItems = new HashSet<>();
        for (AbstractOrderEntryModel orderEntryModel : orderModel.getEntries()) {
            SAPCpiOutboundOrderItemModel sapOrderItem = new SAPCpiOutboundOrderItemModel();
            sapOrderItem.setProductCode(orderEntryModel.getProduct().getCode());
            sapOrderItem.setProductName(orderEntryModel.getProduct().getName());
            sapOrderItem.setEntryNumber(orderEntryModel.getEntryNumber().toString());
            sapOrderItem.setQuantity(orderEntryModel.getQuantity().toString());
            sapOrderItem.setUnit(orderEntryModel.getUnit().getCode());
            sapOrderItem.setLineItemPrice(orderEntryModel.getTotalPrice());
            sapOrderItem.setFreightPrice(orderEntryModel.getFreightPrice());
            sapOrderItems.add(sapOrderItem);
        }
        outboundOrderModel.setSapCpiOutboundOrderItems(sapOrderItems);
    }

    private void setSapCpiHeavyOrderQuestionsModel(AbstractOrderModel orderModel, SAPCpiOutboundOrderModel outboundOrderModel) {
        StringBuilder heavyOrderQuestionnaire = new StringBuilder();
        if(null != orderModel.getHeavyOrderQuestions()){
            HeavyOrderQuestionsModel heavyOrderQuestionsModel = orderModel.getHeavyOrderQuestions();
            heavyOrderQuestionnaire.append(HeavyOrderQuestionsModel.NAME + SPACE)
                    .append(heavyOrderQuestionsModel.getName()).append(DELIM)
                    .append(HeavyOrderQuestionsModel.EMAIL + SPACE)
                    .append(heavyOrderQuestionsModel.getEmail()).append(DELIM)
                    .append(HeavyOrderQuestionsModel.PHONENUMBER + SPACE)
                    .append(heavyOrderQuestionsModel.getPhoneNumber()).append(DELIM)
                    .append(HeavyOrderQuestionsModel.TRUCKSIZE + SPACE)
                    .append(heavyOrderQuestionsModel.getTruckSize()).append(DELIM)
                    .append(HeavyOrderQuestionsModel.LARGETRUCKENTRY + SPACE)
                    .append(heavyOrderQuestionsModel.isLargeTruckEntry()).append(DELIM)
                    .append(HeavyOrderQuestionsModel.LIFTAVAILABLE + SPACE)
                    .append(heavyOrderQuestionsModel.isLiftAvailable()).append(DELIM)
                    .append(HeavyOrderQuestionsModel.LOADINGDOCK + SPACE)
                    .append(heavyOrderQuestionsModel.isLoadingDock()).append(DELIM)
                    .append(HeavyOrderQuestionsModel.ORDERDELIVEREDINSIDE + SPACE)
                    .append(heavyOrderQuestionsModel.isOrderDeliveredInside());
        }
        outboundOrderModel.setSapCpiHeavyOrderQuestions(heavyOrderQuestionnaire.toString());
    }

    private void setSapCpiOutboundConfig(SAPCpiOutboundOrderModel outboundOrderModel) {
        SAPCpiOutboundConfigModel sapCpiConfigModel = new SAPCpiOutboundConfigModel();
        sapCpiConfigModel.setSenderName(getConfigProperty(OcaIntegrationConstants.SAP_CPI_CONFIG_SENDER_NAME));
        sapCpiConfigModel.setSenderPort(getConfigProperty(OcaIntegrationConstants.SAP_CPI_CONFIG_SENDER_PORT));
        sapCpiConfigModel.setReceiverName(getConfigProperty(OcaIntegrationConstants.SAP_CPI_CONFIG_RECEIVER_NAME));
        sapCpiConfigModel.setReceiverPort(getConfigProperty(OcaIntegrationConstants.SAP_CPI_CONFIG_RECEIVER_PORT));
        sapCpiConfigModel.setClient(getConfigProperty(OcaIntegrationConstants.SAP_CPI_CONFIG_CLIENT));
        sapCpiConfigModel.setUrl(getConfigProperty(OcaIntegrationConstants.SAP_CPI_CONFIG_URL));
        sapCpiConfigModel.setUsername(getConfigProperty(OcaIntegrationConstants.SAP_CPI_CONFIG_USERNAME));
        outboundOrderModel.setSapCpiConfig(sapCpiConfigModel);
    }

    private String getConfigProperty(String key) {
        final Configuration configuration = configurationService.getConfiguration();
        return configuration.getString(key);
    }

}
