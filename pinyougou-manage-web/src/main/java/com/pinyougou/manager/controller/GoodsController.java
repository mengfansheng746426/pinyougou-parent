package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;

import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	@Autowired
	private Destination queueTextDestination;//用于发送solr导入的消息
	@Autowired
	private Destination queueDeleteTextDestination;//用于发送solr导入的消息

	@Autowired
	private Destination topicDestination;//用于发送solr导入的消息

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination topicDeleteDestination;
	

	
	/*@Reference
	private ItemSearchService itemSearchService;*/
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		//获取登录名
		String sellerId  = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getGoods().setSellerId(sellerId );
		
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 更新状态
	 * @param ids
	 * @param status
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status){		
		try {
			goodsService.updateStatus(ids, status);
			if(status.equals("1")){//审核通过  将商品信息同步到索引库
	             //从数据库查询商品信息  
					final List<TbItem> itemList = goodsService.findByGoodsIdAndStatus(ids, status);						
					System.out.println("从数据库中查询数据");
					//调用搜索接口实现数据批量导入  将数据导入索引库
					jmsTemplate.send(queueTextDestination,new MessageCreator(){

						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(JSON.toJSONString(itemList));
						}
						
					});
						/*itemSearchService.importList(itemList);*/
						System.out.println("将数据导入到solr");
					
						//将审核通过的商品生成静态页面
						//静态页生成
						for(final Long id:ids){
							//itemPageService.genHtml(id);
							//向
							jmsTemplate.send(topicDestination, new MessageCreator() {

								@Override
								public Message createMessage(Session session) throws JMSException {
									System.out.println("骚伟");
									return session.createTextMessage(id+"");
								}
								
							});
						}	

						
					
			}

			return new Result(true, "成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			/*itemSearchService.deleteByGoodsIds(Arrays.asList(ids));*/
			jmsTemplate.send(queueDeleteTextDestination, new MessageCreator() {

				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
				
			});
			//清除静态页面
			jmsTemplate.send(topicDeleteDestination, new MessageCreator() {

				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
				
			});
			
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	/*@Reference
	private ItemPageService itemPageService;*/
	

	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
}
