package com.leyou.item.api;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
@FeignClient(value = "item-service")
public interface GoodsApi {
	/**
	 * 分页查询商品
	 * @param page
	 * @param rows
	 * @param saleable
	 * @param key
	 * @return
	 */
	@GetMapping("/spu/page")
	PageResult<Spu> querySpuByPage(
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "rows", defaultValue = "5") Integer rows,
			@RequestParam(value = "saleable", defaultValue = "true") Boolean saleable,
			@RequestParam(value = "key", required = false) String key);

	/**
	 * 根据spu商品id查询详情
	 * @param id
	 * @return
	 */
	@GetMapping("/spu/detail/{id}")
	SpuDetail querySpuDetailById(@PathVariable("id") Long id);

	/**
	 * 根据spu的id查询sku
	 * @param id
	 * @return
	 */
	@GetMapping("sku/list")
	List<Sku> querySkuBySpuId(@RequestParam("id") Long id);
	/**
	 * 根据spu的id查询spu
	 * @param id
	 * @return
	 */
	@GetMapping("spu/{id}")
	Spu querySpuById(@PathVariable("id") Long id);

	@GetMapping("sku/{id}")
	Sku querySkuById(@PathVariable("id")Long id);
	/**
	 * 根据skuId批量查询sku
	 * @param ids
	 * @return
	 */
	@GetMapping("sku/list/ids")
	List<Sku> querySkusBuSkuIds(@RequestParam("ids")List<Long> ids);

	/**
	 * 减库存
	 * @param cartDTOS
	 * @return
	 */
	@PutMapping("stock/decrease")
	Void decreaseStock(@RequestBody List<CartDTO>  cartDTOS);
}
