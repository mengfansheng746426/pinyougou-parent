app.controller('seckillController',function($scope,seckillService){
	//查询购物车列表
	$scope.findSeckillList=function(){
		cartService.findSeckillList().success(
				function(response){
					$scope.seckillList=response;
				}
		);
	}

});