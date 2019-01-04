package com.kfzx.leyou.sms.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsListenerTest {
	@Autowired
	private AmqpTemplate amqpTemplate;

	@Test
	public void listenInsertOrUpdate() {
		Map<String, String> msg = new HashMap<>(1);
		msg.put("phone", "15364882057");
		amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);
	}
}