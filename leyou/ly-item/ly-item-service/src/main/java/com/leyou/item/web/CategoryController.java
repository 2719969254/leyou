package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.server.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	@RequestMapping("list")
	public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid")Long pid){
		return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
	}

}
