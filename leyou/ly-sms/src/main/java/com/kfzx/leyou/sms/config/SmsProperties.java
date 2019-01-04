package com.kfzx.leyou.sms.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
@ConfigurationProperties(prefix = "ly.sms")
@Validated
public class SmsProperties {
	private String queryPath;
	private String accountSid;
	private String authToken;
	private String signName;
	private String keyPerfix;
}
