import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getPo,
  getShippingListAPI,
  getDeliveryListAPI,
  getShippingAddresses,
  setSelectedAddress,
  fetchCheckoutData,
  getPayInvoice,
  payByInvoiceSelect,
  placeOrder,
} from "../../services/checkoutApi";

const initialState = {
  poSuccess: null,
  poLoading: false,
  poFailure: null,
  shippingSuccess: null,
  shippingLoading: false,
  shippingFailure: null,
  getShippingListAPI: null,
  getDeliveryListAPI: null,
  deliveryListSuccess: null,
  deliveryListLoading: false,
  deliveryListFailure: null,
  getShipAddressListSuccess: null,
  getShipAddressListLoading: false,
  getShipAddressListFailure: null,
  setShippingAddressSuccess: null,
  setShippingAddressLoading: false,
  setShippingAddressFailure: null,
  checkoutData: null,
  checkoutLoading: true,
  payInvoiceSuccess: null,
  payInvoideFailure: null,
  payInvoiceLoading: false,
  payByInvoiceSelectSuccess: null,
  payByInvoiceSelectFailure: null,
  payByInvoiceSelectLoading: false,
  placeOrderSuccess: null,
  placeOrderFailure: null,
  placeOrderLoading: false,
};

export const getPoNumberData = createAsyncThunk(
  "checkout/getPoNumberData",
  async () => {
    const response = await getPo();
    return response.data;
  }
);

export const getShippingCarrierListThirdParty = createAsyncThunk(
  "checkout/shippingCarrierListForThirdParty",
  async () => {
    const response = await getShippingListAPI();
    return response?.data;
  }
);

export const getDeliveryListThirdParty = createAsyncThunk(
  "checkout/shippingDeliveryListForThirdParty",
  async (data) => {
    const response = await getDeliveryListAPI(data);
    return response?.data;
  }
);

export const getShippingAddressList = createAsyncThunk(
  "checkout/getShippingAddressList",
  async () => {
    const response = await getShippingAddresses();
    return response.data;
  }
);

export const setSelectedAddressData = createAsyncThunk(
  "checkout/setSelectedAddressData",
  async (data) => {
    const response = await setSelectedAddress(data);
    return response.data;
  }
);
export const fetchCheckout = createAsyncThunk(
  "checkout/fetchCheckout",
  async () => {
    const response = await fetchCheckoutData();
    return response.data;
  }
);

export const getPayByInvoice = createAsyncThunk(
  "checkout/getPayByInvoice",
  async () => {
    const response = await getPayInvoice();
    return response?.data;
  }
);

export const payByInvoiceSelection = createAsyncThunk(
  "checkout/payByInvoiceSelect",
  async (payment) => {
    const response = await payByInvoiceSelect(payment);
    return response?.data;
  }
);

export const placeOrderCheckout = createAsyncThunk(
  "checkout/placeOrderCheckout",
  async (orderPlace) => {
    const response = await placeOrder(orderPlace);
    return response?.data;
  }
);

export const checkoutSlice = createSlice({
  name: "checkout",
  initialState,
  reducers: {
    // startFetch: (state) => state.orderLoading = true
  },
  extraReducers: (builder) => {
    builder
      .addCase(getPoNumberData.fulfilled, (state, action) => {
        state.poSuccess = action.payload;
        state.poLoading = false;
      })
      .addCase(getPoNumberData.pending, (state, action) => {
        state.poLoading = true;
      })
      .addCase(getPoNumberData.rejected, (state, action) => {
        state.poFailure = action.payload;
        state.poLoading = false;
      })
      .addCase(getShippingAddressList.fulfilled, (state, action) => {
        state.getShipAddressListSuccess = action.payload;
        state.getShipAddressListLoading = false;
      })
      .addCase(getShippingAddressList.pending, (state, action) => {
        state.getShipAddressListLoading = true;
      })
      .addCase(getShippingAddressList.rejected, (state, action) => {
        state.getShipAddressListFailure = action.payload;
        state.getShipAddressListLoading = false;
      })
      .addCase(setSelectedAddressData.fulfilled, (state, action) => {
        state.setShippingAddressSuccess = action.payload;
        state.setShippingAddressLoading = false;
      })
      .addCase(setSelectedAddressData.pending, (state, action) => {
        state.setShippingAddressLoading = true;
      })
      .addCase(setSelectedAddressData.rejected, (state, action) => {
        state.setShippingAddressFailure = action.payload;
        state.setShippingAddressLoading = false;
      })
      .addCase(fetchCheckout.fulfilled, (state, action) => {
        state.checkoutData = action.payload;
        state.checkoutLoading = false;
      })
      .addCase(fetchCheckout.pending, (state, action) => {
        state.checkoutLoading = true;
      })
      .addCase(fetchCheckout.rejected, (state, action) => {
        state.cartError = action.payload;
        state.checkoutLoading = false;
      })
      .addCase(getShippingCarrierListThirdParty.fulfilled, (state, action) => {
        state.shippingSuccess = action.payload;
        state.shippingLoading = false;
      })
      .addCase(getShippingCarrierListThirdParty.pending, (state, action) => {
        state.shippingLoading = true;
      })
      .addCase(getShippingCarrierListThirdParty.rejected, (state, action) => {
        state.shippingFailure = action.payload;
        state.shippingLoading = false;
      })
      .addCase(getDeliveryListThirdParty.fulfilled, (state, action) => {
        state.deliveryListSuccess = action.payload;
        state.deliveryListLoading = false;
      })
      .addCase(getDeliveryListThirdParty.pending, (state, action) => {
        state.deliveryListLoading = true;
      })
      .addCase(getDeliveryListThirdParty.rejected, (state, action) => {
        state.deliveryListFailure = action.payload;
        state.deliveryListLoading = false;
      })
      .addCase(getPayByInvoice.fulfilled, (state, action) => {
        state.payInvoiceSuccess = action.payload;
        state.payInvoiceLoading = false;
      })
      .addCase(getPayByInvoice.pending, (state, action) => {
        state.payInvoiceLoading = true;
      })
      .addCase(getPayByInvoice.rejected, (state, action) => {
        state.payInvoiceFailure = action.payload;
        state.payInvoiceLoading = false;
      })
      .addCase(payByInvoiceSelection.fulfilled, (state, action) => {
        state.payByInvoiceSelectSuccess = action.payload;
        state.payByInvoiceSelectLoading = false;
      })
      .addCase(payByInvoiceSelection.pending, (state, action) => {
        state.payByInvoiceSelectLoading = true;
      })
      .addCase(payByInvoiceSelection.rejected, (state, action) => {
        state.payByInvoiceSelectFailure = action.payload;
        state.payByInvoiceSelectLoading = false;
      })
      .addCase(placeOrderCheckout.fulfilled, (state, action) => {
        state.placeOrderSuccess =
          action?.payload?.placeOrder?.order?.order_number;
        state.placeOrderLoading = false;
      })
      .addCase(placeOrderCheckout.pending, (state, action) => {
        state.placeOrderLoading = true;
      })
      .addCase(placeOrderCheckout.rejected, (state, action) => {
        state.placeOrderFailure = action.payload;
        state.placeOrderLoading = false;
      });
  },
});

export const getCheckout = (state) => {
  return state.checkout;
};

export default checkoutSlice.reducer;
