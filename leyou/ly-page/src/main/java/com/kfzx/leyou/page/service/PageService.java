
package com.kfzx.leyou.page.service;

import com.kfzx.leyou.page.client.BrandClient;
import com.kfzx.leyou.page.client.CategoryClient;
import com.kfzx.leyou.page.client.GoodsClient;
import com.kfzx.leyou.page.client.SpecificationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class PageService {
	private final GoodsClient goodsClient;

	private final BrandClient brandClient;

	private final CategoryClient categoryClient;

	private final SpecificationClient specificationClient;

//	private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

	@Autowired
	public PageService(GoodsClient goodsClient, BrandClient brandClient, CategoryClient categoryClient, SpecificationClient specificationClient) {
		this.goodsClient = goodsClient;
		this.brandClient = brandClient;
		this.categoryClient = categoryClient;
		this.specificationClient = specificationClient;
	}

	/*public Map<String, Object> loadModel(Long spuId){

		try {
			// 查询spu
			Spu spu = this.goodsClient.querySpuById(spuId);

			// 查询spu详情
			SpuDetail spuDetail = spu.getSpuDetail();

			// 查询sku
			List<Sku> skus = spu.getSkus();

			// 查询品牌
			List<Brand> brands = brandClient.queryBrandByIds(Arrays.asList(spu.getBrandId()));

			// 查询分类
			List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

			// 查询组内参数
			List<SpecGroup> specGroups = this.specificationClient.querySpecsByCid(spu.getCid3());

			// 查询所有特有规格参数
			List<SpecParam> specParams = this.specificationClient.querySpecParam(null, spu.getCid3(), null, false);
			// 处理规格参数
			Map<Long, String> paramMap = new HashMap<>();
			specParams.forEach(param->{
				paramMap.put(param.getId(), param.getName());
			});

			Map<String, Object> map = new HashMap<>();
			map.put("spu", spu);
			map.put("spuDetail", spuDetail);
			map.put("skus", skus);
			map.put("brand", brands.get(0));
			map.put("categories", categories);
			map.put("groups", specGroups);
			map.put("params", paramMap);
			return map;
		} catch (Exception e) {
			logger.error("加载商品数据出错,spuId:{}", spuId, e);
		}
		return null;
	}

	private List<Category> getCategories(Spu spu) {
		try {
			List<String> names = this.categoryClient.queryNameByIds(
					Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
			Category c1 = new Category();
			c1.setName(names.get(0));
			c1.setId(spu.getCid1());

			Category c2 = new Category();
			c2.setName(names.get(1));
			c2.setId(spu.getCid2());

			Category c3 = new Category();
			c3.setName(names.get(2));
			c3.setId(spu.getCid3());

			return Arrays.asList(c1, c2, c3);
		} catch (Exception e) {
			logger.error("查询商品分类出错，spuId：{}", spu.getId(), e);
		}
		return null;
	}*/
}
