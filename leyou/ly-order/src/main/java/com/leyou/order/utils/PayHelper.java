package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.wxpay.sdk.WXPayConstants.FAIL;
import static com.github.wxpay.sdk.WXPayConstants.SUCCESS;

@Slf4j
@Component
public class PayHelper {
	private final StringRedisTemplate redisTemplate;
	private final WXPay wxPay;

	private final PayConfig config;

	private final OrderMapper orderMapper;

	private final OrderStatusMapper statusMapper;
	private static final Logger logger = LoggerFactory.getLogger(PayHelper.class);

	@Autowired
	public PayHelper(WXPay wxPay, PayConfig config, OrderMapper orderMapper, OrderStatusMapper statusMapper, StringRedisTemplate redisTemplate) {
		this.wxPay = wxPay;
		this.config = config;
		this.orderMapper = orderMapper;
		this.statusMapper = statusMapper;
		this.redisTemplate = redisTemplate;
	}


	public String createPayUrl(Long orderId) {
		String key = "leyou.pay.url." + orderId;
		try {
			String url = this.redisTemplate.opsForValue().get(key);
			if (StringUtils.isNotBlank(url)) {
				return url;
			}
		} catch (Exception e) {
			logger.error("查询缓存付款链接异常,订单编号：{}", orderId, e);
		}

		try {
			Map<String, String> data = new HashMap<>();
			// 商品描述
			data.put("body", "乐优商城测试");
			// 订单号
			data.put("out_trade_no", orderId.toString());
			//货币
			data.put("fee_type", "CNY");
			//金额，单位是分
			data.put("total_fee", "1");
			//调用微信支付的终端IP（estore商城的IP）
			data.put("spbill_create_ip", "127.0.0.1");
			//回调地址
			data.put("notify_url", "http://test.leyou.com/wxpay/notify");
			// 交易类型为扫码支付
			data.put("trade_type", "NATIVE");
			//商品id,使用假数据
			data.put("product_id", "1234567");

			Map<String, String> result = this.wxPay.unifiedOrder(data);

			if ("SUCCESS".equals(result.get("return_code"))) {
				String url = result.get("code_url");
				// 将付款地址缓存，时间为10分钟
				try {
					this.redisTemplate.opsForValue().set(key, url, 10, TimeUnit.MINUTES);
				} catch (Exception e) {
					logger.error("缓存付款链接异常,订单编号：{}", orderId, e);
				}
				return url;
			} else {
				logger.error("创建预交易订单失败，错误信息：{}", result.get("return_msg"));
				return null;
			}
		} catch (Exception e) {
			logger.error("创建预交易订单异常", e);
			return null;
		}
	}


	public String createOrders(Long orderId, Long totalPay, String desc) {
		try {
			Map<String, String> data = new HashMap<>();
			//商品描述
			data.put("body", desc);
			//订单号
			data.put("out_trade_no", orderId.toString());
			//金额 单位分
			data.put("total_fee", totalPay.toString());
			//调用微信支付的终端
			data.put("spbill_create_ip", "127.0.0.1");
			//回调地址
			data.put("notify_url", config.getNotifyUrl());
			//支付类型为扫码支付
			data.put("trade_type", "NATIVE");

			//利用wxPay工具完成下单
			Map<String, String> result = wxPay.unifiedOrder(data);

			//判断通信和业务标识
			isSuccess(result);

			//下单成功
			String url = result.get("code_url");
			return url;
		} catch (Exception e) {
			log.error("[微信下单] 创建预交易订单异常失败", e);
			return null;
		}
	}

	public void isSuccess(Map<String, String> result) {
		//判断通信标识
		String returnCode = result.get("return_code");
		if (returnCode.equals(FAIL)) {
			//通信失败
			log.error("[微信下单] 微信下单通信失败,失败原因:{}", result.get("return_msg"));
			throw new LyException(ExceptionEnum.WXPAY_ORDER_ERROR);
		}

		//判断业务标识
		String resultCode = result.get("result_code");
		if (resultCode.equals(FAIL)) {
			//通信失败
			log.error("[微信下单] 微信下单业务失败,错误码:{} , 失败原因:{}", result.get("err_code"), result.get("err_code_des"));
			throw new LyException(ExceptionEnum.WXPAY_ORDER_ERROR);
		}
	}

	public void isValidSign(Map<String, String> result) {
		//重新生成签名
		try {
			String sign1 = WXPayUtil.generateSignature(result, config.getKey(), WXPayConstants.SignType.HMACSHA256);
			String sign2 = WXPayUtil.generateSignature(result, config.getKey(), WXPayConstants.SignType.MD5);

			//和传过来的比较
			String sign = result.get("sign");
			if (!StringUtils.equals(sign, sign1) && !StringUtils.equals(sign, sign2)) {
				//签名有误
				throw new LyException(ExceptionEnum.INVALID_SING_ERROR);
			}
		} catch (Exception e) {
			throw new LyException(ExceptionEnum.INVALID_SING_ERROR);
		}
	}

	public PayState queryPayState(Long orderId) {
		try {
			Map<String, String> data = new HashMap<>();
			//订单号
			data.put("out_trade_no", orderId.toString());
			//发起查询
			Map<String, String> result = wxPay.orderQuery(data);
			//1.校验通信标识和业务标识
			isSuccess(result);
			//2.校验签名
			isValidSign(result);

			//3.校验金额
			String totalFeeStr = result.get("total_fee");
			String tradeNo = result.get("out_trade_no");
			if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)) {
				throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM_ERROR);
			}
			//3.1  获取结果中的金额
			Long resultPay = Long.valueOf(totalFeeStr);
			//3.2  获取订单中的金额
			Long tradeId = Long.valueOf(tradeNo);
			Order order = orderMapper.selectByPrimaryKey(tradeId);
			if (/*order.getActualPay()*/1L != resultPay) {
				throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM_ERROR);
			}

            /*SUCCESS—支付成功

            REFUND—转入退款

            NOTPAY—未支付

            CLOSED—已关闭

            REVOKED—已撤销（刷卡支付）

            USERPAYING--用户支付中

            PAYERROR--支付失败(其他原因，如银行返回失败)
            */
			String state = result.get("trade_state");
			if (state.equals(SUCCESS)) {
				//支付成功
				//修改订单状态
				//4.修改订单状态
				OrderStatus orderStatus = new OrderStatus();
				orderStatus.setOrderId(tradeId);
				orderStatus.setStatus(OrderStatusEnum.PAYED.value());
				orderStatus.setPaymentTime(new Date());
				int count = statusMapper.updateByPrimaryKeySelective(orderStatus);
				if (count != 1) {
					throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
				}
				return PayState.SUCCESS;
			}

			if ("NOTPAY".equals(state) || "USERPAYING".equals(state)) {
				return PayState.NOT_PAY;
			}
			return PayState.FAIL;

		} catch (Exception e) {
			return PayState.NOT_PAY;
		}
	}
}
