package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/30
 */
public interface StockMapper extends BaseMapper<Stock> {
	@Update("UPDATE tb_stock SET stock = stock - #{num} WHERE sku_id = #{skuId} AND stock >= #{num}")
	public int decreaseStock(@Param("skuId") Long skuId , @Param("num") Integer num);
}
