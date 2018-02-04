app.controller('cartController',function($scope,cartService,addressService){
	
	
	//添加地址
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity ); //修改  
		}else{
			
			serviceObject=addressService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					location.href="getOrderInfo.html";//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	//查询实体 
	$scope.findOne=function(id){				
		addressService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	//删除地址
	$scope.dele=function(id){			
		//获取选中的复选框			
		addressService.dele(id).success(
			function(response){
				if(response.success){
					alert(response.message);
					location.href="getOrderInfo.html";//重新加载
				}						
			}		
		);				
	}
	
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
						if($scope.addressList[i].isDefault == '1'){
							$scope.address =$scope.addressList[i];
						}

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
	

	//保存订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		cartService.submitOrder( $scope.order ).success(
			function(response){
				if(response.success){
					//页面跳转
					if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
						location.href="pay.html";
					}else{//如果货到付款，跳转到提示页面
						location.href="paysuccess.html";
					}					
				}else{
					alert(response.message);	//也可以跳转到提示页面				
				}				
			}				
		);		
	}


});