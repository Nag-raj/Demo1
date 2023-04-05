package com.olympus.oca.commerce.integrations.outbound.service;

import com.olympus.oca.commerce.integrations.constants.GeneratedOcaIntegrationConstants;
import com.olympus.oca.commerce.integrations.model.*;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.List;
import java.util.Map;

public interface OcaOutboundService {

    Observable<ResponseEntity<Map>> getSupportTeamDetails(BTPOutboundSalesforceRequestModel salesforceRequestModel);

    void getTeamDetailsFromResponse(ResponseEntity<Map> responseEntityMap, List<Object> users);

    Observable<ResponseEntity<Map>> getContractPrice(BTPOutboundContractPriceRequestModel contractPriceModel);

    Observable<ResponseEntity<Map>> getFreightCost(BTPOutboundFreightPriceRequestModel freightPriceModel);

    Observable<ResponseEntity<Map>> getVertexSalesTax(VertexRequestModel vertexModel);

    Observable<ResponseEntity<Map>> submitOrderToSapBTP(SAPCpiOutboundOrderModel sapOrderModel);

    boolean isSentSuccessfully(ResponseEntity<Map> responseEntityMap);

    Object getPropertyValue(ResponseEntity<Map> responseEntityMap, String property, String requestName);
}
