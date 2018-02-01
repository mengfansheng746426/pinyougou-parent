app.service('contentService',function($http){
	//按照分类查询广告列表
	this.findByCategoryId=function(categoryId){
		return $http.get('../content/findByCategoryId.do?categoryId='+categoryId);
	}
})