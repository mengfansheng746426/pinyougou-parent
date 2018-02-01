package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {
	
	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		
		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> brandPage = (Page<TbBrand>) brandMapper.selectByExample(null);
		
		return new PageResult(brandPage.getTotal(), brandPage.getResult());
	}

	@Override
	public void add(TbBrand brand) {

		brandMapper.insert(brand);
	}

	@Override
	public TbBrand findOne(long id) {
	
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
		
	}

	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			brandMapper.deleteByPrimaryKey(id);
		}		

		
	}

	@Override
	public PageResult findPage(TbBrand brand, int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		TbBrandExample example = new TbBrandExample();
		//创建一个封装查询的对象   条件可以放在criteria这个里
		Criteria criteria = example.createCriteria();
		
		if (brand!=null) {
			if (brand.getName()!=null&&!"".equals(brand.getName())) {
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if (brand.getFirstChar()!=null&&!"".equals(brand.getFirstChar())) {
				criteria.andFirstCharEqualTo(brand.getFirstChar());
			}
		}
		
		Page<TbBrand> brandPage = (Page<TbBrand>) brandMapper.selectByExample(example);
		
		return new PageResult(brandPage.getTotal(), brandPage.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return brandMapper.selectOptionList();
	}
	
}
