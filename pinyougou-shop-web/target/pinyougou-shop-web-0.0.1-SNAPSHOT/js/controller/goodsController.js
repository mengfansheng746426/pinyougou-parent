 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.status=['未审核','已审核','审核未通过','关闭'];
	
	//查询所有分类[{id:1,name:手机}][{1:手机}]
	
	$scope.itemCatList=[];//商品分类列表
	//加载商品分类列表
	$scope.findItemCatList=function(){		
		itemCatService.findAll().success(
				function(response){							
					for(var i=0;i<response.length;i++){
						$scope.itemCatList[response[i].id]=response[i].name;
					}
				}
		);
	}

	
	
	
	
	//读取一级分类   查询顶级
	$scope.selectItemCat1List=function(){
	      itemCatService.findByParentId(0).success(
	    		 function(response){
	    			 $scope.itemCat1List=response; 
	    			 
	    		 }
	      );
	}
	
	$scope.$watch('entity.goods.category1Id', function(newValue, oldValue) {          
    	//根据选择的值，查询二级分类
    	itemCatService.findByParentId(newValue).success(
    		function(response){
    			$scope.itemCat2List=response; 	
    			$scope.itemCat3List=[];
    		}
    	);    	
})

	$scope.$watch('entity.goods.category2Id', function(newValue, oldValue) {          
    	//根据选择的值，查询三级分类
    	itemCatService.findByParentId(newValue).success(
    		function(response){
    			$scope.itemCat3List=response; 	    			
    		}
    	);    	
 })

 //三级分类选择后  读取模板ID
    $scope.$watch('entity.goods.category3Id', function(newValue, oldValue) {    
       	itemCatService.findOne(newValue).success(
       		  function(response){
       			$scope.itemCat=response;
       			 $scope.entity.goods.typeTemplateId=response.typeId;    
       		  }
        );    
    }); 
	//监听模板id，如果变化，查询对应的模板信息，获取品牌数据
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(
				function(response){
					$scope.typeTemplate=response;//获取模板对象
					$scope.brandList=JSON.parse(response.brandIds);//接收品牌数据
					if ($location.search()['id']==null) {
						$scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);//接收扩展属性
						
					}
				}
		);
		typeTemplateService.findSpecListById(newValue).success(
	    		  function(response){
						$scope.specList=response;
	    		  }
	    );

		
	});

	
	
	
	
	
	
	
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};
	
	
	
	
	
	//创建SKU列表
	$scope.creatItemList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:1,isDefault:0}];
		var specItems=$scope.entity.goodsDesc.specificationItems;//根据此数组生成SKU列表
		//
		for(var i=0;i<specItems.length;i++){
//[{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
			//参数1：上一次循环出的list结果 参数2：本次循环的规格名称 参数3：本次循环的规格选项数组
			$scope.entity.itemList=addColumn($scope.entity.itemList,specItems[i].attributeName,specItems[i].attributeValue);
		}
		
	}
	addColumn=function(list,name,values){
		var newList = [];
		for(var i=0;i<list.length;i++){//循环上次生成的sku列表
			var oldRow=list[i];
			for(var j=0;j<values.length;j++){//循环本次规格选项数组
				var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
				newRow.spec[name]=values[j];
				newList.push(newRow);
			}
		}
		return newList;
		
	}
	
	
	//勾选规格选项，操作DESC表中specificationItems(数组)的值
	$scope.updateSpecAttribute=function($event,name,value){
		var object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems ,'attributeName', name);		
			if(object!=null){	
				if($event.target.checked ){
					object.attributeValue.push(value);		
				}else{//取消勾选				object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);//移除选项
					var index=object.attributeValue.indexOf(value);
					object.attributeValue.splice(index,1);
					if(object.attributeValue.length==0){//选项已经没有值了，移除整个规格对象
						var index2=$scope.entity.goodsDesc.specificationItems.indexOf(object);
						$scope.entity.goodsDesc.specificationItems.splice(index2,1);
					}			
				}
			}else{				
	$scope.entity.goodsDesc.specificationItems.push(
	{"attributeName":name,"attributeValue":[value]});
			}		
		}

	
	
	
	
	
	
	//向图片数组中添加图片对象
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//从数组中移除图片对象
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1)
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){	
		var id = $location.search()['id'];//获取参数值
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
				//转换图片
				$scope.entity.goodsDesc.itemImages= JSON.parse($scope.entity.goodsDesc.itemImages);
				
				//转换扩展属性
				$scope.entity.goodsDesc.customAttributeItems=  JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				
				
				//规格				
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);	
				for( var i=0;i<$scope.entity.itemList.length;i++ ){
					$scope.entity.itemList[i].spec = JSON.parse( $scope.entity.itemList[i].spec);		
				}		

			
			
			
			}
		);				
	}
	//检测规格选中状态    	 根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(name,value){
		var items= $scope.entity.goodsDesc.specificationItems;//扩展表中规格数据(数据结构)
		var object= $scope.searchObjectByKey(items,'attributeName',name);
		//判断当前规格名称是否在数组中存在
		if (object!=null) {//数组中存在该规格
			if(object.attributeValue.indexOf(value)>=0){
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	
	
	
	
	
	
	//保存 
	$scope.save=function(){		
		//获取kindeditor中的内容，赋值给introduction
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert('保存成功');
					location.href="goods.html";
					$scope.entity={};//清空
					editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	$scope.image_entity={};//图片对象
	//图片上传
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
				function(response){
					if(response.success){
					 $scope.image_entity.url=response.message;
					}else{
						alert(response.message);
					}
				}
		);
	}
});	
