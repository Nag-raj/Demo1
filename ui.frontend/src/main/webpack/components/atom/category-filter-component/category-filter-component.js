const category = {
    init: function () {
        let $scope = $(".productcollection__root");
        $scope.find(".category-list input[type=radio]").each((index, val) => {
            if ($(val).parent().attr("data-selected") === "1") {
                $(val)[0].checked = true;
            }
        });
        this.filterOnCategory($scope);
    },
    filterOnCategory: function ($this) {
        $this.find(".category-list input[type=radio]").on("click", function (e) {
            let $filterType = $(this).parent().attr("data-filtertype");
            let $filterparams = encodeURIComponent($(this).attr("data-filterparams"));
            let url = new URL(window.location.href);
            // When search query is already present in url
            if (url.search.length && !url.search.includes("search_query")) {
                url.search = "";
                url.search = url.search.concat(`${$filterType}=${$filterparams}`);
            } else if (url.search.length && url.search.includes("search_query")) {
                let paramArray = url.search.split("?")[1].split("&");
                if (paramArray.length > 1) {
                    paramArray.splice(1, 1);
                    url.search = paramArray.join("&");
                }
                url.search = url.search.concat(`&${$filterType}=${$filterparams}`);
            } else {
                url.search = url.search.concat(`${$filterType}=${$filterparams}`);
            }
            window.history.replaceState(null, null, url.href);
            $this.hide();
            $this.parent().find(".cmp-loader-v1").removeClass("hide");
            window.location.reload();
        })
    }
}

$(document).ready(function () {
    category.init();
});