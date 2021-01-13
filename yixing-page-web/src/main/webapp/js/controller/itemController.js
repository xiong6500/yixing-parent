app.controller('itemController', function($scope) {

	$scope.specifictionItems = {} //记录用户选择的规格

	//添加商品到购物车
	$scope.addToCart = function() {
		alert('skuid:' + $scope.sku.id);
	}

	$scope.selectSpecifiction = function(name, value) {
		$scope.specifictionItems[name] = value;
		searchSku();
	}

	$scope.isSelected = function(name, value) {
		if ($scope.specifictionItems[name] == value) {
			return true;
		}
		return false;
	}

	//匹配两个对象
	//{"网络":"移动3G","机身内存":"32G"} map1
	//{"网络":"移动3G","机身内存":"32G"} map2

	matchObject = function(map1, map2) {
		for (k in map1) {
			if (map1[k] != map2[k]) {
				return false;
			}
		}
		for (k in map2) {
			if (map2[k] != map1[k]) {
				return false;
			}
		}
		return true;
	}

	searchSku = function() {
		for (var i = 0; i < skuList.length; i++) {
			if (matchObject(skuList[i].spec, $scope.specifictionItems)) {
				$scope.sku = skuList[i];
				return;
			}
		}
		$scope.sku = {
			id: 0,
			title: '--------',
			price: 0
		}; //如果没有匹配的		
	}

	//加载默认sku
	$scope.loadSku = function() {
		$scope.sku = skuList[0];
		$scope.specifictionItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}

	//数量操作
	$scope.addNum = function(x) {
		$scope.num = $scope.num + x;
		if ($scope.num < 1) {
			$scope.num = 1;
		}
		if ($scope.num > 200) {
			$scope.num = 200;
		}
	}
});
