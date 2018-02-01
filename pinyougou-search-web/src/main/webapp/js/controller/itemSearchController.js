app.controller('itemSearchController',function($scope,itemSearchService,$location){
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':30,'sortField':'','sort':''};//条件map
	
	$scope.resultMap={itemList:[]};//查询结果map
	
	
	//加载查询字符串
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=  $location.search()['keywords'];
		$scope.search();
	}

	
	
	//判断关键字是否包含品牌
	$scope.isBrand=function(){
		
		$scope.resultMap.brandList;
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
				$scope.resultMap.pinpai=$scope.resultMap.brandList[i].text;
				return true;
			}			

		}
		return false;
	}
	
	$scope.sortSearch=function(sortField,sort){
		$scope.searchMap.sortField=sortField;	
		$scope.searchMap.sort=sort;	
		$scope.search();
	}

	
	
	/*$scope.todoSomething=function($event){
		alert(123);
	      if($event.keyCode==39){
	    	  $scope.queryByPage(searchMap.pageNo+1);
	      }
	      if($event.keyCode==37){
	    	  queryByPage(searchMap.pageNo-1);
	      }
	  }*/
	/*$scope.myKeyup = function(e){
		var keycode = window.event?e.keyCode:e.which; 
		alert(123);
		        if(keycode==13){
		        	alert(243);
		        }
		};*/
	
	
	
	//根据页码查询
	$scope.queryByPage=function(page){
		//页码验证
		if(page<1 || page>$scope.resultMap.totalPages){
			return;
		}		
		$scope.searchMap.pageNo=parseInt(page);
		$scope.search();
	}

	
	
	//构建分页标签(totalPages为总页数)
	//构建分页工具条
	$scope.buildPageLabel=function(){
		$scope.pagelabel=[];
		var maxPageNo = $scope.resultMap.totalPages;//最后页码
		var firstPage=1;//起始位置
		var lastPage=maxPageNo;//结束位置
		
		$scope.firstDot=true;//显示前面的点
		$scope.lastDot=true//显示后面的点
		
		
		if(maxPageNo>5){//如果总页数大于5，显示部分页码
			if($scope.searchMap.pageNo<=3){//显示前五条
				lastPage=5;
				$scope.firstDot=false;//前五条不显示前面的点
			}else if($scope.searchMap.pageNo>=maxPageNo-2){//显示后五条
				firstPage=maxPageNo-4;
				$scope.lastDot=false;//后五条不显示后面点
			}else{
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}else{
			$scope.firstDot=false;
			$scope.lastDot=false;
		}
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pagelabel.push(i);
		}
	}	

	
	//设置参数
	$scope.addToMap=function(key,name){
		if(key=='category' || key=='brand'|| key=='price'){//如果点击的是分类或者是品牌
			$scope.searchMap[key]=name;
		}else{
			$scope.searchMap.spec[key]=name;
		}	
		$scope.search();//执行搜索
	}
	//移除参数
	$scope.removeFromMap=function(key){
		if(key=='brand'||key=='category'|| key=='price'){//选择的条件是品牌或者分类f
			$scope.searchMap[key]='';
		}else{
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
	
	
	
	//搜索
	$scope.search=function(){
		itemSearchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;
					$scope.resultMap.itemList=response.rows;
					$scope.resultMap.categoryList=response.categoryList;
					$scope.resultMap.brandList=response.brandList;
					$scope.resultMap.specList=response.specList;
					$scope.buildPageLabel();//搜索后构建分页工具条
					$scope.resultMap.pinpai=response.pinpai;
				}
		);
	}
	
})
