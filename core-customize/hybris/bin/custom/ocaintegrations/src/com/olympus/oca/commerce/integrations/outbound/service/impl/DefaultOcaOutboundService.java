package com.olympus.oca.commerce.integrations.outbound.service.impl;

import com.olympus.oca.commerce.integrations.model.BTPOutboundContractPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundSalesforceRequestModel;
import com.olympus.oca.commerce.integrations.model.VertexRequestModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class DefaultOcaOutboundService implements OcaOutboundService {
    private static final String SALESFORCE_DETAILS_OBJECT = "OutboundSalesForceRequest";
    private static final String SALESFORCE_DESTINATION = "salesforceConsumedDestination";
    protected static final String OUTBOUND_CONTRACT_PRICE_OBJECT = "OutboundContractPriceRequest";
    protected static final String OUTBOUND_CONTRACT_PRICE_DESTINATION = "contractOBPriceRequest";
    protected static final String OUTBOUND_FREIGHT_PRICE_OBJECT = "OutboundFreightCost";
    protected static final String OUTBOUND_FREIGHT_PRICE_DESTINATION = "freightPriceConsumedDestination";
    private static final String SAP_CPI_ORDER_OBJECT = "OutboundOMMOrderOMSOrder";
    private static final String SAP_CPI_ORDER_DESTINATION = "sapOrderConsumedDestination";
    protected static final String VERTEX_SALES_TAX_OBJECT = "OutboundTax";
    protected static final String VERTEX_SALES_TAX_DESTINATION = "salesTaxConsumedDestination";

    private OutboundServiceFacade outboundServiceFacade;

    public void setOutboundServiceFacade(OutboundServiceFacade outboundServiceFacade) {
        this.outboundServiceFacade = outboundServiceFacade;
    }

    @Override
    public Observable<ResponseEntity<Map>> getContractPrice(BTPOutboundContractPriceRequestModel contractPriceModel) {
        return outboundServiceFacade.send(contractPriceModel, OUTBOUND_CONTRACT_PRICE_OBJECT, OUTBOUND_CONTRACT_PRICE_DESTINATION);
    }

    @Override
    public Observable<ResponseEntity<Map>> getFreightCost(BTPOutboundFreightPriceRequestModel freightPriceModel) {
        return outboundServiceFacade.send(freightPriceModel, OUTBOUND_FREIGHT_PRICE_OBJECT, OUTBOUND_FREIGHT_PRICE_DESTINATION);
    }

    @Override
    public Observable<ResponseEntity<Map>> getVertexSalesTax(VertexRequestModel vertexModel) {
        return outboundServiceFacade.send(vertexModel, VERTEX_SALES_TAX_OBJECT, VERTEX_SALES_TAX_DESTINATION);
    }

    @Override
    public Observable<ResponseEntity<Map>> submitOrderToSapBTP(SAPCpiOutboundOrderModel sapOrderModel) {
        return outboundServiceFacade.send(sapOrderModel, SAP_CPI_ORDER_OBJECT, SAP_CPI_ORDER_DESTINATION);
    }

    @Override
    public boolean isSentSuccessfully(ResponseEntity<Map> responseEntityMap) {
        return HttpStatus.OK.equals(responseEntityMap.getStatusCode());
    }

    @Override
    public Object getPropertyValue(ResponseEntity<Map> responseEntityMap, String property, String requestName) {
        Map body = responseEntityMap.getBody();
        if (body != null) {

            Object next = body.keySet().iterator().next();
            checkArgument(next != null, String.format(requestName + " response entity key set cannot be null for property [%s]!", property));

            String responseKey = next.toString();
            checkArgument(responseKey != null && !responseKey.isEmpty(),
                          String.format(requestName + " response property can neither be null nor empty for property [%s]!", property));

            Object propertyValue = ((HashMap) body.get(responseKey)).get(property);
            checkArgument(propertyValue != null, String.format(requestName + " response property [%s] value cannot be null!", property));

            return propertyValue;
        }

        return null;
    }
    @Override
    public Observable<ResponseEntity<Map>> getSupportTeamDetails(
            BTPOutboundSalesforceRequestModel salesforceRequestModel) {
        return outboundServiceFacade.send(salesforceRequestModel, SALESFORCE_DETAILS_OBJECT,
                SALESFORCE_DESTINATION);
    }

    @Override
    public void getTeamDetailsFromResponse(ResponseEntity<Map> responseEntityMap, List<Object> users) {
        if (responseEntityMap.getBody() == null) {
            return;
        }
        List records = (List) responseEntityMap.getBody().get("records");
        if (!CollectionUtils.isEmpty(records)) {
            for (Object record : records) {
                Object value = ((Map) record).get("User");
                users.add(value);
            }
        }
    }
}
