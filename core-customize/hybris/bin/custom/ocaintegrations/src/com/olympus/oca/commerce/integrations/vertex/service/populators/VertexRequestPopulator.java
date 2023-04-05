package com.olympus.oca.commerce.integrations.vertex.service.populators;

import com.olympus.oca.commerce.integrations.constants.OcaIntegrationConstants;
import com.olympus.oca.commerce.integrations.model.BTPOutboundAdministrativeDestinationVertexRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundAdministrativeOriginVertexRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundCustomerVertexRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundDestinationVertexRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundPhysicalOriginVertexRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundQuotationVertexRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundSellerVertexRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundVertexLoginModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundVertexRequestLineItemModel;
import com.olympus.oca.commerce.integrations.model.VertexRequestModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractBtpOutboundAddressVertexRequestModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VertexRequestPopulator implements Populator<AbstractOrderModel, VertexRequestModel> {
    private static final String PRODUCT_CLASS = "norm";
    private static final String FREIGHT_TERM = "SAPFREIGHT";
    private static final String SELLER_CODE = "2002";

    private final ConfigurationService configurationService;

    public VertexRequestPopulator(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public void populate(AbstractOrderModel source, VertexRequestModel target) throws ConversionException {
        BTPOutboundQuotationVertexRequestModel quotationRequest = createQuotationRequest();

        //Seller
        quotationRequest.setSeller(getSeller(source));
        //Customer
        quotationRequest.setCustomer(getCustomer(source));

        //Cart entries
        List<BTPOutboundVertexRequestLineItemModel> lineItemList = new ArrayList<>();

        for (AbstractOrderEntryModel entry : source.getEntries()) {
            lineItemList.add(getRequestLineItemForCartEntry(entry));
        }

        // Delivery cost if applicable
        if (Objects.nonNull(source.getDeliveryCost()) && source.getDeliveryCost() > 0) {
            BTPOutboundVertexRequestLineItemModel lineItemFreight = getRequestLineItem(FREIGHT_TERM, null, null, 1,
                                                                                       source.getDeliveryCost().toString());

            lineItemList.add(lineItemFreight);
        }

        quotationRequest.setLineItems(lineItemList);

        target.setQuotationRequest(quotationRequest);
        target.setLogin(getOutboundVertexLogin());
    }

    protected BTPOutboundVertexRequestLineItemModel getRequestLineItemForCartEntry(AbstractOrderEntryModel candidate) {
        return getRequestLineItem(PRODUCT_CLASS, candidate.getProduct().getCode(), candidate.getUnit().getCode(), candidate.getQuantity(),
                                  candidate.getTotalPrice().toString());
    }

    protected BTPOutboundVertexRequestLineItemModel getRequestLineItem(String productClass, String materialCode, String uom, long quantity,
                                                                       String price) {
        BTPOutboundVertexRequestLineItemModel requestLineItem = createRequestLineItem();
        requestLineItem.setProduct(productClass);
        requestLineItem.setMaterialCode(materialCode);
        requestLineItem.setUnitOfMeasure(uom);
        requestLineItem.setQuantity(quantity);
        requestLineItem.setExtendedPrice(price);
        return requestLineItem;
    }

    protected BTPOutboundVertexLoginModel getOutboundVertexLogin() {
        BTPOutboundVertexLoginModel login = createOutboundVertexLogin();
        login.setUserName(configurationService.getConfiguration().getString(OcaIntegrationConstants.OCA_VERTEX_USERNAME));
        login.setPassword(configurationService.getConfiguration().getString(OcaIntegrationConstants.OCA_VERTEX_PWD));
        return login;
    }

    protected BTPOutboundPhysicalOriginVertexRequestModel getPhysicalOriginForDeliveryAddress(AddressModel deliveryAddress) {
        BTPOutboundPhysicalOriginVertexRequestModel physicalOrigin = createPhysicalOrigin();
        populateAddress(deliveryAddress, physicalOrigin);
        return physicalOrigin;
    }

    protected BTPOutboundAdministrativeOriginVertexRequestModel getAdministrativeOriginForBillingAddress(AddressModel billingAddress) {
        BTPOutboundAdministrativeOriginVertexRequestModel adminOrigin = createAdministrativeOrigin();
        populateAddress(billingAddress, adminOrigin);
        return adminOrigin;
    }

    protected BTPOutboundAdministrativeDestinationVertexRequestModel getAdministrativeDestinationForBillingAddress(AddressModel billingAddress) {
        BTPOutboundAdministrativeDestinationVertexRequestModel administrativeDestination = createAdministrativeDestination();

        populateAddress(billingAddress, administrativeDestination);
        if (billingAddress != null) {
            administrativeDestination.setMainDivision(billingAddress.getRegion() != null ? billingAddress.getRegion().getIsocodeShort() : "");
        }
        return administrativeDestination;
    }

    protected void populateAddress(AddressModel source, AbstractBtpOutboundAddressVertexRequestModel target) {
        if (source != null && target != null) {
            target.setCity(source.getTown());
            target.setState(source.getRegion() != null ? source.getRegion().getIsocodeShort() : "");
            target.setCountry(source.getCountry() != null ? source.getCountry().getIsocode() : "");
            target.setPostalCode(source.getPostalcode());
        }
    }

    protected BTPOutboundSellerVertexRequestModel getSeller(AbstractOrderModel cart) {
        BTPOutboundSellerVertexRequestModel seller = createSeller();
        seller.setCompany(SELLER_CODE);
        seller.setPhysicalOrigin(getPhysicalOriginForDeliveryAddress(cart.getDeliveryAddress()));
        seller.setAdministrativeOrigin(getAdministrativeOriginForBillingAddress(determineBillingAddress(cart)));
        return seller;
    }

    protected BTPOutboundDestinationVertexRequestModel getDestinationForDeliveryAddress(AddressModel deliveryAddress) {
        BTPOutboundDestinationVertexRequestModel destination = createDestination();
        populateAddress(deliveryAddress, destination);
        return destination;
    }

    protected BTPOutboundCustomerVertexRequestModel getCustomer(AbstractOrderModel cart) {
        BTPOutboundCustomerVertexRequestModel customer = new BTPOutboundCustomerVertexRequestModel();
        customer.setAdministrativeDestination(getAdministrativeDestinationForBillingAddress(determineBillingAddress(cart)));
        customer.setCustomerCode(cart.getUnit().getUid());
        customer.setDestination(getDestinationForDeliveryAddress(cart.getDeliveryAddress()));
        return customer;
    }

    protected BTPOutboundSellerVertexRequestModel createSeller() {
        return new BTPOutboundSellerVertexRequestModel();
    }

    protected BTPOutboundQuotationVertexRequestModel createQuotationRequest() {
        return new BTPOutboundQuotationVertexRequestModel();
    }

    protected BTPOutboundVertexLoginModel createOutboundVertexLogin() {
        return new BTPOutboundVertexLoginModel();
    }

    protected BTPOutboundPhysicalOriginVertexRequestModel createPhysicalOrigin() {
        return new BTPOutboundPhysicalOriginVertexRequestModel();
    }

    protected BTPOutboundAdministrativeOriginVertexRequestModel createAdministrativeOrigin() {
        return new BTPOutboundAdministrativeOriginVertexRequestModel();
    }

    protected BTPOutboundAdministrativeDestinationVertexRequestModel createAdministrativeDestination() {
        return new BTPOutboundAdministrativeDestinationVertexRequestModel();
    }

    protected BTPOutboundDestinationVertexRequestModel createDestination() {
        return new BTPOutboundDestinationVertexRequestModel();

    }

    protected BTPOutboundVertexRequestLineItemModel createRequestLineItem() {
        return new BTPOutboundVertexRequestLineItemModel();
    }

    protected AddressModel determineBillingAddress(AbstractOrderModel source) {
        if ((Objects.nonNull(source.getPaymentAddress())) || CollectionUtils.isNotEmpty(source.getUnit().getBillingAddresses())) {
            return source.getPaymentAddress() != null ?
                   source.getPaymentAddress() :
                   source.getUnit().getBillingAddresses().stream().findFirst().orElse(null);
        }
        return null;
    }
}
