package com.pinyougou.sellergoods.service;
/**
 * 品牌的接口层
 * @author Administrator
 *
 */

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	public List<TbBrand> findAll();
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	//需要两个参数   当前页码，和每页显示多少条
	public PageResult findPage(int pageNum,int pageSize);
	/**
	 * 新增品牌
	 * @param brand
	 */
	public void add(TbBrand brand);
	/**
	 * 根据 查询一个对象
	 * @param id
	 * @return
	 */
	public TbBrand findOne(long id);
	
	public void update(TbBrand brand);
	
	public void delete(Long [] ids);
	
	public PageResult findPage(TbBrand brand,int pageNo,int pageSize);
	/**
	 * 品牌下拉框数据
	 * @return
	 */
	public List<Map> selectOptionList();
}
