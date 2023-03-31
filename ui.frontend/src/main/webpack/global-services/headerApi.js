import axios from "axios";

export async function getCartId() {
    const response = await axios({
        method: 'post',
        url: JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint,
        headers: {
          'Content-Type': 'application/json'
        },
        data : JSON.stringify({
            query: `{
                customerCart(
                    orgUnitId: \"${localStorage.getItem('userAccountId')}\",
                    userId: \"${localStorage.getItem('userEmailId')}\"
                  ){
                  code,
                  type
                }
              }`,
            variables: {}
          })
      });
      
    return response.data;
}

export async function getMiniCartCount() {
  const response = await axios({
      method: 'post',
      url: JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint,
      headers: {
        'Content-Type': 'application/json'
      },
      data : JSON.stringify({
          query: `{
                customerCart(userId: \"${localStorage.getItem('userEmailId')}\",cartCount: true)
                {
                  code
                  displayTotalUnitCount
                }
              }`,
          variables: {}
        })
    });
    
  return response.data;
}

export async function getSwitchToAccountData() {
  const response = await axios({
      method: 'POST',
      url: JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint,
      headers: {
        'Content-Type': 'application/json',
      },
      data : JSON.stringify({
          query: `{
            customer(orgUnit: true, customerId: \"${localStorage.getItem('userEmailId')}\") {
              firstname
              units {
                active
                defaultUnit
                name
                nickName
                uid
              }
            }
          }
          `,
          variables: {}
        })
    });
  return response.data;
}
