package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/19
 */
@Service
public class BrandService {
	private final BrandMapper brandMapper;

	@Autowired
	public BrandService(BrandMapper brandMapper) {
		this.brandMapper = brandMapper;
	}

	/**
	 * 分页查询品牌
	 *
	 * @param page
	 * @param rows
	 * @param sortBy
	 * @param desc
	 * @param key
	 * @return
	 */
	public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
		// 分页
		PageHelper.startPage(page, rows);
		// 过滤
		Example example = new Example(Brand.class);
		if (!StringUtils.isEmpty(key)) {
			// 过滤条件
			example.createCriteria().orLike("name", "%" + key + "%").orEqualTo("letter", key.toUpperCase());
		}
		// 排序
		if (!StringUtils.isEmpty(sortBy)) {
			example.setOrderByClause(sortBy + (desc ? " DESC" : " ASC"));
		}
		List<Brand> list = brandMapper.selectByExample(example);
		if (CollectionUtils.isEmpty(list)) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		// 解析分页结果
		PageInfo<Brand> pageInfo = new PageInfo<>(list);
		return new PageResult<>(pageInfo.getTotal(), list);
	}

	@Transactional(rollbackFor = LyException.class)
	public void saveBrand(Brand brand, List<Long> cids) {
		// 先将品牌id设置为null
		brand.setId(null);
		// 新增品牌
		int insert = brandMapper.insert(brand);
		if (insert != 1) {

			throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
		}
		// 新增中间表
		for (Long cid : cids) {
			int i = this.brandMapper.insertCategoryBrand(cid, brand.getId());
			if (i != 1) {
				throw new LyException(ExceptionEnum.CATEGORY_BRAND_SAVE_ERROR);
			}
		}
	}

	public Brand queryById(Long id) {
		Brand brand = brandMapper.selectByPrimaryKey(id);
		if (brand == null) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		return brand;
	}

	public List<Brand> queryBrandByCid(Long cid) {
		List<Brand> brands = brandMapper.queryBrandByCid(cid);
		if (CollectionUtils.isEmpty(brands)) {
			throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
		}
		return brands;
	}

	public List<Brand> queryByIds(List<Long> ids) {
		return brandMapper.selectByIdList(ids);
	}
}
