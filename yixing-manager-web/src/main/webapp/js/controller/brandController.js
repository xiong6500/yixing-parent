app.controller('brandController', function ($scope,$controller,brandService) {
    $controller('baseController',{$scope:$scope});//继承

    $scope.pojo = {};
    $scope.searchEntity = {};

    //对象属性
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            });
    }


    //添加品牌
    $scope.save = function () {
        var methodName = "add";
        if ($scope.pojo.id != null) {
            methodName = "update";
        }
        brandService.save(methodName,$scope.pojo).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //根据id查询品牌
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.pojo = response;
            });
    }

    //删除选中的节点
    $scope.del = function () {
        brandService.del($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //品牌条件查询
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            });
    }
});
