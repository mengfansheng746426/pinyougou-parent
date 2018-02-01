app.service('uploadService',function($http){
	//文件上传
	this.uploadFile=function(){
		var formData= new FormData();
		formData.append("file",file.files[0]);
		return $http({
			url:'../upload/uploadFile.do',
			data:formData,
			method:'POST',
			headers:{'Content-type':undefined},
			transformRequest: angular.identity
		});
	}
	
})