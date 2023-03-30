import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import ShippingServiceComponent from "./ShippingServiceComponent/shippingServiceComponent";
import ShippingAddress from "./shippingAddress/shippingAddress";
import OrderSummaryPage from "../OrderSummaryPage/orderSummaryPage";
import { fetchCheckout, getCheckout } from "../store/reducers/checkoutSlice";
import "./checkoutComponent.scss";
import Loader from "../Loader";
import PaymentMethod from "./PaymentMethod/paymentMethod";
import { setUser, resetUser, getUser } from "../store/reducers/userSlice";

const CheckoutComponent = ({
  locale,
  messages,
  disclaimertext,
  termsofusetext,
  termsofuselink,
  checkoutpagepath,
  shoppagepath,
}) => {
  //const { checkout, checkoutLoading } = useState(false);
  const { checkoutData, orderSummaryData, checkoutLoading } =
    useSelector(getCheckout);
  const [shippingValue, setShippingValue] = useState("");
  const { userData } = useSelector(getUser);
  const [deliveryValue, setDeliveryValue] = useState("");
  const [poNumberValue, setPoNumberValue] = useState("");
  const [attnSuite, setAttnSuite] = useState("");
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
    }
  }, []);

  useEffect(() => {
    if (userData) {
      dispatch(fetchCheckout());
      dispatch(resetUser());
    }
  }, [userData, dispatch]);

  const getShippingCarrierHandler = (shippingValue) => {
    setShippingValue(shippingValue);
  };

  const getDeliveryCodeHandler = (deliveryValue) => {
    setDeliveryValue(deliveryValue);
  };

  const getpoNumberHandler = (poNumberValue) => {
    setPoNumberValue(poNumberValue);
  };

  const getAttnToSuiteHandler = (attnSuite) => {
    setAttnSuite(attnSuite);
  };

  return (
    <div
      className={
        checkoutLoading ? "checkout-cont page-loading" : "checkout-cont"
      }
    >
      {!checkoutLoading ? (
        <div className="checkout-wrapper">
          <div className="checkout-wrapper-left-block">
            <h2 className="checkout-title">
              {messages["checkoutPage.pageTitleText"]}
            </h2>
            <ShippingAddress
              getpoNumberHandler={getpoNumberHandler}
              getAttnToSuiteHandler={getAttnToSuiteHandler}
            />
            <ShippingServiceComponent
              getShippingCarrierHandler={getShippingCarrierHandler}
              getDeliveryCodeHandler={getDeliveryCodeHandler}
              messages={messages}
            />
            <PaymentMethod />
          </div>
          <div className="checkout-wrapper-right-block">
            <OrderSummaryPage
              poNumberValue={poNumberValue}
              attnSuite={attnSuite}
              shippingValue={shippingValue}
              deliveryValue={deliveryValue}
              orderSummaryData={checkoutData?.data?.checkoutSummary}
              disclaimertext={disclaimertext}
              termsofusetext={termsofusetext}
              termsofuselink={termsofuselink}
              checkoutpagepath={checkoutpagepath}
              shoppagepath={shoppagepath}
            />
          </div>
        </div>
      ) : (
        <Loader />
      )}
    </div>
  );
};

export default CheckoutComponent;
