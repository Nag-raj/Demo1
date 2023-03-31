import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { orderPlaceAPI } from "../../services/thankyouAPI";

const initialState = {
  orderPlacedSuccess: null,
  orderPlacedLoading: false,
  orderPlacedFailure: null,
};

export const getOrderPlaced = createAsyncThunk(
  "checkout/getOrderPlaced",
  async (orderId) => {
    const response = await orderPlaceAPI(orderId);
    return response?.data;
  }
);

export const thankyouSlice = createSlice({
  name: "thankyou",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getOrderPlaced.fulfilled, (state, action) => {
        state.orderPlacedSuccess = action.payload?.customer?.orders != null ? JSON.parse(action.payload?.customer?.orders?.items[0]?.id) : action.payload;
        state.orderPlacedLoading = false;
      })
      .addCase(getOrderPlaced.pending, (state, action) => {
        state.orderPlacedLoading = true;
      })
      .addCase(getOrderPlaced.rejected, (state, action) => {
        state.orderPlacedFailure = action?.payload;
        state.orderPlacedLoading = false;
      });
  },
});

export const getThankyou = (state) => {
  return state.thankyou;
};

export default thankyouSlice.reducer;
