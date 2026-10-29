import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

import { getApiError, wishlistApi } from "../services/api";

const initialState = {
  data: { items: [] },
  loading: false,
  error: "",
};

export const fetchWishlist = createAsyncThunk(
  "wishlist/fetch",
  async (_, thunkAPI) => {
    try {
      const response = await wishlistApi.get();
      return response.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(getApiError(error));
    }
  }
);

export const toggleWishlist = createAsyncThunk(
  "wishlist/toggle",
  async ({ productId, exists }, thunkAPI) => {
    try {
      if (exists) {
        await wishlistApi.remove(productId);
      } else {
        await wishlistApi.add(productId);
      }

      const response = await wishlistApi.get();
      return response.data;
    } catch (error) {
      return thunkAPI.rejectWithValue(getApiError(error));
    }
  }
);

const wishlistSlice = createSlice({
  name: "wishlist",
  initialState,
  reducers: {
    resetWishlist: () => initialState,
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchWishlist.pending, (state) => {
        state.loading = true;
        state.error = "";
      })
      .addCase(fetchWishlist.fulfilled, (state, action) => {
        state.loading = false;
        state.data = action.payload;
      })
      .addCase(fetchWishlist.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Wishlist request failed.";
      })
      .addCase(toggleWishlist.pending, (state) => {
        state.loading = true;
        state.error = "";
      })
      .addCase(toggleWishlist.fulfilled, (state, action) => {
        state.loading = false;
        state.data = action.payload;
      })
      .addCase(toggleWishlist.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Wishlist request failed.";
      });
  },
});

export const { resetWishlist } = wishlistSlice.actions;
export default wishlistSlice.reducer;
