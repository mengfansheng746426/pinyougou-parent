package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {
	
	/**
	 * 生成微信支付二维码
	 * @param out_trade_no 订单号
	 * @param total_fee 金额(分)
	 * @return
	 * @throws 
	 * Exception 
	 */
	public Map createNative(String order_no,String money) throws Exception;

	Map queryOrderStatus(String order_no) throws Exception;

}
