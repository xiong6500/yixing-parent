//控制层
app.controller('goodsController', function ($scope, $controller, $location, typeTemplateService, itemCatService, uploadService, goodsService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];

    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        );
    }

    //读取一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        );
    }

    //读取二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        );
    });

    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        );
    });

    //读取模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    });

    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;
                $scope.typeTemplate.brandIds = JSON.parse(response.brandIds);//品牌列表
                if ($location.search()['id'] == null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//品牌列表
                }
            }
        );
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );
    });

    //判断选中的规格项所属的规格是否加入规格结果集了
    /**
     *当选中一个规格项时,先判断规格结果集是否有了该规格项所属的规格
     * 没有就直接加入到规格结果集
     * 有就判断事件源是否选中
     * 选中就加入进去
     * 没选中就去掉,如果结果集的规格的规格项数组为空就删掉该规格
     * @param selectedList 规格结果集
     * @param key  json格式的key值
     * @param attributeName json格式key的value值
     * @returns {null|*}
     */
    $scope.searchObjectByKey = function (selectedList, key, attributeName) {
        for (var i = 0; i < selectedList.length; i++) {
            if (selectedList[i][key] == attributeName) {
                return selectedList[i];
            }
        }
        return null;
    }

    //往tb_goods_desc中添加specification_items
    $scope.updateSpecAttribute = function ($event, attributeName, value) {
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', attributeName);
        if (obj != null) {
            if ($event.target.checked) {
                //如果该规格项被选中
                obj.attributeValue.push(value);
            } else {
                //没有选中
                obj.attributeValue.splice(obj.attributeValue.indexOf(value), 1);
                if (obj.attributeValue.length == 0) {//如果规格项的数组为空,删除整个规格
                    var idx = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(idx, 1);
                }
            }
        } else {
            //规格结果集里不存在该种规格
            $scope.entity.goodsDesc.specificationItems.push({
                'attributeName': attributeName,
                'attributeValue': [value]
            });
        }
    }

    //往tb_item表中添加规格(笛卡尔积)[spec{'网络':'移动3g','机身内存':16g}]
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList,
                items[i].attributeName, items[i].attributeValue);
        }
    }

    //思路:遍历所有的规格属性,根据规格属性的个数定义实现笛卡尔积
    // 自定义一条记录,多一个属性就1*a(a是属性的规格项数组元素个数),
    // 再多一个就1*a*b(b是第二属性的规格项数组元素个数) 循环下去
    addColumn = function (list, attributeName, values) {
        var newList = [];
        for (var k = 0; k < list.length; k++) {
            //[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];
            var oldRow = list[k];
            for (var j = 0; j < values.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[attributeName] = values[j];
                newList.push(newRow);
            }
        }
        return newList;
    }
    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;//设置文件地址
                } else {
                    alert(response.message);
                }
            }
        ).error(function () {
            alert("上传发生错误");
        });
    }

    //定义要保存的组合类goods
    $scope.entity = {
        goods: {},
        goodsDesc: {
            itemImages: [],
            specificationItems: []
        },
        itemList: [],
    }
    //列表中添加图片
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity = function (image) {
        var ids = $scope.entity.goodsDesc.itemImages.indexOf(image);
        $scope.entity.goodsDesc.itemImages.splice(ids, 1);
    }
    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //规格
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                var items = $scope.entity.itemList;
                for (var i=0;i<items.length;i++){
                    items[i].spec = JSON.parse(items[i].spec);
                }
            }
        );
    }

    //判断规格结果集中的规格项是否被包含
    $scope.checkAttrubuteValue = function (specName, specValue) {
        var items = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(items, 'attributeName', specName);
        if (obj == null) {
            return false;
        }
        if (obj.attributeValue.indexOf(specValue) >= 0) {
            return true;
        }else {
            return false;
        }
    }


    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        $scope.entity.goodsDesc.introduction = editor.html();
        serviceObject.success(
            function (response) {
                if (response.success) {
                    location.href="goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

});	