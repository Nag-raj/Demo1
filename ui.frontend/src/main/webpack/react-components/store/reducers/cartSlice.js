import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  fetchCartData,
  updateCartItemsQuantityData,
  removeProduct
} from "../../services/cartAPI";

const initialState = {
  cartData: null,
  cartError: null,
  cartLoading: true,
  updateCartItemsQuantityData: null,
  updateCartItemsQuantityLoading: false,
  removeProductSucess: null,
  removeProductLoading: false
};

export const fetchCart = createAsyncThunk("cart/fetchCart", async () => {
  const response = await fetchCartData();
  return response.data;
});

export const updateCartItemsQuantity = createAsyncThunk(
  "cart/updateCartItemsQuantity",
  async (updateData) => {
    const response = await updateCartItemsQuantityData(updateData);
    return response.data;
  }
);

export const removeProductData = createAsyncThunk(
  "cart/removeProduct",
  async (itemId) => {
    const response = await removeProduct(itemId);
    return response.data;
  }
);

export const cartSlice = createSlice({
  name: "cart",
  initialState,
  reducers: {
    // startFetch: (state) => state.orderLoading = true
    resetCart(state, action) {
      state.updateCartItemsQuantityData = null;
      state.removeProductSucess = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCart.fulfilled, (state, action) => {
        state.cartData = action.payload.data.cart !== null ? action.payload.data.cart : null;
        state.cartLoading = false;
      })
      .addCase(fetchCart.pending, (state, action) => {
        state.cartLoading = true;
      })
      .addCase(fetchCart.rejected, (state, action) => {
        state.cartError = action.payload?.customer?.cart?.items;
        state.cartLoading = false;
      })
      .addCase(updateCartItemsQuantity.fulfilled, (state, action) => {
        state.cartData = action.payload.data.updateCartItems !== null ? action.payload.data.updateCartItems.cart !== null ? action.payload.data.updateCartItems.cart : null : null;
        state.updateCartItemsQuantityData = action.payload.data.updateCartItems !== null ? action.payload.data.updateCartItems.cart !== null ? action.payload.data.updateCartItems.cart : null : null;
        state.updateCartItemsQuantityLoading = false;
        state.cartLoading = false;
      })
      .addCase(updateCartItemsQuantity.pending, (state, action) => {
        state.updateCartItemsQuantityLoading = true;
        state.cartLoading = true;
      })
      .addCase(updateCartItemsQuantity.rejected, (state, action) => {
        // add error state
        state.updateCartItemsQuantityLoading = false;
        state.cartLoading = false;
      })
      .addCase(removeProductData.fulfilled, (state, action) => {
        state.cartData = action.payload.data.removeItemFromCart !== null ? action.payload.data.removeItemFromCart.cart !== null ? action.payload.data.removeItemFromCart.cart : null : null;
        state.removeProductSucess = action.payload.data.removeItemFromCart !== null ? action.payload.data.removeItemFromCart.cart !== null ? action.payload.data.removeItemFromCart.cart : null : null;
        state.removeProductLoading = false;
        state.cartLoading = false;
      })
      .addCase(removeProductData.pending, (state, action) => {
        state.removeProductLoading = true;
        state.cartLoading = true;
      })
      .addCase(removeProductData.rejected, (state, action) => {
        // add error state
        state.removeProductLoading = false;
        state.cartLoading = false;
      });
  },
});

export const getCart = (state) => {
  return state.cart;
};

export const { resetCart } = cartSlice.actions;

export default cartSlice.reducer;
