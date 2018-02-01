package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
@Component
public class MyMessageListener implements MessageListener {
	
	@Autowired
	private ItemSearchService itemSearchService;

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage)message;
		try {
			List<TbItem> itemList = JSON.parseArray(textMessage.getText(),TbItem.class);
			for(TbItem item :itemList) {
				
				Map specMap = JSON.parseObject(item.getSpec(),Map.class);
				item.setSpecMap(specMap);
				
				System.out.println(item.getId()+item.getTitle());
				
			}
			itemSearchService.importList(itemList);
			System.out.println("已经同步到索引库");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
