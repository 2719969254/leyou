package com.kfzx.leyou.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfzx.leyou.search.client.BrandClient;
import com.kfzx.leyou.search.client.CategoryClient;
import com.kfzx.leyou.search.client.GoodsClient;
import com.kfzx.leyou.search.client.SpecificationClient;
import com.kfzx.leyou.search.pojo.Goods;
import com.kfzx.leyou.search.pojo.SearchRequest;
import com.kfzx.leyou.search.repository.GoodsRepository;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
	private final CategoryClient categoryClient;
	private final GoodsClient goodsClient;
	private final BrandClient brandClient;

	private final SpecificationClient specificationClient;

	private final GoodsRepository goodsRepository;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public SearchService(CategoryClient categoryClient, GoodsClient goodsClient, BrandClient brandClient, SpecificationClient specificationClient, GoodsRepository goodsRepository) {
		this.categoryClient = categoryClient;
		this.goodsClient = goodsClient;
		this.brandClient = brandClient;
		this.specificationClient = specificationClient;
		this.goodsRepository = goodsRepository;
	}

	public Goods buildGoods(Spu spu) throws IOException {
		Long spuId = spu.getId();
		//查询分类
		List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
		if (CollectionUtils.isEmpty(categories)) {
			throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
		}
		List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

		//查询品牌
		Brand brand = brandClient.queryBrandById(spu.getBrandId());
		if (brand == null) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		//搜索字段
		String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

		//查询sku
		List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
		if (CollectionUtils.isEmpty(skuList)) {
			throw new LyException(ExceptionEnum.GOOD_SKU_NOT_FOUND);
		}


		// 处理sku，仅封装id、价格、标题、图片，并获得价格集合
		Set<Long> priceList = new HashSet<>();
		List<Map<String, Object>> skus = new ArrayList<>();
		for (Sku sku : skuList) {
			Map<String, Object> skuMap = new HashMap<>(100);
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
		goodsClient.querySpuDetailById(spuId);
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

	public PageResult<Goods> search(SearchRequest request) {
		int page = request.getPage() - 1;
		int size = request.getSize();
		//创建查询构建器
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		//结果过滤
		nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
		//分页
		nativeSearchQueryBuilder.withPageable(PageRequest.of(page, size));
		//过滤
		nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("all", request.getKey()));

		//查询
		Page<Goods> search = goodsRepository.search(nativeSearchQueryBuilder.build());
		//解析结果
		long totalElements = search.getTotalElements();
		long totalPages = search.getTotalPages();
		List<Goods> content = search.getContent();

		return new PageResult<>(totalElements, totalPages, content);

	}
}
