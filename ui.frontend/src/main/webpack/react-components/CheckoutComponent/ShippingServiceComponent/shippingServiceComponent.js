import React, { useEffect, useState } from "react";
import "./shippingServiceComponent.scss";
import Box from "@mui/material/Box";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import FormHelperText from "@mui/material/FormHelperText";
import SvgIcon from "@material-ui/core/SvgIcon";
import {
  getCheckout,
  getShippingCarrierListThirdParty,
  getDeliveryListThirdParty,
} from "../../store/reducers/checkoutSlice";
import { useDispatch, useSelector } from "react-redux";
import Loader from "../../Loader";

const ShippingServiceComponent = ({
  getShippingCarrierHandler,
  getDeliveryCodeHandler,
}) => {
  const { shippingSuccess, shippingLoading } = useSelector(getCheckout);
  const { deliveryListSuccess, deliveryListLoading } = useSelector(getCheckout);
  const [shipping, setShipping] = useState(false);
  const [shippingArray, setShippingArray] = useState([]);
  const [deliveryArray, setDeliveryArray] = useState([]);
  const [shippingValue, setShippingValue] = useState("");
  const [deliveryValue, setDeliveryValue] = useState("");
  const { checkoutData, checkoutLoading } = useSelector(getCheckout);
  const dispatch = useDispatch();

  useEffect(() => {
    if (checkoutData) {
      dispatch(getShippingCarrierListThirdParty());
    }
  }, [dispatch, checkoutData]);

  useEffect(() => {
    if (shippingSuccess) {
      setShippingArray(
        shippingSuccess?.shippingCarrierListForThirdParty?.shippingCarriers
          ? shippingSuccess?.shippingCarrierListForThirdParty?.shippingCarriers
          : []
      );
      setShippingValue(
        shippingSuccess?.shippingCarrierListForThirdParty
          ?.shippingCarriers?.[0]?.["code"]
          ? shippingSuccess?.shippingCarrierListForThirdParty
              ?.shippingCarriers?.[0]?.["code"]
          : ""
      );
      dispatch(
        getDeliveryListThirdParty(
          shippingSuccess?.shippingCarrierListForThirdParty
            ?.shippingCarriers?.[0]?.["code"]
            ? shippingSuccess?.shippingCarrierListForThirdParty
                ?.shippingCarriers?.[0]?.["code"]
            : ""
        )
      );
    }
  }, [shippingSuccess]);

  useEffect(() => {
    if (shippingValue) {
      getShippingCarrierHandler(shippingValue);
    }
  }, [shippingValue]);

  useEffect(() => {
    if (deliveryValue) {
      getDeliveryCodeHandler(deliveryValue);
    }
  }, [deliveryValue]);

  useEffect(() => {
    setDeliveryArray(
      deliveryListSuccess?.shippingCarrierListForThirdParty?.deliveryOption
        ? deliveryListSuccess?.shippingCarrierListForThirdParty?.deliveryOption
        : []
    );
    setDeliveryValue(
      deliveryListSuccess?.shippingCarrierListForThirdParty
        ?.deliveryOption?.[0]?.["code"]
        ? deliveryListSuccess?.shippingCarrierListForThirdParty
            ?.deliveryOption?.[0]?.["code"]
        : ""
    );
  }, [deliveryListSuccess]);

  const getShippingCarrier = () => {
    setShipping(true);
  };

  const getShippingService = () => {
    setShipping(false);
  };

  const getShippingValues = (event) => {
    setShippingValue(event?.target?.value);
    dispatch(getDeliveryListThirdParty(event?.target?.value));
    if (deliveryListSuccess) {
      setDeliveryValue(
        deliveryListSuccess?.shippingCarrierListForThirdParty
          ?.deliveryOption?.[0]?.["code"]
      );
    }
  };
  const getDeliveryValues = (event) => {
    console.log("event");
    setDeliveryValue(event?.target?.value);
  };

  function CustomSvgIcon(props) {
    return (
      <SvgIcon {...props}>
        <path
          id="Path_3853"
          data-name="Path 3853"
          d="M10.707,12.76,1.852,3.9,3.245,2.5,10.707,9.96,18.168,2.5,19.561,3.9Z"
          transform="translate(-1.852 -2.499)"
          fill="#08107B"
        />
      </SvgIcon>
    );
  }
  return (
    <div
      className={
        shippingLoading || deliveryListLoading
          ? "checkout-shipping"
          : "checkout-shipping"
      }
    >
      <Box>
        <div className="shipping-heading">
          <h3>Shipping Service</h3>
          {!shipping ? (
            <a className="shipping-carrier-link" onClick={getShippingCarrier}>
              Add Shipping Carrier
            </a>
          ) : (
            ""
          )}
        </div>
        {!shipping ? (
          <>
            <div className="shipping-dropdown">
              <div className="shipping-panel">
                <FormControl>
                  <InputLabel shrink={true}>Shipping Carrier</InputLabel>
                  <Select
                    IconComponent={CustomSvgIcon}
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={shippingValue}
                    label="Shipping Carrier"
                    variant="standard"
                    onChange={getShippingValues}
                  >
                    {shippingArray?.map((ship) => {
                      return (
                        <MenuItem value={ship?.code}>{ship?.name}</MenuItem>
                      );
                    })}
                  </Select>
                </FormControl>
              </div>
              <div className="shipping-panel">
                <FormControl>
                  <InputLabel shrink={true}>Delivery Option</InputLabel>
                  <Select
                    IconComponent={CustomSvgIcon}
                    variant="standard"
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={deliveryValue}
                    // label="Age"
                    onChange={getDeliveryValues}
                  >
                    {deliveryArray?.map((ship) => {
                      return (
                        <MenuItem value={ship?.code}>{ship?.name}</MenuItem>
                      );
                    })}
                  </Select>
                  <FormHelperText>
                    {" "}
                    All service are not available in all locations. Orders
                    placed after xx pm, may not be shipped today.
                  </FormHelperText>
                </FormControl>
              </div>
            </div>
            {/* <div className="disclaimer">
              Your order contains Aldahol. Chemicals are required to be
              transported by ground. The delivery option selected above will be
              applied to the rest of the order.
            </div> */}
          </>
        ) : (
          <>
            <div className="carrier-confirm">
              <p className="ups">UPS</p>
              <p>1830942</p>
            </div>
            <div className="shipping-dropdown">
              <div className="shipping-panel">
                <FormControl>
                  <InputLabel shrink={true}>Shipping Carrier</InputLabel>
                  <Select
                    IconComponent={CustomSvgIcon}
                    variant="standard"
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={shippingValue}
                    label="Age"
                    // onChange={getShippingValues}
                  >
                    <MenuItem value={10}>Ten</MenuItem>
                    <MenuItem value={20}>Twenty</MenuItem>
                    <MenuItem value={30}>Thirty</MenuItem>
                  </Select>
                </FormControl>
              </div>
              <div className="shipping-panel-textfield">
                <input
                  type="text"
                  placeholder="Example placeholder text"
                  class="ShippingNumber__input-suite input-fields"
                ></input>
              </div>
            </div>
            <div class="shopping-btn-container">
              <button
                id="shoppingBtn"
                type="button"
                class=" confirm btn btn-primary"
                onClick={getShippingService}
              >
                <a id="shoppingText">Confirm</a>
              </button>
            </div>
            <div class="shopping-btn-container">
              <button
                id="shoppingBtn"
                type="button"
                class="cancel cancel-panel btn btn-primary"
                onClick={getShippingService}
              >
                <a className="cancel-button" id="shoppingText">
                  Cancel
                </a>
              </button>
            </div>
          </>
        )}
      </Box>
    </div>
  );
};

export default ShippingServiceComponent;
