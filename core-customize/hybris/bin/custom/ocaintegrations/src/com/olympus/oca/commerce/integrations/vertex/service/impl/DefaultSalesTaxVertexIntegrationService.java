package com.olympus.oca.commerce.integrations.vertex.service.impl;

import com.olympus.oca.commerce.integrations.exceptions.OcaIntegrationException;
import com.olympus.oca.commerce.integrations.model.VertexRequestModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import com.olympus.oca.commerce.integrations.vertex.service.SalesTaxVertexIntegrationService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import rx.Observable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultSalesTaxVertexIntegrationService implements SalesTaxVertexIntegrationService {
    private static final Logger LOG = Logger.getLogger(DefaultSalesTaxVertexIntegrationService.class);

    private static final String REQUEST_NAME = "Vertex Sales Tax";

    private final OcaOutboundService ocaOutboundService;
    private final Converter<AbstractOrderModel, VertexRequestModel> vertexRequestConverter;

    public DefaultSalesTaxVertexIntegrationService(OcaOutboundService ocaOutboundService,
                                                   Converter<AbstractOrderModel, VertexRequestModel> vertexRequestConverter) {
        this.ocaOutboundService = ocaOutboundService;
        this.vertexRequestConverter = vertexRequestConverter;
    }

    @Override
    public AbstractOrderModel fetchSalesTaxForCart(AbstractOrderModel candidate) throws OcaIntegrationException {
        Assert.notNull(candidate, "cart is required to calculate the freight cost");

        fetchSalesTaxFromVertex(candidate).subscribe(
                // onNext
                responseEntityMap -> {
                    if (ocaOutboundService.isSentSuccessfully(responseEntityMap)) {
                        Map resultMap = (HashMap) ocaOutboundService.getPropertyValue(responseEntityMap, "QuotationResponse", REQUEST_NAME);
                        String orderTax = resultMap.get("TotalTax").toString();
                        candidate.setTotalTax(Double.valueOf(orderTax));

                        if (resultMap.get("LineItem") instanceof HashMap) {
                            Map resultMap1 = (Map) resultMap.get("LineItem");
                            fetchSalesTaxFromResponse(resultMap1, candidate);
                        } else {
                            List<Map> valueMapList = (ArrayList) resultMap.get("LineItem");
                            valueMapList.forEach(resultMap1 -> {
                                fetchSalesTaxFromResponse(resultMap1, candidate);
                            });
                        }
                    }

                }, error -> LOG.error(String.format("The Sales Tax for OrderNumber [%s] has not been received from Vertex! %n%s", candidate.getCode(),
                                                    error.getMessage()), error));
        return candidate;
    }

    protected Observable<ResponseEntity<Map>> fetchSalesTaxFromVertex(AbstractOrderModel cart) {
        VertexRequestModel vertexRequest = vertexRequestConverter.convert(cart);
        Instant start = Instant.now();
        Observable<ResponseEntity<Map>> vertexSalesTax = ocaOutboundService.getVertexSalesTax(vertexRequest);
        Instant finish = Instant.now();
        long timeTaken = Duration.between(start, finish).toMillis();

        if (LOG.isDebugEnabled()) {
            LOG.info("Fetch sales tax from vertex took  : "
                     + timeTaken
                     + " milliseconds to get the response, Start Time : "
                     + start
                     + " End Time : "
                     + finish);
        }
        return vertexSalesTax;
    }

    protected void fetchSalesTaxFromResponse(Map<String, ?> resultMap, AbstractOrderModel order) {
        String entryTax = resultMap.get("TotalTax").toString();
        if (Objects.nonNull(entryTax)) {
            if (null != resultMap.get("@materialCode")) {
                String materialCode = resultMap.get("@materialCode").toString();
                for (final AbstractOrderEntryModel orderEntry : order.getEntries()) {
                    if (materialCode.equalsIgnoreCase(orderEntry.getProduct().getCode())) {
                        orderEntry.setSalesTax(Double.valueOf(entryTax));
                    }
                }
            } else {
                order.setDeliverySalesTax(Double.valueOf(entryTax));
            }
        }
    }
}

