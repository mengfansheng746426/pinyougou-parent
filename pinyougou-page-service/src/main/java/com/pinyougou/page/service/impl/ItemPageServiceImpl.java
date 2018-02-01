package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

@Service
public class ItemPageServiceImpl implements ItemPageService {
	@Autowired
	private FreeMarkerConfig freeMarkerConfig; 
	
	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;


	
	
	@Override
	public boolean genHtml(Long goodsId) {
		
		try {
			//得到配置对象
			Configuration configuration = freeMarkerConfig.getConfiguration();
			//获取模版对象
			Template template = configuration.getTemplate("item.ftl");
			//创建数据模型
			Map dataModel=new HashMap<>();
			//1.加载商品表数据
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);			
			//2.加载商品扩展表数据			
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);
			//3.商品分类
			String category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			dataModel.put("category1", category1);
			dataModel.put("category2", category2);
			dataModel.put("category3", category3);
			//4.SKU列表

			
			TbItemExample example = new TbItemExample();
			Criteria createCriteria = example.createCriteria();
			createCriteria.andGoodsIdEqualTo(goodsId);
			createCriteria.andStatusEqualTo("1");//状态为有效
			example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认	
			List<TbItem> itemList = itemMapper.selectByExample(example );
			
			dataModel.put("itemList", itemList);
			//创建输出流
			Writer out=new FileWriter("d:/item/"+goodsId+".html");
			template.process(dataModel, out);
			out.close();
			return true;			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return false;
	}




	@Override
	public boolean deleteHtml(Long[] goodsIds) {
		try {
			for(Long id:goodsIds){
				new File("d:/item/"+id+".html").delete();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		

	}

}
