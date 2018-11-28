package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/18
 */
@Service
public class CategoryService {
	private final CategoryMapper categoryMapper;

	@Autowired
	public CategoryService(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}

	/**
	 * 根据父节点id查询分类管理
	 * @param pid 父节点id
	 * @return 分类管理
	 */
	public List<Category> queryCategoryListByPid(Long pid){
		//查询条件，mapper会把对象的非空属性作为查询条件
		Category category = new Category();
		category.setParentId(pid);
		List<Category> list = categoryMapper.select(category);
		//判断查询结果
		if (CollectionUtils.isEmpty(list)) {
			throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
		}
		return list;
	}

}
