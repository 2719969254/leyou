package com.leyou.item.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/15
 */
@RestController
@RequestMapping("item")
public class ItemController {
	private final ItemService itemService;

	@Autowired
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
	@PostMapping
	public ResponseEntity<Item> saveItem(Item item){
		//校验价格
		if (item.getPrice() == null) {
			throw new LyException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
		}
		Item saveItem = itemService.saveItem(item);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveItem);
	}

}
