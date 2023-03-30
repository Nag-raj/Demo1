import axios from "axios";

export async function fetchCartData() {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: { "Content-Type": "application/json" },
    data: JSON.stringify({
      query: `{
        cart(
            orgUnitId: \"${localStorage.getItem('userAccountId')}\",
            userId: \"${localStorage.getItem('userEmailId')}\"
          ) {
          items {
            id
            formattedContractPrice
            product {
              name
              sku
              meta_description
              baseProduct
              baseProductName
              price {
                  regularPrice {
                      amount {
                          formattedPrice
                      }
                  }
              }
            }
            quantity
            otherVariants {
              id
              formattedContractPrice
              quantity
              product {
                  name
                  sku
                  meta_description
                  baseProduct
                  baseProductName
                  price {
                      regularPrice {
                          amount {
                              formattedPrice
                          }
                      }
                  }
              }
            }
          }
          prices {
            grand_total {
              formattedPrice
            }
            discounts {
                amount {
                    formattedPrice
                }
            }
            applied_taxes {
                amount {
                    formattedPrice
                }
            }
            subtotal_excluding_tax {
              formattedPrice
            }
          }
          total_quantity
          deliveryCost {
              formattedPrice
          }
          shipping_addresses {
              city
              formattedAddress
              company
          }
        }
      }`,
    }),
  });

  return response;
}

export async function updateCartItemsQuantityData(updateData) {
  console.log(updateData);
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: { "Content-Type": "application/json" },
    data: JSON.stringify({
      query: `mutation {
        updateCartItems(
          input: {
            cartId: \"${localStorage.getItem('cartId')}\"
            userId: \"${localStorage.getItem('userEmailId')}\"
            orgUnitId: \"${localStorage.getItem('userAccountId')}\"
            cart_items: [
              {
                cart_item_uid: \"${updateData.uid}\"
                quantity: ${updateData.quantity}
              }
            ]
          }
        ){
          cart {
              items {
              id
              formattedContractPrice
              product {
                  name
                  sku
                  meta_description
                  baseProduct
                  baseProductName
                  price {
                      regularPrice {
                          amount {
                              formattedPrice
                          }
                      }
                  }
              }
              quantity
              otherVariants {
                  id
                  formattedContractPrice
                  quantity
                  product {
                      name
                      sku
                      meta_description
                      baseProduct
                      baseProductName
                      price {
                          regularPrice {
                              amount {
                                  formattedPrice
                              }
                          }
                      }
                  }
              }
              }
              prices {
              grand_total {
                formattedPrice
              }
              discounts {
                  amount {
                    formattedPrice
                  }
              }
              applied_taxes {
                  amount {
                      formattedPrice
                  }
              }
              subtotal_excluding_tax {
                formattedPrice
              }
              }
              total_quantity
              deliveryCost {
                  formattedPrice
              }
              shipping_addresses {
                  city
                  formattedAddress
                  company
              }
          }
        }
      }`,
      variables: {},
    }),
  });
  return response;
}

export async function removeProduct(itemId) {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: { "Content-Type": "application/json" },
    data: JSON.stringify({
      query: `mutation {
        removeItemFromCart(
          input: {
            cartId: \"${localStorage.getItem('cartId')}\",
            cart_item_uid: \"${itemId}\",
            userId: \"${localStorage.getItem('userEmailId')}\",
            orgUnitId: \"${localStorage.getItem('userAccountId')}\"
          }
        )
      {
        cart {
          items {
            id
            formattedContractPrice
            product {
              name
              sku
              meta_description
              baseProduct
              baseProductName
              price {
                  regularPrice {
                      amount {
                          currency
                          formattedPrice
                          value
                      }
                  }
              }
            }
            quantity
            otherVariants {
              id
              formattedContractPrice
              quantity
              product {
                  name
                  sku
                  meta_description
                  baseProduct
                  baseProductName
                  price {
                      regularPrice {
                          amount {
                              currency
                              formattedPrice
                              value
                          }
                      }
                  }
              }
            }
          }
          prices {
            grand_total {
              formattedPrice
            }
            discounts {
                amount {
                    formattedPrice
                }
            }
            applied_taxes {
                amount {
                    formattedPrice
                }
            }
            subtotal_excluding_tax {
              formattedPrice
            }
          }
          total_quantity
          deliveryCost {
              formattedPrice
          }
          shipping_addresses {
              city
              formattedAddress
              company
          }
        }
      }
      }`,
      variables: {},
    }),
  });
  return response;
}