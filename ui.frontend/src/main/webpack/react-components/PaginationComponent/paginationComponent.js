import React, { useEffect, useState } from "react";
import Pagination from "@material-ui/lab/Pagination";
import { useDispatch, useSelector } from "react-redux";
import "./paginationComponent.scss";
import usePagination from "../hooks/usePagination";
import { fetchOrders } from "../store/reducers/orderSlice";

const PaginationComponent = ({ data, email, account, searchTerm }) => {
  const [page, setPage] = useState(1);
  const PER_PAGE = 10;
  const dispatch = useDispatch();
  const count = data?.pagination?.totalPages;
  const paginatedData = usePagination(data?.orders, PER_PAGE);

  const handleChange = (e, pageNumber) => {
    setPage(pageNumber);
    paginatedData.jump(pageNumber);
    const data = {
      email: email,
      account: account,
      currentPage: pageNumber - 1,
      pageSize: 10,
      search: searchTerm,
    };
    dispatch(fetchOrders(data));
    window.scrollTo({
      top: 0,
      behavior: "smooth"
    })
  };
  return (
    <Pagination
      className="productcollection__pagination"
      page={data?.pagination?.currentPage + 1}
      count={count}
      color="primary"
      shape="rounded"
      onChange={handleChange}
    />
  );
};

export default PaginationComponent;
