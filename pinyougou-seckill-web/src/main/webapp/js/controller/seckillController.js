app.controller('seckillController',function($scope,seckillService){
	//查询购物车列表
	$scope.findSeckillList=function(){
		seckillService.findSeckillList().success(
				function(response){
					$scope.seckillList=response;
				}
		);
	}

});