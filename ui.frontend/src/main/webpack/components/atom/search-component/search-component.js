getSearchResult = function (event) {
  if (
    event.target.value.trim() === "" ||
    event.target.value.trim().length < 3
  ) {
    document.getElementById("searchBtn").disabled = true;
  } else {
    document.getElementById("searchBtn").disabled = false;
  }
};
