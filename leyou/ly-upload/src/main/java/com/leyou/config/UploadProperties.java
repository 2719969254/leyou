package com.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 上传属性
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/23
 */
@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {
	private String baseUrl;
	private List<String> allowTypes;

}
