package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;
@Component
public class MyMessageListener implements MessageListener {
	
	@Autowired
	private ItemPageService itemPageService;

	@Override
	public void onMessage(Message message) {
		System.out.println("接收到消息：");
		TextMessage textMessage= (TextMessage) message;
		
		
		try {
			String text = textMessage.getText();
			
			boolean b = itemPageService.genHtml(Long.parseLong(text));
			System.out.println("商品生成静态页面："+text);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	

	
	

}
