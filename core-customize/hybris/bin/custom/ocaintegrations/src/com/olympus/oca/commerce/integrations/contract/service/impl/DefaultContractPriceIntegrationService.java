package com.olympus.oca.commerce.integrations.contract.service.impl;

import com.olympus.oca.commerce.integrations.contract.service.ContractPriceIntegrationService;
import com.olympus.oca.commerce.integrations.model.BTPOutboundContractPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.PricingRequestLineItemsModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import rx.Observable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultContractPriceIntegrationService implements ContractPriceIntegrationService {

    private static final Logger LOG = Logger.getLogger(DefaultContractPriceIntegrationService.class);
    private static final String ITEM_TYPE = "NEW";
    private static final String REQUEST_NAME = "CRM Contract Cost";

    private final OcaOutboundService ocaOutboundService;

    public DefaultContractPriceIntegrationService(OcaOutboundService ocaOutboundService) {
        this.ocaOutboundService = ocaOutboundService;
    }

    @Override
    public AbstractOrderEntryModel fetchContractPriceForCartEntry(final AbstractOrderEntryModel entry) {
        Assert.notNull(entry, "cart is required to calculate the contract price");
        fetchContractPriceFromCrm(entry).subscribe(responseEntityMap -> {
            if (ocaOutboundService.isSentSuccessfully(responseEntityMap)) {
                HashMap resultMap = (HashMap) ocaOutboundService.getPropertyValue(responseEntityMap, "OUTPUT", REQUEST_NAME);
                if (null != resultMap) {
                    fetchContractPriceFromResponse(resultMap, entry);
                }
            }
        }, error -> LOG.error(
                String.format("The entry contract price for OrderNumber [%s] has not been received from BTP! %n%s", entry.getOrder().getCode(),
                              error.getMessage()), error));
        return entry;
    }

    @Override
    public AbstractOrderModel fetchContractPriceForCart(final AbstractOrderModel cart) {
        Assert.notNull(cart, "cart is required to calculate the contract price");
        fetchCartContractPriceFromCrm(cart).subscribe(responseEntityMap -> {
            if (ocaOutboundService.isSentSuccessfully(responseEntityMap)) {
                if (ocaOutboundService.getPropertyValue(responseEntityMap, "OUTPUT", REQUEST_NAME) instanceof HashMap) {
                    HashMap resultMap = (HashMap) ocaOutboundService.getPropertyValue(responseEntityMap, "OUTPUT", REQUEST_NAME);
                    saveContractPriceFromResponse(resultMap, cart);
                } else {
                    List<Map> valueMapList = (ArrayList) ocaOutboundService.getPropertyValue(responseEntityMap, "OUTPUT", REQUEST_NAME);
                    valueMapList.forEach(resultMap -> {
                        saveContractPriceFromResponse((HashMap) resultMap, cart);
                    });
                }
            }
        }, error -> LOG.error(String.format("The cart contract price for OrderNumber [%s] has not been received from BTP! %n%s", cart.getCode(),
                                            error.getMessage()), error));
        return cart;
    }

    protected Observable<ResponseEntity<Map>> fetchContractPriceFromCrm(AbstractOrderEntryModel entryModel) {
        BTPOutboundContractPriceRequestModel contractPriceModel = new BTPOutboundContractPriceRequestModel();
        convertOrderToContractRequest(entryModel, contractPriceModel);
        return getContractPrice(contractPriceModel);
    }

    protected Observable<ResponseEntity<Map>> fetchCartContractPriceFromCrm(AbstractOrderModel cart) {
        BTPOutboundContractPriceRequestModel contractPriceModel = new BTPOutboundContractPriceRequestModel();
        convertOrderToContractListRequest(cart, contractPriceModel);
        return getContractPrice(contractPriceModel);
    }

    protected void convertOrderToContractListRequest(AbstractOrderModel cartModel, BTPOutboundContractPriceRequestModel contractPriceModel) {
        if (cartModel instanceof CartModel) {
            List<PricingRequestLineItemsModel> contractItemList = new ArrayList<>();
            String soldToId = cartModel.getUnit().getUid();
            for (AbstractOrderEntryModel entryModel : cartModel.getEntries()) {
                PricingRequestLineItemsModel contractItem = new PricingRequestLineItemsModel();
                contractItem.setSoldTo(soldToId);
                contractItem.setProduct_id(entryModel.getProduct().getCode());
                contractItem.setItem_type(ITEM_TYPE);
                contractItem.setQuote_id(entryModel.getOrder().getCode());
                contractItem.setLine_item_id(String.valueOf(entryModel.getEntryNumber()));
                contractItemList.add(contractItem);
            }
            contractPriceModel.setPricingRequestLineItems(contractItemList);
        }
    }

    protected void convertOrderToContractRequest(AbstractOrderEntryModel entryModel, BTPOutboundContractPriceRequestModel contractPriceModel) {
        List<PricingRequestLineItemsModel> contractItemList = new ArrayList<>();
        PricingRequestLineItemsModel priceReqModel = new PricingRequestLineItemsModel();
        priceReqModel.setSoldTo(entryModel.getOrder().getUnit().getUid());
        priceReqModel.setProduct_id(entryModel.getProduct().getCode());
        priceReqModel.setItem_type(ITEM_TYPE);
        priceReqModel.setQuote_id(entryModel.getOrder().getCode());
        priceReqModel.setLine_item_id(String.valueOf(entryModel.getEntryNumber()));
        contractItemList.add(priceReqModel);
        contractPriceModel.setPricingRequestLineItems(contractItemList);
    }

    private void fetchContractPriceFromResponse(HashMap resultMap, AbstractOrderEntryModel entry) {
        String lineItem = resultMap.get("LINE_ITEM_ID").toString();
        String contractPrice = resultMap.get("CONTRACT_PRICE").toString();
        if (entry instanceof CartEntryModel) {
            if (lineItem.equalsIgnoreCase(entry.getEntryNumber().toString())) {
                entry.setContractPrice(Double.valueOf(contractPrice));
                entry.setContractPriceFetchedAt(new Date());
                if (entry.getOrder().getContractPriceFetchedAt() == null) {
                    AbstractOrderModel orderModel = entry.getOrder();
                    orderModel.setContractPriceFetchedAt(new Date());
                }
            }
        }
    }

    private void saveContractPriceFromResponse(HashMap resultMap, AbstractOrderModel cart) {
        String lineItem = resultMap.get("LINE_ITEM_ID").toString();
        String contractPrice = resultMap.get("CONTRACT_PRICE").toString();
        if (cart instanceof CartModel) {
            for (AbstractOrderEntryModel entry : cart.getEntries()) {
                if (lineItem.equalsIgnoreCase(entry.getEntryNumber().toString())) {
                    entry.setContractPrice(Double.valueOf(contractPrice));
                    entry.setContractPriceFetchedAt(new Date());
                }
            }
            cart.setContractPriceFetchedAt(new Date());
        }
    }

    protected Observable<ResponseEntity<Map>> getContractPrice(BTPOutboundContractPriceRequestModel contractPriceModel) {
        Instant start = Instant.now();
        Observable<ResponseEntity<Map>> contractPrice = ocaOutboundService.getContractPrice(contractPriceModel);
        Instant finish = Instant.now();
        long timeTaken = Duration.between(start, finish).toMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetch contract price for cart entries from CRM took  : "
                      + timeTaken
                      + " milliseconds to get the response, Start Time : "
                      + start
                      + " End Time : "
                      + finish);
        }
        return contractPrice;
    }
}
