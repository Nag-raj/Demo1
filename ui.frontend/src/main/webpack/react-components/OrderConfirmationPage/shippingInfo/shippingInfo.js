import "./shippingInfo.scss"
import Box from "@mui/material/Box";

const ShippingInfo = ({shipInfo, address, paymentmethod, paymentType}) =>{

    return (
        <div className="shippingInfo">
            <Box className="shippingInfo__shipping">
                <div className="shippingInfo__shipping-content">
                    <div className="shippingInfo__shipping-title">
                    {address}
                    </div>
                    <div className="shippingInfo__shipping-desc">
                        <span className="shippingInfo__shipping-desc-company">
                            {shipInfo?.companyName}
                        </span>
                        <span>
                        {/* 1930 Springfield Drive Los Angels, California 90018 */}
                        {shipInfo?.formattedAddress}
                        </span>
                    </div>
                </div>
            </Box>
            <Box className="shippingInfo__payment">
                <div className="shippingInfo__payment-content" >
                    <div className="shippingInfo__payment-title">
                    {paymentmethod}
                    </div>
                    {paymentType?.code === "ACCOUNT" && <div className="shippingInfo__payment-desc">
                        <span className="shippingInfo__payment-desc-company">
                        {/* Visa ending in 3819 Billing Address: 1930 Springfield Drive Los Angels, California 90018 */}
                        Invoice
                        </span>
                        <span>
                            You will receive an invoice with further payment instructions.
                        </span>
                    </div>}
                </div>
            </Box>
        </div>
    )
}



export default ShippingInfo;