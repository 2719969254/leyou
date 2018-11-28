package com.leyou.item.service;

import com.leyou.item.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/15
 */
@Service
public class ItemService {
	public Item saveItem(Item item){
		int id = new Random().nextInt(100);
		item.setId(id);
		return item;
	}
}
