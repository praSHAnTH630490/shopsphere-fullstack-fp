import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

import { cartApi, getApiError } from "../services/api";

const initialState = {
  data: { items: [], totalAmount: 0 },
  loading: false,
  error: "",
};

export const fetchCart = createAsyncThunk("cart/fetch", async (_, thunkAPI) => {
  try {
    const response = await cartApi.get();
    return response.data;
  } catch (error) {
    return thunkAPI.rejectWithValue(getApiError(error));
  }
});

export const addToCart = createAsyncThunk("cart/add", async (data, thunkAPI) => {
  try {
    await cartApi.add(data);
    const response = await cartApi.get();
    return response.data;
  } catch (error) {
    return thunkAPI.rejectWithValue(getApiError(error));
  }
});

export const updateCartItem = createAsyncThunk(
  "cart/update",
  async ({ id, quantity }, thunkAPI) => {
    try {
      await cartApi.update(id, { quantity });
      const response = await cartApi.get();
      return response.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(getApiError(error));
    }
  }
);

export const removeCartItem = createAsyncThunk(
  "cart/remove",
  async (id, thunkAPI) => {
    try {
      await cartApi.remove(id);
      const response = await cartApi.get();
      return response.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(getApiError(error));
    }
  }
);

export const clearCart = createAsyncThunk("cart/clear", async (_, thunkAPI) => {
  try {
    await cartApi.clear();
    return { items: [], totalAmount: 0 };
  } catch (error) {
    return thunkAPI.rejectWithValue(getApiError(error));
  }
});

const cartSlice = createSlice({
  name: "cart",
  initialState,
  reducers: {
    resetCart: () => initialState,
  },
  extraReducers: (builder) => {
    const requests = [
      fetchCart,
      addToCart,
      updateCartItem,
      removeCartItem,
      clearCart,
    ];

    requests.forEach((request) => {
      builder.addCase(request.pending, (state) => {
        state.loading = true;
        state.error = "";
      });
      builder.addCase(request.fulfilled, (state, action) => {
        state.loading = false;
        state.data = action.payload;
      });
      builder.addCase(request.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Cart request failed.";
      });
    });
  },
});

export const { resetCart } = cartSlice.actions;
export default cartSlice.reducer;
