package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/2/5
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}