import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getOrders, reorderProduct } from "../store/reducers/orderSlice";
import { getMiniCart, fetchMiniCart } from "../store/reducers/commonSlice";
import './orderHistory.scss';

const OrderRow = ({order, messages}) => {
    const { reorderSuccess, reorderFailure, reorderLoading } = useSelector(getOrders);
    const { miniCart, miniCartLoading} = useSelector(getMiniCart);
    const [selectedSku, setSelectedSku] = useState();
    const dispatch = useDispatch();
    const mainOrder = order;

    useEffect(() => {
        if (!reorderLoading) {
            if (!reorderSuccess?.errors && reorderSuccess?.data.addProductsToCart.cart.items !== null && selectedSku) {
                setSelectedSku('');
                console.log("reorder-Loading", reorderLoading);
                console.log("reorder value", reorderSuccess);
                dispatch(fetchMiniCart({email: localStorage.getItem('userEmailId')}));
            } else {
                if (reorderSuccess?.errors) {
                    setSelectedSku('');
                }
            }
        }
    }, [reorderSuccess, reorderLoading, dispatch]);

    useEffect(() => {
        if (miniCart?.customerCart?.displayTotalUnitCount != null &&
                reorderSuccess?.data.addProductsToCart.cart.items !== null &&
                !reorderLoading &&
                !miniCartLoading &&
                selectedSku == '' &&
                miniCart?.customerCart?.displayTotalUnitCount !== "0") {
            const minicartAnchor = document.querySelector('#hoverme');
            const openPannel = () => {
                document.getElementById("notif").style.display = "block";
                document.getElementById("overlay").style.display = "block";
              };
            minicartAnchor.removeEventListener('click', window.openPanel);
            minicartAnchor.href = minicartAnchor.getAttribute('data-link');
            minicartAnchor.querySelector('#minicart-value').style.display = 'block';
            $(minicartAnchor.querySelector('#minicart-value')).text(miniCart?.customerCart?.displayTotalUnitCount);
        }
    }, [miniCart, reorderSuccess, reorderLoading, miniCartLoading]);

    const handleReorder = (event, sku, quantity) => {
        setSelectedSku(sku);
        const data = {
            cartId: localStorage.getItem('cartId'),
            product: {
                sku,
                quantity
            }
        };
        dispatch(reorderProduct(data));
    };

    return (
        <div className="orderhistory__item">
            <div className="orderhistory__item-top">
                <div className="orderhistory__item-top-date">
                    <span>{messages['orderRow.orderDate']}: {mainOrder.creationTime} on MyOlympus</span>
                    <span>{messages['orderRow.orderNumber']} {mainOrder.erpOrderNumber} | {messages['orderRow.poNumber']} {mainOrder.purchaseOrderNumber}</span>
                </div>
                <div className="orderhistory__item-top-price">
                    <span>{messages['orderRow.totalPrice']}: {mainOrder.totalPrice} ({mainOrder.totalEntries} items)</span>
                </div>
            </div>
            {mainOrder?.consignments?.map((consignment) =>
                <div className="orderhistory__item-content">
                    <div className="orderhistory__item-content--main">
                        <span className="orderhistory__item-content--main-status">{(consignment.consignmentStatus).toLowerCase()}</span>
                        {consignment?.statusMessage ? <span className="orderhistory__item-content--main-edd">{consignment.statusMessage}</span> : null}
                        {consignment?.trackingID ? <span className="orderhistory__item-content--main-tracking">{messages['orderRow.tracking']}: <a href="#">#{consignment.trackingID}</a></span> : null}
                    </div>
                    {consignment?.entries?.map((entry) =>
                        <>
                            <div className="orderhistory__item-content--title">
                                {entry.orderEntry.product.baseProductName}
                            </div>
                            <div className="orderhistory__item-content--header">
                                <span className="orderhistory__item-content--header-item">{messages['orderRow.modelNumber']}</span>
                                <span className="orderhistory__item-content--header-item">{messages['orderRow.product']}</span>
                                <span className="orderhistory__item-content--header-item">{messages['orderRow.description']}</span>
                                <span className="orderhistory__item-content--header-item">{messages['orderRow.quantity']}</span>
                            </div>
                            <div className="orderhistory__item-content--data">
                                <div className="orderhistory__item-content--data-row">
                                    <div className="orderhistory__item-content--data-row--first">
                                        <span>{entry.orderEntry.product.code}
                                            {entry.orderEntry.product.purchaseEnabled ? <button type="button" disabled={selectedSku === entry.orderEntry.product.code} className="orderhistory__reorder" onClick={(event) => handleReorder(event, entry.orderEntry.product.code, entry.orderEntry.quantity)}>{messages['orderRow.reorder']}</button> : null}
                                        </span>
                                    </div>
                                    <span>{entry.orderEntry.product.name}</span>
                                    <span>{entry.orderEntry.product.description}</span>
                                    <div>
                                        <input className="orderhistory__item-content--data-quantity" type="text" value={entry.orderEntry.quantity} disabled="true" />
                                    </div>
                                </div>
                                {entry.otherVariants?.map((variant) =>
                                    <div className="orderhistory__item-content--data-row">
                                        <div className="orderhistory__item-content--data-row--first">
                                            <span>{variant.orderEntry.product.code}</span>
                                            {variant.orderEntry.product.purchaseEnabled ? <button type="button" disabled={selectedSku === variant.orderEntry.product.code} className="orderhistory__reorder" onClick={(event) => handleReorder(event, variant.orderEntry.product.code, variant.orderEntry.quantity)}>Reorder</button> : null}
                                        </div>
                                        <span>{variant.orderEntry.product.name}</span>
                                        <span>{variant.orderEntry.product.description}</span>
                                        <div>
                                            <input className="orderhistory__item-content--data-quantity" type="text" value={variant.orderEntry.quantity} disabled="true" />
                                        </div>
                                    </div>
                                )}
                            </div>
                            <div className="orderhistory__item-mobile">
                                <div className="orderhistory__item-mobile-row">
                                    <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.modelNumber']}</span>
                                    <div className="orderhistory__item-content--data-row--first">
                                        <span>{entry.orderEntry.product.code}</span>
                                        {entry.orderEntry.product.purchaseEnabled ? <button type="button" className="orderhistory__reorder" disabled={selectedSku === entry.orderEntry.product.code} onClick={(event) => handleReorder(event, entry.orderEntry.product.code, entry.orderEntry.quantity)}>{messages['orderRow.reorder']}</button> : null}
                                    </div>
                                    <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.product']}</span>
                                    <span className="orderhistory__item-mobile-row--value">{entry.orderEntry.product.name}</span>
                                    <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.description']}</span>
                                    <span className="orderhistory__item-mobile-row--value">{entry.orderEntry.product.description}</span>
                                    <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.quantity']}</span>
                                    <div>
                                        <input className="orderhistory__item-content--data-quantity" type="text" value={entry.orderEntry.quantity} disabled="true" />
                                    </div>
                                </div>
                                {entry.otherVariants?.map((variant) =>
                                    <div className="orderhistory__item-mobile-row">
                                        <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.modelNumber']}</span>
                                        <div className="orderhistory__item-content--data-row--first">
                                            <span>{variant.orderEntry.product.code}</span>
                                            {variant.orderEntry.product.purchaseEnabled ? <button type="button" className="orderhistory__reorder" disabled={selectedSku === variant.orderEntry.product.code} onClick={(event) => handleReorder(event, variant.orderEntry.product.code, variant.orderEntry.quantity)}>{messages['orderRow.reorder']}</button> : null}
                                        </div>
                                        <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.product']}</span>
                                        <span className="orderhistory__item-mobile-row--value">{variant.orderEntry.product.name}</span>
                                        <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.description']}</span>
                                        <span className="orderhistory__item-mobile-row--value">{variant.orderEntry.product.description}</span>
                                        <span className="orderhistory__item-mobile-row--heading">{messages['orderRow.quantity']}</span>
                                        <div>
                                            <input className="orderhistory__item-content--data-quantity" type="text" value={variant.orderEntry.quantity} disabled="true" />
                                        </div>
                                    </div>
                                )}
                                {consignment?.trackingID ? <div className="orderhistory__item-mobile-row--tracking">
                                    <span>{messages['orderRow.tracking']}: <a href="#">#{consignment.trackingID}</a></span>
                                </div> : null}
                                {consignment?.statusMessage ? <div className="orderhistory__item-mobile-row--edd">
                                    <span>{consignment?.statusMessage}</span>
                                </div> : null}
                            </div>
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default OrderRow;
