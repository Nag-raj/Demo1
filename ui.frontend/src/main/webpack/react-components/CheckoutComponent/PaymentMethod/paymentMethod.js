import React, { useEffect, useState } from "react";
import "../ShippingServiceComponent/shippingServiceComponent.scss";
import Box from "@mui/material/Box";
import Radio from "@mui/material/Radio";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import "./paymentMethod.scss";
import { useDispatch, useSelector } from "react-redux";
import {
  getCheckout,
  getPayByInvoice,
  payByInvoiceSelection,
} from "../../store/reducers/checkoutSlice";

const PaymentMethod = () => {
  const { checkoutData, checkoutLoading } = useSelector(getCheckout);
  const { payInvoiceSuccess } = useSelector(getCheckout);
  const [showPaymentMethod, setShowPaymentMethod] = useState([]);
  const [showPaymentCard, setShowPaymentCard] = useState(false);
  const [showPaymentInvoice, setShowPaymentInvoice] = useState(false);
  const dispatch = useDispatch();

  useEffect(() => {
    if (checkoutData) {
      dispatch(getPayByInvoice());
    }
  }, [dispatch, checkoutData]);

  useEffect(() => {
    if (payInvoiceSuccess) {
      setShowPaymentMethod(payInvoiceSuccess?.cart?.available_payment_methods);
      setShowPaymentCard(
        payInvoiceSuccess?.cart?.available_payment_methods?.[0]?.isActive
      );
      setShowPaymentInvoice(
        payInvoiceSuccess?.cart?.available_payment_methods?.[1]?.isActive
      );
    }
  }, [payInvoiceSuccess]);

  const paymentClicked = (event) => {
    const paymentMethod = event?.target?.value;
    if (paymentMethod) {
      const data = {
        payment_method: `\"${paymentMethod}\"`,
      };
      dispatch(payByInvoiceSelection(data?.payment_method));
    }
  };
  return (
    <div className={"payment-method"}>
      <Box>
        <div className="payment-heading">
          <h4>Payment Method</h4>
        </div>
        <div>
          <FormControl>
            <FormLabel id="demo-row-radio-buttons-group-label">
              If you proceed without entering credit card information, Olympus
              will contact your account payer with an invoice.
            </FormLabel>
            <RadioGroup
              aria-labelledby="demo-row-radio-buttons-group-label"
              name="row-radio-buttons-group"
            >
              <FormControlLabel
                disabled={!showPaymentCard}
                value={
                  payInvoiceSuccess
                    ? showPaymentMethod?.[0]?.code
                    : "pay_credit"
                }
                sx={{
                  color: "#444444",
                }}
                onClick={(event) => paymentClicked(event)}
                control={
                  <Radio
                    sx={{
                      color: "#08107B",
                      "&.Mui-checked": {
                        color: "#08107B",
                      },
                    }}
                  />
                }
                label="Pay with Credit Card"
              />
              <FormControlLabel
                disabled={!showPaymentInvoice}
                value={
                  payInvoiceSuccess
                    ? showPaymentMethod?.[1]?.code
                    : "pay_invoice"
                }
                onClick={paymentClicked}
                sx={{
                  color: "#444444",
                }}
                control={
                  <Radio
                    sx={{
                      color: "#08107B",
                      "&.Mui-checked": {
                        color: "#08107B",
                      },
                    }}
                  />
                }
                label="Pay via Invoice"
              />
            </RadioGroup>
          </FormControl>
        </div>
      </Box>
    </div>
  );
};

export default PaymentMethod;
