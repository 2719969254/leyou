package com.leyou.order.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/25
 */
@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
	private String pubKeyPath;
	/**
	 * 公钥
	 */
	private PublicKey publicKey;

	private String cookieName;

	/**
	 * 构造函数执行完毕执行   <bean id="" class="" init=""/>
	 */
	@PostConstruct
	private void init() throws Exception {
		//读取公钥和私钥
		this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
	}
}