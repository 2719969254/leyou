package com.leyou.getway.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
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
public class JWTProperties {
	private String pubKeyPath;
	private String cookieName;
	private PublicKey publicKey;

	@PostConstruct
	public void init() throws Exception {
		// 读取公钥和私钥
		this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
	}
}
