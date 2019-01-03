package com.kfzx.leyou.search.mq;

import com.kfzx.leyou.search.service.SearchService;
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
	private final SearchService searchService;

	@Autowired
	public ItemListener(SearchService searchService) {
		this.searchService = searchService;
	}

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(name = "search.item.insert.queue", durable = "true"),
			exchange = @Exchange(name = "leyou.item.exchange", type = ExchangeTypes.TOPIC),
			key = {"item.insert", "item.update"}
	))
	public void listenInsertOrUpdate(Long spuId) {
		if (spuId == null) {
			return;
		}
		//处理消息，对索引库进行修改或增加
		searchService.createIndex(spuId);
	}

	/**
	 * 	监听消息删除
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(name = "search.item.delete.queue", durable = "true"),
			exchange = @Exchange(name = "leyou.item.exchange", type = ExchangeTypes.TOPIC),
			key = {"item.delete"}
	))
	public void listenDelete(Long spuId) {
		if (spuId == null) {
			return;
		}
		//处理消息，对索引库进行修改或增加
		searchService.deleteIndex(spuId);
	}
}
