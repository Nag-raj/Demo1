import axios from "axios";

export async function orderPlaceAPI(orderId) {
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
            customer(customerId: \"${localStorage.getItem("userEmailId")}\", orderId: ${orderId}) {
              orders {
                items {
                  id
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
