package com.leyou.order.web;

import com.leyou.common.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/2/6
 */
@RestController
@RequestMapping("order")
@Api("订单服务接口")
public class OrderController {
	private final OrderService orderService;

	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * 创建订单
	 *
	 * @param orderDTO
	 * @return
	 */
	@PostMapping
	@ApiOperation(value = "创建订单接口，返回订单编号", notes = "创建订单")
	@ApiImplicitParam(name = "order", required = true, value = "订单的json对象,包含订单条目和物流信息")
	public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO) {
		// 创建订单
		return ResponseEntity.ok(orderService.createOrder(orderDTO));
	}

	/**
	 * 查询订单
	 *
	 * @param orderId
	 * @return
	 */
	@GetMapping("{orderId}")
	@ApiOperation(value = "根据订单编号查询订单，返回订单对象", notes = "查询订单")
	@ApiImplicitParam(name = "orderId", required = true, value = "订单编号", type = "Long")
	public ResponseEntity<Order> queryOrderById(@PathVariable("orderId") Long orderId) {
		// 查询订单
		return ResponseEntity.ok(orderService.queryOrderById(orderId));
	}

	/**
	 * 创建支付url
	 *
	 * @param orderId
	 * @return
	 */
	@GetMapping("/url/{orderId}")
	@ApiOperation(value = "生成微信扫描支付付款链接", notes = "生成付款链接")
	@ApiImplicitParam(name = "id", value = "订单编号", type = "Long")
	@ApiResponses({
			@ApiResponse(code = 200, message = "根据订单编号生成的微信支付地址"),
			@ApiResponse(code = 404, message = "生成链接失败"),
			@ApiResponse(code = 500, message = "服务器异常")
	})
	public ResponseEntity<String> createPayUrl(@PathVariable("orderId") Long orderId) {
		return ResponseEntity.ok(orderService.createPayUrl(orderId));
	}

	@GetMapping("/state/{id}")
	@ApiOperation(value = "更新订单状态", notes = "更新订单状态")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "订单编号", type = "Long"),
			@ApiImplicitParam(name = "status", value = "订单状态：1未付款，" +
					"2已付款未发货，" +
					"3已发货未确认，" +
					"4已确认未评价，" +
					"5交易关闭，" +
					"6交易成功，已评价", defaultValue = "1", type = "Integer")
	})
	@ApiResponses({
			@ApiResponse(code = 204, message = "true:修改成功；false:修改状态失败"),
			@ApiResponse(code = 400, message = "请求参数有误"),
			@ApiResponse(code = 500, message = "服务器异常")
	})
	public ResponseEntity<Integer> queryPayStateByOrderId(@PathVariable("id") Long orderId) {
		return ResponseEntity.ok(orderService.queryPayStateByOrderId(orderId).intValue());
	}

}
