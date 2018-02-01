package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String file_server_url;

	@RequestMapping("/uploadFile")
	public Result uploadFile(MultipartFile file){
		String path = file.getOriginalFilename();//获取文件的绝对路径
		String extName = path.substring(path.lastIndexOf(".")+1);//截取文件名，获取扩展名
		try {
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");//加载配置文件，获取工具类
			//3、执行上传处理
			String uploadFile = fastDFSClient.uploadFile(file.getBytes(), extName, null);//文件上传 参数1：图片流信息
			return new Result(true,file_server_url+uploadFile);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"上传操作失败");
		}
	}
}
