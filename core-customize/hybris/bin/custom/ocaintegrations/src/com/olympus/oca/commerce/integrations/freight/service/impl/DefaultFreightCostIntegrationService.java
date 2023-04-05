package com.olympus.oca.commerce.integrations.freight.service.impl;

import com.olympus.oca.commerce.integrations.exceptions.OcaIntegrationException;
import com.olympus.oca.commerce.integrations.freight.service.FreightCostIntegrationService;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceItemRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceRequestModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
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

public class DefaultFreightCostIntegrationService implements FreightCostIntegrationService {
    private static final Logger LOG = Logger.getLogger(DefaultFreightCostIntegrationService.class);
    private static final String PROCESS_TYPE = "ZAOR";
    private static final String FREIGHT_TERM = "A45";
    private static final String ITEM_TYPE = "U960";

    private static final String REQUEST_NAME = "CRM Freight Cost";

    private final OcaOutboundService ocaOutboundService;

    public DefaultFreightCostIntegrationService(OcaOutboundService ocaOutboundService) {
        this.ocaOutboundService = ocaOutboundService;
    }

    @Override
    public AbstractOrderModel fetchFreightCostForCart(AbstractOrderModel candidate) throws OcaIntegrationException {
        Assert.notNull(candidate, "cart is required to calculate the freight cost");
        if (candidate.getDeliveryAddress() != null) {
            fetchFreightPriceFromCrm(candidate).subscribe(
                    // onNext
                    responseEntityMap -> {

                        if (ocaOutboundService.isSentSuccessfully(responseEntityMap)) {
                            if (ocaOutboundService.getPropertyValue(responseEntityMap, "ITEM", REQUEST_NAME) instanceof HashMap) {
                                Map resultMap = (HashMap) ocaOutboundService.getPropertyValue(responseEntityMap, "ITEM", REQUEST_NAME);
                                fetchFreightCostFromResponse(resultMap, candidate);
                            } else {
                                List<Map> valueMapList = (ArrayList) ocaOutboundService.getPropertyValue(responseEntityMap, "ITEM", REQUEST_NAME);
                                valueMapList.forEach(resultMap -> {
                                    fetchFreightCostFromResponse(resultMap, candidate);
                                });
                            }
                        }
                    }
                    // onError
                    , error -> LOG.error(
                            String.format("The freight price for OrderNumber [%s] has not been received from BTP! %n%s", candidate.getCode(),
                                          error.getMessage()), error));
        }
        return candidate;
    }

    protected Observable<ResponseEntity<Map>> fetchFreightPriceFromCrm(AbstractOrderModel cart) {
        BTPOutboundFreightPriceRequestModel freightPriceModel = new BTPOutboundFreightPriceRequestModel();
        convertOrderToFreightRequest(cart, freightPriceModel);
        Instant start = Instant.now();
        Observable<ResponseEntity<Map>> freightCost = ocaOutboundService.getFreightCost(freightPriceModel);
        Instant finish = Instant.now();
        long timeTaken = Duration.between(start, finish).toMillis();
        if (LOG.isDebugEnabled()) {
            LOG.info("Fetch freight Cost from CRM took  : "
                     + timeTaken
                     + " milliseconds to get the response, Start Time : "
                     + start
                     + " End Time : "
                     + finish);
        }
        return freightCost;
    }

    protected void fetchFreightCostFromResponse(Map<String, ?> resultMap, AbstractOrderModel order) {
        if (resultMap.containsKey("LINE_ITEM_ID") && resultMap.containsKey("FREIGHT")) {
            String lineItem = resultMap.get("LINE_ITEM_ID").toString();
            String freightPrice = resultMap.get("FREIGHT").toString();
            for (final AbstractOrderEntryModel e : order.getEntries()) {
                if (lineItem.equalsIgnoreCase(e.getEntryNumber().toString())) {
                    e.setFreightPrice(Double.valueOf(freightPrice));
                }
            }
        }
    }

    protected void convertOrderToFreightRequest(AbstractOrderModel cart, BTPOutboundFreightPriceRequestModel freightPriceModel) {
        if (cart instanceof CartModel) {
            List<BTPOutboundFreightPriceItemRequestModel> freightItemSets = new ArrayList<>();
            freightPriceModel.setProcess_type(PROCESS_TYPE);
            freightPriceModel.setQuoteId(cart.getCode());
            freightPriceModel.setSoldTo(cart.getUnit().getUid());
            for (AbstractOrderEntryModel cartEntry : cart.getEntries()) {
                BTPOutboundFreightPriceItemRequestModel freightItem = new BTPOutboundFreightPriceItemRequestModel();
                freightItem.setProduct_id(cartEntry.getProduct().getCode());
                freightItem.setLine_item_id(String.valueOf(cartEntry.getEntryNumber()));
                freightItem.setFreight_term(FREIGHT_TERM);
                freightItem.setUom(cartEntry.getUnit().getCode());
                freightItem.setQuantity(cartEntry.getQuantity());
                freightItem.setItem_type(ITEM_TYPE);
                freightItem.setFreight("");
                freightItem.setContract_id("");
                freightItemSets.add(freightItem);
            }
            freightPriceModel.setBtpOutboundFreightPriceItems(freightItemSets);
        }
    }

}
