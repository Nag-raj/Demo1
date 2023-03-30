import axios from "axios";

export async function fetchMiniCartValue(params) {
    const response = await axios({
        method: 'post',
        url: JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint,
        headers: {
          'Content-Type': 'application/json'
        },
        data : JSON.stringify({
            query: `{
                  customerCart(userId: \"${params.email}\",cartCount: true)
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
