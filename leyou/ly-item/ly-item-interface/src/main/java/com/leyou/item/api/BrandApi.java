package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
public interface BrandApi {
	@GetMapping("brand/{id}")
	Brand queryBrandById(@PathVariable("id") Long id);
	@GetMapping("brand/list")
	List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);

}
