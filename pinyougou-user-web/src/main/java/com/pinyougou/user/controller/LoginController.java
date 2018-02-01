package com.pinyougou.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	
		@RequestMapping("/showName")
		public Map showName(){
			String username = SecurityContextHolder.getContext().getAuthentication().getName();//得到登陆人账号
			Map map=new HashMap<>();
			map.put("username", username);
			return map;		
	
	}

}
