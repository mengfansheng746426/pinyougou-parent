app.controller('contentController',function($scope,contentService){
	
	$scope.contentList=[];//所有广告的集合
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}

	//通用
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
				function(response){
					$scope.contentList[categoryId]=response;
				}
		);
	}
})
