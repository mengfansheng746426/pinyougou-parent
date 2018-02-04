package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.constant.SeckillConstant;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;
import com.pinyougou.seckill.service.SeckillGoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillGoods> page=   (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id){
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillGoods!=null){			
						if(seckillGoods.getTitle()!=null && seckillGoods.getTitle().length()>0){
				criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(seckillGoods.getSmallPic()!=null && seckillGoods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(seckillGoods.getSellerId()!=null && seckillGoods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(seckillGoods.getStatus()!=null && seckillGoods.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(seckillGoods.getIntroduction()!=null && seckillGoods.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
		
		Page<TbSeckillGoods> page= (Page<TbSeckillGoods>)seckillGoodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		/**
		 * 查询秒杀商品列表
		 * @return
		 * @throws Exception
		 */
		@Override
		public List<TbSeckillGoods> findSeckillGoodsList() throws Exception {
			/**
             * 1、组装查询条件
             *      商品审核通过
             *      商品秒杀中(startTime < now) (endTime > now)
             *      库存量 > 0
             *  2、调用dao
             *  
             *  3、redis缓存改造
             */
			List<TbSeckillGoods> seckillList = redisTemplate.boundHashOps(SeckillConstant.SECKILL_GOODS_LIST).values();
			if (seckillList==null||seckillList.size()<=0) {
				 //读取db中的数据
                TbSeckillGoodsExample example = new TbSeckillGoodsExample();
                Criteria criteria = example.createCriteria();
                criteria.andStatusEqualTo("1");//审核通过
                Date now = new Date();
                criteria.andStartTimeLessThan(now);
                criteria.andEndTimeGreaterThanOrEqualTo(now);
                criteria.andStockCountGreaterThan(0);
                seckillList = seckillGoodsMapper.selectByExample(example);
              //把db中的数据存放到redis缓存中、
                for (TbSeckillGoods tbSeckillGoods : seckillList) {
					redisTemplate.boundHashOps(SeckillConstant.SECKILL_GOODS_LIST).put(tbSeckillGoods.getId()+"", tbSeckillGoods);
					 System.out.println("将商品" + tbSeckillGoods.getId() + "放入redis缓存,库存为" + tbSeckillGoods.getStockCount());
				}
			}
			 return seckillList;
		}
	
}
