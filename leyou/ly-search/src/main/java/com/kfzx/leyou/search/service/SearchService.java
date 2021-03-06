package com.kfzx.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfzx.leyou.search.client.BrandClient;
import com.kfzx.leyou.search.client.CategoryClient;
import com.kfzx.leyou.search.client.GoodsClient;
import com.kfzx.leyou.search.client.SpecificationClient;
import com.kfzx.leyou.search.pojo.Goods;
import com.kfzx.leyou.search.pojo.SearchRequest;
import com.kfzx.leyou.search.pojo.SearchResult;
import com.kfzx.leyou.search.repository.GoodsRepository;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
	private final GoodsRepository goodsRepository;

	@Autowired
	public SearchService(CategoryClient categoryClient, GoodsClient goodsClient, BrandClient brandClient,
	                     SpecificationClient specificationClient, ElasticsearchTemplate elasticsearchTemplate, GoodsRepository goodsRepository) {
		this.categoryClient = categoryClient;
		this.goodsClient = goodsClient;
		this.brandClient = brandClient;
		this.specificationClient = specificationClient;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.goodsRepository = goodsRepository;
	}


	public Goods buildGoods(Spu spu) {
		Long spuId = spu.getId();
		// 查询分类
		List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
		if (CollectionUtils.isEmpty(categories)) {
			throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
		}
		List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

		// 查询品牌
		Brand brand = brandClient.queryBrandById(spu.getBrandId());
		if (brand == null) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		// 搜索字段
		String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

		// 查询sku
		List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
		if (CollectionUtils.isEmpty(skuList)) {
			System.out.println("___________________________________________"+spu.getId());
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

		// 查询规格参数
		List<SpecParam> params = specificationClient.queryParam(null, spu.getCid3(), true);
		if (CollectionUtils.isEmpty(params)) {
			throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
		}
		// 查询商品详情
		SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);
		// 获取通用规格
		Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
		// 获取特有规格
		Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
		});

		// 规格参数，key是规格参数的名字，值是规格参数的值
		Map<String, Object> specs = new HashMap<>(100);
		for (SpecParam param : params) {
			// 规格名称
			String key = param.getName();
			Object value = "";
			// 判断是否是通用规格参数
			if (param.getGeneric()) {
				assert genericSpec != null;
				value = genericSpec.get(param.getId());
				//判断是否为数值类型
				if (param.getNumeric()) {
					// 分段
					value = chooseSegment(value.toString(), param);
				}
			} else {
				assert specialSpec != null;
				value = specialSpec.get(param.getId());
			}
			// 存入map
			specs.put(key, value);
		}


		// 规格参数
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
		try {
			goods.setSkus(mapper.writeValueAsString(skus));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		goods.setSpecs(specs);
		return goods;
	}


	private String chooseSegment(String value, SpecParam p) {
		double val = NumberUtils.toDouble(value);
		String result = "其它";
		// 保存数值段
		for (String segment : p.getSegments().split(",")) {
			String[] segs = segment.split("-");
			// 获取数值范围
			double begin = NumberUtils.toDouble(segs[0]);
			double end = Double.MAX_VALUE;
			if (segs.length == 2) {
				end = NumberUtils.toDouble(segs[1]);
			}
			// 判断是否在范围内
			if (val >= begin && val < end) {
				if (segs.length == 1) {
					result = segs[0] + p.getUnit() + "以上";
				} else if (begin == 0) {
					result = segs[1] + p.getUnit() + "以下";
				} else {
					result = segment + p.getUnit();
				}
				break;
			}
		}
		return result;
	}

	public PageResult<Goods> search(SearchRequest request) {
		String key = request.getKey();
		// 判断是否有查询条件，如果没有直接返回null
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		int page = request.getPage() - 1;
		int size = request.getSize();
		// 创建查询构建器
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		// 结果过滤
		nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
		// 分页
		nativeSearchQueryBuilder.withPageable(PageRequest.of(page, size));
		// 搜索条件
		QueryBuilder basicQuery = buildBasicQueryWithFilter(request);
		nativeSearchQueryBuilder.withQuery(basicQuery);

		// 聚合分类和品牌信息
		// 聚合分类
		String categoryAggName = "category_agg";
		nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
		// 聚合品牌
		String brandAggName = "brand_agg";
		nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

		// 查询
		//Page<Goods> search = goodsRepository.search(nativeSearchQueryBuilder.build());
		AggregatedPage<Goods> search = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
		// 解析分页结果
		long totalElements = search.getTotalElements();
		long totalPages = search.getTotalPages();
		List<Goods> content = search.getContent();
		// 解析聚合结果
		Aggregations aggregations = search.getAggregations();
		List<Category> categories = parseCategoryAgg(aggregations.get(categoryAggName));
		List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));

		// 完成规格参数聚合
		List<Map<String, Object>> specs = new ArrayList<>(100);

		if (categories != null && categories.size() == 1) {
			// 商品分类唯一，可以聚合规格参数
			specs = buildSpecificationAgg(categories.get(0).getId(), basicQuery);
		}

		return new SearchResult(totalElements, totalPages, content, categories, brands,specs);

	}

	private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
		try {
			List<Map<String, Object>> specs = new ArrayList<>(100);
			// 1先知道对谁聚合
			// 1.1查询需要聚合的规格参数
			List<SpecParam> params = specificationClient.queryParam(null, cid, true);
			// 2聚合
			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
			// 2.1带上查询条件
			nativeSearchQueryBuilder.withQuery(basicQuery);
			//2.2聚合
			for (SpecParam param : params) {
				String name = param.getName();
				nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
			}
			// 获取结果
			AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
		/*Map<String, Aggregation> aggs = this.elasticsearchTemplate.query(nativeSearchQueryBuilder.build(),
				SearchResponse::getAggregations).asMap();*/

			// 解析结果
			Aggregations aggregations = result.getAggregations();
			for (SpecParam param : params) {
				// 规格参数名
				String name = param.getName();
				StringTerms terms = aggregations.get(name);
				// 准备map
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


	/**
	 * 	构建基本查询条件
	 */
	private QueryBuilder buildBasicQueryWithFilter(SearchRequest request) {
		// 创建布尔
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		// 基本查询条件
		queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
		// 整理过滤条件
		Map<String, String> map = request.getFilter();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			// 商品分类和品牌要特殊处理
			if (!"cid3".equals(key) && !"brandId".equals(key)) {
				key = "specs." + key + ".keyword";
			}
			// 字符串类型，进行term查询
			queryBuilder.filter(QueryBuilders.termQuery(key, value));
		}
		// 添加过滤条件
		//queryBuilder.filter(filterQueryBuilder);
		return queryBuilder;
	}


	public void createIndex(Long spuId) {
		// 查询spu
		Spu spu = goodsClient.querySpuById(spuId);
		// 构建goods对象
		Goods goods = buildGoods(spu);
		// 存入索引库
		goodsRepository.save(goods);

	}

	public void deleteIndex(Long spuId) {
		goodsRepository.deleteById(spuId);
	}
}
