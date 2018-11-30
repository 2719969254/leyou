package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/28
 */
@Service
public class GoodsService {
	private final SpuMapper spuMapper;
	private final SpuDetailMapper spuDetailMapper;
	private final CategoryService categoryService;
	private final BrandService brandService;
	private final StockMapper stockMapper;
	private final SkuMapper skuMapper;
	@Autowired
	public GoodsService(SpuDetailMapper spuDetailMapper, SpuMapper spuMapper, CategoryService categoryService, BrandService brandService, StockMapper stockMapper, SkuMapper skuMapper) {
		this.spuDetailMapper = spuDetailMapper;
		this.spuMapper = spuMapper;
		this.categoryService = categoryService;
		this.brandService = brandService;
		this.stockMapper = stockMapper;
		this.skuMapper = skuMapper;
	}

	public PageResult<Spu> querySpuByPageAndSort(Integer page, Integer rows, Boolean saleable, String key) {
		//分页
		PageHelper.startPage(page, rows);
		//过滤
		Example example = new Example(Spu.class);
		Example.Criteria criteria = example.createCriteria();
		//搜索字段过滤
		if (StringUtils.isNotBlank(key)) {
			criteria.andLike("title", "%" + key + "%");
		}
		//上下架过滤
		if (saleable != null) {
			criteria.andEqualTo("saleable", saleable);
		}
		//默认排序
		example.setOrderByClause("last_update_time DESC");
		//查询
		List<Spu> spus = spuMapper.selectByExample(example);
		if (CollectionUtils.isEmpty(spus)) {
			throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
		}
		//解析分类和品牌的名称
		loadCategoryAndBrandName(spus);
		//解析分页结果
		PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);
		return new PageResult<>(spuPageInfo.getTotal(), spus);
	}

	private void loadCategoryAndBrandName(List<Spu> spus) {
		for (Spu spu : spus) {
			//处理分类名称
			List<String> collect = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
					.stream().map(Category::getName).collect(Collectors.toList());
			spu.setCname(StringUtils.join(collect, "/"));
			//处理品牌名称
			spu.setBname(brandService.queryById(spu.getBrandId()).getName());
		}
	}
	@Transactional(rollbackFor = LyException.class)
	public void saveGoods(Spu spu) {
		//新增spu
		spu.setId(null);
		spu.setCreateTime(new Date());
		spu.setLastUpdateTime(spu.getCreateTime());
		spu.setSaleable(true);
		spu.setValid(false);
		int insert = spuMapper.insert(spu);
		if (insert != 1) {
			throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
		}
		//新增spudetil
		SpuDetail spuDetail = spu.getSpuDetail();
		spuDetail.setSpuId(spu.getId());
		spuDetailMapper.insert(spuDetail);

		//定义库存集合
		List<Stock> stocks = new ArrayList<>();


		//新增sku
		List<Sku> skus = spu.getSkus();
		for (Sku sku : skus) {
			sku.setCreateTime(new Date());
			sku.setLastUpdateTime(new Date());
			sku.setSpuId(spu.getId());
			int insert1 = skuMapper.insert(sku);
			if (insert1 != 1) {
				throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
			}

			//新增库存
			Stock stock = new Stock();
			stock.setSkuId(sku.getId());
			stock.setStock(sku.getStock());
			stocks.add(stock);
		}
		//批量新增库存
		stockMapper.insertList(stocks);


	}
}
