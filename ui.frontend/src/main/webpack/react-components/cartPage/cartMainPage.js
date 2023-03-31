import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import CartProductList from "./cartProductList";
import OrderSummaryPage from "../OrderSummaryPage/orderSummaryPage";
import { fetchMiniCart, getMiniCart } from "../store/reducers/commonSlice";
import { setUser, resetUser, getUser } from "../store/reducers/userSlice";
import {
  fetchCart,
  getCart,
  resetCart
} from "../store/reducers/cartSlice";
import Loader from "../Loader";

const CartMainPage = ({ 
  messages,
  checkoutpagepath,
  shoppagepath
 }) => {
  const {
    cartData,
    orderSummaryData,
    cartLoading,
    orderSummaryLoading,
    removeProductSucess,
    removeProductLoading
  } = useSelector(getCart);
  const { miniCart, miniCartLoading } = useSelector(getMiniCart);
  const { userData } = useSelector(getUser);
  const dispatch = useDispatch();

  useEffect(() => {
    const changeUserState = () => {
      if (window.userState) {
        dispatch(setUser());
        window.useState = false;
      }
    }
    window.addEventListener("userAdded", changeUserState);
    return () => {
      window.removeEventListener("userAdded", changeUserState);
      dispatch(resetCart());
    }
  }, []);
  
  useEffect(() => {
    if (userData) {
      console.log("User Data", userData);
      dispatch(fetchCart());
      dispatch(resetUser());
    }
  }, [userData, dispatch]);

  useEffect(() => {
    if (removeProductSucess !== null) {
      if (removeProductSucess?.items === null && !removeProductLoading) {
        dispatch(fetchMiniCart({ email: localStorage.getItem("userEmailId") }));
      }
    }
  }, [removeProductSucess, removeProductLoading]);

  useEffect(() => {
    const minicartAnchor = document.querySelector("#hoverme");
    if (miniCart?.customerCart?.displayTotalUnitCount != null &&
      removeProductSucess?.items !== null &&
      !removeProductLoading &&
      !miniCartLoading &&
      miniCart?.customerCart?.displayTotalUnitCount !== "0"
    ) {
      minicartAnchor.removeEventListener("click", window.openPanel);
      minicartAnchor.href = minicartAnchor.getAttribute('data-link');
      minicartAnchor.querySelector("#minicart-value").style.display = "block";
      $(minicartAnchor.querySelector("#minicart-value")).text(
        miniCart?.customerCart?.displayTotalUnitCount
      );
    } else if (miniCart?.customerCart?.displayTotalUnitCount === "0") {
      minicartAnchor.addEventListener("click", window.openPanel);
      minicartAnchor.href = '#';
      minicartAnchor.querySelector("#minicart-value").style.display = "none";
    }
  }, [
    miniCart,
    miniCartLoading,
    removeProductSucess,
    removeProductLoading
  ]);

  console.log("cartdata", cartData);
  return (
    <div className={cartLoading || orderSummaryLoading ? "cartpage-wrapper page-loading" : "cartpage-wrapper"}>
      {cartLoading || orderSummaryLoading ? (
        <Loader />
      ) : cartData !== null && cartData?.items !== null ? (
        <div className="cartpage-wrapper-main">
          <div className="cartpage-wrapper-inner">
            <h2 className="cartpage__title">
              Your Cart
            </h2>
            <CartProductList cartData={cartData} messages={messages} />
          </div>
          <div>
            <OrderSummaryPage orderSummaryData={cartData} checkoutpagepath={checkoutpagepath} shoppagepath={shoppagepath} />
          </div>
        </div>
      ) : (
        <div className="cartpage__no-results">
          <h2>Your Cart is Empty</h2>
          <a href={shoppagepath}>Continue Shopping</a>
        </div>
      )}
    </div>
  );
};
export default CartMainPage;
