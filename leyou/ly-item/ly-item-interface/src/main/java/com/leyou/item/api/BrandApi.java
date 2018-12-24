package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
public interface BrandApi {
	@GetMapping("brand/{id}")
	Brand queryBrandById(@PathVariable("id") Long id);
}
