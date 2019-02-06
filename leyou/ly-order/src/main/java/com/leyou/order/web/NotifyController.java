package com.leyou.order.web;

import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {
    @Autowired
    private OrderService orderService;

    //微信官方要求回调的接口不能带参数?key=value 所以请求方式为post 参数类型为xml格式 同时返回的结果用xml格式标识
    @PostMapping(value = "wxpay",produces = /*声明返回值一定是***格式*/"application/xml")
    @ResponseBody
    public Map<String,String> wxpayNotify(@RequestBody Map<String,String> result){
        //处理回调
        orderService.handleNotify(result);

        log.info("[支付回调] 接收微信支付回调 结果:{}",result);

        //返回值
        Map<String,String> msg = new HashMap<>();
        msg.put("return_code","SUCCESS");
        msg.put("return_msg","OK");
        return msg;
    }
}
