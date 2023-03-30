let selectedAccout,
    setAccountCookie,
    getshowAccountPopupValue,
    intialAccountValue,
    firstValue,
    confirmAccountValue;

async function getAccounts(id) {
    const data = window.switchAccount?.data?.customer?.units;
    let accountsOptions = '';
    if(data !== null) {
        for (var i = 0; i < data?.length; i++) {
            const accountOptionsValue = data[i]; 
            accountsOptions +='<a href="#" onclick="onChangeAccount(event)">#' + accountOptionsValue?.uid + ' - ' + accountOptionsValue?.nickName +'</a>';
        }

        if (id === 'myDropdown') {
            // iterate over all the units array to find the defaultUnit value
            if(data) {
            let isDefaultValueSet = data.every((val) => val.defaultUnit === false);

            if(localStorage.getItem("userAccountId")) {
                const existingUserId = localStorage.getItem("userAccountId");
                const filteredValue = data?.filter((val) => val?.uid === existingUserId);
                // localStorage.setItem("userAccountId", filteredValue[0]?.uid);
                firstValue = '#' + filteredValue[0]?.uid + ' - ' + filteredValue[0]?.nickName;
            } else {
                if(isDefaultValueSet) {
                    firstValue = '#' + data[0]?.uid + ' - ' + data[0]?.nickName;
                } else {
                    let defaultUnitValue = data.filter((val) => val.defaultUnit === true);
                    firstValue = '#' + defaultUnitValue[0]?.uid + ' - ' + defaultUnitValue[0]?.nickName;
                }
            }
        }
        }
    document.getElementById(id).innerHTML = accountsOptions;
    }    
}

getshowAccountPopupCookie = function (name) {
    var r = document.cookie.match('\\b' + name + '=([^;]*)\\b');

    return r ? r[1] : null;
};

onChangeAccount = function (event) {
    getshowAccountPopupValue = getshowAccountPopupCookie('showAccountPopup');
    buttonId = event.currentTarget.offsetParent.id;

    if (
        getshowAccountPopupValue == 'false' ||
        getshowAccountPopupValue == null ||
        getshowAccountPopupValue == undefined
    ) {
        if (buttonId === 'myDropdown') {
            $('#toggleMyForm').text(event.currentTarget.innerText);
            selectedAccout = event.currentTarget.innerText;

            $('#showAccouts').modal({
                backdrop: 'static',
                keyboard: false,
            });
            $('#showAccouts').modal('show');
            var myButton =
                document.getElementById('myHeader')?.clientHeight +
                document.getElementsByClassName('modal-body')[0]?.clientHeight;

            document.getElementById('myButton').style.top = myButton + 'px';

            getAccounts('myDropdownModal').then(() =>
                $('#toggleMyFormModal').text(selectedAccout),
            );
        } else {
            if (buttonId === 'myDropdownModal') {
                $('#toggleMyFormModal').text(event.currentTarget.innerText);
            }
        }
        confirmAccountValue = event.currentTarget.innerText;
    } else {
        $('#toggleMyForm').text(event.currentTarget.innerText);
        localStorage.setItem("userAccountId", (((((event.currentTarget.innerText).split("-"))[0]).trim()).split('#'))[1]);
        window.userState = true;
        if (window.location.href.includes('checkout')) {
            fetch(JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint, {
                method: 'POST',
                body: JSON.stringify({
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
                  }`
                }),
                headers: {
                    'content-type': 'application/json'
                }
              }).then(async(data) => {
                const response = await data.json();
                if (response?.data?.cart) {
                    window.dispatchEvent(new Event("userAdded"));
                }
            });
        } else {
            window.dispatchEvent(new Event("userAdded"));
        }
    }
};

confirmPopupAccounts = function () {
    setAccountCookie = $('#showAccoutsPopup').is(':checked');

    document.cookie = 'showAccountPopup =' + setAccountCookie + ';secure;';
    const acctionSelection = (typeof confirmAccountValue === 'string' ? confirmAccountValue : confirmAccountValue.toString()).split('-'); 
    const confirmedAccountSelection = acctionSelection[0].substr(1);
    localStorage.setItem("userAccountId", (confirmedAccountSelection).trim());
    window.userState = true;
    if (window.location.href.includes('checkout')) {
        fetch(JSON.parse(document.querySelector(`[name='store-config']`)?.getAttribute('content'))?.graphqlEndpoint, {
            method: 'POST',
            body: JSON.stringify({
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
              }`
            }),
            headers: {
                'content-type': 'application/json'
            }
          }).then(async(data) => {
            const response = await data.json();
            console.log("cart", response);
            if (response?.data?.cart) {
                window.dispatchEvent(new Event("userAdded"));
            }
        });
    } else {
        window.dispatchEvent(new Event("userAdded"));
    }
    $('#showAccouts').modal('hide');
    $('#toggleMyForm').text(confirmAccountValue);
    firstValue = confirmAccountValue;
};

closePopupAccounts = function () {
    $('#toggleMyForm').text(firstValue);
    if (document.getElementById('myDropdownModal').classList.contains('show')) {
        document.getElementById('myDropdownModal').classList.toggle('show');
    }
};

mutationObserverCallback = function (mutations) {
    for (let mutation of mutations) {
      if(mutation.type === 'childList') {
        getAccounts('myDropdown').then(() => {
            $('#toggleMyForm').text(firstValue);
        });
      }
    }
};

$(document).ready(function () {
    let target = document.querySelector("#customer-name");
    if(target) {
        let myObserver = new MutationObserver(mutationObserverCallback);
        myObserver.observe(target, {childList: true, characterData: true, characterDataOldValue : true, subtree: true});
    }
    let btn = document.getElementById('toggleMyDropdown');
    if (btn) {
        btn?.addEventListener('click', (event) => {
            document.getElementById('myDropdown').classList.toggle('show');
            intialAccountValue = $('#toggleMyForm').text();
        });
        getshowAccountPopupValue = getshowAccountPopupCookie('showAccountPopup');
    }

    let popupbtn = document.getElementById('toggleMyDropdownModal');
    popupbtn?.addEventListener('click', (event) => {
        document.getElementById('myDropdownModal').classList.toggle('show');
        if ($(window).width() <= 765) {
            if (document.getElementById('myDropdownModal').classList.contains('show')) {
                var myButton =
                    document.getElementById('myHeader')?.clientHeight +
                    document.getElementsByClassName('modal-body')[0]?.clientHeight +
                    124;
                document.getElementById('myButton').style.top = myButton + 'px';
            }
            else {
                var myButton =
                    document.getElementById('myHeader')?.clientHeight +
                    document.getElementsByClassName('modal-body')[0]?.clientHeight;
                document.getElementById('myButton').style.top = myButton + 'px';

            }
        }
    });
});