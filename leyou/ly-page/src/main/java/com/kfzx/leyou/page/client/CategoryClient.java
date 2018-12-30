package com.kfzx.leyou.page.client;

import com.leyou.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
@FeignClient("item-service")
public interface CategoryClient {
	/**
	 * 根据ids查询商品分类
	 * @param ids
	 * @return
	 */
	@GetMapping("category/list/ids")
	List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);
}
