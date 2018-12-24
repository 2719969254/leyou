package com.kfzx.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "item-service")
@RequestMapping("/goods")
public interface GoodsClient extends GoodsApi {


}