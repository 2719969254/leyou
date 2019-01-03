package com.kfzx.leyou.sms.mq;

import com.kfzx.leyou.sms.utils.SmsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsListenerTest {
	@Autowired
	private AmqpTemplate amqpTemplate;
	@Autowired
	private SmsUtils smsUtils;
	@Test
	public void listenInsertOrUpdate() {
		Map<String,String> msg = new HashMap<>(1);
		msg.put("phone","15364882057");
		amqpTemplate.convertAndSend("leyou.sms.exchange","sms.verify.code",msg);
	}
}