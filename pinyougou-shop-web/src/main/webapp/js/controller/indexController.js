app.controller('indexController',function($scope,$controller,loginService){
	$scope.findName=function(){
		loginService.findName().success(
				function(response){
					$scope.loginName=response.loginName;
				}
				
		);
	}
});