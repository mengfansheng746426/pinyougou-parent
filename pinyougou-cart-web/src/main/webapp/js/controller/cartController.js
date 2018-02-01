app.controller('cartController',function($scope,cartService){
	
	//添加商品到购物车
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
				function(response){
					if(response.success){
						$scope.findCartList();
					}else{
						alert(response.message);
					}
				}
		);
	}
	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
				function(response){
					$scope.cartList=response;
					//计算总价格
					$scope.totalValue = sum($scope.cartList);
				}
		);
	}
	
	sum=function(cartList){
		
		var totalValue={totalNum:0,totalMoney:0.0};
		for(var i=0;i<cartList.length;i++){
			var cart = cartList[i];
			var itemList = cart.orderItemList;
			for(var j=0;j<itemList.length;j++){
				var orderItem = itemList[j];
				totalValue.totalNum += orderItem.num;
				totalValue.totalMoney += orderItem.totalFee;
			}
			
		}
		return totalValue;
	}
	//查询地址列表
	$scope.findAddressList=function(){
		cartService.findAddressListByUserId().success(
				function(response){
					$scope.addressList=response;
					//默认选中地址
					for (var i = 0; i < $scope.addressList.length; i++) {
						$scope.address=$scope.addressList[i];
						

					}
				}
		);
	}
	
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;		
	}
	//判断是否是当前选中的地址
	$scope.isSelectedAddress=function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}		
	}
	
	
	//支付方式的选择
	$scope.order={paymentType:'1'};
	$scope.selectPayType=function(paymentType){
		$scope.order.paymentType= paymentType;
		
	}
	


});