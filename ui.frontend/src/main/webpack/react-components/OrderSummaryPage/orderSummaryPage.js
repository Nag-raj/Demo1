import React, { useEffect, useState } from "react";
import "./orderSummaryPage.scss";
import {
  getCheckout,
  getPayByInvoice,
  placeOrderCheckout,
} from "../../react-components/store/reducers/checkoutSlice";
import { useDispatch, useSelector } from "react-redux";

const OrderSummaryPage = ({
  orderSummaryData,
  disclaimertext,
  termsofusetext,
  termsofuselink,
  checkoutpagepath,
  shoppagepath,
  shippingValue,
  deliveryValue,
  poNumberValue,
  attnSuite,
}) => {
  const data = orderSummaryData;
  const [isCheckoutPageVisible, setIsCheckoutPageVisible] = useState(false);
  const [isChecked, setIsChecked] = useState(false);
  const [showStickySummary, setShowStickySummary] = useState(true);
  const { placeOrderSuccess } = useSelector(getCheckout);
  const dispatch = useDispatch();

  useEffect(() => {
    const isCheckoutPageElement =
      document.getElementsByClassName("checkoutpage");
    if (isCheckoutPageElement[0]?.className?.includes("checkoutpage")) {
      setIsCheckoutPageVisible(true);
    }

    // onload it executes once to handle Sticky Order Summary
    showStickyOrderSummary();

    window.addEventListener("scroll", showStickyOrderSummary);
    return () => {
      window.removeEventListener("scroll", showStickyOrderSummary);
    };
  }, []);

  useEffect(() => {
    if (placeOrderSuccess) {
      window.location.href = `${checkoutpagepath}?orderId=${placeOrderSuccess}`;
    }
  }, [placeOrderSuccess]);

  /**
   * function handles the user acceptance check-box selected in orderSummary component
   * @returns {void}
   */
  const handleOnChange = () => {
    setIsChecked(!isChecked);
  };

  /**
   * function handles the show stick ordersummary toggle in mobile resolution
   * @returns {void}
   */
  const showStickyOrderSummary = (event) => {
    const orderSummaryElement = document.getElementsByClassName(
      "orderSummary__container-box"
    );

    const orderSummaryPosition =
      orderSummaryElement[0]?.getBoundingClientRect();
    if (orderSummaryPosition.top <= window.innerHeight) {
      setShowStickySummary(false);
    } else {
      setShowStickySummary(true);
    }
  };

  const getOrderPlaced = () => {
    const placeOrder = {
      additionalAddressLine: `\"${attnSuite}\"`,
      deliveryOptionCode: `\"${deliveryValue}\"`,
      poNumber: `\"${poNumberValue}\"`,
      shippingCarrierCode: `\"${shippingValue}\"`,
    };
    dispatch(placeOrderCheckout(placeOrder));
  };
  return (
    <>
      <div className="orderSummary__container-box">
        <h2 className="orderSummary__heading">Order Summary</h2>
        <div className="orderSummary__table-data">
          <span className="orderSummary__left-content">
            {data?.total_quantity} Items
          </span>
          <span className="orderSummary__right-content">
            {data?.prices?.subtotal_excluding_tax?.formattedPrice}
          </span>
          <span className="orderSummary__left-content">Delivery</span>
          <span className="orderSummary__right-content">
            {!isCheckoutPageVisible
              ? "TBD"
              : shippingValue !== "shipByOlympus"
              ? "Charged by your carrier"
              : data?.deliveryCost
              ? data?.deliveryCost?.formattedPrice === null ||
                data?.deliveryCost?.formattedPrice === "$0.00"
                ? "-"
                : data?.deliveryCost?.formattedPrice
              : "-"}
          </span>
          <span className="orderSummary__left-content">Sales Tax</span>
          <span className="orderSummary__right-content">
            {!isCheckoutPageVisible
              ? data?.prices?.applied_taxes[0]?.amount?.formattedPrice ===
              null ||
              data?.prices?.applied_taxes[0]?.amount?.formattedPrice ===
                "$0.00"
              ? "TBD"
              : data?.prices?.applied_taxes[0]?.amount?.formattedPrice
              : data?.prices?.applied_taxes[0]?.amount?.formattedPrice ===
                  null ||
                data?.prices?.applied_taxes[0]?.amount?.formattedPrice ===
                  "$0.00"
              ? "-"
              : data?.prices?.applied_taxes[0]?.amount?.formattedPrice}
          </span>
          <span className="orderSummary__left-content">Contract Savings</span>
          <span className="orderSummary__right-content">
            {data?.prices?.discounts[0]?.amount?.formattedPrice === null ||
            data?.prices?.discounts[0]?.amount?.formattedPrice === "$0.00"
              ? data?.prices?.discounts[0]?.amount?.formattedPrice
              : `-${data?.prices?.discounts[0]?.amount?.formattedPrice}`}
          </span>
          <span className="orderSummary__left-content bold">
            {!isCheckoutPageVisible ? "Estimated Total" : "Total"}
          </span>
          <span className="orderSummary__right-content bold">
            {data?.prices?.grand_total?.formattedPrice}
          </span>
        </div>
        {isCheckoutPageVisible && (
          <div className="orderSummary__text-content">
            <input
              className="orderSummary__text-content--purchase-disclaimer"
              type="checkbox"
              onChange={handleOnChange}
              value="true"
              checked={isChecked}
            />
            <p>
              {disclaimertext}
              <a href={termsofuselink} target="_blank">
                {termsofusetext}
              </a>
            </p>
          </div>
        )}
        <div className="shopping-btn-container">
          {isCheckoutPageVisible ? (
            <button
              id="shoppingBtn"
              type="button"
              className="btn btn-primary placeOrder-btn"
              disabled={isCheckoutPageVisible && !isChecked}
            >
              <a onClick={getOrderPlaced} id="shoppingText">
                Place Order
                <img
                  className="shopping-arrow"
                  src="/content/dam/olympus/icon/Arrow_White.svg"
                  alt="arrow"
                />
                <img
                  className="shopping-arrow-white"
                  src="/content/dam/olympus/icon/Arrow_OlyBlue.svg"
                  alt="arrow"
                />
              </a>
            </button>
          ) : (
            <button
              id="shoppingBtn"
              type="button"
              className="btn btn-primary proceedtoCheckout-btn"
              disabled={isCheckoutPageVisible && !isChecked}
            >
              <a href={checkoutpagepath} id="shoppingText">
                Proceed to Checkout
                <img
                  className="shopping-arrow"
                  src="/content/dam/olympus/icon/Arrow_White.svg"
                  alt="arrow"
                />
                <img
                  className="shopping-arrow-white"
                  src="/content/dam/olympus/icon/Arrow_OlyBlue.svg"
                  alt="arrow"
                />
              </a>
            </button>
          )}
        </div>
        <div className="orderSummary__link">
          <a href={shoppagepath}>Continue Shopping</a>
          <a className="content-text" href="">
            Check More Prices
          </a>
        </div>
      </div>
      {showStickySummary && (
        <div className="orderSummary__container-box sticky-content">
          <div className="orderSummary__table-data">
            <span className="bold">
             {data?.prices?.grand_total?.formattedPrice} {!isCheckoutPageVisible ? "Estimated Total" : "Total"}
•{" "}
             {data?.total_quantity} Items
            </span>
          </div>
          <div className="shopping-btn-container">
            {isCheckoutPageVisible ? (
              <button
                id="shoppingBtn"
                type="button"
                className="btn btn-primary placeOrder-btn"
                disabled={isCheckoutPageVisible && !isChecked}
              >
                <a id="shoppingText" onClick={getOrderPlaced}>
                  Place Order
                  <img
                    className="shopping-arrow"
                    src="/content/dam/olympus/icon/Arrow_White.svg"
                    alt="arrow"
                  />
                  <img
                    className="shopping-arrow-white"
                    src="/content/dam/olympus/icon/Arrow_OlyBlue.svg"
                    alt="arrow"
                  />
                </a>
              </button>
            ) : (
              <button
                id="shoppingBtn"
                type="button"
                className="btn btn-primary proceedtoCheckout-btn"
                disabled={isCheckoutPageVisible && !isChecked}
              >
                <a href={checkoutpagepath} id="shoppingText">
                  Proceed to Checkout
                  <img
                    className="shopping-arrow"
                    src="/content/dam/olympus/icon/Arrow_White.svg"
                    alt="arrow"
                  />
                  <img
                    className="shopping-arrow-white"
                    src="/content/dam/olympus/icon/Arrow_OlyBlue.svg"
                    alt="arrow"
                  />
                </a>
              </button>
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default OrderSummaryPage;
