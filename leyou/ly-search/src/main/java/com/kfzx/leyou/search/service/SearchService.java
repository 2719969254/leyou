package com.kfzx.leyou.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfzx.leyou.search.client.BrandClient;
import com.kfzx.leyou.search.client.CategoryClient;
import com.kfzx.leyou.search.client.GoodsClient;
import com.kfzx.leyou.search.client.SpecificationClient;
import com.kfzx.leyou.search.pojo.Goods;
import com.kfzx.leyou.search.pojo.SearchRequest;
import com.kfzx.leyou.search.pojo.SearchResult;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
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

	private final ElasticsearchTemplate elasticsearchTemplate;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public SearchService(CategoryClient categoryClient, GoodsClient goodsClient, BrandClient brandClient,
	                     SpecificationClient specificationClient, ElasticsearchTemplate elasticsearchTemplate) {
		this.categoryClient = categoryClient;
		this.goodsClient = goodsClient;
		this.brandClient = brandClient;
		this.specificationClient = specificationClient;
		this.elasticsearchTemplate = elasticsearchTemplate;
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
		String key = request.getKey();
		//判断是否有查询条件，如果没有直接返回null
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		int page = request.getPage() - 1;
		int size = request.getSize();
		//创建查询构建器
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		//结果过滤
		nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
		//分页
		nativeSearchQueryBuilder.withPageable(PageRequest.of(page, size));
		//搜索条件
		QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key);
		nativeSearchQueryBuilder.withQuery(basicQuery);

		//聚合分类和品牌信息
		//聚合分类
		String categoryAggName = "category_agg";
		nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
		//聚合品牌
		String brandAggName = "brand_agg";
		nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

		//查询
		//Page<Goods> search = goodsRepository.search(nativeSearchQueryBuilder.build());
		AggregatedPage<Goods> search = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
		//解析分页结果
		long totalElements = search.getTotalElements();
		long totalPages = search.getTotalPages();
		List<Goods> content = search.getContent();
		//解析聚合结果
		Aggregations aggregations = search.getAggregations();
		List<Category> categories = parseCategoryAgg(aggregations.get(categoryAggName));
		List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));

		//完成规格参数聚合
		List<Map<String, Object>> specs = new ArrayList<>(100);

		if (categories != null && categories.size() == 1) {
			//商品分类唯一，可以聚合规格参数
			specs = buildSpecificationAgg(categories.get(0).getId(), basicQuery);
		}

		return new SearchResult(totalElements, totalPages, content, categories, brands);

	}

	private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
		try {
			List<Map<String, Object>> specs = new ArrayList<>(100);
			//1先知道对谁聚合
			//1.1查询需要聚合的规格参数
			List<SpecParam> params = specificationClient.queryParam(null, cid, true);
			//2聚合
			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
			//2.1带上查询条件
			nativeSearchQueryBuilder.withQuery(basicQuery);
			//2.2聚合
			for (SpecParam param : params) {
				String name = param.getName();
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
			}
			//获取结果
			AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
		/*Map<String, Aggregation> aggs = this.elasticsearchTemplate.query(nativeSearchQueryBuilder.build(),
				SearchResponse::getAggregations).asMap();*/

			//解析结果
			Aggregations aggregations = result.getAggregations();
			for (SpecParam param : params) {
				//规格参数名
				String name = param.getName();
				StringTerms terms = aggregations.get(name);
				//准备map
				Map<String, Object> map = new HashMap<>(100);
				map.put("k", name);
				map.put("options", terms.getBuckets()
						.stream().map(b -> b.getKeyAsString()).collect(Collectors.toList()));

				specs.add(map);
			}

			// 解析聚合结果
		/*params.forEach(param -> {
			Map<String, Object> spec = new HashMap<>();
			String key = param.getName();
			spec.put("k", key);
			StringTerms terms = (StringTerms) aggs.get(key);
			spec.put("options", terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString));
			specs.add(spec);
		});*/
			return specs;

		} catch (Exception e) {
			return null;
		}
	}

	private List<Brand> parseBrandAgg(LongTerms terms) {
		try {
			List<Long> ids = terms.getBuckets()
					.stream().map(b -> b.getKeyAsNumber().longValue())
					.collect(Collectors.toList());
			return brandClient.queryBrandByIds(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Category> parseCategoryAgg(LongTerms terms) {
		try {
			List<Long> ids = terms.getBuckets()
					.stream().map(b -> b.getKeyAsNumber().longValue())
					.collect(Collectors.toList());
			return categoryClient.queryCategoryByIds(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
