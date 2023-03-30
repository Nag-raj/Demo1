import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import OrderRow from "./orderRow";
import { getOrders, fetchOrders } from "../store/reducers/orderSlice";
import Loader from "../Loader";
import "./orderHistory.scss";
import PaginationComponent from "../PaginationComponent/paginationComponent";
import { setUser, resetUser, getUser } from "../store/reducers/userSlice";

const OrderHistoryPage = ({ locale, messages }) => {
  const { orders, orderLoading, orderError } = useSelector(getOrders);
  const [searchTerm, setSearchTerm] = useState("");
  const [email, setEmail] = useState("");
  const [account, setAccount] = useState("");
  const [disabled, setButtonDisabled] = useState(true);
  const [showPagination, setShowPagination] = useState(false);
  const { userData } = useSelector(getUser);
  const dispatch = useDispatch();

  useEffect(() => {
    const changeUserState = () => {
      if (window.userState) {
        dispatch(setUser());
        window.useState = false;
      }
    }
    window.addEventListener("userAdded", changeUserState);
    return () => {
      window.removeEventListener("userAdded", changeUserState);
    }
  }, []);

  useEffect(() => {
    if (userData) {
      const data = {
        currentPage: 0,
        pageSize: 10,
        search: "",
      };
      dispatch(fetchOrders(data));
      setShowPagination(true);
      dispatch(resetUser());
    }
  }, [userData, dispatch]);

  const setSearchTermValue = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleOnKeyUp = (event) => {
    if (
      event.target.value.trim() === "" ||
      event.target.value.trim().length < 1
    ) {
      setButtonDisabled(true);
    } else {
      setButtonDisabled(false);
    }
  };

  const handleSubmit = (event) => {
    event?.preventDefault();
    if (searchTerm.trim() === "" || searchTerm.trim().length < 1) {
      return;
    } else {
      const data = {
        email,
        account,
        currentPage: 0,
        pageSize: 10,
        search: searchTerm,
      };
      dispatch(fetchOrders(data));
    }
  };
  return (
    <div
      className={orderLoading ? "loading-height" : ""
      }
    >
      <h2 className="orderhistory__title">
        {messages["orderHistoryPage.pageTitleText"]}
      </h2>
      <div className="orderhistory__search">
        <form onSubmit={handleSubmit}>
          <input
            className="orderhistory__search-input"
            placeholder={messages["orderHistoryPage.search"]}
            value={searchTerm}
            onChange={setSearchTermValue}
            onKeyUp={handleOnKeyUp}
          />
          <button
            className="orderhistory__search-button"
            type="submit"
            disabled={disabled}
          >
            {messages["orderHistoryPage.serachButton"]}
          </button>
        </form>
      </div>
      <div className="orderhistory__filters">
        <div className="orderhistory__filters-filter"></div>
        <div className="orderhistory__filters-sort"></div>
      </div>
      <div className="orderhistory__data">
        {orderLoading ? (
          <Loader />
        ) : orders?.orders ? (
          <div className="orderhistory__items">
            {orders?.orders?.map((order) => (
              <OrderRow order={order} messages={messages} />
            ))}
          </div>
        ) : (
          <div className="orderhistory__no-results">
            <h2>{messages["orderHistoryPage.emptyDataMessageOne"]}</h2>
            <p>{messages["orderHistoryPage.emptyDataMessageTwo"]}</p>
            <p>{messages["orderHistoryPage.emptyDataMessageThree"]}</p>
          </div>
        )}
        {showPagination
          ? orders?.orders &&
            orders?.pagination?.totalPages > 1 && (
              <div className="orderhistory__pagination">
                <PaginationComponent
                  email={email}
                  account={account}
                  data={orders}
                  searchTerm={searchTerm}
                ></PaginationComponent>
              </div>
            )
          : ""}
      </div>
    </div>
  );
};

export default OrderHistoryPage;
