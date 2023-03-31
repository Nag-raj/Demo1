import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import "./orderConfirmationPage.scss";
import ShippingInfo from "./shippingInfo/shippingInfo";
import OrderList from "./orderList/orderList";
import { getThankyou, getOrderPlaced } from "../store/reducers/thankyouSlice";
import Loader from "../Loader";

const OrderConfirmationPage = ({
  title,
  description,
  address,
  paymentmethod,
  dateplaced,
  ordernumber,
  ponumber,
  totalprice,
  modelstatuslabel,
  modelnumber,
  product,
  modeldescription,
  quantity,
}) => {
  const { orderPlacedSuccess, orderPlacedLoading } = useSelector(getThankyou);
  const dispatch = useDispatch();
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const paramValue = params.get("orderId");
    const data = {
      orderId: `\"${paramValue}\"`,
    };
    dispatch(getOrderPlaced(data?.orderId));
  }, [dispatch]);

  return (
    <div className={orderPlacedLoading ? "page-loading" : "orderConfirmation"}>
      {orderPlacedLoading ? <Loader/> : <><div className="orderConfirmation__heading">
        <div className="orderConfirmation__title">{title}</div>
        <p className="orderConfirmation__desc">{description}</p>
      </div>
      <ShippingInfo
        shipInfo={orderPlacedSuccess?.deliveryAddress}
        address={address}
        paymentmethod={paymentmethod}
        paymentType={orderPlacedSuccess?.paymentType}
      />
      <OrderList order={orderPlacedSuccess} dateplaced={dateplaced} ponumber={ponumber} totalprice={totalprice} modelstatuslabel={modelstatuslabel} modelnumber={modelnumber} product={product} modeldescription={modeldescription} quantity={quantity} />
      </>
      }
    </div>
  );
};

export default OrderConfirmationPage;
