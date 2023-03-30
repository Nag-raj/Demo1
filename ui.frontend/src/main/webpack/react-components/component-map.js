import CartMainPage from "./cartPage/cartMainPage";
import CheckoutComponent from "./CheckoutComponent";
import OrderConfirmationPage from "./OrderConfirmationPage";
import OrderHistoryPage from "./OrderHistoryPage";

const componentMap = {
  "order-history": OrderHistoryPage,
  "cart-page": CartMainPage,
  "checkout-page": CheckoutComponent,
  "order-confirmation-page": OrderConfirmationPage
};

export default componentMap;
