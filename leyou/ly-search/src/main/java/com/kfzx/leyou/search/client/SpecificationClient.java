package com.kfzx.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
