
package com.kfzx.leyou.page.service;

import com.kfzx.leyou.page.client.BrandClient;
import com.kfzx.leyou.page.client.CategoryClient;
import com.kfzx.leyou.page.client.GoodsClient;
import com.kfzx.leyou.page.client.SpecificationClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MR.Tian
 */
@Slf4j
@Service
public class PageService {
	private final GoodsClient goodsClient;

	private final BrandClient brandClient;

	private final CategoryClient categoryClient;

	private final SpecificationClient specificationClient;

	@Autowired
	public PageService(GoodsClient goodsClient, BrandClient brandClient, CategoryClient categoryClient, SpecificationClient specificationClient) {
		this.goodsClient = goodsClient;
		this.brandClient = brandClient;
		this.categoryClient = categoryClient;
		this.specificationClient = specificationClient;
	}

	public Map<String, Object> loadModel(Long spuId) {
		Map<String, Object> model = new HashMap<>(30);
		Spu spu = goodsClient.querySpuById(spuId);
		// 查询商品是否上架
		if (!spu.getSaleable()) {
			throw new LyException(ExceptionEnum.GOODS_NOT_SALEABLE);
		}

		SpuDetail detail = spu.getSpuDetail();
		List<Sku> skus = spu.getSkus();
		Brand brand = brandClient.queryBrandById(spu.getBrandId());
		// 查询三级分类
		List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
		// 查询规格组
		List<SpecGroup> specs = specificationClient.queryGroupByCid(spu.getCid3());

		model.put("brand", brand);
		model.put("categories", categories);
		model.put("spu", spu);
		model.put("skus", skus);
		model.put("detail", detail);
		model.put("specs", specs);
		return model;
	}
}
