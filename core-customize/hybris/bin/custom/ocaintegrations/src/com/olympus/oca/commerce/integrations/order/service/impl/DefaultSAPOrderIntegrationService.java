package com.olympus.oca.commerce.integrations.order.service.impl;

import com.olympus.oca.commerce.integrations.exceptions.OcaIntegrationException;
import com.olympus.oca.commerce.integrations.order.service.SAPOrderIntegrationService;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import rx.Observable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DefaultSAPOrderIntegrationService implements SAPOrderIntegrationService {

    private static final Logger LOG = LogManager.getLogger(DefaultSAPOrderIntegrationService.class);

    @Resource(name = "ocaOutboundService")
    private OcaOutboundService ocaOutboundService;

    @Resource(name = "outboundOrderConverter")
    private Converter<AbstractOrderModel, SAPCpiOutboundOrderModel> outboundOrderConverter;

    @Override
    public AbstractOrderModel submitOrderToSapBTP(AbstractOrderModel candidate) throws OcaIntegrationException {
        Assert.notNull(candidate, "order is not required to be submitted to SAP BTP");
        submitOrderToBTP(candidate).subscribe(
                // onNext
                responseEntityMap -> {
                    if( ocaOutboundService.isSentSuccessfully(responseEntityMap)){
                    }
                }
                // onError
                , error -> LOG.error(
                        String.format("The Order for OrderNumber [%s] has not been submitted to BTP! %n%s", candidate.getCode(),
                                error.getMessage()), error));
        return candidate;
    }

    private Observable<ResponseEntity<Map>> submitOrderToBTP(AbstractOrderModel candidate) {
        final SAPCpiOutboundOrderModel outboundOrderModel = new SAPCpiOutboundOrderModel();
        convertOrderToOutBoundRequest(candidate, outboundOrderModel);
        final Instant start = Instant.now();
        final Observable<ResponseEntity<Map>> outBoundOrderResponse = ocaOutboundService.submitOrderToSapBTP(outboundOrderModel);
        final Instant finish = Instant.now();
        final long timeTaken = Duration.between(start, finish).toMillis();
        if(LOG.isDebugEnabled()) {
            LOG.info("Submitting order to SAP BTP took  : " + timeTaken + " milliseconds to get the response, Start Time : " + start + " End Time : " + finish);
        }
        return outBoundOrderResponse;
    }

    private void convertOrderToOutBoundRequest(AbstractOrderModel orderModel, SAPCpiOutboundOrderModel outboundOrderModel) {
        outboundOrderConverter.convert(orderModel, outboundOrderModel);
    }

}
