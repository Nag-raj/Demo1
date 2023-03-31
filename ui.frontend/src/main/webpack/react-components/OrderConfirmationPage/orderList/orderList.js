import "./orderList.scss"
import Box from "@mui/material/Box";

const OrderList = ({order, dateplaced, ponumber, totalprice, modelstatuslabel, modelnumber, product, modeldescription, quantity}) =>{

    return (

        <div className="orderList">
            <div className="orderList__orderData">
                <div className="orderList__orderData-content">
                    <div>{dateplaced}: {order?.createdDate} on MyOlympus</div>
                    <div>{ponumber} {order?.purchaseOrderNumber}</div>
                </div>
                <div className="orderList__orderData-price">
                    <span className="orderList__orderData-price-data">{totalprice}: {order?.totalPrice?.formattedValue} ({order?.totalUnitCount} items)</span>  
                </div>
            </div>
            <Box>
                <div className="orderList__product-detail">
                    <span className="orderList__product-detail-status">{order?.status ? (order?.status).toLowerCase() : ''}</span>
                    {order?.entries?.map((entry) => 
                        <div className="orderList__product-detail-content">
                        <div className="orderList__product-detail-title">{entry?.product?.baseProductName}</div>
                        <div className="orderList__product-detail-list">
                            <div className="orderList__product-detail-header">
                                <span className="orderList__product-detail-header-model">{modelnumber}</span>
                                <span className="orderList__product-detail-header-product">{product}</span>
                                <span className="orderList__product-detail-header-quote">{modeldescription}</span>
                                <span className="orderList__product-detail-header-quantity">{quantity}</span>
                            </div>
                            <div className="orderList__product-detail-data">
                                <span className="orderList__product-mobile">{modelnumber}</span>
                                <span className="orderList__product-detail-data-model">{entry?.product?.code}</span>
                                <span className="orderList__product-mobile">{product}</span>
                                <span className="orderList__product-detail-data-product">{entry?.product?.name}</span>
                                <span className="orderList__product-mobile">{modeldescription}</span>
                                <span className="orderList__product-detail-data-quote">{entry?.product?.description}</span>
                                <span className="orderList__product-mobile">{quantity}</span>
                                <span className="orderList__product-detail-data-quantity">{entry?.quantity}</span>
                            </div>
                            {entry?.otherVariants?.map((variant) => {
                                <div className="orderList__product-detail-data">
                                    <span className="orderList__product-mobile">{modelnumber}</span>
                                    <span className="orderList__product-detail-data-model">{variant?.product?.code}</span>
                                    <span className="orderList__product-mobile">{product}</span>
                                    <span className="orderList__product-detail-data-product">{variant?.product?.name}</span>
                                    <span className="orderList__product-mobile">{modeldescription}</span>
                                    <span className="orderList__product-detail-data-quote">{variant?.product?.description}</span>
                                    <span className="orderList__product-mobile">{quantity}</span>
                                    <span className="orderList__product-detail-data-quantity">{variant?.quantity}</span>
                                </div>
                            })
                            }
                        </div>
                    </div>)}
                </div>
            </Box>
        </div>
    )
}



export default OrderList;