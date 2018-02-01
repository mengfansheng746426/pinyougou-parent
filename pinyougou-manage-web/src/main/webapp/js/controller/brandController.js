//定义控制器
app.controller('brandController', function($scope,$controller, $http, brandService) {
	
	
	$controller('baseController',{$scope:$scope});
	
	// 查询list列表
	$scope.findAll = function() {
		brandService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页查询
	$scope.findPage = function(page, rows) {
		brandService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;// 品牌分页当前页数据
			$scope.paginationConf.totalItems = response.total;// 接收总页数
		});
	}

	$scope.entity = {};// 新增的品牌对象
	$scope.add = function() {
		brandService.add($scope.entity).success(function(response) {
			if (response.success) {
				alert(response.message);
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}

	// 根据id查询一个品牌对象
	$scope.findOne = function(id) {
		brandService.findOne(id).success(function(response) {
			$scope.entity = response;// 接收品牌对象
		});
	}
	// 品牌修改
	$scope.update = function() {
		brandService.update($scope.entity).success(function(response) {
			if (response.success) {
				alert(response.message);
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}

	$scope.save = function() {
		var methodObject = null;
		if ($scope.entity.id != null) {// 如果当前id不为空，那么是修改
			methodObject = brandService.update($scope.entity);
		} else {// id为空，添加
			methodObject = brandService.add($scope.entity);
		}

		methodObject.success(function(response) {
			if (response.success) {
				alert(response.message);
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}

	// 批量删除品牌
	$scope.dele = function() {
		brandService.dele($scope.selectIds).success(function(response) {
			if (response.success) {
				alert(response.message);
				$scope.reloadList();
				$scope.selectIds = [];
			} else {
				alert(response.message);
			}
		});
	}

	$scope.searchEntity = {};// 封装查询条件实体
	// 条件分页查询
	$scope.search = function(page, rows) {
		brandService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;// 品牌分页当前页数据
					$scope.paginationConf.totalItems = response.total;// 接收总页数
				});
	}

});