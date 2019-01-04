package com.kfzx.leyou.sms.mq;

import com.kfzx.leyou.sms.config.SmsProperties;
import com.kfzx.leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/2
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {
	private final SmsUtils smsUtils;
	private final SmsProperties smsProperties;

	@Autowired
	public SmsListener(SmsUtils smsUtils, SmsProperties smsProperties) {
		this.smsUtils = smsUtils;
		this.smsProperties = smsProperties;
	}

	/**
	 * 发送短信验证码
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(name = "sms.verify.code.queue", durable = "true"),
			exchange = @Exchange(name = "leyou.sms.exchange", type = ExchangeTypes.TOPIC),
			key = {"sms.verify.code"}
	))
	public void listenInsertOrUpdate(Map<String, String> msg) {
		if (CollectionUtils.isEmpty(msg)) {
			return;
		}
		String phone = msg.get("phone");
		if (StringUtils.isBlank(phone)) {
			return;
		}
		//处理消息，对索引库进行修改或增加
		String code = smsUtils.sendCode(phone, smsProperties.getSignName());
		log.info("【短信服务】，发送短信验证码，手机号：{}，验证码：{}", phone, code);
	}

}
