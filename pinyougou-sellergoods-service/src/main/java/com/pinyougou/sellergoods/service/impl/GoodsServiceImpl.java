package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper  goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;


	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//设置一下商品状态
		goods.getGoods().setAuditStatus("0");//刚添加  未审核
		//1.添加基本信息
		goodsMapper.insert(goods.getGoods());
		//2.添加扩展信息
		
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		//添加SKU列表
		insertItems(goods);
	}
	
	private void insertItems(Goods goods) {
		if("1".equals(goods.getGoods().getIsEnableSpec())){//启用规格
			//3.添加SKU列表
			for(TbItem item : goods.getItemList()){
				//标题
				String title= goods.getGoods().getGoodsName();
				Map<String, Object> specMap=JSON.parseObject(item.getSpec());
				for(String key:specMap.keySet()){
					title+=" "+specMap.get(key);
				}
				item.setTitle(title);
				setItem(item,goods);
				
				itemMapper.insert(item);	
			}
		}else{//不启用规格，SKU只有一条
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//将SPU名称作为SKU名称
			item.setPrice(goods.getGoods().getPrice());//将SPU的价格作为SKU的价格
			item.setNum(99999);
			item.setStatus("1");
			item.setIsDefault("1");
			setItem(item,goods);
			itemMapper.insert(item);
		}
	}
	
	//设置SKU部分属性
		private void setItem(TbItem item,Goods goods){
			item.setCategoryid(goods.getGoods().getCategory3Id());
			item.setCreateTime(new Date());
			item.setUpdateTime(new Date());
			item.setGoodsId(goods.getGoods().getId());//当前SKU是属于哪个SPU的
			item.setSellerId(goods.getGoods().getSellerId());//当前SKU是属于哪个商家的
			
			//设置分类
			TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
			item.setCategory(itemCat.getName());
			//设置品牌
			TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
			item.setBrand(brand.getName());
			//设置商家
			TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			item.setSeller(seller.getNickName());
			
			//获取商品图片列表，将第一个图片的URL设置到item中
			List<Map> imageList=JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
			item.setImage((String)imageList.get(0).get("url"));
		}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getGoods().setAuditStatus("0");
		//设置未申请状态:如果是经过修改的商品，需要重新设置状态
		goodsMapper.updateByPrimaryKey(goods.getGoods());//保存商品表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());//保存商品表
		
		
		//删除原有的sku列表数据	
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example );
		
		//2重新关联页面提交的SKU列表
		insertItems(goods);
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		//查询基本信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		//查询扩展信息
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		
		goods.setGoodsDesc(tbGoodsDesc);
		
		//查询SKU列表
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(id);//以SPU的id为外键查询SKU列表
		List<TbItem> selectByExample = itemMapper.selectByExample(example);
		goods.setItemList(selectByExample);
		
		
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
							//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							//精确查询
							criteria.andSellerIdEqualTo(goods.getSellerId());

			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
			criteria.andIsDeleteIsNull();//非删除状态
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
		/**
		 * 批量修改
		 */
		@Override
		public void updateStatus(Long[] ids, String status) {
			for (Long id : ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
				
			}
			
		}

		@Override
		public List<TbItem> findByGoodsIdAndStatus(Long[] goodsIds, String status) {
			TbItemExample example = new TbItemExample();
			com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
			createCriteria.andGoodsIdIn(Arrays.asList(goodsIds));
			createCriteria.andStatusEqualTo(status);
			List<TbItem> selectByExample = itemMapper.selectByExample(example);
			
			return selectByExample;
		}

	
}
