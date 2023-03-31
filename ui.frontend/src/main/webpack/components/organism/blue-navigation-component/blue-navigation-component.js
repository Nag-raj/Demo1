import { getCartId, getMiniCartCount, getSwitchToAccountData } from "../../../global-services/headerApi";


window.openPanel = function() {
  document.getElementById("notif").style.display = "block",
  document.getElementById("overlay").style.display = "block"
};

(function (window, document, undefined) {
  $(document).ready(function () {    
    var button = document.getElementById("hoverme");
    var closePanel = document.getElementById("closePanel");
    var slideout = document.getElementById("notif");
    var overlayShow = document.getElementById("overlay");
    var startShopping = document.getElementById("startShopping");
    button?.addEventListener("click", window.openPanel);
    closePanel?.addEventListener("click", (event) => {
      document.getElementById("overlay").style.display = "none";
      document.getElementById("notif").style.display = "none";
    });
    const shoppingBtn = document.getElementById("shoppingBtn");
    
    const customerName = $("#customer-name");
    getSwitchToAccountData().then(async(data) => {
      const customername = await data;
      if(data !== null) {
        const customernameEl = `<span id="customer-name">`+`${customername?.data?.customer?.firstname}`+`</span>`
        $(customerName).html(customernameEl);
        window.switchAccount = data;
        const responseData = data?.data?.customer?.units;
        // check for the defaultUnit value and set localstorage
        if(responseData) {
          let isDefaultValueSet = responseData?.every((val) => val.defaultUnit === false);
          console.log('isdefaultvalue', isDefaultValueSet);
          if(localStorage.getItem("userAccountId")) {
            const existingUserId = localStorage.getItem("userAccountId");
            const filteredValue = responseData?.filter((val) => val.uid === existingUserId);
            // localStorage.setItem("userAccountId", filteredValue[0]?.uid);
            window.userState = true;
            // window.dispatchEvent(new Event("userAdded"));
          } else {
            if (isDefaultValueSet) {
              const defaultAccountValue = responseData[0]?.uid.toString().trim();
              localStorage.setItem("userAccountId", (defaultAccountValue));
              window.userState = true;
              // window.dispatchEvent(new Event("userAdded"));
          } else {
              let defaultUnitValue = responseData.filter((val) => val.defaultUnit === true);
              const defaultAccountValue = defaultUnitValue[0]?.uid.toString().trim();
              localStorage.setItem("userAccountId", (defaultAccountValue));
              window.userState = true;
              // window.dispatchEvent(new Event("userAdded"));
          }
          }

          getCartId().then(async(data) => {
            console.log("CartID", data);
            if (data?.data?.customerCart?.code !== null) {
              localStorage.setItem("cartId", data?.data?.customerCart?.code);
              window.dispatchEvent(new Event("userAdded"));
              getMiniCartCount().then(async(data) => {
                console.log("minicart-count", data);
                if (data?.data?.customerCart?.displayTotalUnitCount !== "0" && data?.data?.customerCart?.displayTotalUnitCount !== null) {
                  const minicartAnchor = document.querySelector('#hoverme');
                  minicartAnchor.removeEventListener("click", window.openPanel);
                  minicartAnchor.href = minicartAnchor.getAttribute('data-link');
                  minicartAnchor.querySelector('#minicart-value').style.display = 'block';
                  $(minicartAnchor.querySelector('#minicart-value')).text(data?.data?.customerCart?.displayTotalUnitCount);
                }
              });
            }
          }
          );
        }
      }
    });
  });
})(window, document, undefined);