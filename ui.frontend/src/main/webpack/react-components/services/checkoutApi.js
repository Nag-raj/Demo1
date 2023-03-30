import axios from "axios";

export async function getPo() {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `mutation{ purchaseOrder(orgUnitId:\"${localStorage.getItem(
        "userAccountId"
      )}\",
          cartId:\"${localStorage.getItem("cartId")}\",
          userId:\"${localStorage.getItem("userEmailId")}\")
          {
            purchaseOrderNumber
          }
        }`,
      variables: {},
    }),
  });

  return response.data;
}

export async function getShippingAddresses() {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `{getAddresses(userId: \"${localStorage.getItem(
        "userEmailId"
      )}\", orgUnitId: \"${localStorage.getItem("userAccountId")}\"){
            shippingaddresses{
                country{
                    isocode
                    name
                }
                id
                defaultAddress
                formattedAddress
                line1
                postalCode
                shippingAddress
                town
                visibleInAddressBook
            }
        }
    }`,
      variables: {},
    }),
  });

  return response.data;
}

export async function setSelectedAddress(data) {
  console.log("in api " + data);
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `mutation{
          setShippingAddressesOnCart(input: {
              cart_id: \"${localStorage.getItem("cartId")}\",
              addressId:\"${data.id}\",
              userId: \"${localStorage.getItem("userEmailId")}\"
          }){
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
      variables: {},
    }),
  });
  return response.data;
}

export async function fetchCheckoutData() {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: { "Content-Type": "application/json" },
    data: JSON.stringify({
      query: `{
        checkoutSummary(userId: \"${localStorage.getItem(
          "userEmailId"
        )}\", orgUnitId: \"${localStorage.getItem("userAccountId")}\"){
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
      variables: {},
    }),
  });
  return response;
}
export async function getDeliveryListAPI(data) {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `{
        shippingCarrierListForThirdParty(shippingCarrierCode: \"${data}\"){
            deliveryOption{
                code
                name
            }
        }
    }
    `,
      variables: {},
    }),
  });
  return response.data;
}
export async function getShippingListAPI() {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `{
        shippingCarrierListForThirdParty(
          shippingCarrierCode: ""
          cartId: \"${localStorage.getItem("cartId")}\"
          userId: \"${localStorage.getItem("userEmailId")}\"
      ){
        shippingCarriers {
          code
          name
          accountReferenceNumber
      }
    }
  }
    `,
      variables: {},
    }),
  });
  return response.data;
}

export async function getPayInvoice() {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `{
        cart(
            orgUnitId: \"${localStorage.getItem("userAccountId")}\"
            userId: \"${localStorage.getItem("userEmailId")}\"
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
          available_payment_methods {
              code
              title
              isActive
          }
        }
      }
    `,
      variables: {},
    }),
  });
  return response.data;
}
export async function payByInvoiceSelect(payment) {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `mutation {
        setPaymentMethodOnCart(
          input: {
            cartId: \"${localStorage.getItem("cartId")}\"
            userId: \"${localStorage.getItem("userEmailId")}\"
            payment_method: { code: ${payment} }
          }
        ) {
          cart {
            selected_payment_method {
              code
              title
            }
          }
        }
      }
      
    `,
      variables: {},
    }),
  });
  return response.data;
}

export async function placeOrder(orderPlace) {
  const response = await axios({
    method: "post",
    url: JSON.parse(
      document.querySelector(`[name='store-config']`)?.getAttribute("content")
    )?.graphqlEndpoint,
    headers: {
      "Content-Type": "application/json",
    },
    data: JSON.stringify({
      query: `mutation {
        placeOrder(
          input: {
            cartId: \"${localStorage.getItem("cartId")}\"
            userId:\"${localStorage.getItem("userEmailId")}\"
            additionalAddressLine: ${orderPlace.additionalAddressLine}
            deliveryOptionCode: ${orderPlace.deliveryOptionCode}
            poNumber: ${orderPlace.poNumber}
            shippingCarrierCode: ${orderPlace.shippingCarrierCode}
          }
        ) {
          order {
            order_number
          }
        }
      }
    `,
      variables: {},
    }),
  });
  return response.data;
}
