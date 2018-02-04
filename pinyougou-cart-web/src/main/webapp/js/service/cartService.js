app.service('cartService',function($http){
	
	//添加商品到购物车列表
	this.addGoodsToCartList=function(itemId,num){
		return $http.get('./cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
	}
	
	//查询购物车列表
	this.findCartList=function(){
		return $http.get("./cart/findCartList.do");
	}
	//获取地址列表
	this.findAddressListByUserId=function(){
		return $http.get("./address/findListByUserId.do");
	}
	//保存订单
	this.submitOrder=function(order){
		return $http.post('order/add.do',order);		
	}

});