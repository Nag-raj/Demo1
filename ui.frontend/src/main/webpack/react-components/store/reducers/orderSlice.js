import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { fetchOrdersData, reorder } from "../../services/orderAPI";

const initialState = {
  orders: null,
  orderError: null,
  orderLoading: true,
  reorderSuccess: null,
  reorderLoading: false,
  reorderFailure: null
};

export const fetchOrders = createAsyncThunk(
    'order/fetchOrders',
    async (params) => {
        const response = await fetchOrdersData(params);
        return response.data;
    }
);

export const reorderProduct = createAsyncThunk(
    'order/reorderProduct',
    async (reorderData) => {
        const response = await reorder(reorderData);
        return response.data;
    }
);

export const orderSlice = createSlice({
  name: "order",
  initialState,
  reducers: {
    // startFetch: (state) => state.orderLoading = true
  },

    extraReducers: (builder) => {
        builder
        .addCase(fetchOrders.fulfilled, (state, action) => {
            state.orders = action.payload?.customer?.orders != null ? JSON.parse(action.payload?.customer?.orders?.items[0]?.id) : action.payload;
            state.orderLoading = false;
        })
        .addCase(fetchOrders.pending, (state, action) => {
            state.orderLoading = true;
        })
        .addCase(fetchOrders.rejected, (state, action) => {
            state.orderError = action.payload;
            state.orderLoading = false;
        })
        .addCase(reorderProduct.fulfilled, (state, action) => {
            state.reorderSuccess = action.payload;
            state.reorderLoading = false;
        })
        .addCase(reorderProduct.pending, (state, action) => {
            state.reorderLoading = true;
        })
        .addCase(reorderProduct.rejected, (state, action) => {
            state.reorderFailure = action.payload;
            state.reorderLoading = false;
        })
    }
});

export const getOrders = (state) => state.order;

export default orderSlice.reducer;