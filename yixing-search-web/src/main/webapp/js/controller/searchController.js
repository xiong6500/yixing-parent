app.controller('searchController', function ($scope,$location,searchService) {

    $scope.searchMap = {
        keywords: "",
        category: "",
        brand: "",
        spec: {},
        price: "",
        pageNo: 1,
        pageSize: 20,
        sortValue: "",
        sortField: ""
    }

    //重定向到本页面,获取url的参数
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    }

    //判断关键字是否是品牌
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i])) {
                return true;
            }
        }
        return false;
    }


    //设置排序规则
    $scope.sortSearch = function (sortFiled, sort) {
        $scope.searchMap.sortValue = sort;
        $scope.searchMap.sortFiled = sortFiled;
        $scope.searchMap.pageNo=1;
        $scope.search2();
    }

    //判断是否是第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        }
        return false;
    }

    //判断是否是最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search2();
    }

    //构建分页标签
    /**
     * 页面一共展示5页,前后有...
     */
    $scope.buildPageLabel = function () {
        //分页标签
        $scope.pageLabel = [];
        var totalPages = $scope.resultMap.totalPages;
        var firstPage = 1;//开始页码
        var endPage = totalPages;
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if (totalPages > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                endPage = 5;
                $scope.firstDot = false;//前面没点
            } else {
                if (totalPages - $scope.searchMap.pageNo <= 2) {
                    firstPage = endPage - 4;//后5页
                    $scope.lastDot = false;//后面没点
                } else {
                    firstPage = $scope.searchMap.pageNo - 2;
                    endPage = $scope.searchMap.pageNo + 2;
                }
            }
        } else {
            $scope.firstDot = false;//前面没点
            $scope.lastDot = false;//后面没点
        }

        for (var i = firstPage; i <= endPage; i++) {
            $scope.pageLabel.push(i);
        }
    }

    //移除搜索项
    $scope.removeSearchItem = function (key) {
        $scope.searchMap.pageNo=1;
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
        //执行搜索
        $scope.search2();
    }

    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        $scope.searchMap.pageNo=1;
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //执行搜索
        $scope.search2();
    }

    //搜索2
    $scope.search2 = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);//执行查询前,转换为int类型
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                $scope.buildPageLabel();
            }
        );
    }

    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);//执行查询前,转换为int类型
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                $scope.searchMap.category = "";
                $scope.searchMap.brand = "";
                $scope.searchMap.spec = {};
                $scope.searchMap.price = "";
                $scope.buildPageLabel();
            }
        );
    }


});