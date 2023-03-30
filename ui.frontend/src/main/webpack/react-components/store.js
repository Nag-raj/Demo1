import { configureStore } from '@reduxjs/toolkit';
import orderReducer from './store/reducers/orderSlice';
import cartReducer from './store/reducers/cartSlice';
import checkoutReducer from './store/reducers/checkoutSlice';
import commonReducer from './store/reducers/commonSlice';
import userReducer from './store/reducers/userSlice';
import thankyouReducer from './store/reducers/thankyouSlice'

export const store = configureStore({
    reducer:{
        order: orderReducer,
        cart: cartReducer,
        checkout: checkoutReducer,
        common: commonReducer,
        user: userReducer,
        thankyou: thankyouReducer
    }
});
