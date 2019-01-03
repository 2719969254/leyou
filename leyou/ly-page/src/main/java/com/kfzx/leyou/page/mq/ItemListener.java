package com.kfzx.leyou.page.mq;

import com.kfzx.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/2
 */
@Component
public class ItemListener {
	private final PageService pageService;

	@Autowired
	public ItemListener(PageService searchService) {
		this.pageService = searchService;
	}

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(name = "page.item.insert.queue", durable = "true"),
			exchange = @Exchange(name = "leyou.item.exchange", type = ExchangeTypes.TOPIC),
			key = {"item.insert", "item.update"}
	))
	public void listenInsertOrUpdate(Long spuId) {
		if (spuId == null) {
			return;
		}
		//处理消息，重新创建html静态页面
		pageService.createHtml(spuId);
	}

	/**
	 * 	监听消息删除
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(name = "page.item.delete.queue", durable = "true"),
			exchange = @Exchange(name = "leyou.item.exchange", type = ExchangeTypes.TOPIC),
			key = {"item.delete"}
	))
	public void listenDelete(Long spuId) {
		if (spuId == null) {
			return;
		}
		//处理消息，对静态页面进行删除
		pageService.deleteHtml(spuId);
	}
}
