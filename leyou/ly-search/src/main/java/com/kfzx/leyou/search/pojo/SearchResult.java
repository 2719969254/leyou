package com.kfzx.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult extends PageResult<Goods> {

    private List<Category> categories;

    private List<Brand> brands;
    /**
     * 规格参数过滤条件
     */
    private List<Map<String,Object>> specs;

	public SearchResult(Long total, Long totalPage, List<Goods> items,
	                    List<Category> categories, List<Brand> brands,
	                    List<Map<String,Object>> specs) {
		super(total, totalPage, items);
		this.categories = categories;
		this.brands = brands;
		this.specs = specs;
	}
	public SearchResult(Long total, Long totalPage, List<Goods> items,
	                    List<Category> categories, List<Brand> brands) {
		super(total, totalPage, items);
		this.categories = categories;
		this.brands = brands;
	}
}