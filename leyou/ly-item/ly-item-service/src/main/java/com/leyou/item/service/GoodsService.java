package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
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
	private final AmqpTemplate amqpTemplate;

	@Autowired
	public GoodsService(SpuDetailMapper spuDetailMapper, SpuMapper spuMapper,
	                    CategoryService categoryService, BrandService brandService,
	                    StockMapper stockMapper, SkuMapper skuMapper,
	                    AmqpTemplate amqpTemplate) {
		this.spuDetailMapper = spuDetailMapper;
		this.spuMapper = spuMapper;
		this.categoryService = categoryService;
		this.brandService = brandService;
		this.stockMapper = stockMapper;
		this.skuMapper = skuMapper;
		this.amqpTemplate = amqpTemplate;
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
		spu.setCreateTime(new Date());
		spu.setId(spu.getId());
		spu.setLastUpdateTime(spu.getCreateTime());
		spu.setSaleable(true);
		spu.setValid(false);
		int insert = spuMapper.insert(spu);
		if (insert != 1) {
			throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
		}
		//新增spuDetail
		SpuDetail spuDetail = spu.getSpuDetail();
		spuDetail.setSpuId(spu.getId());
		spuDetailMapper.insert(spuDetail);
		// 新增sku和库存
		saveSkuAndStock(spu);

		//发送mq消息
		amqpTemplate.convertAndSend("item.insert", spu.getId());

	}

	private void saveSkuAndStock(Spu spu) {
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
		int count = stockMapper.insertList(stocks);
		if (count != stocks.size()) {
			throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
		}

	}

	public SpuDetail querySpuDetailById(Long id) {
		SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
		if (spuDetail == null) {
			throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
		}
		return spuDetail;
	}

	public List<Sku> querySkuBySpuId(Long supId) {
		//查询sku
		Sku record = new Sku();
		record.setSpuId(supId);
		List<Sku> skuList = skuMapper.select(record);
		if (CollectionUtils.isEmpty(skuList)) {
			throw new LyException(ExceptionEnum.GOOD_SPU_NOT_FOUND);
		}
		//查询库存
		/*for (Sku sku : skuList) {
			Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
			if (stock == null) {
				throw new LyException(ExceptionEnum.GOOD_STOCK_NOT_FOUND);
			}
			sku.setStock(stock.getStock());
		}*/
		loadStockInSku(skuList);
		return skuList;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateGoods(Spu spu) {
		Sku sku = new Sku();
		sku.setSpuId(spu.getId());
		//查询sku
		List<Sku> skuList = skuMapper.select(sku);
		if (!CollectionUtils.isEmpty(skuList)) {
			skuMapper.delete(sku);
			List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
			stockMapper.deleteByIdList(ids);
		}
		//修改spu
		spu.setId(spu.getId());
		spu.setValid(null);
		spu.setSaleable(null);
		spu.setLastUpdateTime(null);
		spu.setCreateTime(null);
		int count = spuMapper.updateByPrimaryKeySelective(spu);
		if (count != 1) {
			throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
		}
		count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
		if (count != 1) {
			throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
		}

		saveSkuAndStock(spu);

		//发送mq消息
		amqpTemplate.convertAndSend("item.update", spu.getId());

	}

	public Spu querySpuById(Long id) {
		//查询spu
		Spu spu = spuMapper.selectByPrimaryKey(id);
		if (spu == null) {
			throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
		}
		//查询sku
		spu.setSkus(querySkuBySpuId(id));

		//查询Detail
		spu.setSpuDetail(querySpuDetailById(id));
		return spu;
	}

	public List<Sku> querySkuByIds(List<Long> ids) {
		List<Sku> skuList = skuMapper.selectByIdList(ids);
		if (CollectionUtils.isEmpty(skuList)) {
			throw new LyException(ExceptionEnum.GOOD_SKU_NOT_FOUND);
		}

		loadStockInSku(skuList);
		return skuList;
	}

	/**
	 * 查询集合里的库存
	 *
	 * @param skuList
	 */
	private void loadStockInSku(List<Sku> skuList) {
		List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
		List<Stock> stockList = stockMapper.selectByIdList(ids);
		if (CollectionUtils.isEmpty(stockList)) {
			throw new LyException(ExceptionEnum.GOOD_STOCK_NOT_FOUND);
		}
		//我们把stock变成一个map，其key是sku的id，值是库存值
		Map<Long, Long> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
		skuList.forEach(sku -> sku.setStock(stockMap.get(sku.getId())));
	}

	public Sku querySkuById(Long id) {
		return this.skuMapper.selectByPrimaryKey(id);
	}
	public List<Sku> querySkusBuSkuIds(List<Long> ids) {
		List<Sku> skus = skuMapper.selectByIdList(ids);
		if(CollectionUtils.isEmpty(skus)){
			throw new LyException(ExceptionEnum.GOOD_SKU_NOT_FOUND);
		}
		return skus;
	}

	@Transactional
	public Void decreaseStock(List<CartDTO> cartDTOS) {
		for (CartDTO cartDTO : cartDTOS) {
			int count = stockMapper.decreaseStock(cartDTO.getSkuId(), cartDTO.getNum());
			if(count != 1){
				throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
			}
		}
		return null;
	}
}
