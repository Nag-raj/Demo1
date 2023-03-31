import axios from "axios";

export async function fetchOrdersData(params) {
    const response = await axios({
        method: 'post',
        url: JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint,
        headers: {
          'Content-Type': 'application/json'
        },
        data : JSON.stringify({
            query: `{
              customer(search:\"${params.search ? params.search : ""}\",customerId:\"${localStorage.getItem('userEmailId')}\",accountNumber:\"${localStorage.getItem('userAccountId')}\",currentPage:${params.currentPage},pageSize:${params.pageSize}){
                  orders
                  {
                      items{
                          id
                          }
                      }
                  }
              }`,
            variables: {}
          })
      });
      
    return response.data;   
}

export async function reorder(reorderData) {
    const response = await axios({
      method: 'post',
      url: JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint,
      headers: {
        'Content-Type': 'application/json'
      },
      data : JSON.stringify({
          query: `mutation {
            addProductsToCart(cartId: \"${reorderData.cartId}\"
              userId: \"${localStorage.getItem('userEmailId')}\"
              orgUnitId: \"${localStorage.getItem('userAccountId')}\"
              cartItems: [{ quantity: ${reorderData.product.quantity}, sku: \"${reorderData.product.sku}\" }]
            ) {
              cart {
                items {
                  id
                  product {
                    name
                    sku
                  }
                  quantity
                }
              }
            }
          }`,
          variables: {}
        })
    });
    
  return response;
}