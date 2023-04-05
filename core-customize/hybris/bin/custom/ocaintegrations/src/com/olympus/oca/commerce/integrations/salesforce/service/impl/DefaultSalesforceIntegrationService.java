package com.olympus.oca.commerce.integrations.salesforce.service.impl;

import com.olympus.oca.commerce.integrations.model.BTPOutboundSalesforceRequestModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import com.olympus.oca.commerce.integrations.salesforce.service.SalesforceIntegrationService;
import org.springframework.http.ResponseEntity;
import rx.Observable;
import org.apache.log4j.Logger;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSalesforceIntegrationService implements SalesforceIntegrationService {
    private static final Logger LOG = Logger.getLogger(DefaultSalesforceIntegrationService.class);
    private OcaOutboundService ocaOutboundService;

    @Override
    public List<Object> getSupportTeamContactDetails(String soldTo){
        List<Object> users = new ArrayList<>();
        populateGetSupportTeamRequest(soldTo).subscribe(
                responseEntityMap -> {
                    if (ocaOutboundService.isSentSuccessfully(responseEntityMap)) {
                        getOcaOutboundService().getTeamDetailsFromResponse(responseEntityMap,users);
                    }
                }, error -> LOG.error(String.format("The Team Contact Details for the soldTo [%s] has not been recieved from BTP! %n%s",soldTo,error.getMessage()),error) );
        return users;
    }
    protected Observable<ResponseEntity<Map>> populateGetSupportTeamRequest(String soldTo){
        final String SOLDTO_REQTYPE = "CONTACT_INFOS";

        BTPOutboundSalesforceRequestModel salesforceRequest = new BTPOutboundSalesforceRequestModel();
        salesforceRequest.setSalesforceReqType(SOLDTO_REQTYPE);
        salesforceRequest.setSalesforceReqParam(soldTo);
        Observable<ResponseEntity<Map>> teamDetails = getOcaOutboundService().getSupportTeamDetails(salesforceRequest);
        return teamDetails;
    }



    public OcaOutboundService getOcaOutboundService() {
        return ocaOutboundService;
    }

    public void setOcaOutboundService(OcaOutboundService ocaOutboundService) {
        this.ocaOutboundService = ocaOutboundService;
    }
}