import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { fetchMiniCartValue } from "../../services/commonAPI";

const initialState = {
  miniCart: null,
  miniCartError: null,
  miniCartLoading: false
};

export const fetchMiniCart = createAsyncThunk(
    'common/fetchMiniCart',
    async (params) => {
        const response = await fetchMiniCartValue(params);
        return response.data;
    }
);

export const commonSlice = createSlice({
  name: "common",
  initialState,
  reducers: {
    // startFetch: (state) => state.orderLoading = true
  },

    extraReducers: (builder) => {
        builder
        .addCase(fetchMiniCart.fulfilled, (state, action) => {
            state.miniCart = action.payload;
            state.miniCartLoading = false;
        })
        .addCase(fetchMiniCart.pending, (state, action) => {
            state.miniCartLoading = true;
        })
        .addCase(fetchMiniCart.rejected, (state, action) => {
            state.miniCartError = action.payload;
            state.miniCartLoading = false;
        })
    }
});

export const getMiniCart = (state) => state.common;

export default commonSlice.reducer;