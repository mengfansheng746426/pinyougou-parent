package com.pinyougou.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//权限的列表，真正的是从数据库中查询
				List<GrantedAuthority> authorities = new ArrayList<>();
				//给当前所有用户都提供了role_user的权限
				GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
				authorities.add(grantedAuthority);
				return new User(username, "", authorities);
	}

}
