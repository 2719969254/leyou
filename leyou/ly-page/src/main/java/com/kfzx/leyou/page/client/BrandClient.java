package com.kfzx.leyou.page.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
