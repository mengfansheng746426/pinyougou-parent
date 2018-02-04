app.service('seckillService',function($http){
	
	//添加商品到购物车列表
	this.findSeckillList=function(itemId,num){
		return $http.get("./seckillGoods/findSeckillList.do");
	}
	
	
});