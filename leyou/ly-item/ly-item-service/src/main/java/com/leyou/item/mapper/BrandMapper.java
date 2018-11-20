package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/19
 */
public interface BrandMapper extends Mapper<Brand> {
	/**
	 * 新增商品分类和品牌中间表数据
	 * @param cid 商品分类id
	 * @param bid 品牌id
	 * @return int
	 */
	@Insert("insert into tb_category_brand(category_id,brand_id) values (#{cid},#{bid})")
	int insertCategoryBrand(@Param("cid") Long cid,@Param("bid")Long bid);
}
