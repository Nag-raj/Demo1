(function (window, document, undefined) {
  window.onload = init;

  function init() {
    if (
      document.querySelectorAll("main.container")[0].clientHeight < window.innerHeight && !document.querySelectorAll(".cmp-loader-container").length 
    ) {
      document.querySelectorAll("footer.experiencefragment")[0].style.position = "fixed";
      document.querySelectorAll("footer.experiencefragment")[0].style.bottom = "0";
    } 
  }
})(window, document, undefined);
