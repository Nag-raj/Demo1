package com.olympus.oca.commerce.integrations.outbound.orderexchange.impl;

import com.olympus.oca.commerce.core.enums.PartnerFunctionCode;
import com.olympus.oca.commerce.core.enums.ShippingMethodType;
import com.olympus.oca.commerce.integrations.constants.OcaIntegrationConstants;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchangeb2b.outbound.impl.DefaultB2BPartnerContributor;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;

public class DefaultOcaB2BPartnerContributor extends DefaultB2BPartnerContributor {

    @Resource(name = "b2bUnitService")
    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    protected List<Map<String, Object>> createB2BRows(final OrderModel order) {

        final Map<String, Object> soldToRow = createPartnerRow(order, PartnerFunctionCode.SOLD_TO, soldToFromOrder(order));
        final Map<String, Object> contactRow = createPartnerRow(order, PartnerFunctionCode.CONTACT_PERSON, contactFromOrder(order));

        final Map<String, Object> shipToRow = createPartnerRow(order, PartnerFunctionCode.SHIP_TO, shipToFromOrder(order));
        final Map<String, Object> billToRow = createPartnerRow(order, PartnerFunctionCode.BILL_TO, partnerCodeFromOrder(order, PartnerFunctionCode.BILL_TO));
        final Map<String, Object> payerRow = createPartnerRow(order, PartnerFunctionCode.PAYER, partnerCodeFromOrder(order, PartnerFunctionCode.PAYER));

        final String shippingCarrierAcNumber = getShippingCarrierAccountNumber(order, PartnerFunctionCode.THIRD_PARTY_CARRIER);
        Map<String, Object> thirdPartyCarrierRow = new HashMap<>();
        if(StringUtils.isNotEmpty(shippingCarrierAcNumber)){
            thirdPartyCarrierRow = createPartnerRow(order, PartnerFunctionCode.THIRD_PARTY_CARRIER, shippingCarrierAcNumber);
        }

        final Map<String, Object> empResponsibleRow = createPartnerRow(order, PartnerFunctionCode.EMPLOYEE_RESPONSIBLE, getEmployeeResponsiblePartnerId(PartnerFunctionCode.EMPLOYEE_RESPONSIBLE));

        final List<Map<String, Object>> result = new ArrayList<>(3);

        addToResult(soldToRow, result);
        addToResult(contactRow, result);
        addToResult(shipToRow, result);
        addToResult(billToRow, result);
        addToResult(payerRow, result);
        addToResult(thirdPartyCarrierRow, result);
        addToResult(empResponsibleRow, result);

        return result;
    }

    protected Map<String, Object> createPartnerRow(final OrderModel order, PartnerFunctionCode partnerRole, String partnerId) {
        final String partnerRoleCode = getConfigValue(partnerRole, OcaIntegrationConstants.OCA_ORDER_PARTNER_FUNCTION);
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRoleCode);
        row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
        return row;
    }

    @Override
    protected String soldToFromOrder(final OrderModel order)
    {
        return order.getUnit().getUid();
    }

    protected String shipToFromOrder(OrderModel order) {
        return null != order.getDeliveryAddress()
                && null != order.getDeliveryAddress().getParentPartnerId()
                ? order.getDeliveryAddress().getParentPartnerId()
                : soldToFromOrder(order);
    }

    protected String partnerCodeFromOrder(OrderModel order, PartnerFunctionCode partnerFunctionCode) {
        Optional<B2BUnitModel> b2bUnit = getB2BUnitModel(order, partnerFunctionCode);
        return b2bUnit.isPresent() ? b2bUnit.get().getUid() : order.getUnit().getUid();
    }

    private Optional<B2BUnitModel> getB2BUnitModel(OrderModel order, PartnerFunctionCode partnerFunctionCode) {
        Optional<B2BUnitModel> b2bUnit =
                b2bUnitService.getBranch(order.getUnit()).stream()
                        .filter(unit -> unit.getPartnerFunctionType().contains(partnerFunctionCode))
                        .findFirst();
        return b2bUnit;
    }

    protected String getShippingCarrierAccountNumber(final OrderModel order, final PartnerFunctionCode partnerFunctionCode)
    {
        String shippingCarrierAccountNumber;
        if (ShippingMethodType.SHIP_BY_OLYMPUS.equals(order.getSelectedShippingMethod()))
        {
            shippingCarrierAccountNumber = StringUtils.EMPTY;
        }
        else
        {
            if (ShippingMethodType.THIRD_PARTY.equals(order.getSelectedShippingMethod()))
            {
                shippingCarrierAccountNumber = configurationService.getConfiguration()
                        .getString(OcaIntegrationConstants.OCA_THIRD_PARTY_CARRIER_ACCOUNT_NUMBER
                                + order.getThirdPartyShippingAccount().getShippingCarrierCode());
            }
            else
            {
                final Optional<B2BUnitModel> b2bUnit = getB2BUnitModel(order, partnerFunctionCode);
                shippingCarrierAccountNumber = b2bUnit.isPresent() ? b2bUnit.get().getUid() : StringUtils.EMPTY;
            }
        }
        return shippingCarrierAccountNumber;
    }

    private String getEmployeeResponsiblePartnerId(PartnerFunctionCode partnerRole) {
        return getConfigValue(partnerRole, OcaIntegrationConstants.OCA_ORDER_PARTNER_FUNCTION_PARTNER_ID);
    }

    private static void addToResult(Map<String, Object> soldToRow, List<Map<String, Object>> result) {
        if (!MapUtils.isEmpty(soldToRow)) {
            result.add(soldToRow);
        }
    }

    private String getConfigValue(PartnerFunctionCode partnerRole, String partnerFunctionKey) {
        final Configuration configuration = configurationService.getConfiguration();
        final String configValue = configuration.getString(partnerFunctionKey + partnerRole.getCode());
        return configValue;
    }

}
