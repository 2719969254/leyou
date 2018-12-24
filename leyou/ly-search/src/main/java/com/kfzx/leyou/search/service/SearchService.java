package com.kfzx.leyou.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfzx.leyou.search.client.BrandClient;
import com.kfzx.leyou.search.client.CategoryClient;
import com.kfzx.leyou.search.client.GoodsClient;
import com.kfzx.leyou.search.client.SpecificationClient;
import com.kfzx.leyou.search.pojo.Goods;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
@Service
public class SearchService {
	@Autowired
	private CategoryClient categoryClient;
	@Autowired
	private GoodsClient goodsClient;
	@Autowired
	private BrandClient brandClient;

	@Autowired
	private SpecificationClient specificationClient;

	private ObjectMapper mapper = new ObjectMapper();

	public Goods buildGoods(Spu spu) throws IOException {
		Long spuId = spu.getId();
		//查询分类
		List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
		if (CollectionUtils.isEmpty(categories)){
			throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
		}
		List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

		//查询品牌
		Brand brand = brandClient.queryBrandById(spu.getBrandId());
		if (brand == null) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		//搜索字段
		String all = spu.getTitle()+StringUtils.join(names," ")+brand.getName();

		//查询sku
		List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
		if (CollectionUtils.isEmpty(skuList)) {
			throw new LyException(ExceptionEnum.GOOD_SKU_NOT_FOUND);
		}


		// 处理sku，仅封装id、价格、标题、图片，并获得价格集合
		Set<Long> priceList = new HashSet<>();
		List<Map<String, Object>> skus = new ArrayList<>();
		for (Sku sku : skuList) {
			Map<String, Object> skuMap = new HashMap<>();
			skuMap.put("id", sku.getId());
			skuMap.put("title", sku.getTitle());
			skuMap.put("price", sku.getPrice());
			skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
			skus.add(skuMap);
			priceList.add(sku.getPrice());
		}

		//查询规格参数
		List<SpecParam> params = specificationClient.queryParam(null, spu.getCid3(), true);
		if (CollectionUtils.isEmpty(params)) {
			throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
		}
		//查询商品详情
		SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);
		//获取通用规格
		//获取特有规格
		//规格参数


		Goods goods = new Goods();
		goods.setId(spu.getId());
		goods.setSubTitle(spu.getSubTitle());
		goods.setBrandId(spu.getBrandId());
		goods.setCid1(spu.getCid1());
		goods.setCid2(spu.getCid2());
		goods.setCid3(spu.getCid3());
		goods.setCreateTime(spu.getCreateTime());
		goods.setAll(all);
		goods.setPrice(priceList);
		goods.setSkus(mapper.writeValueAsString(skus));
		return goods;
	}


}
