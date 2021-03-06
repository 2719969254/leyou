package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/18
 */
@RestController
@RequestMapping("category")
public class CategoryController {
	private final CategoryService categoryService;

	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	/**
	 * 根据父节点查询商品分类
	 */
	@RequestMapping("list")
	public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid")Long pid){
		return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
	}

	@GetMapping("list/ids")
	public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids){
		return ResponseEntity.ok(categoryService.queryByIds(ids));
	}
	@GetMapping("all/level")
	public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id){
		List<Category> list = categoryService.queryAllByCid3(id);
		if (list == null || list.size() < 1) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(list);
	}
}
