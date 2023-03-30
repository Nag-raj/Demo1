const filters = {
    init: function () {
        let $scope = $(".productcollection__root");
        let url = new URL(window.location.href);
        let lowerCategoryTitle = $scope.find(".category-list").attr("data-filtertype");
        let showFilter = false;
        $scope.find(".filter_icon").on("click", function () {
            $scope.find(".filter_icon").hide();
            $scope.find(".productcollection__filters").show();
            $scope.find(".productcollection__listing").hide();
        })
        $scope.find(".filter-back-text").on("click", function () {
            $scope.find(".filter_icon").show();
            $scope.find(".productcollection__filters").hide();
            $scope.find(".productcollection__listing").show();
        })

        checkSelectedFilter = function () {
            $scope.find(".productcollection__filter-item-oly").each((ind, ele) => {
                let dataFilterType = $(ele).parents(".productcollection__filter-items-oly").siblings(".productcollection__filter-header-oly").attr("data-filtertype")
                if ($(ele).attr("data-filterselected") === "1") {
                    showFilter = true;
                    $scope.find(".filter-selection").append(`<div class="selected-filter">
            <img src="/content/dam/olympus/icon/Close_Inverse.svg" data-filtertype=${dataFilterType} data-paramtype=${$(ele).attr("data-filterparams")}>
            <span class="selected-filter-text">${$(ele).find(".filter-text").text()}</span>
        </div>`)
                    $(ele).find("input")[0].checked = true;
                }

            })
        }
        checkSelectedFilter();
        if (showFilter) {
            $scope.find(".filter-selection").show();
        } else {
            $scope.find(".filter-selection").hide();
        }

        showFilterSelected = function () {
            let filterTypeCollapse = $scope.find(".productcollection__filter-items-oly");
            filterTypeCollapse.each(function (ind, ele) {
                let checkboxes = $(ele).find('input[type="checkbox"]');
                let atLeastOneChecked = false;
                for (var i = 0; i < checkboxes.length; i++) {
                    if (checkboxes[i].checked) {
                        atLeastOneChecked = true;
                        break;
                    }
                }
                if (atLeastOneChecked) {
                    $(ele).toggle()
                    $(ele).siblings().find(".productcollection__filter-title-oly").toggleClass("remove_togglestyle");
                    $(ele).siblings().find(".productcollection__filter-icon").toggleClass("transform_270");
                }
            })
            // let checkboxes = $scope.find("productcollection__filter-items-oly").find('input[type="checkbox"]');
            // Check if at least one checkbox is checked            

            // If at least one checkbox is checked, keep the dropdown open            
        };
        showFilterSelected();

        $scope.find(".productcollection__filter-header-oly").on("click", function () {
            $(this).find(".productcollection__filter-icon").toggleClass("transform_270");
            $(this).parents(".productcollection__filter-oly").find(".productcollection__filter-items-oly").toggle();
            $(this).find(".productcollection__filter-title-oly").toggleClass("remove_togglestyle");
        })

        unCheck = function ($filterType, $filterparams) {
            if ((url.search.includes(`${lowerCategoryTitle}=`) && !url.search.includes("search_query")) || (!url.search.includes(`${lowerCategoryTitle}=`) && url.search.includes("search_query"))) {
                let paramArray = url.search.split("?")[1].split("&");
                if (paramArray.length > 1) {
                    paramArray.splice(1, 1);
                    url.search = paramArray.join("&");
                }
                url.search = $filterparams.split("%3Arelevance")[1] === "" ? url.search.concat("") : url.search.concat(`&${$filterType}=${$filterparams}`);
            }
            // else if (url.search.includes(lowerCategoryTitle) && url.search.includes("search_query")) {
            //     let paramArray = url.search.split("?")[1].split("&");
            //     if (paramArray.length > 2) {
            //         paramArray.splice(2, 1);
            //         url.search = paramArray.join("&");
            //     }
            //     url.search = $filterparams.split("%3Arelevance")[1] === "" ? url.search.concat("") : url.search.concat(`&${$filterType}=${$filterparams}`);
            // }
            else {
                url.search = ""
                url.search = $filterparams.split("%3Arelevance")[1] === "" ? "" : url.search.concat(`${$filterType}=${$filterparams}`);
            }
            window.history.replaceState(null, null, url.href);
        }
        $scope.find(".selected-filter img").on("click", function (e) {
            let $filterType = encodeURIComponent($(this).attr("data-filtertype"));
            let $filterparams = encodeURIComponent($(this).attr("data-paramtype"));
            unCheck($filterType, $filterparams);
            $(this).parent().remove();
            $scope.hide();
            $scope.parent().find(".cmp-loader-v1").removeClass("hide");
            window.location.reload();
        })
        $scope.find(".clear-all").on("click", function () {
            let paramArray = url.search.split("?")[1].split("&");
            if (paramArray.length > 1) {
                paramArray.splice(1, paramArray.length);
                url.search = paramArray.join("&");
            } else {
                url.search = "";
            }
            window.history.replaceState(null, null, url.href)
            $scope.hide();
            $scope.parent().find(".cmp-loader-v1").removeClass("hide");
            window.location.reload();
        })
        $scope.find(".productcollection__filter-item-oly input[type=checkbox]").on("change", function (event) {
            let $filterType = $(this).parents(".productcollection__filter-items-oly").siblings(".productcollection__filter-header-oly").attr("data-filtertype");
            let $filterparams = encodeURIComponent($(this).parent().attr("data-filterparams"));
            if ($(this).is(':checked')) {
                if (url.search.length) {
                    if ((url.search.includes(`${lowerCategoryTitle}=`) && !url.search.includes("search_query")) || (!url.search.includes(`${lowerCategoryTitle}=`) && url.search.includes("search_query"))) {
                        let paramArray = url.search.split("?")[1].split("&");
                        if (paramArray.length > 1) {
                            paramArray.splice(1, 1);
                            url.search = paramArray.join("&");
                        }
                        url.search = url.search.concat(`&${$filterType}=${$filterparams}`);
                    } else if (url.search.includes(`${lowerCategoryTitle}=`) && url.search.includes("search_query")) {
                        let paramArray = url.search.split("?")[1].split("&");
                        if (paramArray.length >= 2) {
                            paramArray.splice(1, 1);
                            url.search = paramArray.join("&");
                        }
                        url.search = url.search.concat(`&${$filterType}=${$filterparams}`);
                    }
                    else {
                        url.search = ""
                        url.search = url.search.concat(`${$filterType}=${$filterparams}`);
                    }
                } else {
                    url.search = url.search.concat(`${$filterType}=${$filterparams}`);
                }
                window.history.replaceState(null, null, url.href);
            } else {
                unCheck($filterType, $filterparams);
            }
            $scope.hide();
            $scope.parent().find(".cmp-loader-v1").removeClass("hide");
            window.location.reload();

        })
    }
}

$(document).ready(function () {
    filters.init();
});
