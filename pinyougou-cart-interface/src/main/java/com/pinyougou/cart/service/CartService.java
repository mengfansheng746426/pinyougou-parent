package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

public interface CartService {
	 /**
     * 
     * 添加商品到购物车
     * @param cartList ： 购物车列表
     * @param itemId ： 商品skuid
     * @param num : 商品数量
     * @return 添加完商品后的购物车列表
     * @throws Exception<br/>
     * ============History===========<br/>
     * 2018年1月30日   Administrator    新建
     */
	 public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num)throws Exception;

	 /**
	     * 
	     * 从redis中取购物车信息
	     * @param username ： 用户信息
	     * @return<br/>
	     * ============History===========<br/>
	     * 2018年1月30日   Administrator    新建
	     */
	    public List<Cart> findCartListFromRedis(String username);


	    /**
	     * 
	     * 把当前用户的购物车存放到redis中
	     * @param cartList ： 购物车列表
	     * @param username ： 用户名<br/>
	     * ============History===========<br/>
	     * 2018年1月30日   Administrator    新建
	     */
	    public void saveCartListToRedis(List<Cart> cartList, String username);


	    /**
	     * 
	     * 合并两个购物车
	     * @param cartList_redis ： redis购物车
	     * @param cartList_cookie ： cookie购物车
	     * @return 合并后的购物车<br/>
	     * ============History===========<br/>
	     * 2018年1月30日   Administrator    新建
	     */
	    public List<Cart> mergeCartList(List<Cart> cartList_redis, List<Cart> cartList_cookie);
}
