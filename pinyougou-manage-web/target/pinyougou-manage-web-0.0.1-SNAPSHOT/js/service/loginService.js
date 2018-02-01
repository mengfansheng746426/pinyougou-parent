app.service('loginService',function($http){
	//读取登录人名称
	this.findName=function(){
		return $http.get('../login/findName.do');		
	}
});
