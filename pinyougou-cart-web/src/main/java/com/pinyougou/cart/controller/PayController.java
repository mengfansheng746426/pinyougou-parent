package com.pinyougou.cart.controller;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;
import util.IdWorker;

@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference//dubbo服务
	private  WeixinPayService payService;
	
	@Reference
	private OrderService orderService;

	/**
	 * 生成二维码
	 * @return
	 */
	@RequestMapping("/createNative")

	public Map createNative() {
		 /**
         * 1、准备订单号和金额
         * 2、调用服务层
         * 3、返回结果
         */
		try {
//          IdWorker idWorker = new IdWorker();
            
          //查询订单支付日志(订单号、总金额)
          String userid = SecurityContextHolder.getContext().getAuthentication().getName();
          TbPayLog paylog = orderService.queryPayLog(userid);
//          Map map = payService.createNative(idWorker.nextId()+"", "1");
          Map map = payService.createNative(paylog.getOutTradeNo(), paylog.getTotalFee()+"");
          return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	/**
     * 
     * 查询订单支付状态
     * @param order_no ： 订单号
     * @return<br/>
     * ============History===========<br/>
     * 2018年2月2日   Administrator    新建
     */
    @RequestMapping("/queryOrderStatus")
    public Result queryOrderStatus(String order_no){
        
        Result result = new Result(false, "支付失败");
        int x=0;
        try {
            while(true){
                Map statusMap = payService.queryOrderStatus(order_no);
                if(statusMap == null){
                    break;
                }
                //支付成功
                if(statusMap.get("trade_state").equals("SUCCESS")){
                    result = new Result(true, "支付成功");
                    
                    //支付成功，修改订单状态
                    Object id = statusMap.get("transaction_id");
                    String transaction_id = (String) id;
                    orderService.updateOrderStatus(order_no, transaction_id);
                    break;
                }
                if(x>=9){
                    //5分钟=100*3秒
                    result = new Result(true, "TIME_OUT");
                    break;
                }
                Thread.sleep(3000);
                x++;
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
}
