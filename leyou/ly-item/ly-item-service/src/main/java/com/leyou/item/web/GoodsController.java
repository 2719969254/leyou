package com.leyou.item.web;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/28
 */
@RestController
public class GoodsController {
	private final GoodsService goodsService;

	@Autowired
	public GoodsController(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	@GetMapping("/spu/page")
	public ResponseEntity<PageResult<Spu>> querySpuByPage(
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "rows", defaultValue = "5") Integer rows,
			@RequestParam(value = "saleable", required = false) Boolean saleable,
			@RequestParam(value = "key", required = false) String key) {
		return ResponseEntity.ok(goodsService.querySpuByPageAndSort(page, rows, saleable, key));
	}

	/**
	 * 商品新增
	 *
	 * @param spu
	 * @return
	 */
	@PostMapping("/goods")
	public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
		goodsService.saveGoods(spu);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 根据spu的id查询详情
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/spu/detail/{id}")
	public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id) {
		SpuDetail spuDetail = goodsService.querySpuDetailById(id);
		if (spuDetail == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(spuDetail);
	}

	/**
	 * 根据spu查询下面所有sku
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("sku/list")
	public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id) {
		List<Sku> skuList = goodsService.querySkuBySpuId(id);
		if (skuList == null || skuList.size() == 0) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		return ResponseEntity.ok(skuList);
	}


	/**
	 * 根据sku的id集合查询所有sku
	 *
	 * @param ids
	 * @return
	 */
	@GetMapping("sku/list/ids")
	public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("ids") List<Long> ids) {
		List<Sku> skuList = goodsService.querySkuByIds(ids);
		if (skuList == null || skuList.size() == 0) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		return ResponseEntity.ok(skuList);
	}


	/**
	 * 新增商品
	 *
	 * @param spu
	 * @return
	 */
	@PutMapping
	public ResponseEntity<Void> updateGoods(@RequestBody Spu spu) {
		try {
			this.goodsService.updateGoods(spu);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@GetMapping("spu/{id}")
	public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
		Spu spu = this.goodsService.querySpuById(id);
		if (spu == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(spu);
	}

	@GetMapping("sku/{id}")
	public ResponseEntity<Sku> querySkuById(@PathVariable("id")Long id){
		Sku sku = this.goodsService.querySkuById(id);
		if (sku == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(sku);
	}


	/**
	 * 减库存
	 * @param cartDTOS
	 * @return
	 */
	@PutMapping("stock/decrease")
	public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO>  cartDTOS){
		return ResponseEntity.ok(goodsService.decreaseStock(cartDTOS));
	}
}
