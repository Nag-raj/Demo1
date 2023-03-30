import { createSlice } from "@reduxjs/toolkit";
const initialState = {
  userData: false
};
export const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setUser(state, action) {
        state.userData = true;
    },
    resetUser(state, action) {
        state.userData = false;
    }
  }
});
export const getUser = (state) => state.user;
export const { setUser, resetUser } = userSlice.actions;
export default userSlice.reducer;
