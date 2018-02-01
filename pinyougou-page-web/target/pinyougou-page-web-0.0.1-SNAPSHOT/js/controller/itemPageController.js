//商品详细页（控制层）
app.controller('itemPageController',function($scope){
	$scope.addNum=function(x){
		$scope.num=parseInt($scope.num) +x;
		if($scope.num<1){
			$scope.num=1;
		}
	}		
	$scope.specificationItems={};//记录用户选择的规格  保存用户选择的规格
	//用户选择规格
	$scope.selectSpecification=function(name,value){	
		$scope.specificationItems[name]=value;
		$scope.searchSku();
	}	
	//判断某规格选项是否被用户选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}		
	}
	$scope.sku={};
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=itemList[0];		
		$scope.specificationItems= JSON.parse(JSON.stringify(itemList[0].spec));
	}
	
	
	
	//匹配两个对象
	$scope.matchObject=function(map1,map2){		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}			
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}			
		}
		return true;		
	}
	//查询SKU
	$scope.searchSku=function(){
		for(var i=0;i<itemList.length;i++ ){
			if( $scope.matchObject(itemList[i].spec ,$scope.specificationItems ) ){
				$scope.sku=itemList[i];
				return ;
			}			
		}	
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的

	}
	
	//添加商品到购物车
	$scope.addToCart=function(){
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(
				function(){
					if (responser.success) {
						location.href='http://localhost:9107/cart.html';//跳转到购物车页面
					}else {
						alert(response.message);
					}
					
				}
		
		);
			
	}

	
});

