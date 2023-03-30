const header = {
  init: function (val) {
    let $scope = $(val);
    if ($(window).width() <= 765) {
      $scope.find("#hamburger-menu").click(function () {
        $scope.find("#hamburger-menu").hide();
        $scope.find("#close-menu").show();
        $scope.find(".navigation").css("display","block");
        $scope.find(".text").css("display","block");
        $scope.css("height","100vh");
        $("main").hide();
      });
      $scope.find("#close-menu").click(function () {
        $scope.find("#hamburger-menu").show();
        $scope.find("#close-menu").hide();
        $scope.find(".navigation").css("display","none");
        $scope.find(".text").css("display","none");
        $scope.css("height","auto");
        $("main").show()
      })
    }
  }
};

(function () {
  if ($("header.experiencefragment").length) {
    $("header.experiencefragment").each((ind, val) => {
      header.init(val);
    })
  }
})();