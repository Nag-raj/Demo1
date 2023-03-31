import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { updateCartItemsQuantity, removeProductData } from "../store/reducers/cartSlice";
import { getCart } from "../store/reducers/cartSlice";
import { getMiniCart, fetchMiniCart } from "../store/reducers/commonSlice";
import "./cartPage.scss";

const cartProductList = ({ cartData, messages }) => {
  const { updateCartItemsQuantityData, updateCartItemsQuantityLoading, removeProductSucess, removeProductLoading } =
    useSelector(getCart);
  const { miniCart, miniCartLoading } = useSelector(getMiniCart);
  const [quantity, setquantity] = useState({});
  const dispatch = useDispatch();

  const variantList = cartData;

  useEffect(() => {
    if (updateCartItemsQuantityData !== null) {
      if (updateCartItemsQuantityData?.items !== null && !updateCartItemsQuantityLoading) {
        dispatch(fetchMiniCart({ email: localStorage.getItem("userEmailId") }));
      }
    }
  }, [updateCartItemsQuantityData, updateCartItemsQuantityLoading]);

  useEffect(() => {
    if (removeProductSucess !== null) {
      if (removeProductSucess?.items !== null && !removeProductLoading) {
        console.log("Test");
        dispatch(fetchMiniCart({ email: localStorage.getItem("userEmailId") }));
      }
    }
  }, [removeProductSucess, removeProductLoading]);

  useEffect(() => {
    const minicartAnchor = document.querySelector("#hoverme");
    if (
      miniCart?.customerCart?.displayTotalUnitCount != null &&
      updateCartItemsQuantityData?.items !== null &&
      removeProductSucess?.items !== null &&
      !removeProductLoading &&
      !updateCartItemsQuantityLoading &&
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
    updateCartItemsQuantityData,
    updateCartItemsQuantityLoading,
    miniCartLoading,
    removeProductSucess,
    removeProductLoading
  ]);

  const onQuantityChange = (event, id) => {
    if (/^[0-9\b]+$/.test(event.target.value) || event.target.value === "") {
      if (/^0/.test(event.target.value)) {
        setquantity({ ...quantity, [id]: "" });
      } else {
        setquantity({ ...quantity, [id]: event.target.value.slice(0, 4) });
      }
    }
  };

  const updateQuantity = (event, uid, quantity) => {
    if (quantity) {
      const updateData = {
        uid: uid,
        quantity: parseInt(quantity),
      };
      console.log("Mobile trigger", updateData);
      dispatch(updateCartItemsQuantity(updateData));
    }
  };

  const removeProduct = (uid) => {
    dispatch(removeProductData(uid));
  };

  return (
    <>
      {variantList?.items?.map((list) => (
        <div className="productcartpanel product-desktop">
          <div className="productcartpanel-variant">
            <div className="productcartpanel-baseproduct">
              {list?.product?.baseProductName}
            </div>
            <div className="productcartpanel-variant-header">
              <span className="productcartpanel-variant-header-item">
                {messages["cartProductList.itemNumber"]}
              </span>
              <span className="productcartpanel-variant-header-item">
                {messages["cartProductList.product"]}
              </span>
              <span className="productcartpanel-variant-header-item">
                {messages["cartProductList.price"]}
              </span>
              <span className="productcartpanel-variant-header-item">
                {messages["cartProductList.quantity"]}
              </span>
            </div>
            <div className="productcartpanel-variant-data">
              <div className="productcartpanel-variant-data-row">
                <span className="productcartpanel-variant-data-item">
                  {list?.product?.sku}
                </span>
                <span className="productcartpanel-variant-data-item">
                  {list?.product?.name}
                </span>
                <div className="price productcartpanel-variant-data-item">
                  {(list?.formattedContractPrice !== null && list?.formattedContractPrice !== "$0.00") && <span className="list-price">
                    {list?.product?.price?.regularPrice?.amount?.formattedPrice}
                  </span>}
                  <span className="contract-price">
                    {list?.formattedContractPrice !== null && list?.formattedContractPrice !== "$0.00" ? list?.formattedContractPrice : list?.product?.price?.regularPrice?.amount?.formattedPrice}
                  </span>
                </div>
                <div className="productcartpanel__item-quantity productcartpanel-variant-data-item">
                  <input
                    className="productcartpanel__item-quantity-value"
                    defaultValue={list?.quantity}
                    value={quantity[list?.id]}
                    onChange={(event) => onQuantityChange(event, list?.id)}
                  />
                </div>
                <div className="productcartpanel__item-actions">
                  <button
                    type="button"
                    className="productcartpanel__item-button"
                    onClick={(event) =>
                      updateQuantity(event, list?.id, quantity[list?.id])
                    }
                  >
                    <span className="productcartpanel__item-button-content">
                      <span>
                        {window.innerWidth >= 1025
                          ? messages["cartProductList.updateDesktop"]
                          : messages["cartProductList.updateMob"]}
                      </span>
                    </span>
                  </button>
                  <button
                    className="productcartpanel__item-button"
                    type="button"
                    onClick={() => removeProduct(list?.id)}
                  >
                    <span className="productcartpanel__item-button-content">
                      <img src="/content/dam/olympus/icon/Delete.svg" />
                      <span>{messages["cartProductList.remove"]}</span>
                    </span>
                  </button>
                </div>
              </div>
              {list?.otherVariants.length > 0 &&
                list?.otherVariants?.map((variant) => (
                  <div className="productcartpanel-variant-data-row">
                    <span className="productcartpanel-variant-data-item">
                      {variant?.product?.sku}
                    </span>
                    <span className="productcartpanel-variant-data-item">
                      {variant?.product?.name}
                    </span>
                    <div className="price productcartpanel-variant-data-item">
                      {(variant?.formattedContractPrice !== null && variant?.formattedContractPrice !== "$0.00") && <span className="list-price">
                        {
                          variant?.product?.price?.regularPrice?.amount?.formattedPrice
                        }
                      </span>}
                      <span className="contract-price">
                        {variant?.formattedContractPrice !== null && variant?.formattedContractPrice !== "$0.00" ? variant?.formattedContractPrice : variant?.product?.price?.regularPrice?.amount?.formattedPrice}
                      </span>
                    </div>
                    <div className="productcartpanel__item-quantity productcartpanel-variant-data-item">
                      <input
                        className="productcartpanel__item-quantity-value"
                        defaultValue={variant?.quantity}
                        value={quantity[variant?.id]}
                        onChange={(event) =>
                          onQuantityChange(event, variant?.id)
                        }
                      />
                    </div>
                    <div className="productcartpanel__item-actions">
                      <button
                        type="button"
                        className="productcartpanel__item-button"
                        onClick={(event) =>
                          updateQuantity(
                            event,
                            variant?.id,
                            quantity[variant?.id]
                          )
                        }
                      >
                        <span className="productcartpanel__item-button-content">
                          <span>
                            {window.innerWidth >= 1025
                              ? messages["cartProductList.updateDesktop"]
                              : messages["cartProductList.updateMob"]}
                          </span>
                        </span>
                      </button>
                      <button
                        className="productcartpanel__item-button"
                        type="button"
                        onClick={() => removeProduct(variant?.id)}
                      >
                        <span className="productcartpanel__item-button-content">
                          <img src="/content/dam/olympus/icon/Delete.svg" />
                          <span>{messages["cartProductList.remove"]}</span>
                        </span>
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        </div>
      ))}

      {variantList?.items?.map((list) => (
        <div className="productcartpanel product-mobile">
          <div className="productcartpanel-variant-mobile">
            <div className="productcartpanel-baseproduct">
              {list?.product?.baseProductName}
            </div>
            <div className="productcartpanel-variant-mobile-row">
              <span className="productcartpanel-variant-mobile-row-title">
                {messages["cartProductList.itemNumber"]}
              </span>
              <span className="productcartpanel-variant-mobile-data-item">
                {list?.product?.sku}
              </span>
              <span className="productcartpanel-variant-mobile-row-title">
                {messages["cartProductList.product"]}
              </span>
              <span className="productcartpanel-variant-mobile-data-item">
                {list.product?.name}
              </span>
              <div className="productcartpanel-variant-mobile-row-actions-price">
                <span className="productcartpanel-variant-mobile-row-title">
                  {messages["cartProductList.price"]}
                </span>
                <div className="price productcartpanel-variant-mobile-data-item">
                  {(list?.formattedContractPrice !== null && list?.formattedContractPrice !== "$0.00") && <span className="list-price">
                    {list?.product?.price?.regularPrice?.amount?.formattedPrice}
                  </span>}
                  <span className="contract-price">
                    {list?.formattedContractPrice !== null && list?.formattedContractPrice !== "$0.00" ? list?.formattedContractPrice : list?.product?.price?.regularPrice?.amount?.formattedPrice}
                  </span>
                </div>
              </div>
              <div className="productcartpanel-variant-mobile-row-actions">
                <div className="productcartpanel__item-quantity productcartpanel-variant-mobile-row-title">
                  <span>{messages["cartProductList.quantity"]}</span>
                  <input
                    className="productcartpanel__item-quantity-value"
                    defaultValue={list?.quantity}
                    value={quantity[list?.id]}
                    onChange={(event) => onQuantityChange(event, list?.id)}
                  />
                </div>
                <div className="productcartpanel__item-actions">
                  <button
                    type="button"
                    className="productcartpanel__item-button"
                    onClick={(event) =>
                      updateQuantity(event, list?.id, quantity[list?.id])
                    }
                  >
                    <span className="productcartpanel__item-button-content">
                      <span>{messages["cartProductList.updateMob"]}</span>
                    </span>
                  </button>
                  <button
                    className="productcartpanel__item-button"
                    type="button"
                    onClick={() => removeProduct(list?.id)}
                  >
                    <span className="productcartpanel__item-button-content">
                      <img src="/content/dam/olympus/icon/Delete.svg" />
                      <span>{messages["cartProductList.remove"]}</span>
                    </span>
                  </button>
                </div>
              </div>
            </div>
            {list?.otherVariants?.map((variant) => (
              <div className="productcartpanel-variant-mobile-row">
                <span className="productcartpanel-variant-mobile-row-title">
                  {messages["cartProductList.itemNumber"]}
                </span>
                <span className="productcartpanel-variant-mobile-data-item">
                  {variant?.product?.sku}
                </span>
                <span className="productcartpanel-variant-mobile-row-title">
                  {messages["cartProductList.product"]}
                </span>
                <span className="productcartpanel-variant-mobile-data-item">
                  {variant?.product?.name}
                </span>
                <div className="productcartpanel-variant-mobile-row-actions-price">
                  <span className="productcartpanel-variant-mobile-row-title">
                    {messages["cartProductList.price"]}
                  </span>
                  <div className="price productcartpanel-variant-mobile-data-item">
                    {(variant?.formattedContractPrice !== null && variant?.formattedContractPrice !== "0.00") && <span className="list-price">
                      {
                        variant?.product?.price?.regularPrice?.amount?.formattedPrice
                      }
                    </span>}
                    <span className="contract-price">
                      {variant?.formattedContractPrice !== null && variant?.formattedContractPrice !== "0.00" ? variant?.formattedContractPrice : variant?.product?.price?.regularPrice?.amount?.formattedPrice}
                    </span>
                  </div>
                </div>
                <div className="productcartpanel-variant-mobile-row-actions">
                  <div className="productcartpanel__item-quantity productcartpanel-variant-mobile-row-title">
                    <span>{messages["cartProductList.quantity"]}</span>
                    <input
                      className="productcartpanel__item-quantity-value"
                      defaultValue={variant?.quantity}
                      value={quantity[variant?.id]}
                      onChange={(event) => onQuantityChange(event, variant?.id)}
                    />
                  </div>
                  <div className="productcartpanel__item-actions">
                    <button
                      type="button"
                      className="productcartpanel__item-button"
                      onClick={(event) =>
                        updateQuantity(
                          event,
                          variant?.id,
                          quantity[variant?.id]
                        )
                      }
                    >
                      <span className="productcartpanel__item-button-content">
                        <span>{messages["cartProductList.updateMob"]}</span>
                      </span>
                    </button>
                    <button
                      className="productcartpanel__item-button"
                      type="button"
                      onClick={() => removeProduct(variant?.id)}
                    >
                      <span className="productcartpanel__item-button-content">
                        <img src="/content/dam/olympus/icon/Delete.svg" />
                        <span>{messages["cartProductList.remove"]}</span>
                      </span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </>
  );
};

export default cartProductList;
