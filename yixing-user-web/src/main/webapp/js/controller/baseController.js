app.controller('baseController' ,function($scope){
    $scope.selectIds = [];
//重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

//分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    }

    //更新复选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果被选中,则增加到数组
            $scope.selectIds.push(id);
        } else {//点击为未被选中,从数组中删除
            var idx = $scope.selectIds.indexOf(id);//获得id的下标
            $scope.selectIds.splice(idx, 1);
        }
    }
});
