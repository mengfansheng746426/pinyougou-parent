package com.pinyougou.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
	@RequestMapping("/findName")
	public Map findName() {
		Map map = new HashMap<>();
		//获取登录的用户名
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		map.put("loginName", name);
		return map;
		
	}
}
