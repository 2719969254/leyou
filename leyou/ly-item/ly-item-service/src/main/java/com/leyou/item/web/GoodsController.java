package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
			@RequestParam(value = "page",defaultValue = "1")Integer page,
			@RequestParam(value = "rows", defaultValue = "5") Integer rows,
			@RequestParam(value = "saleable", required = false) Boolean saleable,
			@RequestParam(value = "key", required = false) String key) {
		return ResponseEntity.ok(goodsService.querySpuByPageAndSort(page, rows, saleable, key));
	}
	@PostMapping("/goods")
	public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
		goodsService.saveGoods(spu);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
