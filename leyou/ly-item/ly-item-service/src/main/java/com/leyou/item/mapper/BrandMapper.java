package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

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

	/**
	 * 通过多表连级查询，查询出cid所属的品牌
	 * @param cid cid
	 * @return Brand集合
	 */
	@Select("SELECT b.* FROM tb_brand b LEFT JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
	List<Brand> queryBrandByCid(@Param("cid") Long cid);
}
