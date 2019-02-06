package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ly.worker")
public class IdWorkProperties {
	/**
	 * 当前机器id
	 */
	private long workId;
	/**
	 * 序列号
	 */
	private long dataCenterId;
}
