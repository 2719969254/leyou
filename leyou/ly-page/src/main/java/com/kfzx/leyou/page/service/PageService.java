
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	private final TemplateEngine templateEngine;

	@Autowired
	public PageService(GoodsClient goodsClient, BrandClient brandClient, CategoryClient categoryClient, SpecificationClient specificationClient, TemplateEngine templateEngine) {
		this.goodsClient = goodsClient;
		this.brandClient = brandClient;
		this.categoryClient = categoryClient;
		this.specificationClient = specificationClient;
		this.templateEngine = templateEngine;
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

	public void createHtml(Long spuId) {
		// 上下文
		Context context = new Context();
		context.setVariables(loadModel(spuId));
		// 输出流
		File file = new File("F:\\IDEA\\leyou\\static", spuId + ".html");
		// 判断文件是否存在，如果已经存在就先删除
		if (file.exists()){
			file.delete();
		}

		try {
			PrintWriter writer = new PrintWriter(file,"UTF-8");
			// 生成HTML
			templateEngine.process("item",context,writer);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error("静态页面服务，生成静态页面异常",e);
		}

	}

	public void deleteHtml(Long spuId) {
		// 输出流
		File file = new File("F:\\IDEA\\leyou\\static", spuId + ".html");
		// 判断文件是否存在，如果已经存在就先删除
		if (file.exists()){
			file.delete();
		}
	}
}
