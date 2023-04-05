package com.olympus.oca.commerce.core.cart;

import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import java.util.List;

public interface OcaCartService extends CartService {

    List<ShippingCarrierModel> getShippingCarrierListForThirdParty();

    void setThirdPartyShippingAccount(String carrierCode, String carrierAccount);

    CartModel updateSessionCart(String b2bUnitId, boolean calculateCart);

    List<DeliveryOptionModel> getDeliveryOptions(String shippingCarrierCode);

    ShippingCarrierModel getShippingCarrierForCode(String carrierCode);

    void setShippingMethod(String carrierCode);
}
