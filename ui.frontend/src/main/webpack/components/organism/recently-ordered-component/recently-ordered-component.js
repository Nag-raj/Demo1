
const recentlyOrderedList = {
    orderList: {
        "subcategories": [
            {
                "id": "Cables and Power Cords",
                "code": "NM-610L-0421",
                "url": "/categories/Cables and Power Cords/products",
                "subcategories": [
                    {
                        "id": "Connector Cables",
                        "url": "/categories/Connector Cables/products"
                    },
                    {
                        "id": "Communication Cable",
                        "url": "/categories/Communication Cable/products"
                    },
                    {
                        "id": "Power Cords",
                        "url": "/categories/Power Cords/products"
                    }
                ]
            },
            {
                "id": "Adapters & Expansion Kits for Adapters",
                "code": "NM-610L-0421",
                "url": "/categories/Adapters & Expansion Kits for Adapters/products"
            },
            {
                "id": "Carts & Workstations Accessories",
                "code": "NM-610L-0421",
                "url": "/categories/Carts & Workstations Accessories/products"
            },
            {
                "id": "Footswitchs & Foot holders",
                "code": "NM-610L-0421",
                "url": "/categories/Footswitchs & Foot holders/products"
            },
            {
                "id": "Footswitches & Foot holders",
                "code": "NM-610L-0421",
                "url": "/categories/Footswitches & Foot holders/products"
            },
            {
                "id": "Other Ancillary Devices",
                "code": "NM-610L-0421",
                "url": "/categories/Other Ancillary Devices/products"
            },
            {
                "id": "Transducers",
                "code": "Transducers",
                "url": "/categories/Transducers/products"
            }
        ]
    },
    init: function (val) {
        $curScope = $(val);

        for(let i = 0; i < this.orderList.subcategories.length; i++) {
            $curScope.append(`<li class="recently-ordered-list">
            <div class="product-name">${this.orderList.subcategories[i].id}</div>
            <div class="product-code">${this.orderList.subcategories[i].code}</div>
            <a href="${this.orderList.subcategories[i].url}">Reorder</a>
        </li>`)
        }
    }
}
$(document).ready(function () {
    if ($(".recently-ordered").length) {
        $(".recently-ordered").each((ind, val) => {
            recentlyOrderedList.init(val);
        })
    }
});